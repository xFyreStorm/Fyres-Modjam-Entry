package assets.fyresmodjam;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
	
	public static String[] sounds = {"pillarActivated", "coin"};

    @ForgeSubscribe
    public void onSound(SoundLoadEvent event) {
        for(String s : sounds) {event.manager.addSound("fyresmodjam:" + s + ".wav");}
    }
	
	@Override
	public void register() {
		 TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
	     KeyBindingRegistry.registerKeyBinding(new FyresKeyHandler());
	     ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPillar.class, new TileEntityPillarRenderer());
	     MinecraftForge.EVENT_BUS.register(this);
	}
}
