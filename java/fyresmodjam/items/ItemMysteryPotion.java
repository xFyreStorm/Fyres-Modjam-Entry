package fyresmodjam.items;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.ModjamMod;
import fyresmodjam.entities.EntityMysteryPotion;
import fyresmodjam.handlers.CommonTickHandler;
import fyresmodjam.handlers.NewPacketHandler;

public class ItemMysteryPotion extends Item {

	public IIcon[] icons = null;

	public ItemMysteryPotion() {
		super();
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		icons = new IIcon[26];

		for(int i = 0; i < 13; i++) {
			icons[i] = par1IconRegister.registerIcon("fyresmodjam:mysteryPotion_" + (i + 1));
			icons[i + 13] = par1IconRegister.registerIcon("fyresmodjam:mysteryPotionThrowable_" + (i + 1));
		}

		itemIcon = icons[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1)  {
		return icons[par1 % 26];
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for(int i = 0; i < 13; i++) {par3List.add(new ItemStack(par1, 1, i));}
		for(int i = 0; i < 13; i++) {par3List.add(new ItemStack(par1, 1, i + 13));}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getItemStackDisplayName(ItemStack par1ItemStack) {
		int damage = par1ItemStack.getItemDamage() % 13;
		String name = "Mystery Potion #" + (damage + 1);

		String blessing = null;

		if(FMLCommonHandler.instance().getSide() == Side.CLIENT) {blessing = getBlessing();}

		if(damage < 12) {
			if(Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().thePlayer.getEntityData().hasKey("PotionKnowledge")) {
				if(Minecraft.getMinecraft().thePlayer.getEntityData().getIntArray("PotionKnowledge")[damage] != -1) {
					Potion potion = Potion.potionTypes[NewPacketHandler.potionValues[damage]];
					name = StatCollector.translateToLocal(potion.getName()) + " Potion";

					if(!potion.isInstant()) {
						int time = NewPacketHandler.potionDurations[damage];
						name += " (" + time + " seconds)";
					}
				}
			}
		} else if(damage >= 12) {
			name = "Wildcard Potion";
		}

		if((blessing != null && blessing.equals("Alchemist"))) {name = "\u00A7k" + name;}

		return name;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack par1ItemStack) {
		return par1ItemStack.getItemDamage() < 13 ? EnumAction.drink : super.getItemUseAction(par1ItemStack);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		if(par1ItemStack.getItemDamage() < 13) {
			par3EntityPlayer.setItemInUse(par1ItemStack, getMaxItemUseDuration(par1ItemStack));
		} else {
			if(!par3EntityPlayer.capabilities.isCreativeMode) {par1ItemStack.stackSize--;}
			par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

			if(!par2World.isRemote) {
				int value = 0;

				if(par1ItemStack.getItemDamage() % 13 >= 12) {
					value = ModjamMod.r.nextInt(Potion.potionTypes.length);
					while(Potion.potionTypes[value] == null) {value = ModjamMod.r.nextInt(Potion.potionTypes.length);}
				} else {
					value = CommonTickHandler.worldData.potionValues[par1ItemStack.getItemDamage() % 13];
				}

				par2World.spawnEntityInWorld(new EntityMysteryPotion(par2World, par3EntityPlayer, par1ItemStack));
			}
		}

		return par1ItemStack;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return par1ItemStack.getItemDamage() < 13 ? 32 : super.getMaxItemUseDuration(par1ItemStack);
	}

	@Override
	public ItemStack onEaten(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
		if(!par3EntityPlayer.capabilities.isCreativeMode) {--par1ItemStack.stackSize;}

		String blessing = (par3EntityPlayer != null && par3EntityPlayer.getEntityData().hasKey("Blessing")) ? par3EntityPlayer.getEntityData().getString("Blessing") : null;

		int damage = par1ItemStack.getItemDamage() % 13;

		if((blessing == null || !blessing.equals("Alchemist")) && damage < 12) {
			if(!par2World.isRemote) {

				int value = CommonTickHandler.worldData.potionValues[damage];

				if(!Potion.potionTypes[value].isInstant()) {
					par3EntityPlayer.addPotionEffect(new PotionEffect(value, CommonTickHandler.worldData.potionDurations[damage] * 20, 1, false));
				} else {
					Potion.potionTypes[value].affectEntity(par3EntityPlayer, par3EntityPlayer, 1, 1);
				}

				if(!par3EntityPlayer.getEntityData().hasKey("PotionKnowledge")) {par3EntityPlayer.getEntityData().setIntArray("PotionKnowledge", new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});}

				if(par3EntityPlayer.getEntityData().getIntArray("PotionKnowledge")[damage] == -1) {
					par3EntityPlayer.getEntityData().getIntArray("PotionKnowledge")[damage] = 1;

					NewPacketHandler.UPDATE_POTION_KNOWLEDGE.sendToPlayer(par3EntityPlayer, par3EntityPlayer.getEntityData().getIntArray("PotionKnowledge"));

					Potion potion = Potion.potionTypes[CommonTickHandler.worldData.potionValues[damage]];
					String name = StatCollector.translateToLocal(potion.getName()) + " Potion";

					if(!potion.isInstant()) {
						int time = CommonTickHandler.worldData.potionDurations[damage];
						name += " (" + time + " seconds)";
					}

					NewPacketHandler.SEND_MESSAGE.sendToPlayer(par3EntityPlayer, "\u00A7oYou learnt Mystery Potion #" + (damage + 1) + " was a " + name + "!");
				}
			}

		} else if(!par2World.isRemote) {
			int value = ModjamMod.r.nextInt(Potion.potionTypes.length);
			while(Potion.potionTypes[value] == null) {value = ModjamMod.r.nextInt(Potion.potionTypes.length);}
			int time = 5 + ModjamMod.r.nextInt(26);

			if(!Potion.potionTypes[value].isInstant()) {
				par3EntityPlayer.addPotionEffect(new PotionEffect(value, time * 20, 1, false));
			} else {
				Potion.potionTypes[value].affectEntity(par3EntityPlayer, par3EntityPlayer, 1, 1);
			}

			Potion potion = Potion.potionTypes[value];
			String name = StatCollector.translateToLocal(potion.getName()) + " Potion";

			if(!potion.isInstant()) {name += " (" + time + " seconds)";}

			NewPacketHandler.SEND_MESSAGE.sendToPlayer(par3EntityPlayer, "\u00A7oYou drank a " + name + ".");
		}

		return par1ItemStack;
	}

	@SideOnly(Side.CLIENT)
	public String getBlessing() {
		return (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().thePlayer.getEntityData().hasKey("Blessing")) ? Minecraft.getMinecraft().thePlayer.getEntityData().getString("Blessing") : null;
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
		return false;
	}

}
