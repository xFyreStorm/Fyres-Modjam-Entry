package fyresmodjam.tileentities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.ModjamMod;
import fyresmodjam.handlers.NewPacketHandler;

public class TileEntityTrap extends TileEntity {

	public static String[] settings = {"invisible to and damages all but player", "visible to all and damages all but player", "visible to all and only damages mobs", "decorative"};

	public String placedBy = null;
	public int setting = 0;

	public TileEntityTrap() {}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if(worldObj.isRemote) {spawnParticles();}
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);

		if(placedBy != null) {par1NBTTagCompound.setString("PlacedBy", placedBy);}
		par1NBTTagCompound.setInteger("Setting", setting % settings.length);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);

		if(par1NBTTagCompound.hasKey("PlacedBy")) {placedBy = par1NBTTagCompound.getString("PlacedBy");} else {placedBy = null;}
		if(par1NBTTagCompound.hasKey("Setting")) {setting = par1NBTTagCompound.getInteger("Setting") % settings.length;}
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {readFromNBT(pkt.func_148857_g());}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		return (player != null && (player.getCommandSenderName().equals(placedBy) || setting != 0)) ? 4096.0F: 36.0F;
	}

	@SideOnly(Side.CLIENT)
	public void spawnParticles() {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		int type = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

		if(player != null && (!NewPacketHandler.trapsDisabled || placedBy != null) && (player.getCommandSenderName().equals(placedBy) || player.isSneaking() || setting != 0 || (player.getEntityData().hasKey("Blessing") && player.getEntityData().getString("Blessing").equals("Scout"))) && getDistanceFrom(TileEntityRendererDispatcher.staticPlayerX, TileEntityRendererDispatcher.staticPlayerY, TileEntityRendererDispatcher.staticPlayerZ) < (player.getCommandSenderName().equals(placedBy) ? 4096 : 36.0F)) {
			if(type == 1) {
				if(ModjamMod.r.nextInt(5) == 0) {worldObj.spawnParticle("smoke", xCoord + 0.5F, yCoord + 0.175F, zCoord + 0.5F, (ModjamMod.r.nextFloat() - 0.5F)/16, ModjamMod.r.nextFloat()/16, (ModjamMod.r.nextFloat() - 0.5F)/16);}
				worldObj.spawnParticle("flame", xCoord + 0.5F, yCoord + 0.175F, zCoord + 0.5F, 0.0F, 0.0F, 0.0F);
			} else if(type == 2) {
				for(int i = 0; i < 3; i++) {worldObj.spawnParticle("smoke", xCoord + 0.5F, yCoord + 0.175F, zCoord + 0.5F, (ModjamMod.r.nextFloat() - 0.5F)/16, ModjamMod.r.nextFloat()/16, (ModjamMod.r.nextFloat() - 0.5F)/16);}
			}
		}
	}
}