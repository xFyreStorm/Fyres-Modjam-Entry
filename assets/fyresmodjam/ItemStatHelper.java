package assets.fyresmodjam;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemStatHelper {
	public static ItemStack giveStat(ItemStack stack, String name, String value) {
		if(!stack.hasTagCompound()) {stack.setTagCompound(new NBTTagCompound());}
		NBTTagCompound data = stack.stackTagCompound;
		data.setString(name, value);
		return stack;
	}
	
	public static ItemStack setStackName(ItemStack stack, String name) {
		if(!stack.hasTagCompound()) {stack.setTagCompound(new NBTTagCompound());}
		if(!stack.getTagCompound().hasKey("display")) {stack.getTagCompound().setTag("display", new NBTTagCompound());}
		stack.getTagCompound().getCompoundTag("display").setString("Name", name);
		return stack;
	}
}
