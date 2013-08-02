package assets.fyresmodjam;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
	@Override
	public void register() {
		 TickRegistry.registerTickHandler(new ClientTickHandler(), Side.CLIENT);
	     KeyBindingRegistry.registerKeyBinding(new FyresKeyHandler());
	}
}
