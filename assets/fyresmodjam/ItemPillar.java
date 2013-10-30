package assets.fyresmodjam;

import java.util.List;

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

public class ItemPillar extends Item
{
    public ItemPillar(int par1) {
        super(par1);
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.hasSubtypes = true;
    }
    
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
        if(ModjamMod.showAllPillarsInCreative) {
        	for(int i = 0; i < TileEntityPillar.validBlessings.length + 1; i++) {par3List.add(new ItemStack(par1, 1, i));}
        } else {super.getSubItems(par1, par2CreativeTabs, par3List);}
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon("fyresmodjam:itemPillar");
    }
    
    @SideOnly(Side.CLIENT)
	public String getItemDisplayName(ItemStack par1ItemStack) {
    	return super.getItemDisplayName(par1ItemStack) + (par1ItemStack.getItemDamage() == 0 ? "" : " (Blessing of the " + TileEntityPillar.validBlessings[(par1ItemStack.getItemDamage() - 1) % TileEntityPillar.validBlessings.length] + ")");
    }

    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        Block block = ModjamMod.blockPillar;

        if(par7 == 0) {par5 -= 2;}
        if(par7 == 1 && !Block.blocksList[par3World.getBlockId(par4, par5, par6)].isBlockReplaceable(par3World, par4, par5, par6)) {++par5;}
        if(par7 == 2) {--par6;}
        if(par7 == 3) {++par6;}
        if(par7 == 4) {--par4;}
        if(par7 == 5) {++par4;}
        
        if(!block.canPlaceBlockAt(par3World, par4, par5, par6)) {
            return false;
        } else {
            par3World.setBlock(par4, par5, par6, block.blockID);
            par3World.setBlockMetadataWithNotify(par4, par5, par6, 0, 0);
            
            par3World.setBlock(par4, par5 + 1, par6, block.blockID);
            par3World.setBlockMetadataWithNotify(par4, par5 + 1, par6, 1, 0);
            
           if(par1ItemStack.getItemDamage() != 0) {((TileEntityPillar) par3World.getBlockTileEntity(par4, par5, par6)).blessing = TileEntityPillar.validBlessings[par1ItemStack.getItemDamage() - 1];}
            
            --par1ItemStack.stackSize;
            
            return true;
        }
    }
}
