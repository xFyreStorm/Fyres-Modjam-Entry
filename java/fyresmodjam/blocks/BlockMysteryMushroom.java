package fyresmodjam.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.ModjamMod;
import fyresmodjam.handlers.NewPacketHandler;

public class BlockMysteryMushroom extends BlockFlower {

	public IIcon icon;
	public IIcon overlay;

	public BlockMysteryMushroom() {
		super(0);
		float f = 0.2F;
		setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
		setTickRandomly(true);
		setCreativeTab(null);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		icon = par1IconRegister.registerIcon("fyresmodjam:mushroomBlock");
		overlay = par1IconRegister.registerIcon("fyresmodjam:mushroomBlock_overlay");
		blockIcon = icon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2) {
		return renderPass == 0 ? icon : overlay;
	}

	@Override
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
						if (par1World.getBlock(i1, k1, j1) == this)
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

			for (int l1 = 0; l1 < 4; ++l1) {
				if (par1World.isAirBlock(i1, j1, k1) && canBlockStay(par1World, i1, j1, k1)) {
					par2 = i1;
					par3 = j1;
					par4 = k1;
				}

				i1 = par2 + par5Random.nextInt(3) - 1;
				j1 = par3 + par5Random.nextInt(2) - par5Random.nextInt(2);
				k1 = par4 + par5Random.nextInt(3) - 1;
			}

			if (par1World.isAirBlock(i1, j1, k1) && canBlockStay(par1World, i1, j1, k1))
			{
				par1World.setBlock(i1, j1, k1, this, par1World.getBlockMetadata(par2, par3, par4), 2);
			}
		}
	}

	 @Override
	 public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
		return super.canPlaceBlockAt(par1World, par2, par3, par4) && canBlockStay(par1World, par2, par3, par4);
	 }

	 @Override
	 public boolean canBlockStay(World par1World, int par2, int par3, int par4) {
		 if (par3 >= 0 && par3 < 256) {
			 Block soil = par1World.getBlock(par2, par3 - 1, par4);
			 return (soil != null && soil.canSustainPlant(par1World, par2, par3 - 1, par4, ForgeDirection.UP, this));
		 } else {
			 return false;
		 }
	 }

	 @Override
	 @SuppressWarnings({ "unchecked", "rawtypes" })
	 @SideOnly(Side.CLIENT)
	 public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		 for(int i = 0; i < 13; i++) {par3List.add(new ItemStack(par1, 1, i));}
	 }

	 @Override
	 public int damageDropped(int par1) {
		 return par1 % 13;
	 }

	 @Override
	 @SideOnly(Side.CLIENT)
	 public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
		 return NewPacketHandler.mushroomColors[par1IBlockAccess.getBlockMetadata(par2, par3, par4)][renderPass];
	 }

	 public static int renderPass;
	 @Override
	 public boolean canRenderInPass(int pass) {
		 renderPass = pass;
		 return true;
	 }

	 @Override
	 public ItemStack getPickBlock(MovingObjectPosition object, World par1World, int par2, int par3, int par4) {
		 return new ItemStack(ModjamMod.mysteryMushroom);
	 }

	 @Override
	 public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
	 {
		 return ModjamMod.mysteryMushroom;
	 }

	 @Override
	 public int getRenderBlockPass() {
		 return 1;
	 }
}
