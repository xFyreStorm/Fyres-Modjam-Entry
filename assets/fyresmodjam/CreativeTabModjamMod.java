package assets.fyresmodjam;

import java.lang.reflect.Field;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CreativeTabModjamMod extends CreativeTabs {

	public CreativeTabModjamMod(int par1, String par2Str) {
		super(par1, par2Str);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		return new ItemStack(ModjamMod.itemTrap.itemID, 1, 0);
	}
	
	public String getTranslatedTabLabel(){
		return "The \"You Will Die\" Mod";
	}
	
	public void displayAllReleventItems(List par1List) {
		try {
			for(Field f : ModjamMod.class.getFields()) {
				if(f.getType() == Item.class) {
					Item item = (Item) f.get(ModjamMod.instance);
					if(item.getCreativeTab() == null) {continue;}
					item.getSubItems(item.itemID, this, par1List);
				} else if(f.getType() == Block.class) {
					Block block = (Block) f.get(ModjamMod.instance);
					if(block.getCreativeTabToDisplayOn() == null) {continue;}
					block.getSubBlocks(block.blockID, this, par1List);
				}
			}
		} catch (Exception e) {e.printStackTrace();}
	}

}
