package fyresmodjam;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import fyresmodjam.entities.EntityMysteryPotion;
import fyresmodjam.entities.renderers.RenderMysteryPotion;
import fyresmodjam.handlers.ClientTickHandler;
import fyresmodjam.handlers.FyresKeyHandler;
import fyresmodjam.handlers.NewPacketHandler;
import fyresmodjam.misc.EntityStatHelper;
import fyresmodjam.tileentities.TileEntityCrystal;
import fyresmodjam.tileentities.TileEntityCrystalStand;
import fyresmodjam.tileentities.TileEntityPillar;
import fyresmodjam.tileentities.TileEntityTrap;
import fyresmodjam.tileentities.renderers.TileEntityCrystalRenderer;
import fyresmodjam.tileentities.renderers.TileEntityCrystalStandRenderer;
import fyresmodjam.tileentities.renderers.TileEntityPillarRenderer;
import fyresmodjam.tileentities.renderers.TileEntityTrapRenderer;

public class ClientProxy extends CommonProxy {

	@SubscribeEvent
	public void guiRenderEvent(RenderGameOverlayEvent.Post event) {
		if(event.type == RenderGameOverlayEvent.ElementType.HOTBAR) {
			MovingObjectPosition mouse = Minecraft.getMinecraft().objectMouseOver;

			World world = Minecraft.getMinecraft().theWorld;

			if(mouse != null && world != null && mouse.typeOfHit == MovingObjectType.BLOCK) {
				TileEntity te = world.getTileEntity(mouse.blockX, mouse.blockY, mouse.blockZ);
				Block id = world.getBlock(mouse.blockX, mouse.blockY, mouse.blockZ);

				if(id == ModjamMod.blockPillar || (id == ModjamMod.blockTrap && te != null && te instanceof TileEntityTrap && ((TileEntityTrap) te).placedBy != null)) {
					String key = Keyboard.getKeyName(FyresKeyHandler.examine.getKeyCode());
					String string = "Press " + key + " to Examine";

					if(te != null && Minecraft.getMinecraft().thePlayer != null && te instanceof TileEntityTrap && ((TileEntityTrap) te).placedBy.equals(Minecraft.getMinecraft().thePlayer.getCommandSenderName())) {
						string = Minecraft.getMinecraft().thePlayer.isSneaking() ? "Use to disarm (Stand to toggle setting)" : "Use to toggle setting (Sneak to disarm)";
					}

					Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(string, (event.resolution.getScaledWidth() / 2) - (Minecraft.getMinecraft().fontRenderer.getStringWidth(string) / 2), event.resolution.getScaledHeight() / 2 + 16, Color.WHITE.getRGB());
				}
			}

			if(Minecraft.getMinecraft().thePlayer != null) {
				EntityPlayer player = Minecraft.getMinecraft().thePlayer;
				String blessing = EntityStatHelper.getStat(player, "Blessing");

				if(blessing != null) {
					if(blessing.equals("Berserker")) {
						if(!EntityStatHelper.hasStat(player, "BlessingCounter")) {EntityStatHelper.giveStat(player, "BlessingCounter", 0);}
						String string = EntityStatHelper.getStat(player, "BlessingCounter");
						Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(string, (event.resolution.getScaledWidth() / 2) - (Minecraft.getMinecraft().fontRenderer.getStringWidth(string) / 2), event.resolution.getScaledHeight() - 48 + (player.capabilities.isCreativeMode ? 16 : 0), Color.RED.getRGB());
					}
				}
			}
		}
	}

	@Override
	public void register() {
		ClientTickHandler clientHandler = new ClientTickHandler();
		FMLCommonHandler.instance().bus().register(clientHandler);

		FMLCommonHandler.instance().bus().register(new FyresKeyHandler());

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPillar.class, new TileEntityPillarRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTrap.class, new TileEntityTrapRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrystal.class, new TileEntityCrystalRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrystalStand.class, new TileEntityCrystalStandRenderer());

		RenderingRegistry.registerEntityRenderingHandler(EntityMysteryPotion.class, new RenderMysteryPotion(ModjamMod.mysteryPotion));

		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void sendPlayerMessage(String message) {
		//Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(message));
		
		NewPacketHandler.SEND_MESSAGE.data = new Object[] {message};
		NewPacketHandler.SEND_MESSAGE.executeClient(Minecraft.getMinecraft().thePlayer);
	}

	@Override
	public void loadFromConfig(Configuration config) {
		super.loadFromConfig(config);

		ModjamMod.examineKey = config.get("Keybindings", "examine_key", ModjamMod.examineKey).getInt(ModjamMod.examineKey);
		ModjamMod.blessingKey = config.get("Keybindings", "blessing_key", ModjamMod.blessingKey).getInt(ModjamMod.blessingKey);

		FyresKeyHandler.examine.setKeyCode(ModjamMod.examineKey);
		FyresKeyHandler.activateBlessing.setKeyCode(ModjamMod.blessingKey);
	}
}
