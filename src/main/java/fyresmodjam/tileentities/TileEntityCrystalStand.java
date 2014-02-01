package fyresmodjam.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

public class TileEntityCrystalStand extends TileEntity
{
    public TileEntityCrystalStand() {}

    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {super.writeToNBT(par1NBTTagCompound);}

    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {super.readFromNBT(par1NBTTagCompound);}

    public Packet getDescriptionPacket()
    {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }

    public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {this.readFromNBT(packet.data);}
}
