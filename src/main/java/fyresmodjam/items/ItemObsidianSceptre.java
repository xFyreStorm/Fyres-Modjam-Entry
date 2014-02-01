package fyresmodjam.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class ItemObsidianSceptre extends Item {

	public Icon icon, icon2;
	
	public ItemObsidianSceptre(int par1) {
		super(par1);
		this.hasSubtypes = true;
	}

	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
		icon = par1IconRegister.registerIcon("fyresmodjam:unenchantedSceptre");
		icon2 = par1IconRegister.registerIcon("fyresmodjam:enchantedSceptre");
        this.itemIcon = icon;
    }
	
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
        for(int i = 0; i < 2; i++) {par3List.add(new ItemStack(par1, 1, i));}
    }
	
	@SideOnly(Side.CLIENT)
	public String getItemDisplayName(ItemStack par1ItemStack) {
		return (par1ItemStack.getItemDamage() == 0 ? "" : "Infused ") + super.getItemDisplayName(par1ItemStack);
	}
	
	@SideOnly(Side.CLIENT)
    public Icon getIconFromDamage(int par1) {
        return par1 == 0 ? icon : icon2;
    }
	
	@SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack par1ItemStack) {
		return par1ItemStack.getItemDamage() > 0;
	}
}
