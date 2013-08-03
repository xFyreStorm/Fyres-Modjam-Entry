package assets.fyresmodjam;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityTrap extends TileEntity {
	
    public TileEntityTrap() {}

    /*public void updateEntity() {
    	super.updateEntity();
    }*/
    
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
    	super.writeToNBT(par1NBTTagCompound);
    }

    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
    	super.readFromNBT(par1NBTTagCompound);
    }

    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
    }

    public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {this.readFromNBT(packet.customParam1);}
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
    	//fixes rendering bug, but on downside, always renders, even off screen
        return INFINITE_EXTENT_AABB;
    }
    
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 49.0D;
    }
}