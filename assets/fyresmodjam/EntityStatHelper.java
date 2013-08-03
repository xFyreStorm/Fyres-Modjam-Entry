package assets.fyresmodjam;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;

import assets.fyresmodjam.EntityStatHelper.EntityStat;
import assets.fyresmodjam.ItemStatHelper.ItemStatTracker;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet5PlayerInventory;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class EntityStatHelper {
	
	//There's probably a better way of doing all of this. :P Oh well.
	
	public static class EntityStatTracker {
		public Class[] classes;
		
		public boolean instanceAllowed = false;
		
		public EntityStatTracker(Class[] classes, boolean instancesAllowed) {this.classes = classes; this.instanceAllowed = instancesAllowed;}
		public EntityStatTracker(Class c, boolean instancesAllowed) {this(new Class[] {c}, instancesAllowed);}

		//public HashMap<String, String> stats = new HashMap<String, String>();
		public ArrayList<EntityStat> stats = new ArrayList<EntityStat>();
		//public StatTracker giveStat(String name, String value) {stats.put(name, value); return this;}

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
		public String getAlteredEntityName(EntityLiving entity) {return entity.getEntityName();}
		public void modifyEntity(Entity entity) {}
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
		entity.getEntityData().setString(name, value.toString());
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
	
	@ForgeSubscribe
	public void entityJoinWorld(EntityJoinWorldEvent event) {
		if(!event.world.isRemote) {processEntity(event.entity, ModjamMod.r);}
	}
	
	public static ArrayList<EntityStatTracker> temp = new ArrayList<EntityStatTracker>();
	
	public static void processEntity(Entity entity, Random r) {
		
		if(entity == null) {return;}
		
		String processed = EntityStatHelper.getStat(entity, "processed");
		
		if(processed == null || processed.equals("false")) {
			
			temp.clear();
			
			if(statTrackersByClass.containsKey(entity.getClass())) {temp.add(statTrackersByClass.get(entity.getClass()));}
			
			for(EntityStatTracker e : genericTrackers) {
				if(!temp.contains(e)) {
					for(Class c : e.classes) {if(c.isAssignableFrom(entity.getClass())) {temp.add(e); break;}}
				}
			}
			
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
	
	public void register() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@ForgeSubscribe
	public void livingDeath(LivingDeathEvent event) {
		
		if(event.entity instanceof EntityLivingBase && event.source != null && event.source.getEntity() != null) {
			if(event.source.getEntity().getEntityData().hasKey("Blessing")) {
				String blessing = event.source.getEntity().getEntityData().getString("Blessing");
				
				if(blessing.equals("Thief")) {
					if(event.entity.worldObj.getGameRules().getGameRuleBooleanValue("doMobLoot") && ModjamMod.r.nextInt(5) == 0) {
						if(!event.entity.worldObj.isRemote) {event.entity.dropItem(Item.goldNugget.itemID, 1);}
						event.entity.worldObj.playSoundAtEntity(event.entity, "fyresmodjam:coin", 1.0F, 1.0F);
					}
				}
			}
		}
		
		int level = 0;
		if(event.entity.getEntityData().hasKey("Level")) {level = Integer.parseInt(event.entity.getEntityData().getString("Level"));}
		if(ModjamMod.r.nextInt(20) == 0 || level == 5) {event.entity.entityDropItem(new ItemStack(ModjamMod.mysteryPotion.itemID, 1, ModjamMod.r.nextInt(12)), event.entity.height/2);}
	}
}
