package assets.fyresmodjam;

import java.util.EnumSet;

import net.minecraft.world.World;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class CommonTickHandler implements ITickHandler {
	public static UnmarkedPotionData worldData = null;

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		System.out.println(true);
		
		if(type.equals(EnumSet.of(TickType.WORLDLOAD))){
			World world = null;

			for(int i = 0; i < tickData.length; i++) {
				if(tickData[i] instanceof World) {world = (World) tickData[i];}
			}

			if(world == null) {return;}

			worldData = UnmarkedPotionData.forWorld(world);
			worldData.markDirty();
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {

	}

	@Override
	public EnumSet ticks() {
        return EnumSet.of(TickType.SERVER, TickType.WORLDLOAD);
    }

	@Override
	public String getLabel() {
		return null;
	}
}