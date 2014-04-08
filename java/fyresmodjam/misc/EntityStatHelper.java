package fyresmodjam.misc;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.EnumEntitySize;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import fyresmodjam.ModjamMod;
import fyresmodjam.handlers.CommonTickHandler;
import fyresmodjam.handlers.NewPacketHandler;
import fyresmodjam.worldgen.FyresWorldData;

@SuppressWarnings("rawtypes")
public class EntityStatHelper {

	public static String[] knowledge = {"Clueless", "Novice", "Competent", "Talented", "Expert", "Professional", "Master", "Legendary"};
	public static int[] killCount = {0, 10, 25, 50, 100, 250, 500, 1000};
	public static float[] damageBonus = {0, 0.01F, 0.025F, 0.05F, 0.075F, 0.1F, 0.15F, 0.2F};
	public static String[] damageBonusString = {"0", "1", "2.5", "5", "7.5", "10", "15", "20"};

	public static class EntityStatTracker {
		public Class[] classes;

		public boolean instanceAllowed = false;

		public EntityStatTracker(Class[] classes, boolean instancesAllowed) {this.classes = classes; instanceAllowed = instancesAllowed;}
		public EntityStatTracker(Class c, boolean instancesAllowed) {this(new Class[] {c}, instancesAllowed);}

		public ArrayList<EntityStat> stats = new ArrayList<EntityStat>();

		public void addStat(EntityStat stat) {
			if(!stats.contains(stat)) {stats.add(stat);}
		}
	}

	public static class EntityStat {
		public String name;
		public String value;

		public EntityStat(String name, String value) {
			this.name = name;
			this.value = value;
		}

		public Object getNewValue(Random r) {return value;}
		public String getAlteredEntityName(EntityLiving entity) {return entity.getCommandSenderName();}
		public void modifyEntity(Entity entity) {}
	}

	public static String getUnalteredName(Entity entity) {
		String s = EntityList.getEntityString(entity);
		if(s == null) {s = "generic";}
		return StatCollector.translateToLocal("entity." + s + ".name");
	}

	public static String getUnalteredItemName(Item item) {
		return StatCollector.translateToLocal(item.getUnlocalizedName() + ".name");
	}

	public static HashMap<Class, EntityStatTracker> statTrackersByClass = new HashMap<Class, EntityStatTracker>();
	public static ArrayList<EntityStatTracker> genericTrackers = new ArrayList<EntityStatTracker>();

	public static void addStatTracker(EntityStatTracker statTracker) {
		if(statTracker.classes != null) {
			for(Class c : statTracker.classes) {statTrackersByClass.put(c, statTracker);}
			if(statTracker.instanceAllowed) {genericTrackers.add(statTracker);}
		}
	}

	public static Entity giveStat(Entity entity, String name, Object value) {
		if(entity != null && name != null && value != null) {entity.getEntityData().setString(name, value.toString());}
		return entity;
	}

	public static Entity setName(EntityLiving entity, String name) {
		entity.setCustomNameTag(name);
		return entity;
	}

	public static String getStat(Entity entity, String name) {
		String s = null;
		if(entity.getEntityData() != null && entity.getEntityData().hasKey(name)) {s = entity.getEntityData().getString(name);}
		return s;
	}

	public static boolean hasStat(Entity entity, String name) {
		if(entity.getEntityData() != null && entity.getEntityData().hasKey(name)) {return true;}
		return false;
	}

