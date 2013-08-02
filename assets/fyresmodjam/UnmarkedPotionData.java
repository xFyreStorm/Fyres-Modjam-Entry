package assets.fyresmodjam;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class UnmarkedPotionData extends WorldSavedData {

	public static String key = "FyresWorldData";

	public UnmarkedPotionData() {
		super(key);
	}

	public UnmarkedPotionData(String key) {
		super(key);
	}

	public static UnmarkedPotionData forWorld(World world) {
		MapStorage storage = world.perWorldStorage;
		UnmarkedPotionData result = (UnmarkedPotionData) storage.loadData(UnmarkedPotionData.class, key);
		if (result == null) {result = new UnmarkedPotionData(); storage.setData(key, result); }
		return result;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		
	}
}
