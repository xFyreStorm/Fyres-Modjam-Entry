package fyresmodjam.misc;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import fyresmodjam.ModjamMod;

public class BehaviorDispenseTrap extends BehaviorDefaultDispenseItem {

	private final BehaviorDefaultDispenseItem field_96465_b = new BehaviorDefaultDispenseItem();

	@Override
	public ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack) {

		EnumFacing enumfacing = BlockDispenser.func_149937_b(par1IBlockSource.getBlockMetadata());

		World world = par1IBlockSource.getWorld();

		int i = par1IBlockSource.getXInt() + enumfacing.getFrontOffsetX();
		int j = par1IBlockSource.getYInt() + enumfacing.getFrontOffsetY();
		int k = par1IBlockSource.getZInt() + enumfacing.getFrontOffsetZ();
		
		if(!ModjamMod.blockTrap.canPlaceBlockAt(world, i, j, k)){
			return field_96465_b.dispense(par1IBlockSource, par2ItemStack);
		}

		world.setBlock(i, j, k, ModjamMod.blockTrap, par2ItemStack.getItemDamage(), 0);
		world.markBlockForUpdate(i, j, k);

		par2ItemStack.splitStack(1);

		return par2ItemStack;

	}
}
