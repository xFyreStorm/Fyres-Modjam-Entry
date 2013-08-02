package assets.fyresmodjam;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.entity;

import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;

import assets.fyresmodjam.EntityStatHelper.EntityStat;

import net.minecraft.entity.Entity;
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
		public Class c;
		
		public EntityStatTracker(Class c, int id) {
			this.c = c;
		}

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
	
	public static void addStatTracker(EntityStatTracker statTracker, Class c, int id) {
		if(c != null) {statTrackersByClass.put(c, statTracker);}
	}
	
	public static Entity giveStat(Entity entity, String name, String value) {
		entity.getEntityData().setString(name, value);
		return entity;
	}
	
	public static Entity setName(Entity entity, String name) {
		if(!entity.hasTagCompound()) {entity.setTagCompound(new NBTTagCompound());}
		if(!entity.getTagCompound().hasKey("display")) {entity.getTagCompound().setTag("display", new NBTTagCompound());}
		entity.getTagCompound().getCompoundTag("display").setString("Name", name);
		return entity;
	}
	
	public static Entity addLore(Entity entity, String lore) {
		if(!entity.hasTagCompound()) {entity.setTagCompound(new NBTTagCompound());}
		if(!entity.getTagCompound().hasKey("display")) {entity.getTagCompound().setTag("display", new NBTTagCompound());}
		if(!entity.getTagCompound().getCompoundTag("display").hasKey("Lore")) {entity.getTagCompound().getCompoundTag("display").setTag("Lore", new NBTTagList());}
		entity.getTagCompound().getCompoundTag("display").getTagList("Lore").appendTag(new NBTTagString("", lore));
		return entity;
	}
	
	public static String getName(Entity entity) {
		if(entity.getTagCompound() != null && entity.getTagCompound().hasKey("display") && entity.getTagCompound().getCompoundTag("display").hasKey("Name")) {
			return entity.getTagCompound().getCompoundTag("display").getString("Name");
		}
		
		return null;
	}
	
	public static String getStat(Entity entity, String name) {
		String s = null;
		if(entity.getTagCompound() != null && entity.getTagCompound().hasKey(name)) {s = entity.getTagCompound().getString(name);}
		return s;
	}
	
	public static boolean hasStat(Entity entity, String name) {
		if(entity.getTagCompound() != null && entity.getTagCompound().hasKey(name)) {return true;}
		return false;
	}
	
	@ForgeSubscribe
	public void entityJoinWorld(EntityJoinWorldEvent event) {
		if(!event.world.isRemote) {
		}
	}
	
	public static void processEntity(Entity entity, Random r) {
		Class c = entity.getClass();
		
		if(entity != null && (statTrackersByClass.containsKey(c) || statTrackersByID.containsKey(id))) {
			String processed = EntityStatHelper.getStat(entity, "processed");
			if(processed == null || processed.equals("false")) {
				
				EntityStatTracker statTrackerClass = statTrackersByClass.get(c);
				EntityStatTracker statTrackerID = statTrackersByID.get(c);
				
				EntityStatHelper.giveStat(entity, "processed", "true");
				
				if(statTrackerClass != null) {
					for(EntityStat s : statTrackerClass.stats) {
						/*String[] value = statTrackerClass.stats.get(s).split(",");
						if(value.length == 3 && value[0].equals("#i")) {value[0] = "" + (Integer.parseInt(value[1]) + ModjamMod.r.nextInt(Integer.parseInt(value[2])));}
						String[] data = s.replace("%v", value[0]).split(",");
						
						System.out.println(data + ", " + value);
						
						giveStat(entity, data[0], value[0]);
						
						if(data.length > 1) {addLore(entity, data[1]);}*/
						
						giveStat(entity, s.name, s.getNewValue(r).toString());
						
						String lore = s.getLore(entity);
						if(lore != null) {addLore(entity, lore);}
						
						setName(entity, s.getAlteredentityName(entity));
					}
				}
				
				if(statTrackerID != null) {
					for(EntityStat s : statTrackerID.stats) {
						giveStat(entity, s.name, s.getNewValue(r).toString());
						
						String lore = s.getLore(entity);
						if(lore != null) {addLore(entity, lore);}
					}
				}
				
				//Apparently is was syncing fine. :P
				
				/*try {
	                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
	                DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
	                //dataoutputstream.writeInt(this.currentWindowId);
	                Packet.writeEntity(entity, dataoutputstream);
	                //PacketDispatcher.sendPacketToAllPlayers(new Packet250CustomPayload("MC|TrList", bytearrayoutputstream.toByteArray()));
	            } catch (IOException ioexception) {ioexception.printentityTrace();}*/
			}
		}
	}
	
	public void register() {
		MinecraftForge.EVENT_BUS.register(this);
	}
}
