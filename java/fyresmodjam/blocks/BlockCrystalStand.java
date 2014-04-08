package fyresmodjam.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.tileentities.TileEntityCrystalStand;

public class BlockCrystalStand extends BlockContainer {

	public BlockCrystalStand() {
		super(Material.rock);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer player, int i) {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityCrystalStand();
	}

	@Override
	public boolean hasTileEntity(int meta) {return true;}

	@Override
	public boolean isOpaqueCube() {return false;}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int i, int i2, int i3, int i4) {
		return false;
	}
}
