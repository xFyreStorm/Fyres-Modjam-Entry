package assets.fyresmodjam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler implements IPacketHandler {

	//Packet types
	public static final byte UPDATE_BLESSING = 1, PLAY_SOUND = 2, UPDATE_POTION_KNOWLEDGE = 3, SEND_MESSAGE = 4, UPDATE_POTION_DATA = 5;
	
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
						
						default: return;
					}
				} else if (side == Side.CLIENT) {
					EntityPlayer player = (EntityPlayer) playerEntity;
					
					if(MysteryPotionData.potionValues == null) {MysteryPotionData.potionValues = new int[12];}
					if(MysteryPotionData.potionDurations == null) {MysteryPotionData.potionDurations = new int[12];}
					
					switch(type) {
						case UPDATE_BLESSING: player.getEntityData().setString("Blessing", inputStream.readUTF()); return;
						case UPDATE_POTION_KNOWLEDGE: int[] potionKnowledge = new int[12]; for(int i = 0; i < 12; i++) {potionKnowledge[i] = inputStream.readInt();} player.getEntityData().setIntArray("PotionKnowledge", potionKnowledge); return;
						case SEND_MESSAGE: Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(inputStream.readUTF()); return;
						case UPDATE_POTION_DATA: for(int i = 0; i < 12; i++) {MysteryPotionData.potionValues[i] = inputStream.readInt();} for(int i = 0; i < 12; i++) {MysteryPotionData.potionDurations[i] = inputStream.readInt();} return;
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