	@SubscribeEvent
	public void entityJoinWorld(EntityJoinWorldEvent event) {
		if(!event.world.isRemote) {
			processEntity(event.entity, ModjamMod.r);

			boolean isClone = true;

			isClone = event.entity.getEntityData().hasKey("isClone") ? event.entity.getEntityData().getBoolean("isClone") : false;

			if(CommonTickHandler.worldData != null && CommonTickHandler.worldData.getDisadvantage().equals("Increased Mob Spawn") && (event.entity instanceof EntityMob) && !(event.entity instanceof EntityDragon) && !isClone && ModjamMod.r.nextInt(3) == 0) {

				event.entity.getEntityData().setBoolean("isClone", true);

				Entity entityNew = null;

				try {
					Constructor[] constructors = event.entity.getClass().getConstructors();

					for(int i = 0; i < constructors.length; i++) {
						Class[] parameters = constructors[i].getParameterTypes();
						if(parameters.length == 1 && parameters[0].equals(World.class)) {entityNew = (Entity) event.entity.getClass().getConstructors()[i].newInstance(event.world);}
					}

				} catch (Exception e) {e.printStackTrace();}

				if(entityNew != null) {
					entityNew.setLocationAndAngles(event.entity.posX, event.entity.posY, event.entity.posZ, event.entity.rotationYaw, event.entity.rotationPitch);
					entityNew.getEntityData().setBoolean("isClone", true);
					entityNew.dimension = event.entity.dimension;
					CommonTickHandler.addLater.add(entityNew);
				}
			}

			if(event.entity instanceof EntityPlayer) {
				if(!event.entity.getEntityData().hasKey("Blessing") && CommonTickHandler.worldData.blessingByPlayer.containsKey(event.entity.getCommandSenderName())) {
					event.entity.getEntityData().setString("Blessing", CommonTickHandler.worldData.blessingByPlayer.get(event.entity.getCommandSenderName()));

					NewPacketHandler.UPDATE_BLESSING.sendToPlayer((EntityPlayer) event.entity, event.entity.getEntityData().getString("Blessing"));

					CommonTickHandler.worldData.blessingByPlayer.remove(event.entity.getCommandSenderName());
					CommonTickHandler.worldData.markDirty();
				}

				if(!event.entity.getEntityData().hasKey("PotionKnowledge") && CommonTickHandler.worldData.potionKnowledgeByPlayer.containsKey(event.entity.getCommandSenderName())) {
					event.entity.getEntityData().setIntArray("PotionKnowledge", CommonTickHandler.worldData.potionKnowledgeByPlayer.get(event.entity.getCommandSenderName()));

					NewPacketHandler.UPDATE_POTION_KNOWLEDGE.sendToPlayer((EntityPlayer) event.entity, event.entity.getEntityData().getIntArray("PotionKnowledge"));

					CommonTickHandler.worldData.potionKnowledgeByPlayer.remove(event.entity.getCommandSenderName());
					CommonTickHandler.worldData.markDirty();
				}

				if(!event.entity.getEntityData().hasKey("KillStats") && CommonTickHandler.worldData.killStatsByPlayer.containsKey(event.entity.getCommandSenderName())) {
					event.entity.getEntityData().setTag("KillStats", CommonTickHandler.worldData.killStatsByPlayer.get(event.entity.getCommandSenderName()));
					CommonTickHandler.worldData.killStatsByPlayer.remove(event.entity.getCommandSenderName());
					CommonTickHandler.worldData.markDirty();
				}

				if(!event.entity.getEntityData().hasKey("WeaponStats") && CommonTickHandler.worldData.weaponStatsByPlayer.containsKey(event.entity.getCommandSenderName())) {
					event.entity.getEntityData().setTag("WeaponStats", CommonTickHandler.worldData.weaponStatsByPlayer.get(event.entity.getCommandSenderName()));
					CommonTickHandler.worldData.killStatsByPlayer.remove(event.entity.getCommandSenderName());
					CommonTickHandler.worldData.markDirty();
				}

				if(!event.entity.getEntityData().hasKey("CraftingStats") && CommonTickHandler.worldData.craftingStatsByPlayer.containsKey(event.entity.getCommandSenderName())) {
					event.entity.getEntityData().setTag("CraftingStats", CommonTickHandler.worldData.craftingStatsByPlayer.get(event.entity.getCommandSenderName()));
					CommonTickHandler.worldData.craftingStatsByPlayer.remove(event.entity.getCommandSenderName());
					CommonTickHandler.worldData.markDirty();
				}
			}
		}
	}

	public static ArrayList<EntityStatTracker> temp = new ArrayList<EntityStatTracker>();

	public static boolean b = false;

	@SuppressWarnings("unchecked")
	public static void processEntity(Entity entity, Random r) {

		if(entity == null) {return;}

		temp.clear();

		if(statTrackersByClass.containsKey(entity.getClass())) {temp.add(statTrackersByClass.get(entity.getClass()));}

		for(EntityStatTracker e : genericTrackers) {
			if(!temp.contains(e)) {
				for(Class c : e.classes) {if(c.isAssignableFrom(entity.getClass())) {temp.add(e); break;}}
			}
		}

		if(!temp.isEmpty()) {
			String processed = EntityStatHelper.getStat(entity, "processed");

			if(processed == null || processed.equals("false")) {
				EntityStatHelper.giveStat(entity, "processed", "true");

				for(EntityStatTracker statTracker : temp) {
					for(EntityStat s : statTracker.stats) {
						giveStat(entity, s.name, s.getNewValue(r).toString());
						if(entity instanceof EntityLiving) {setName((EntityLiving) entity, s.getAlteredEntityName((EntityLiving) entity));}
						s.modifyEntity(entity);
					}
				}
			}
		}
	}

