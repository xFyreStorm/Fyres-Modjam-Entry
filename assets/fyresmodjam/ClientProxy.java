package assets.fyresmodjam;

import java.awt.Color;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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
    
    @ForgeSubscribe
    public void guiRenderEvent(RenderGameOverlayEvent.Post event) {
    	if(event.type == RenderGameOverlayEvent.ElementType.HOTBAR) {
	    	MovingObjectPosition mouse = Minecraft.getMinecraft().objectMouseOver;
			
			if(mouse != null && Minecraft.getMinecraft().theWorld != null && mouse.typeOfHit == EnumMovingObjectType.TILE && Minecraft.getMinecraft().theWorld.getBlockId(mouse.blockX, mouse.blockY, mouse.blockZ) == ModjamMod.blockPillar.blockID) {
		        String key = Keyboard.getKeyName(FyresKeyHandler.examine.keyCode);
		        String string = "Press " + key + " to Examine";
		        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(string, (event.resolution.getScaledWidth() / 2) - (Minecraft.getMinecraft().fontRenderer.getStringWidth(string) / 2), event.resolution.getScaledHeight() / 2 + 16, Color.WHITE.getRGB());
			}
    	}
    }
	
	@Override
	public void register() {
		 //TickRegistry.registerTickHandler(new CommonTickHandler(), Side.SERVER);
		 TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
		 
	     KeyBindingRegistry.registerKeyBinding(new FyresKeyHandler());
	     
	     ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPillar.class, new TileEntityPillarRenderer());
	     ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTrap.class, new TileEntityTrapRenderer());
	     
	     MinecraftForge.EVENT_BUS.register(this);
	}
}
