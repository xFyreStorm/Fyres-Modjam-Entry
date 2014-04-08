package fyresmodjam.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import fyresmodjam.ModjamMod;
import fyresmodjam.handlers.CommonTickHandler;
import fyresmodjam.handlers.NewPacketHandler;

@SuppressWarnings("rawtypes")
public class ItemStatHelper {
	
	public static class ItemStatTracker {
		public Class[] classes;

		public boolean instanceAllowed = false;

		public ItemStatTracker(Class[] classes, boolean instanceAllowed) {
			this.classes = classes;
			this.instanceAllowed = instanceAllowed;
		}

		public ItemStatTracker(Class c, boolean instanceAllowed) {this(new Class[] {c}, instanceAllowed);}

		public ArrayList<ItemStat> stats = new ArrayList<ItemStat>();

		public void addStat(ItemStat stat) {
			if(!stats.contains(stat)) {stats.add(stat);}
		}
	}

	public static class ItemStat {
		public String name;
		public String value;

		public ItemStat(String name, Object value) {
			this.name = name;
			this.value = value.toString();
		}

		public Object getNewValue(ItemStack stack, Random r) {return value;}
		public String getLore(ItemStack stack) {return null;}
		public String getAlteredStackName(ItemStack stack, Random r) {return stack.getDisplayName();}
		public void modifyStack(ItemStack stack, Random r) {}
	}

	public static HashMap<Class, ArrayList<ItemStatTracker>> statTrackersByClass = new HashMap<Class, ArrayList<ItemStatTracker>>();

	public static ArrayList<ItemStatTracker> genericTrackers = new ArrayList<ItemStatTracker>();
	
	public static ArrayList<Class> skip = new ArrayList<Class>();

	public static void addStatTracker(ItemStatTracker statTracker) {
		if(statTracker.classes != null) {
			for(Class c : statTracker.classes) {
				if(!statTrackersByClass.containsKey(c)) {statTrackersByClass.put(c, new ArrayList<ItemStatTracker>());}
				statTrackersByClass.get(c).add(statTracker);
			}
		}
		
		if(statTracker.instanceAllowed) {genericTrackers.add(statTracker);}
	}

	public static ItemStack giveStat(ItemStack stack, String name, Object value) {
		if(!stack.hasTagCompound()) {stack.setTagCompound(new NBTTagCompound());}
		NBTTagCompound data = stack.stackTagCompound;
		data.setString(name, value.toString());
		return stack;
	}

	public static ItemStack setName(ItemStack stack, String name) {
		if(!stack.hasTagCompound()) {stack.setTagCompound(new NBTTagCompound());}
		if(!stack.getTagCompound().hasKey("display")) {stack.getTagCompound().setTag("display", new NBTTagCompound());}
		stack.getTagCompound().getCompoundTag("display").setString("Name", name);

		return stack;
	}

	public static ItemStack addLore(ItemStack stack, String lore) {
		if(!stack.hasTagCompound()) {stack.setTagCompound(new NBTTagCompound());}
		if(!stack.getTagCompound().hasKey("display")) {stack.getTagCompound().setTag("display", new NBTTagCompound());}
		if(!stack.getTagCompound().getCompoundTag("display").hasKey("Lore")) {stack.getTagCompound().getCompoundTag("display").setTag("Lore", new NBTTagList());}
		if(lore != null) {((NBTTagList) stack.getTagCompound().getCompoundTag("display").getTag("Lore")).appendTag(new NBTTagString(lore));}
		return stack;
	}

	public static String getName(ItemStack stack) {
		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("display") && stack.getTagCompound().getCompoundTag("display").hasKey("Name")) {
			return stack.getTagCompound().getCompoundTag("display").getString("Name");
		}

