package assets.fyresmodjam;

import java.util.EnumSet;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class FyresKeyHandler extends KeyHandler {
	
	public static KeyBinding examine = new KeyBinding("Examine", Keyboard.KEY_X);
	
	public static KeyBinding[] keyBindings = new KeyBinding[] {examine};
	public static boolean[] repeat = new boolean[] {false};

	public FyresKeyHandler() {
		super(keyBindings, repeat);
	}

	@Override
	public String getLabel() {
		return "KeyHandler for FyresModJamMod";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		if(kb.equals(examine) && tickEnd) {
			Minecraft minecraft = Minecraft.getMinecraft();
			EntityPlayer player = minecraft.thePlayer;
			
			if(player != null && minecraft.objectMouseOver != null) {
				TileEntity te = minecraft.theWorld.getBlockTileEntity(minecraft.objectMouseOver.blockX, minecraft.objectMouseOver.blockY, minecraft.objectMouseOver.blockZ);
				
				if(te != null && te instanceof TileEntityPillar) {
					int index = 0;
					for(int i = 0; i < TileEntityPillar.validBlessings.length; i++) {if(TileEntityPillar.validBlessings[i].equals(((TileEntityPillar) te).blessing)) {index = i; break;}}
					Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage("Blessing of the " + ((TileEntityPillar) te).blessing + ": " + TileEntityPillar.blessingDescriptions[index] + ".");
				}
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}
