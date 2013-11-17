package fyresmodjam.misc;

import cpw.mods.fml.common.network.PacketDispatcher;
import fyresmodjam.ModjamMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockRailBase;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.item.ItemMinecart;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorDispenseTrap extends BehaviorDefaultDispenseItem {

	private final BehaviorDefaultDispenseItem field_96465_b = new BehaviorDefaultDispenseItem();

	public ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack) {
		
		EnumFacing enumfacing = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
		
		World world = par1IBlockSource.getWorld();
		
		double d0 = par1IBlockSource.getX() + (double)((float)enumfacing.getFrontOffsetX() * 1.125F);
		double d1 = par1IBlockSource.getY() + (double)((float)enumfacing.getFrontOffsetY() * 1.125F);
		double d2 = par1IBlockSource.getZ() + (double)((float)enumfacing.getFrontOffsetZ() * 1.125F);
		
		int i = par1IBlockSource.getXInt() + enumfacing.getFrontOffsetX();
		int j = par1IBlockSource.getYInt() + enumfacing.getFrontOffsetY();
		int k = par1IBlockSource.getZInt() + enumfacing.getFrontOffsetZ();
		
		int l = world.getBlockId(i, j, k);

		if(!Block.blocksList[ModjamMod.blockTrap.blockID].canPlaceBlockAt(world, i, j, k)){
			return this.field_96465_b.dispense(par1IBlockSource, par2ItemStack);
		}

		world.setBlock(i, j, k, ModjamMod.blockTrap.blockID, par2ItemStack.getItemDamage(), 0);
		world.markBlockForUpdate(i, j, k);
		
		//TileEntity te = world.getBlockTileEntity(i, j, k);
		//if(te != null && !world.isRemote) {PacketDispatcher.sendPacketToAllPlayers(te.getDescriptionPacket());}
		
		par2ItemStack.splitStack(1);
		
		return par2ItemStack;
	
	}
}
