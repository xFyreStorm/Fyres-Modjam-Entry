package assets.fyresmodjam;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Icon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemMysteryPotion extends Item {

	public static Icon[] icons = null;
	
	public ItemMysteryPotion(int par1) {
		super(par1);
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
		if(icons == null) {
			icons = new Icon[12];
			for(int i = 0; i < 12; i++) {icons[i] = par1IconRegister.registerIcon("fyresmodjam:mysteryPotion_" + (i + 1));}
		}
		
        this.itemIcon = icons[0];
    }
	
	@SideOnly(Side.CLIENT)
    public Icon getIconFromDamage(int par1)  {
        return par1 < 12 ? icons[par1] : icons[0];
    }
	
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List) {
        for(int i = 0; i < 12; i++) {par3List.add(new ItemStack(par1, 1, i));}
    }
	
	public String getItemDisplayName(ItemStack par1ItemStack) {
        return "Mystery Potion #" + (par1ItemStack.getItemDamage() + 1);
    }
	
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.drink;
    }
	
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        par3EntityPlayer.setItemInUse(par1ItemStack, this.getMaxItemUseDuration(par1ItemStack));
        return par1ItemStack;
    }
	
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
        return 32;
    }
	
	public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        if(!par3EntityPlayer.capabilities.isCreativeMode) {--par1ItemStack.stackSize;}
        if(!par2World.isRemote) {par3EntityPlayer.addPotionEffect(new PotionEffect(UnmarkedPotionData.potionValues[par1ItemStack.getItemDamage()], 100, 1, true));}
        return par1ItemStack;
    }
	
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
        return false;
    }

}
