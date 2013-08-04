package assets.fyresmodjam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler implements IPacketHandler {

	//Packet types
	public static final byte UPDATE_BLESSING = 1, PLAY_SOUND = 2, UPDATE_POTION_KNOWLEDGE = 3, SEND_MESSAGE = 4, UPDATE_WORLD_DATA = 5, UPDATE_PLAYER_ITEMS = 6, DISARM_TRAP = 7;
	
	public static int[] potionValues = null;
	public static int[] potionDurations = null;
	
	public static String currentDisadvantage = null;
	
	public static String currentTask = null;
	public static int currentTaskID = -1;
	public static int currentTaskAmount = 0;
	public static int progress = 0;
	public static int tasksCompleted = 0;
	
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player playerEntity) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();

		DataInputStream inputStream = null;
		byte type = 0;

		try {
			
			inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
			
			if(inputStream != null) {
				type = inputStream.readByte();
				
				if (side == Side.SERVER) {
					EntityPlayerMP player = (EntityPlayerMP) playerEntity;
					
					switch(type) {
						case PLAY_SOUND: 
							
							String sound = inputStream.readUTF();
							
							int x = inputStream.readInt();
							int y = inputStream.readInt();
							int z = inputStream.readInt();
							
							Minecraft.getMinecraft().theWorld.playSound(x, y, z, "fyresmodjam:" + sound, 1.0F, 1.0F, false);
							return;
							
						case UPDATE_PLAYER_ITEMS:
							
							//if(player.openContainer != null) {
								for(Object stack : player.inventory.mainInventory) {
									if(stack == null || !(stack instanceof ItemStack)) {continue;}
									ItemStatHelper.processItemStack((ItemStack) stack, ModjamMod.r);
								}
								
								//player.openContainer.detectAndSendChanges();
								//((EntityPlayerMP) player).sendContainerAndContentsToPlayer(player.openContainer, player.openContainer.getInventory());
							//}
							
							return;
							
						case DISARM_TRAP:
							int blockX = inputStream.readInt();
							int blockY = inputStream.readInt();
							int blockZ = inputStream.readInt();
							
							boolean mechanic = inputStream.readBoolean();
							
							if(mechanic ? ModjamMod.r.nextInt(4) != 0 : ModjamMod.r.nextInt(4) == 0) {
								boolean salvage = ModjamMod.r.nextBoolean() && mechanic;
								PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7e\u00A7o" + (!salvage ? "You disarmed the trap." : "You disarm and salvage the trap.")}), (Player) player);
								if(salvage) {player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, blockX + 0.5F, blockY, blockZ + 0.5F, new ItemStack(ModjamMod.itemTrap.itemID, 1, player.worldObj.getBlockMetadata(blockX, blockY, blockZ) % BlockTrap.trapTypes)));}
								player.worldObj.setBlockToAir(blockX, blockY, blockZ);
							} else {
								player.attackEntityFrom(DamageSource.cactus, 1.0F);
								PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7c\u00A7oYou failed to disarm the trap."}), (Player) player);
								if(CommonTickHandler.worldData.currentDisadvantage.equals("Explosive Traps")) {player.worldObj.setBlockToAir(blockX, blockY, blockZ); player.worldObj.createExplosion(null, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, 1.33F, true);}
							}
							
							return;
						
						default: return;
					}
				} else if (side == Side.CLIENT) {
					EntityPlayer player = (EntityPlayer) playerEntity;
					
					if(potionValues == null) {potionValues = new int[12];}
					if(potionDurations == null) {potionDurations = new int[12];}
					
					switch(type) {
						case UPDATE_BLESSING: player.getEntityData().setString("Blessing", inputStream.readUTF()); return;
						case UPDATE_POTION_KNOWLEDGE: int[] potionKnowledge = new int[12]; for(int i = 0; i < 12; i++) {potionKnowledge[i] = inputStream.readInt();} player.getEntityData().setIntArray("PotionKnowledge", potionKnowledge); return;
						case SEND_MESSAGE: Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(inputStream.readUTF()); return;
						case UPDATE_WORLD_DATA: for(int i = 0; i < 12; i++) {potionValues[i] = inputStream.readInt();} for(int i = 0; i < 12; i++) {potionDurations[i] = inputStream.readInt();} currentDisadvantage = inputStream.readUTF(); currentTask = inputStream.readUTF(); currentTaskID = inputStream.readInt(); currentTaskAmount = inputStream.readInt(); progress = inputStream.readInt(); tasksCompleted = inputStream.readInt(); return;
						default: return;
					}
				}
			}
			
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public static Packet250CustomPayload newPacket(byte type, Object[] data) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);

        try {
            outputStream.writeByte(type);

            if(data != null) {
                for(int i = 0; i < data.length; i++) {
                	if(data[i] instanceof Integer) {outputStream.writeInt((Integer) data[i]);}
                	else if(data[i] instanceof int[]) {
                		for(int i2 = 0; i2 < ((int[]) data[i]).length; i2++) {outputStream.writeInt(((int[]) data[i])[i2]);}
                	} else if(data[i] instanceof Boolean) {outputStream.writeBoolean((Boolean) data[i]);}
                	else if(data[i] instanceof String) {outputStream.writeUTF((String) data[i]);}
                	else if(data[i] instanceof Byte) {outputStream.writeByte((Byte) data[i]);}
                	else if(data[i] instanceof Float) {outputStream.writeDouble((Double) data[i]);}
                	else if(data[i] instanceof Double) {outputStream.writeFloat((Float) data[i]);}
                	else if(data[i] instanceof Character) {outputStream.writeChar((Character) data[i]);}
                }
            }
        } catch (Exception ex) {ex.printStackTrace();}

        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "FyresModJamMod";
        packet.data = bos.toByteArray();
        packet.length = bos.size();
        return packet;
    }

    public static Packet250CustomPayload newPacket(byte type) {return newPacket(type, null);}

}