		return null;
	}

	public static String getStat(ItemStack stack, String name) {
		String s = null;
		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey(name)) {s = stack.getTagCompound().getString(name);}
		return s;
	}

	public static boolean hasStat(ItemStack stack, String name) {
		if(stack.getTagCompound() != null && stack.getTagCompound().hasKey(name)) {return true;}
		return false;
	}

	@SubscribeEvent
	public void playerDrops(PlayerDropsEvent event) {
		if(!event.entity.worldObj.isRemote) {
			if(CommonTickHandler.worldData.getDisadvantage().equals("Permadeath")) {
				for(EntityItem i : event.drops) {i.setDead();}
			}
		}
	}

	@SubscribeEvent
	public void livingHurt(LivingHurtEvent event) {
		if(!event.entity.worldObj.isRemote) {
			float damageMultiplier = 1.0F;

			boolean skip = false;

			if((CommonTickHandler.worldData.getDisadvantage().equals("Weak") && event.source.getDamageType().equals("player")) || (CommonTickHandler.worldData.getDisadvantage().equals("Tougher Mobs") && event.entity instanceof EntityMob)) {
				damageMultiplier -= 0.25F;
			}

			if(event.entity.getEntityData().hasKey("Blessing")) {
				String blessing = event.entity.getEntityData().getString("Blessing");

				if(blessing.equals("Guardian")) {
					damageMultiplier -= 0.20F;
				} else if(blessing.equals("Inferno") && (event.source.isFireDamage() || event.source.getDamageType().equals("inFire")  || event.source.getDamageType().equals("onFire")  || event.source.getDamageType().equals("lava"))) {
					skip = true;
					damageMultiplier = 0;
				} else if(blessing.equals("Paratrooper") && event.source.getDamageType().equals("fall")) {
					skip = true;
					damageMultiplier = 0;
				} else if(blessing.equals("Vampire")) {
					if(event.entity.getBrightness(1.0F) > 0.5F && event.entity.worldObj.canBlockSeeTheSky((int) (event.entity.posX), (int) (event.entity.posY), (int) (event.entity.posZ))) {
						damageMultiplier += 0.2F;
					}
				} else if(blessing.equals("Porcupine") && event.source.getEntity() != null && event.source.getEntity() instanceof EntityLivingBase && !event.source.isProjectile() && (event.source.damageType.equals("mob") || event.source.getDamageType().equals("player"))) {
					DamageSource damage = DamageSource.causeThornsDamage(event.entity);
					((EntityLivingBase) event.source.getEntity()).attackEntityFrom(damage, event.ammount * 0.07F);
				}
			}

			if(!skip && event.entity instanceof EntityLivingBase) {
				EntityLivingBase entity = (EntityLivingBase) event.entity;

				for(int i = 0; i < 4; i++) {
					ItemStack stack = entity.getEquipmentInSlot(i + 1);
					if(stack == null || stack.getTagCompound() == null || !stack.getTagCompound().hasKey("DamageReduction")) {continue;}
					damageMultiplier -= Float.parseFloat(stack.getTagCompound().getString("DamageReduction").trim().replace(",", ".")) * 0.01F;
				}
			}

			if(!skip && event.source != null && event.source.getEntity() != null) {
				if(event.source.getEntity() instanceof EntityLivingBase) {
					EntityLivingBase entity = (EntityLivingBase) event.source.getEntity();

					ItemStack held = entity.getEquipmentInSlot(0);

					if(held != null && ((event.source.getDamageType().equals("player") || event.source.getDamageType().equals("mob") || (held.getItem() == Items.bow && event.source.isProjectile())))) {
						String s = getStat(held, "BonusDamage");
						if(s != null) {event.ammount += Float.parseFloat(s.trim().replace(",", "."));}
					}
				}

				String mob = EntityStatHelper.getUnalteredName(event.entity);
				if(ModjamMod.enableMobKillStats && event.source.getEntity() instanceof EntityPlayer && event.source.getEntity().getEntityData().hasKey("KillStats") && event.source.getEntity().getEntityData().getCompoundTag("KillStats").hasKey(mob)) {
					int kills = event.source.getEntity().getEntityData().getCompoundTag("KillStats").getInteger(mob);

					int last = 0;
					for(int i = 0; i < EntityStatHelper.killCount.length; i++) {
						if(kills >= EntityStatHelper.killCount[i]) {last = i; continue;} else {break;}
					}

					damageMultiplier += EntityStatHelper.damageBonus[last];
				}

				String weapon = "misc";
				if(ModjamMod.enableWeaponKillStats && event.source.getEntity() instanceof EntityPlayer && event.source.getEntity().getEntityData().hasKey("WeaponStats") && event.source.getEntity().getEntityData().getCompoundTag("WeaponStats").hasKey(weapon)) {
					EntityPlayer player = (EntityPlayer) event.source.getEntity();

					if(player.getHeldItem() == null) {
						weapon = "fist";
					} else if(player.getHeldItem().getItem() != null && player.getHeldItem().getItem() instanceof ItemSword || player.getHeldItem().getItem() instanceof ItemBow || player.getHeldItem().getItem() instanceof ItemAxe) {
						weapon = EntityStatHelper.getUnalteredItemName(player.getHeldItem().getItem());
					}

					int kills = event.source.getEntity().getEntityData().getCompoundTag("WeaponStats").getInteger(weapon);

					int last = 0;
					for(int i = 0; i < EntityStatHelper.killCount.length; i++) {
						if(kills >= EntityStatHelper.killCount[i] * 2) {last = i; continue;} else {break;}
					}

					damageMultiplier += EntityStatHelper.damageBonus[last];
				}

				if(event.source.getEntity().getEntityData().hasKey("Blessing")) {
					String blessing = event.source.getEntity().getEntityData().getString("Blessing");

					ItemStack held = null;
					if(event.source.getEntity() instanceof EntityLivingBase) {held = ((EntityLivingBase) event.source.getEntity()).getHeldItem();}

					if(blessing.equals("Warrior") && (event.source.getDamageType().equals("player") || event.source.getDamageType().equals("mob"))) {
						damageMultiplier += 0.2F;
					} else if(blessing.equals("Hunter") && event.source.isProjectile()) {
						damageMultiplier += 0.2F;
					} else if(blessing.equals("Miner") && held != null && held.getItem() instanceof ItemPickaxe) {
						damageMultiplier += 0.2F;
					} else if(blessing.equals("Lumberjack") && held != null && held.getItem() instanceof ItemAxe) {
						damageMultiplier += 0.15F;
					} else if(event.entityLiving != null && blessing.equals("Ninja") && event.source.getEntity().isSneaking() && event.entityLiving.getHealth() == event.entityLiving.getMaxHealth()) {
						damageMultiplier += 1.0F;
					} else if(blessing.equals("Swamp") && event.entityLiving != null) {
						event.entityLiving.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 1, false));
					} else if(blessing.equals("Vampire") && event.source.getEntity() instanceof EntityLivingBase) {
						((EntityLivingBase) event.source.getEntity()).heal(event.ammount * damageMultiplier * 0.07F);

						if(event.source.getEntity().getBrightness(1.0F) > 0.5F && event.source.getEntity().worldObj.canBlockSeeTheSky((int) (event.source.getEntity().posX), (int) (event.source.getEntity().posY), (int) (event.source.getEntity().posZ))) {
							damageMultiplier -= 0.2F;
						}
					} else if(blessing.equals("Inferno") && event.source.getEntity().isBurning()) {
						damageMultiplier += 0.35F;
					} else if(blessing.equals("Berserker") && EntityStatHelper.hasStat(event.source.getEntity(), "BlessingActive") ? Boolean.parseBoolean(EntityStatHelper.getStat(event.source.getEntity(), "BlessingActive")) : false) {
						damageMultiplier += 0.3F;
					} else if(event.source.getEntity() instanceof EntityLivingBase && blessing.equals("Loner")) {
						damageMultiplier += 0.35F * (1.0F - (((EntityLivingBase) event.source.getEntity()).getHealth() / ((EntityLivingBase) event.source.getEntity()).getMaxHealth()));
					}
				}
			}

			event.ammount *= damageMultiplier;
		}
	}

	public static ArrayList<ItemStatTracker> temp = new ArrayList<ItemStatTracker>();

	@SuppressWarnings("unchecked")
	public static void processItemStack(ItemStack stack, Random r) {
		if(stack == null) {return;}

		temp.clear();

		Class itemClass = stack.getItem().getClass();
		
		if(statTrackersByClass.containsKey(itemClass)) {temp.addAll(statTrackersByClass.get(itemClass));}
		
		for(ItemStatTracker e : genericTrackers) {
			if(!temp.contains(e)) {
				for(Class c : e.classes) {
					if(c.isAssignableFrom(itemClass)) {
						ArrayList<ItemStatTracker> list = null;
						
						if(statTrackersByClass.containsKey(itemClass)) {
							list = new ArrayList<ItemStatTracker>();
							statTrackersByClass.put(itemClass, list);
						} else {list = statTrackersByClass.get(itemClass);}
						
						if(!statTrackersByClass.get(itemClass).contains(e)) {list.add(e);}
						
						temp.add(e);
						
						break;
					}
				}
			}
		}

		if(!temp.isEmpty()) {
			if(!stack.hasTagCompound()) {stack.setTagCompound(new NBTTagCompound());}

			String processed = ItemStatHelper.getStat(stack, "processed");

			if(processed == null || processed.equals("false")) {
				stack.getTagCompound().setTag("Lore", new NBTTagList());

				ItemStatHelper.giveStat(stack, "processed", "true");

				for(ItemStatTracker statTracker : temp) {
					for(ItemStat s : statTracker.stats) {
						giveStat(stack, s.name, s.getNewValue(stack, r).toString());

						String lore = s.getLore(stack);
						if(lore != null) {addLore(stack, lore);}

						setName(stack, s.getAlteredStackName(stack, r));

						s.modifyStack(stack, r);
					}
				}
			}
		} else {skip.add(itemClass);}
	}

	@SubscribeEvent
	public void onCrafting(ItemCraftedEvent event) {

		EntityPlayer player = event.player;
		ItemStack item = event.crafting;

		if(!player.worldObj.isRemote) {
			String itemName = null;

			if(item.getItem() != null && item.getItem() instanceof ItemSword || item.getItem() instanceof ItemBow || item.getItem() instanceof ItemAxe) {itemName = EntityStatHelper.getUnalteredItemName(item.getItem());}

			if(ModjamMod.enableCraftingStats && itemName != null) {
				if(!player.getEntityData().hasKey("CraftingStats")) {player.getEntityData().setTag("CraftingStats", new NBTTagCompound());}
				NBTTagCompound craftingStats = player.getEntityData().getCompoundTag("CraftingStats");

				if(!craftingStats.hasKey(itemName)) {
					craftingStats.setInteger(itemName, 0);

					if(!craftingStats.hasKey("TrackedItemList")) {
						craftingStats.setString("TrackedItemList", itemName);
					} else {
						craftingStats.setString("TrackedItemList", craftingStats.getString("TrackedItemList") + ";" + itemName);
					}
				}

				craftingStats.setInteger(itemName, craftingStats.getInteger(itemName) + 1);

				for(int i = 0; i < EntityStatHelper.knowledge.length; i++) {
					if(EntityStatHelper.killCount[i] == craftingStats.getInteger(itemName)) {
						NewPacketHandler.SEND_MESSAGE.sendToPlayer(player, "\u00A7o\u00A73You've become a " + EntityStatHelper.knowledge[i].toLowerCase() + " " + itemName.toLowerCase() + " smith! (" + (i < EntityStatHelper.knowledge.length - 1 ? (EntityStatHelper.killCount[i + 1] * 2 - EntityStatHelper.killCount[i] * 2) + " " + itemName.toLowerCase() + " crafts to next rank." : ""));
						break;
					}
				}
			}
		}
	}

	public void register() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void onSmelting(EntityPlayer player, ItemStack item) {}
}
