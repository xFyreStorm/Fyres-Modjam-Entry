package assets.fyresmodjam;

import java.util.EnumSet;

import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class FyresKeyHandler extends KeyHandler {
	
	public static KeyBinding[] keyBindings = new KeyBinding[] {};
	public static boolean[] repeat = new boolean[] {};

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
		
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}
