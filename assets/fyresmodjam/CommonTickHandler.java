package assets.fyresmodjam;

import java.util.EnumSet;

import net.minecraft.world.World;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class CommonTickHandler implements ITickHandler {
	public static MysteryPotionData worldData = null;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		if(type.equals(EnumSet.of(TickType.WORLDLOAD))){
			World world = null;

			for(int i = 0; i < tickData.length; i++) {
				if(tickData[i] instanceof World) {world = (World) tickData[i];}
			}

			if(world == null) {return;}

			worldData = MysteryPotionData.forWorld(world);
			worldData.markDirty();
		} else if(type.equals(EnumSet.of(TickType.SERVER))) {
			serverTick();
		}
		
	}
	
	public void serverTick() {}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {

	}

	@Override
	public EnumSet ticks() {
        return EnumSet.of(TickType.SERVER, TickType.WORLDLOAD);
    }

	@Override
	public String getLabel() {
		return "FyresModJamCommonTicker";
	}
}