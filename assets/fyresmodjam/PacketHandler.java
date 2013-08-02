package assets.fyresmodjam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler implements IPacketHandler {

	//Packet types
	public static final int UPDATE_BlESSING = 1;
	
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player playerEntity) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();

		DataInputStream inputStream = null;
		byte type = 0;

		try {
			inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
			type = inputStream.readByte();
		} catch (Exception e) {e.printStackTrace();}

		if(inputStream != null) {
			if (side == Side.SERVER) {
				EntityPlayerMP player = (EntityPlayerMP) playerEntity;
			} else if (side == Side.CLIENT) {
				EntityPlayer player = (EntityPlayer) playerEntity;
			}
		}
	}
	
	public static Packet250CustomPayload newPacket(byte type, Object[] data) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);

        try {
            outputStream.writeByte(type);

            if(data != null) {
                for(int i = 0; i < data.length; i++) {
                	if(data[i] instanceof Integer) {outputStream.writeInt((Integer) data[i]);}
                	else if(data[i] instanceof Boolean) {outputStream.writeBoolean((Boolean) data[i]);}
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

    public static Packet250CustomPayload newPacket(byte type, Object data) {return newPacket(type, new Object[] {data}); }

    public static Packet250CustomPayload newPacket(byte type) {return newPacket(type, null);}

}
