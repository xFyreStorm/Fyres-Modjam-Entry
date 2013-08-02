package assets.fyresmodjam;

import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
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
		MinecraftForge.EVENT_BUS.register(new ItemStatHelper());
		
		NetworkRegistry.instance().registerGuiHandler(this, new GUIHandler());
		
		ItemStatTracker swordTracker = new ItemStatTracker();
		
		swordTracker.stats.add(new ItemStat("BonusDamage", "") {
			public Object getNewValue(Random r) {return r.nextInt(7);}
			
			public String getLore(ItemStack stack) {
				int damage = Integer.parseInt(stack.getTagCompound().getString(name));
				return damage > 0 ? "+" + damage + " bonues damage" : null;
			}
		});
		
		//swordTracker.giveStat("BonusDamage,+%v bonus damage", "#i,0,6");
		//swordTracker.giveStat("Rank,Rank: %v", "#i,1,5");
		
		ItemStatHelper.addStatTracker(swordTracker, ItemSword.class, -1);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
}
