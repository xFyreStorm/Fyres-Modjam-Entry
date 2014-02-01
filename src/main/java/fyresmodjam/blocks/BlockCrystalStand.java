package fyresmodjam.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.tileentities.TileEntityCrystalStand;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCrystalStand extends BlockContainer
{
    public BlockCrystalStand(int par1)
    {
        super(par1, Material.rock);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
    }

    public boolean canHarvestBlock(EntityPlayer player, int i)
    {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world)
    {
        return new TileEntityCrystalStand();
    }

    public boolean hasTileEntity(int meta) {return true;}

    public boolean isOpaqueCube() {return false;}

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int i, int i2, int i3, int i4)
    {
        return false;
    }

	/*@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister) {
		this.blockIcon = iconRegister.registerIcon("fyresmodjam:crystal_stand_item"); // TODO fix rendering later
	}*/
}
