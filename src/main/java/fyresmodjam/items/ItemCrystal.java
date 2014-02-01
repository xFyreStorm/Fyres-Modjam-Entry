package fyresmodjam.items;

import java.awt.Color;
import java.util.List;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.ModjamMod;
import fyresmodjam.handlers.PacketHandler;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemCrystal extends ItemBlock {

	public Icon texture;
	
	public static String[] names = new String[] {"Shining", "Void", "Firey"};
	public static String[] particleNames = new String[] {"spell", "portal", "flame"};
	public static Color[] colors = new Color[] {new Color(255, 255, 173), new Color(33, 0, 73), new Color(255, 55, 0)};
	
	public ItemCrystal(int par1) {
		super(par1);
		this.hasSubtypes = true;
		this.setMaxStackSize(1);
	}
	
	public void getSubItems(int id, CreativeTabs creativeTab, List list) {
		for(int i = 0; i < names.length; i++) {list.add(new ItemStack(id, 1, i));}
	}
	
	public String getItemDisplayName(ItemStack itemStack) {
		return names[itemStack.getItemDamage() % names.length] + " Crystal";
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		texture = iconRegister.registerIcon("fyresmodjam:crystal_item");
		this.itemIcon = texture;
	}

	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack itemStack, int i) {
		return colors[itemStack.getItemDamage() % colors.length].getRGB();
	}

	/*public int getBlockID() {
		return ModjamMod.crystal.blockID;
	}*/ //Why is this even a thing if it isn't called in the numerous places that use block id? :P

	@SideOnly(Side.CLIENT)
	public int getSpriteNumber() {
		return 1;
	}
	
	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamage(int par1) {
		return this.itemIcon;
	}
	
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {

		int value = (!par3EntityPlayer.getEntityData().hasKey("equippedCrystal") || par3EntityPlayer.getEntityData().getInteger("equippedCrystal") != par1ItemStack.getItemDamage()) ? par1ItemStack.getItemDamage() : -1;
		
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
	
	@SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTab() {return CreativeTabs.tabMaterials;}
	
	//public String getUnlocalizedName() {return super.getUnlocalizedName();}
	//public String getUnlocalizedName(ItemStack stack) {return super.getUnlocalizedName(stack);}
}