	public void register() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public void livingDeath(LivingDeathEvent event) {
		if(!event.entity.worldObj.isRemote) {
			if(event.entity.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot")) {

				if(event.entity instanceof EntityLivingBase && event.source != null && event.source.getEntity() != null) {
					if(event.source.getEntity().getEntityData().hasKey("Blessing")) {
						String blessing = event.source.getEntity().getEntityData().getString("Blessing");

						if(blessing.equals("Thief") && ModjamMod.r.nextInt(20) == 0) {
							if(!event.entity.worldObj.isRemote) {
								event.entity.dropItem(Items.gold_nugget, 1);
							}
						}
					}
				}

				int level = 0;
				if(event.entity.getEntityData().hasKey("Level")) {level = Integer.parseInt(event.entity.getEntityData().getString("Level"));}
				if(ModjamMod.r.nextInt(30) == 0 || level == 5) {event.entity.entityDropItem(new ItemStack(ModjamMod.mysteryPotion, 1, ModjamMod.r.nextInt(13)), event.entity.height/2);}

			}

			if(event.entity instanceof EntityLivingBase && event.source != null && event.source.getEntity() != null) {
				if(event.source.getEntity().getEntityData().hasKey("Blessing")) {
					String blessing = event.source.getEntity().getEntityData().getString("Blessing");

					if(blessing.equals("Berserker")) {
						if(!EntityStatHelper.hasStat(event.source.getEntity(), "BlessingCounter")) {EntityStatHelper.giveStat(event.source.getEntity(), "BlessingCounter", 0);}
						EntityStatHelper.giveStat(event.source.getEntity(), "BlessingCounter", Math.min(10, Integer.parseInt(EntityStatHelper.getStat(event.source.getEntity(), "BlessingCounter")) + 1));
					}
				}
			}

			if(CommonTickHandler.worldData.currentTask.equals("Kill") && FyresWorldData.validMobs[CommonTickHandler.worldData.currentTaskID].isAssignableFrom(event.entity.getClass())) {
				CommonTickHandler.worldData.progress++;

				String name1 = CommonTickHandler.worldData.currentTask.equals("Kill") ? FyresWorldData.validMobNames[CommonTickHandler.worldData.currentTaskID] : FyresWorldData.validItems[CommonTickHandler.worldData.currentTaskID].getDisplayName();

				if(name1.contains("Block")) {if(name1.contains("Block")) {name1 = name1.replace("Block", "Blocks").replace("block", "blocks");}}
				else {name1 += "s";}

				NewPacketHandler.SEND_MESSAGE.sendToAllPlayers("\u00A7fCurrent Goal Progress: " + CommonTickHandler.worldData.progress + "/" + CommonTickHandler.worldData.currentTaskAmount + " " + name1 + " "+ CommonTickHandler.worldData.currentTask + "ed.");

				if(CommonTickHandler.worldData.progress >= CommonTickHandler.worldData.currentTaskAmount) {
					CommonTickHandler.worldData.progress = 0;
					CommonTickHandler.worldData.tasksCompleted++;

					NewPacketHandler.LEVEL_UP.sendToAllPlayers(CommonTickHandler.worldData.rewardLevels);

					if(!CommonTickHandler.worldData.enderDragonKilled && event.entity instanceof EntityDragon) {CommonTickHandler.worldData.enderDragonKilled = true;}
					CommonTickHandler.worldData.giveNewTask();

					NewPacketHandler.SEND_MESSAGE.sendToAllPlayers("\u00A7eA world goal has been completed!" + (!CommonTickHandler.worldData.getDisadvantage().equals("None") ? " World disadvantage has been lifted!": ""));

					NewPacketHandler.SEND_MESSAGE.sendToAllPlayers("\u00A7eA new world goal has been set: " + (CommonTickHandler.worldData.currentTask + " " + CommonTickHandler.worldData.currentTaskAmount + " " + (CommonTickHandler.worldData.currentTask.equals("Kill") ? FyresWorldData.validMobNames[CommonTickHandler.worldData.currentTaskID] : FyresWorldData.validItems[CommonTickHandler.worldData.currentTaskID].getDisplayName()) + "s. (" + CommonTickHandler.worldData.progress + " " + CommonTickHandler.worldData.currentTask + "ed)"));

					CommonTickHandler.worldData.currentDisadvantage = "None";
				}

				NewPacketHandler.UPDATE_WORLD_DATA.sendToAllPlayers(CommonTickHandler.worldData.potionValues, CommonTickHandler.worldData.potionDurations, CommonTickHandler.worldData.getDisadvantage(), CommonTickHandler.worldData.currentTask, CommonTickHandler.worldData.currentTaskID, CommonTickHandler.worldData.currentTaskAmount, CommonTickHandler.worldData.progress, CommonTickHandler.worldData.tasksCompleted, CommonTickHandler.worldData.enderDragonKilled, ModjamMod.spawnTraps, CommonTickHandler.worldData.rewardLevels, CommonTickHandler.worldData.mushroomColors);

				CommonTickHandler.worldData.markDirty();
			}

			if(!CommonTickHandler.worldData.enderDragonKilled && event.entity instanceof EntityDragon) {
				CommonTickHandler.worldData.enderDragonKilled = true;
				NewPacketHandler.UPDATE_WORLD_DATA.sendToAllPlayers(CommonTickHandler.worldData.potionValues, CommonTickHandler.worldData.potionDurations, CommonTickHandler.worldData.getDisadvantage(), CommonTickHandler.worldData.currentTask, CommonTickHandler.worldData.currentTaskID, CommonTickHandler.worldData.currentTaskAmount, CommonTickHandler.worldData.progress, CommonTickHandler.worldData.tasksCompleted, CommonTickHandler.worldData.enderDragonKilled, ModjamMod.spawnTraps, CommonTickHandler.worldData.rewardLevels, CommonTickHandler.worldData.mushroomColors);
				CommonTickHandler.worldData.markDirty();
			}
		}

		if(event.entity instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) event.entity;

			player.triggerAchievement(ModjamMod.losingIsFun);

			CommonTickHandler.worldData.blessingByPlayer.put(player.getCommandSenderName(), player.getEntityData().getString("Blessing"));
			CommonTickHandler.worldData.potionKnowledgeByPlayer.put(player.getCommandSenderName(), player.getEntityData().getIntArray("PotionKnowledge"));
			if(player.getEntityData() != null && player.getEntityData().hasKey("KillStats")) {CommonTickHandler.worldData.killStatsByPlayer.put(player.getCommandSenderName(), player.getEntityData().getCompoundTag("KillStats"));}
			if(player.getEntityData() != null && player.getEntityData().hasKey("WeaponStats")) {CommonTickHandler.worldData.weaponStatsByPlayer.put(player.getCommandSenderName(), player.getEntityData().getCompoundTag("WeaponStats"));}
			if(player.getEntityData() != null && player.getEntityData().hasKey("CraftingStats")) {CommonTickHandler.worldData.craftingStatsByPlayer.put(player.getCommandSenderName(), player.getEntityData().getCompoundTag("CraftingStats"));}

		} else if(event.source != null && event.source.getEntity() != null && event.source.getEntity() instanceof EntityPlayer) {

			EntityPlayer player = (EntityPlayer) event.source.getEntity();
			String mob = getUnalteredName(event.entity);

			if(!player.getEntityData().hasKey("KillStats")) {player.getEntityData().setTag("KillStats", new NBTTagCompound());}
			NBTTagCompound killStats = player.getEntityData().getCompoundTag("KillStats");

			if(!killStats.hasKey(mob)) {
				killStats.setInteger(mob, 0);

				if(!killStats.hasKey("TrackedMobList")) {
					killStats.setString("TrackedMobList", mob);
				} else {
					killStats.setString("TrackedMobList", killStats.getString("TrackedMobList") + ";" + mob);
				}
			}

			killStats.setInteger(mob, killStats.getInteger(mob) + 1);

			if(ModjamMod.enableMobKillStats) {
				for(int i = 0; i < knowledge.length; i++) {
					if(killCount[i] == killStats.getInteger(mob)) {
						NewPacketHandler.SEND_MESSAGE.sendToPlayer(player, "\u00A7o\u00A73You've become a " + knowledge[i].toLowerCase() + " " + mob.toLowerCase() + " slayer! (+" + damageBonusString[i] + "% damage against " + mob.toLowerCase() + "s.)" + (i < knowledge.length - 1 ? " " + (killCount[i + 1] - killCount[i]) + " " + mob.toLowerCase() + " kills to next rank." : ""));
						break;
					}
				}

				int count = 0;

				if(killStats.hasKey("TrackedMobList") && killStats.getString("TrackedMobList") != null && killStats.getString("TrackedMobList").length() > 0) {
					for(String object : killStats.getString("TrackedMobList").split(";")) {
						if(killStats.hasKey(object) && killStats.getInteger(object) >= killCount[2]) {count++;}
					}
				}

				if(count >= 5) {player.triggerAchievement(ModjamMod.theHunt);}
			}

			String weapon = "misc";

			if(player.getHeldItem() == null) {
				weapon = "fist";
			} else if(player.getHeldItem().getItem() != null && player.getHeldItem().getItem() instanceof ItemSword || player.getHeldItem().getItem() instanceof ItemBow || player.getHeldItem().getItem() instanceof ItemAxe) {
				weapon = getUnalteredItemName(player.getHeldItem().getItem());
			}

			if(!player.getEntityData().hasKey("WeaponStats")) {player.getEntityData().setTag("WeaponStats", new NBTTagCompound());}
			NBTTagCompound weaponStats = player.getEntityData().getCompoundTag("WeaponStats");

			if(!weaponStats.hasKey(weapon)) {
				weaponStats.setInteger(weapon, 0);

				if(!weaponStats.hasKey("TrackedItemList")) {
					weaponStats.setString("TrackedItemList", weapon);
				} else {
					weaponStats.setString("TrackedItemList", weaponStats.getString("TrackedItemList") + ";" + weapon);
				}
			}

			weaponStats.setInteger(weapon, weaponStats.getInteger(weapon) + 1);

			if(ModjamMod.enableWeaponKillStats) {
				for(int i = 0; i < knowledge.length; i++) {
					if(killCount[i] * 2 == weaponStats.getInteger(weapon)) {
						NewPacketHandler.SEND_MESSAGE.sendToPlayer(player, "\u00A7o\u00A73You've become a " + knowledge[i].toLowerCase() + " " + weapon.toLowerCase() + " user! (+" + damageBonusString[i] + "% damage with " + weapon.toLowerCase() + "s.)" + (i < knowledge.length - 1 ? " " + (killCount[i + 1] * 2 - killCount[i] * 2) + " " + weapon.toLowerCase() + " kills to next rank." : ""));
						break;
					}
				}

				int count = 0;

				if(weaponStats.hasKey("TrackedItemList") && weaponStats.getString("TrackedItemList") != null && weaponStats.getString("TrackedItemList").length() > 0) {
					for(String object : weaponStats.getString("TrackedItemList").split(";")) {
						if(weaponStats.hasKey(object) && weaponStats.getInteger(object) >= killCount[1] * 2) {count++;}
					}
				}

				if(count >= 10) {player.triggerAchievement(ModjamMod.jackOfAllTrades);}
			}

		}
	}

	public static void setEntitySize(Entity entity, float par1, float par2) {
		float f2;

		if (par1 != entity.width || par2 != entity.height) {
			f2 = entity.width;
			entity.width = par1;
			entity.height = par2;
			entity.boundingBox.maxX = entity.boundingBox.minX + entity.width;
			entity.boundingBox.maxZ = entity.boundingBox.minZ + entity.width;
			entity.boundingBox.maxY = entity.boundingBox.minY + entity.height;
		}

		f2 = par1 % 2.0F;

		if (f2 < 0.375D) {
			entity.myEntitySize = EnumEntitySize.SIZE_1;
		} else if (f2 < 0.75D) {
			entity.myEntitySize = EnumEntitySize.SIZE_2;
		} else if (f2 < 1.0D) {
			entity.myEntitySize = EnumEntitySize.SIZE_3;
		} else if (f2 < 1.375D) {
			entity.myEntitySize = EnumEntitySize.SIZE_4;
		} else if (f2 < 1.75D) {
			entity.myEntitySize = EnumEntitySize.SIZE_5;
		} else {
			entity.myEntitySize = EnumEntitySize.SIZE_6;
		}
	}
}
