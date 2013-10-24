package assets.fyresmodjam;

import java.awt.Color;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class ItemMysteryMushroom extends Item {

	public static Icon overlay, icon;
	
	public ItemMysteryMushroom(int par1) {
		super(par1);
		this.setHasSubtypes(true);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
    public void registerIcons(IconRegister par1IconRegister) {
		if(icon == null) {icon = par1IconRegister.registerIcon("fyresmodjam:mushroom");}
		if(overlay == null) {overlay = par1IconRegister.registerIcon("fyresmodjam:mushroom_overlay");}
        this.itemIcon = icon;
    }
	
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
        for(int i = 0; i < 13; i++) {par3List.add(new ItemStack(par1, 1, i));}
    }
	
	@SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
		return PacketHandler.mushroomColors[par1ItemStack.getItemDamage() % 13][par2 % 2];
    }
	
	@SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamageForRenderPass(int par1, int par2) {
        return par2 > 0 ? overlay : icon;
    }

}
