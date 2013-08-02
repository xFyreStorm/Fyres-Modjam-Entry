package assets.fyresmodjam;

import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;

import assets.fyresmodjam.ItemStatHelper.*;
import assets.fyresmodjam.EntityStatHelper.*;

@Mod(modid = "fyresmodjam", name = "Fyres ModJam Mod", version = "0.0.0a")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = {"FyresModJamMod"}, packetHandler = PacketHandler.class)
public class ModjamMod  {
	
	@SidedProxy(clientSide = "assets.fyresmodjam.ClientProxy", serverSide = "assets.fyresmodjam.CommonProxy")
    public static CommonProxy proxy;
    
    @Instance("fyresmodjam")
    public static ModjamMod instance;
    
    public static Random r = new Random();
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.register();
		
		MinecraftForge.EVENT_BUS.register(this);
		new ItemStatHelper().register();
		
		NetworkRegistry.instance().registerGuiHandler(this, new GUIHandler());
		
		//Entity Trackers
		
		EntityStatTracker creeperTracker = new EntityStatTracker(EntityCreeper.class);
		
		creeperTracker.addStat(new EntityStat("Level", "") {
			public Object getNewValue(Random r) {return 1 + r.nextInt(5);}
			public String getAlteredEntityName(EntityLiving entity) {return entity.getEntityName() + ", Level" + entity.getEntityData().getString(name);}
		});
		
		EntityStatHelper.addStatTracker(creeperTracker);
		
		//Item Trackers
		
		ItemStatTracker swordTracker = new ItemStatTracker(ItemSword.class, -1);
		
		swordTracker.addStat(new ItemStat("Prefix", "") {
			public String[] prefixes = new String[] {"Old", "Sharp", "Average"};
			public Object getNewValue(Random r) {return prefixes[r.nextInt(prefixes.length)];}
			public String getAlteredStackName(ItemStack stack) {return "\u00A7f" + stack.getTagCompound().getString(name) + " " + stack.getDisplayName();}
		});
		
		swordTracker.addStat(new ItemStat("BonusDamage", "") {
			public Object getNewValue(Random r) {return r.nextInt(7);}
			
			public String getLore(ItemStack stack) {
				int damage = Integer.parseInt(stack.getTagCompound().getString(name));
				return damage > 0 ? "\u00A77\u00A7o  +" + damage + " bonus damage" : null;
			}
		});
		
		swordTracker.addStat(new ItemStat("Rank", "") {
			public Object getNewValue(Random r) {return 1 + r.nextInt(5);}
			public String getLore(ItemStack stack) {return "\u00A7eRank: "+ Integer.parseInt(stack.getTagCompound().getString(name));}
		});
		
		//swordTracker.giveStat("BonusDamage,+%v bonus damage", "#i,0,6");
		//swordTracker.giveStat("Rank,Rank: %v", "#i,1,5");
		
		ItemStatHelper.addStatTracker(swordTracker);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
