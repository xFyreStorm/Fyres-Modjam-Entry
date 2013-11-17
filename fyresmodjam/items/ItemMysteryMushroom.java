package fyresmodjam.items;

import java.awt.Color;
import java.util.List;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.ModjamMod;
import fyresmodjam.handlers.PacketHandler;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemMysteryMushroom extends Item {

	public Icon overlay, icon;
	
	public ItemMysteryMushroom(int par1) {
		super(par1);
		this.setHasSubtypes(true);
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
		icon = par1IconRegister.registerIcon("fyresmodjam:mushroom");
		overlay = par1IconRegister.registerIcon("fyresmodjam:mushroom_overlay");
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

    @Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        int i1 = par3World.getBlockId(par4, par5, par6);

        if (i1 == Block.snow.blockID && (par3World.getBlockMetadata(par4, par5, par6) & 7) < 1)
        {
            par7 = 1;
        }
        else if (i1 != Block.vine.blockID && i1 != Block.tallGrass.blockID && i1 != Block.deadBush.blockID
                && (Block.blocksList[i1] == null || !Block.blocksList[i1].isBlockReplaceable(par3World, par4, par5, par6)))
        {
            if (par7 == 0)
            {
                --par5;
            }

            if (par7 == 1)
            {
                ++par5;
            }

            if (par7 == 2)
            {
                --par6;
            }

            if (par7 == 3)
            {
                ++par6;
            }

            if (par7 == 4)
            {
                --par4;
            }

            if (par7 == 5)
            {
                ++par4;
            }
        }

        if (par1ItemStack.stackSize == 0)
        {
            return false;
        }
        else if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack))
        {
            return false;
        }
        else if (par5 == 255 && Block.blocksList[ModjamMod.mysteryMushroomBlock.blockID].blockMaterial.isSolid())
        {
            return false;
        }
        else if (par3World.canPlaceEntityOnSide(ModjamMod.mysteryMushroomBlock.blockID, par4, par5, par6, false, par7, par2EntityPlayer, par1ItemStack))
        {
        	
            Block block = Block.blocksList[ModjamMod.mysteryMushroomBlock.blockID];
            int j1 = par1ItemStack.getItemDamage();
            int k1 = Block.blocksList[ModjamMod.mysteryMushroomBlock.blockID].onBlockPlaced(par3World, par4, par5, par6, par7, par8, par9, par10, j1);

            if(placeBlockAt(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10, k1)) {
                par3World.playSoundEffect((double)((float)par4 + 0.5F), (double)((float)par5 + 0.5F), (double)((float)par6 + 0.5F), block.stepSound.getPlaceSound(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
                --par1ItemStack.stackSize;
            }

            return true;
        } else {
            return false;
        }
    }
    
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
       if (!world.setBlock(x, y, z, ModjamMod.mysteryMushroomBlock.blockID, metadata, 3))
       {
           return false;
       }

       if (world.getBlockId(x, y, z) == ModjamMod.mysteryMushroomBlock.blockID)
       {
           Block.blocksList[ModjamMod.mysteryMushroomBlock.blockID].onBlockPlacedBy(world, x, y, z, player, stack);
           Block.blocksList[ModjamMod.mysteryMushroomBlock.blockID].onPostBlockPlaced(world, x, y, z, metadata);
       }

       return true;
    }
}
