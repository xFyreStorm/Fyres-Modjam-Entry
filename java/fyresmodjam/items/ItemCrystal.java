package fyresmodjam.items;

import java.awt.Color;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.ModjamMod;

public class ItemCrystal extends ItemBlock {

	public IIcon texture;

	public static String[] names = new String[] {"Shining", "Void", "Firey"};
	public static String[] particleNames = new String[] {"spell", "portal", "flame"};
	public static Color[] colors = new Color[] {new Color(255, 255, 173), new Color(33, 0, 73), new Color(255, 55, 0)};

	public ItemCrystal() {
		super(ModjamMod.crystal);
		hasSubtypes = true;
		setMaxStackSize(1);
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(Item id, CreativeTabs creativeTab, List list) {
		for(int i = 0; i < names.length; i++) {list.add(new ItemStack(id, 1, i));}
	}

	public String getItemDisplayName(ItemStack itemStack) {
		return names[itemStack.getItemDamage() % names.length] + " Crystal";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		texture = iconRegister.registerIcon("fyresmodjam:crystal_item");
		itemIcon = texture;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack itemStack, int i) {
		return colors[itemStack.getItemDamage() % colors.length].getRGB();
	}

	/*public int getBlockID() {
		return ModjamMod.crystal.blockID;
	}*/ //Why is this even a thing if it isn't called in the numerous places that use block id? :P

	@Override
	@SideOnly(Side.CLIENT)
	public int getSpriteNumber() {
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1) {
		return itemIcon;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {

		//int value = (!par3EntityPlayer.getEntityData().hasKey("equippedCrystal") || par3EntityPlayer.getEntityData().getInteger("equippedCrystal") != par1ItemStack.getItemDamage()) ? par1ItemStack.getItemDamage() : -1;

		/*if(par2World.isRemote) {
			PacketDispatcher.sendPacketToServer(PacketHandler.newPacket(PacketHandler.UPDATE_STAT, new Object[] {"equippedCrystal", "int", par1ItemStack.getItemDamage()}));
		} else {
			PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.UPDATE_STAT, new Object[] {"equippedCrystal", "int", par1ItemStack.getItemDamage()}), (Player) par3EntityPlayer);
		}*/

		/*if(par2World.isRemote) {
			par3EntityPlayer.openGui(ModjamMod.instance, 1, par2World, (int) par3EntityPlayer.posX, (int) par3EntityPlayer.posY, (int) par3EntityPlayer.posZ);
		}*/

		return par1ItemStack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public CreativeTabs getCreativeTab() {return CreativeTabs.tabMaterials;}
}
