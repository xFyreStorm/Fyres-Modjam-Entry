package assets.fyresmodjam;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTrap extends BlockContainer
{
    protected BlockTrap(int par1) {
        super(par1, Material.iron);
        this.setLightOpacity(0);
        this.setCreativeTab(CreativeTabs.tabCombat);
        //this.setTickRandomly(true);
        //this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
    }

    /*@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon("fyresmodjam:trap");
    }*/

    public int idDropped(int par1, Random par2Random, int par3) {
        return 0;
    }

    public int idPicked(World par1World, int par2, int par3, int par4) {
        return ModjamMod.blockTrap.blockID;
    }

    public boolean canHarvestBlock(EntityPlayer par1EntityPlayer, int par2) {
        return false;
    }
    
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)  {	
    	return true;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityTrap();
    }

    public int getMobilityFlag() {
        return 2;
    }
    
    /*public boolean isCollidable() {
        return false;
    }*/
}
