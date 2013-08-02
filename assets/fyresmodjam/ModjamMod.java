package assets.fyresmodjam;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = "fyresmodjam", name = "Fyres ModJam Mod", version = "0.0.0a")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = {"FyresModJamMod"}, packetHandler = PacketHandler.class)
public class ModjamMod  {
	
	@SidedProxy(clientSide = "assets.fyresmodjam.ClientProxy", serverSide = "assets.fyresmodjam.CommonProxy")
    public static CommonProxy proxy;
    
    @Instance("fyresmodjam")
    public static ModjamMod instance;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.register();
		MinecraftForge.EVENT_BUS.register(this);
		NetworkRegistry.instance().registerGuiHandler(this, new GUIHandler());
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
	
	@ForgeSubscribe
	public void entityJoinWorld(EntityJoinWorldEvent event) {
		if(!event.world.isRemote && event.entity instanceof EntityItem) {
			EntityItem item = (EntityItem) event.entity;
			ItemStack stack = item.getDataWatcher().getWatchableObjectItemStack(10);
			//ItemStatHelper.setStackName(stack, "Is this working?");
		}
	}
}
