package fyresmodjam.blocks;

import java.awt.Color;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.ModjamMod;
import fyresmodjam.items.ItemCrystal;
import fyresmodjam.tileentities.TileEntityCrystal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCrystal extends BlockContainer {

	//need to add subtype information
	
	public BlockCrystal(int par1) {
		super(par1, Material.glass);
		this.setBlockBounds(0.25F, 0.1F, 0.25F, 0.75F, 0.9F, 0.75F);
		// TODO set block bounds
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		this.blockIcon = ((ItemCrystal) ModjamMod.crystalItem).texture;
	}

	public boolean canHarvestBlock(EntityPlayer player, int i) {
		return false; //player will get it through activating
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {return new TileEntityCrystal();}

	public boolean hasTileEntity(int meta) {return true;}

	public boolean isOpaqueCube() {return false;}

	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int i, int i2, int i3, int i4) {
		return false;
	}
	
	public int idPicked(World world, int i, int i2, int i3) {
		return ModjamMod.crystalItem.itemID;
	}
	
	public int getDamageValue(World world, int i, int i2, int i3) {
		return world.getBlockMetadata(i, i2, i3);
	}
	
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
		par1World.setBlockMetadataWithNotify(par2, par3, par4, par6ItemStack.getItemDamage(), 0);
	}
	
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return 10;
	}

	/*@SideOnly(Side.CLIENT)
	public void getSubBlocks(int id, CreativeTabs creativeTab, List list) {
		for(int i = 0; i < names.length; i++) {list.add(new ItemStack(id, 1, i));}
	}*/
}
