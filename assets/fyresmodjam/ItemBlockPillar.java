package assets.fyresmodjam;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemBlockPillar extends Item
{
    public ItemBlockPillar(int par1) {
        super(par1);
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon("fyresmodjam:itemPillar");
    }

    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        Block block = ModjamMod.blockPillar;

        if (!block.canPlaceBlockAt(par3World, par4, par5, par6)) {
            return false;
        } else {
            par3World.setBlock(par4, par5, par6, block.blockID);
            par3World.setBlockMetadataWithNotify(par4, par5, par6, 0, 0);
            
            par3World.setBlock(par4, par5 + 1, par6, block.blockID);
            par3World.setBlockMetadataWithNotify(par4, par5 + 1, par6, 2, 0);
            
            --par1ItemStack.stackSize;
            
            return true;
        }
    }
}
