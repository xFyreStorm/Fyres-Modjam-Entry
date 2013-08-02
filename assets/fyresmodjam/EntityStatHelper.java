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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet5PlayerInventory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class EntityStatHelper {
	
	//There's probably a better way of doing all of this. :P Oh well.
	
	public static class EntityStatTracker {
		public Class[] classes;
		
		public EntityStatTracker(Class[] classes) {this.classes = classes;}
		public EntityStatTracker(Class c) {this(new Class[] {c});}

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
		public String getAlteredEntityName(Entity entity) {return entity.getEntityName();}
	}
	
	public static HashMap<Class, EntityStatTracker> statTrackersByClass = new HashMap<Class, EntityStatTracker>();
	
	public static void addStatTracker(EntityStatTracker statTracker) {
		for(Class c : statTracker.classes) {statTrackersByClass.put(c, statTracker);}
	}
	
	public static Entity giveStat(Entity entity, String name, String value) {
		entity.getEntityData().setString(name, value);
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
		if(!event.world.isRemote) {
		}
	}
	
	public static void processEntity(Entity entity, Random r) {
		Class c = entity.getClass();
		
		if(entity != null && statTrackersByClass.containsKey(c)) {
			String processed = EntityStatHelper.getStat(entity, "processed");
			if(processed == null || processed.equals("false")) {
				
				EntityStatTracker statTrackerClass = statTrackersByClass.get(c);
				
				EntityStatHelper.giveStat(entity, "processed", "true");
				
				if(statTrackerClass != null) {
					for(EntityStat s : statTrackerClass.stats) {
						giveStat(entity, s.name, s.getNewValue(r).toString());
						if(entity instanceof EntityLiving) {setName((EntityLiving) entity, s.getAlteredEntityName(entity));}
					}
				}
			}
		}
	}
	
	public void register() {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
