package assets.fyresmodjam;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler implements IPacketHandler {

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

}
