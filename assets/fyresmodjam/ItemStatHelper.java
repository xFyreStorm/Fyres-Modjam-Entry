package assets.fyresmodjam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class ItemStatHelper {
	
	//There's probably a better way of doing all of this. :P Oh well.
	
	public static class StatTracker {
		public HashMap<String, String> stats = new HashMap<String, String>();
		public StatTracker giveStat(String name, String value) {stats.put(name, value); return this;}
	}
	
	//public static String[] swordPrefixes = new String[] {"Old", "Sharp", "Average"};
	
	public static HashMap<Class, StatTracker> statTrackersByClass = new HashMap<Class, StatTracker>();
	public static HashMap<Integer, StatTracker> statTrackersByID = new HashMap<Integer, StatTracker>();
	
	public static ItemStack giveStat(ItemStack stack, String name, String value) {
		if(!stack.hasTagCompound()) {stack.setTagCompound(new NBTTagCompound());}
		NBTTagCompound data = stack.stackTagCompound;
		data.setString(name, value);
		return stack;
	}
	
	public static ItemStack setName(ItemStack stack, String name) {
		if(!stack.hasTagCompound()) {stack.setTagCompound(new NBTTagCompound());}
		if(!stack.getTagCompound().hasKey("display")) {stack.getTagCompound().setTag("display", new NBTTagCompound());}
		stack.getTagCompound().getCompoundTag("display").setString("Name", name);
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
	
	@ForgeSubscribe
	public void entityJoinWorld(EntityJoinWorldEvent event) {
		if(!event.world.isRemote && event.entity instanceof EntityItem) {
			EntityItem item = (EntityItem) event.entity;
			processItemStack(item.getDataWatcher().getWatchableObjectItemStack(10), ModjamMod.r);
		}
	}
	
	@ForgeSubscribe
	public void livingHurt(LivingHurtEvent event) {
		if(event.entity instanceof EntityLivingBase) {
			EntityLivingBase entity = (EntityLivingBase) event.entity;
			
			ItemStack held = entity.getCurrentItemOrArmor(0);
			
			if(held != null) {
				if(held.getItem() instanceof ItemSword) {
					if(ItemStatHelper.hasStat(held, "display")) {}
				}
			}
		}
	}
	
	public static void processItemStack(ItemStack stack, Random r) {
		Class c = stack.getItem().getClass();
		int id = stack.itemID;
		
		if(stack != null && (statTrackersByClass.containsKey(c) || statTrackersByID.containsKey(id))) {
			String processed = ItemStatHelper.getStat(stack, "processed");
			if(processed == null || processed.equals("false")) {
				
				StatTracker statTrackerClass = statTrackersByClass.get(c);
				StatTracker statTrackerID = statTrackersByID.get(c);
				
				ItemStatHelper.giveStat(stack, "processed", "true");
				
				if(statTrackerClass != null) {
					for(String s : statTrackerClass.stats.keySet()) {giveStat(stack, s, statTrackerClass.stats.get(s));}
				}
				
				if(statTrackerID != null) {
					for(String s : statTrackerID.stats.keySet()) {giveStat(stack, s, statTrackerID.stats.get(s));}
				}
				
				/*if(stack.getItem() instanceof ItemSword) {
					int i = r.nextInt(swordPrefixes.length);
					ItemStatHelper.giveStat(stack, "namePrefix", "" + i);
					ItemStatHelper.setName(stack, swordPrefixes[i] + " " + stack.getDisplayName());
				}*/
			}
		}
	}
}
