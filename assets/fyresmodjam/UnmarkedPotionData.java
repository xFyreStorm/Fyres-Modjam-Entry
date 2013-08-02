package assets.fyresmodjam;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class UnmarkedPotionData extends WorldSavedData {

	public static String key = "FyresWorldData";
	
	public static int[] potionValues = null;

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
		if(nbttagcompound.hasKey("values")) {potionValues = nbttagcompound.getIntArray("values");}
		else {
			potionValues = new int[12];
			
			for(int i = 0; i < 12; i++) {
				for(int i2 = ModjamMod.r.nextInt(Potion.potionTypes.length); ; i2 = ModjamMod.r.nextInt(Potion.potionTypes.length)) {
					if(Potion.potionTypes[i2] != null) {
						boolean skip = false;
						for(int i3 = 0; i3 < potionValues.length; i3++) {if(potionValues[i] == i3) {skip = true;}}
						if(skip) {continue;}
						
						potionValues[i] = i2; break;
					}
				}
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		if(potionValues == null) {
			potionValues = new int[12];
			
			for(int i = 0; i < 12; i++) {
				for(int i2 = ModjamMod.r.nextInt(Potion.potionTypes.length); ; i2 = ModjamMod.r.nextInt(Potion.potionTypes.length)) {
					if(Potion.potionTypes[i2] != null) {
						boolean skip = false;
						for(int i3 = 0; i3 < potionValues.length; i3++) {if(potionValues[i] == i3) {skip = true;}}
						if(skip) {continue;}
						
						potionValues[i] = i2; break;
					}
				}
			}
		}
		
		nbttagcompound.setIntArray("values", potionValues);
	}
}
