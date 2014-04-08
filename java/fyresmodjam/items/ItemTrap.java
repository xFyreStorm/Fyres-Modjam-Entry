package fyresmodjam.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.ModjamMod;
import fyresmodjam.blocks.BlockTrap;
import fyresmodjam.misc.BehaviorDispenseTrap;

public class ItemTrap extends Item {

	private static final IBehaviorDispenseItem dispenseTrap = new BehaviorDispenseTrap();

	public IIcon[] icons;
	public static String[] iconLocations = new String[] {"fyresmodjam:itemTrap", "fyresmodjam:trap2", "fyresmodjam:trap3"};

	public ItemTrap() {
		super();
		setHasSubtypes(true);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, dispenseTrap);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		icons = new IIcon[iconLocations.length];
		for(int i = 0; i < iconLocations.length; i++) {icons[i] = par1IconRegister.registerIcon(iconLocations[i]);}

		itemIcon = icons[0];
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for(int i = 0; i < BlockTrap.trapTypes; i++) {par3List.add(new ItemStack(par1, 1, i));}
	}

	public static String[] names = {"Spike Trap", "Flame Trap", "Smoke Trap"};

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack par1ItemStack) {
		return names[par1ItemStack.getItemDamage() % BlockTrap.trapTypes];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1) {
		return icons[par1 % BlockTrap.trapTypes];
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
		Block i1 = par3World.getBlock(par4, par5, par6);

		if(i1 == Blocks.snow && (par3World.getBlockMetadata(par4, par5, par6) & 7) < 1) {
			par7 = 1;
		} else if(i1 != Blocks.vine && i1 != Blocks.tallgrass && i1 != Blocks.deadbush && (i1 == null || !i1.isReplaceable(par3World, par4, par5, par6))) {
			if(par7 == 0) {--par5;}
			if(par7 == 1) {++par5;}
			if(par7 == 2) {--par6;}
			if(par7 == 3) {++par6;}
			if(par7 == 4) {--par4;}
			if(par7 == 5) {++par4;}
		}

		if(par1ItemStack.stackSize == 0) {
			return false;
		} else if(!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack)) {
			return false;
		} else if(par5 == 255 && ModjamMod.blockTrap.getMaterial().isSolid()) {
			return false;
		} else if(par3World.canPlaceEntityOnSide(ModjamMod.blockTrap, par4, par5, par6, false, par7, par2EntityPlayer, par1ItemStack)) {
			Block block = ModjamMod.blockTrap;
			int j1 = getMetadata(par1ItemStack.getItemDamage());
			int k1 = ModjamMod.blockTrap.onBlockPlaced(par3World, par4, par5, par6, par7, par8, par9, par10, j1);

			if(placeBlockAt(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10, k1)) {
				par3World.playSoundEffect(par4 + 0.5F, par5 + 0.5F, par6 + 0.5F, block.stepSound.func_150496_b(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
				--par1ItemStack.stackSize;
			}

			return true;
		} else {return false;}
	}

	@SideOnly(Side.CLIENT)
	public boolean canPlaceItemBlockOnSide(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer, ItemStack par7ItemStack) {
		Block i1 = par1World.getBlock(par2, par3, par4);

		if(i1 == Blocks.snow) {
			par5 = 1;
		} else if(i1 != Blocks.vine && i1 != Blocks.tallgrass && i1 != Blocks.deadbush && (i1 == null || !i1.isReplaceable(par1World, par2, par3, par4))) {
			if(par5 == 0) {--par3;}
			if(par5 == 1) {++par3;}
			if(par5 == 2) {--par4;}
			if(par5 == 3) {++par4;}
			if(par5 == 4) {--par2;}
			if(par5 == 5) {++par2;}
		}

		return par1World.canPlaceEntityOnSide(ModjamMod.blockTrap, par2, par3, par4, false, par5, (Entity)null, par7ItemStack);
	}

	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		if(!world.setBlock(x, y, z, ModjamMod.blockTrap, metadata, 3)) {return false;}

		if(world.getBlock(x, y, z) == ModjamMod.blockTrap) {
			ModjamMod.blockTrap.onBlockPlacedBy(world, x, y, z, player, stack);
			ModjamMod.blockTrap.onPostBlockPlaced(world, x, y, z, metadata);
		}

		return true;
	}
}
