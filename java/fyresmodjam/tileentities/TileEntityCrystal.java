package fyresmodjam.tileentities;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import fyresmodjam.items.ItemCrystal;

public class TileEntityCrystal extends TileEntity {
	public TileEntityCrystal() {}

	public static Random random = new Random();

	@Override
	public void updateEntity() {
		super.updateEntity();

		if(random.nextInt(4) == 0) {
			worldObj.spawnParticle(ItemCrystal.particleNames[getBlockMetadata() % ItemCrystal.particleNames.length], xCoord + random.nextFloat(), yCoord + random.nextFloat(), zCoord + random.nextFloat(), 0.0f, 0.0f, 0.0f);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {super.writeToNBT(par1NBTTagCompound);}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {super.readFromNBT(par1NBTTagCompound);}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {readFromNBT(pkt.func_148857_g());}
}
