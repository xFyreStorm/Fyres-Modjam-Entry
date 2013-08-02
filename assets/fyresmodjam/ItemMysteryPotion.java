package assets.fyresmodjam;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;

public class ItemMysteryPotion extends Item {

	public static Icon[] icons = null;
	
	public ItemMysteryPotion(int par1) {
		super(par1);
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
		if(icons == null) {
			icons = new Icon[12];
			for(int i = 0; i < 12; i++) {icons[i] = par1IconRegister.registerIcon("fyresmodjam:mysteryPotion_" + (i + 1));}
		}
		
        this.itemIcon = icons[0];
    }
	
	@SideOnly(Side.CLIENT)
    public Icon getIconFromDamage(int par1)  {
        return par1 < 12 ? icons[par1] : icons[0];
    }
	
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
        for(int i = 0; i < 12; i++) {par3List.add(new ItemStack(par1, 1, i));}
    }
	
	public String getItemDisplayName(ItemStack par1ItemStack) {
        return "Mystery Potion #" + par1ItemStack.getItemDamage();
    }

}
