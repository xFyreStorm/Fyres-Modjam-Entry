package fyresmodjam.misc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.ModjamMod;

public class CreativeTabModjamMod extends CreativeTabs {

	public CreativeTabModjamMod(int par1, String par2Str) {
		super(par1, par2Str);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		return new ItemStack(ModjamMod.itemTrap, 1, 0);
	}

	@Override
	public String getTranslatedTabLabel(){
		return "The \"You Will Die\" Mod";
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void displayAllReleventItems(List par1List) {
		try {
			ArrayList<ItemStack> list = new ArrayList<ItemStack>();

			for(Field f : ModjamMod.class.getFields()) {
				if(f.getType() == Item.class) {
					Item item = (Item) f.get(ModjamMod.instance);
					if(item == null || item.getCreativeTab() == null) {continue;}
					item.getSubItems(item, this, list);
				} else if(f.getType() == Block.class) {
					Block block = (Block) f.get(ModjamMod.instance);
					if(block == null || block.getCreativeTabToDisplayOn() == null) {continue;}
					block.getSubBlocks(block.getItem(null, 0, 0, 0), this, list);
				}
			}

			for(ItemStack i : list) {
				if(i == null || i.getItem() == null) {continue;}
				if(i.getItem().getIconIndex(i) != null) {par1List.add(i);}
			}
		} catch (Exception e) {e.printStackTrace();}
	}

	@Override
	public Item getTabIconItem() {
		return ModjamMod.itemTrap;
	}

}
