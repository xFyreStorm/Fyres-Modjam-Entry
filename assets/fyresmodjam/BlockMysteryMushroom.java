package assets.fyresmodjam;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;

import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;

public class BlockMysteryMushroom extends BlockFlower
{

	public Icon icon;
	public Icon overlay;
	
    protected BlockMysteryMushroom(int par1) {
        super(par1);
        float f = 0.2F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
        this.setTickRandomly(true);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
    	icon = par1IconRegister.registerIcon("fyresmodjam:mushroomBlock");
    	overlay = par1IconRegister.registerIcon("fyresmodjam:mushroomBlock_overlay");
        this.blockIcon = icon;
    }
    
    @SideOnly(Side.CLIENT)
    public Icon getIcon(int par1, int par2) {
        return renderPass == 0 ? icon : overlay;
    }

    public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
        if (par5Random.nextInt(25) == 0) {
            byte b0 = 4;
            int l = 5;
            int i1;
            int j1;
            int k1;

            for (i1 = par2 - b0; i1 <= par2 + b0; ++i1)
            {
                for (j1 = par4 - b0; j1 <= par4 + b0; ++j1)
                {
                    for (k1 = par3 - 1; k1 <= par3 + 1; ++k1)
                    {
                        if (par1World.getBlockId(i1, k1, j1) == this.blockID)
                        {
                            --l;

                            if (l <= 0)
                            {
                                return;
                            }
                        }
                    }
                }
            }

            i1 = par2 + par5Random.nextInt(3) - 1;
            j1 = par3 + par5Random.nextInt(2) - par5Random.nextInt(2);
            k1 = par4 + par5Random.nextInt(3) - 1;

            for (int l1 = 0; l1 < 4; ++l1)
            {
                if (par1World.isAirBlock(i1, j1, k1) && this.canBlockStay(par1World, i1, j1, k1))
                {
                    par2 = i1;
                    par3 = j1;
                    par4 = k1;
                }

                i1 = par2 + par5Random.nextInt(3) - 1;
                j1 = par3 + par5Random.nextInt(2) - par5Random.nextInt(2);
                k1 = par4 + par5Random.nextInt(3) - 1;
            }

            if (par1World.isAirBlock(i1, j1, k1) && this.canBlockStay(par1World, i1, j1, k1))
            {
                par1World.setBlock(i1, j1, k1, this.blockID, par1World.getBlockMetadata(par2, par3, par4), 2);
            }
        }
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        return super.canPlaceBlockAt(par1World, par2, par3, par4) && this.canBlockStay(par1World, par2, par3, par4);
    }

    /**
     * Gets passed in the blockID of the block below and supposed to return true if its allowed to grow on the type of
     * blockID passed in. Args: blockID
     */
    protected boolean canThisPlantGrowOnThisBlockID(int par1)
    {
        return Block.opaqueCubeLookup[par1];
    }

    /**
     * Can this block stay at this position.  Similar to canPlaceBlockAt except gets checked often with plants.
     */
    public boolean canBlockStay(World par1World, int par2, int par3, int par4)
    {
        if (par3 >= 0 && par3 < 256)
        {
            int l = par1World.getBlockId(par2, par3 - 1, par4);
            Block soil = Block.blocksList[l];
            return /*(l == Block.mycelium.blockID || par1World.getFullBlockLightValue(par2, par3, par4) < 13) &&*/
                   (soil != null && soil.canSustainPlant(par1World, par2, par3 - 1, par4, ForgeDirection.UP, this));
        }
        else
        {
            return false;
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List) {
        for(int i = 0; i < 13; i++) {par3List.add(new ItemStack(par1, 1, i));}
    }
    
    public int damageDropped(int par1) {
        return par1 % 13;
    }

    /*public boolean fertilizeMushroom(World par1World, int par2, int par3, int par4, Random par5Random)
    {
        int l = par1World.getBlockMetadata(par2, par3, par4);
        par1World.setBlockToAir(par2, par3, par4);
        WorldGenBigMushroom worldgenbigmushroom = null;

        if (this.blockID == Block.mushroomBrown.blockID)
        {
            worldgenbigmushroom = new WorldGenBigMushroom(0);
        }
        else if (this.blockID == Block.mushroomRed.blockID)
        {
            worldgenbigmushroom = new WorldGenBigMushroom(1);
        }

        if (worldgenbigmushroom != null && worldgenbigmushroom.generate(par1World, par5Random, par2, par3, par4))
        {
            return true;
        }
        else
        {
            par1World.setBlock(par2, par3, par4, this.blockID, l, 3);
            return false;
        }
    }*/
    
    public static int lastMeta = 0;
    
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int par1) {
        return PacketHandler.mushroomColors[lastMeta][par1];
    }
    
    @SideOnly(Side.CLIENT)
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
    	lastMeta = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
        return PacketHandler.mushroomColors[lastMeta][renderPass];
    }
    
    public static int renderPass;
    public boolean canRenderInPass(int pass) {
    	this.renderPass = pass;
        return true;
    }
    
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
    	return super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
    }
    
    public int idPicked(World par1World, int par2, int par3, int par4) {
        return ModjamMod.mysteryMushroom.itemID;
    }
    
    public int idDropped(int par1, Random par2Random, int par3) {
        return ModjamMod.mysteryMushroom.itemID;
    }
    
    //public int getRenderType() {return ClientProxy.mushroomRendererID;}
}
