package fyresmodjam.tileentities;

import fyresmodjam.items.ItemCrystal;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

import java.util.Random;

public class TileEntityCrystal extends TileEntity
{
    public TileEntityCrystal() {}

    public static Random random = new Random();

    public void updateEntity()
    {
        super.updateEntity();

        if (random.nextInt(4) == 0)
        {
            this.worldObj.spawnParticle(ItemCrystal.particleNames[this.getBlockMetadata() % ItemCrystal.particleNames.length], this.xCoord + random.nextFloat(), this.yCoord + random.nextFloat(), this.zCoord + random.nextFloat(), 0.0f, 0.0f, 0.0f);
        }
    }

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
