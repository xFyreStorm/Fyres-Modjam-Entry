package fyresmodjam.handlers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;


import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import fyresmodjam.EntityStatHelper;
import fyresmodjam.ItemStatHelper;
import fyresmodjam.ModjamMod;
import fyresmodjam.blocks.BlockTrap;
import fyresmodjam.tileentities.TileEntityTrap;

public class PacketHandler implements IPacketHandler {

	//Packet types
	public static final byte UPDATE_BLESSING = 1, PLAY_SOUND = 2, UPDATE_POTION_KNOWLEDGE = 3, SEND_MESSAGE = 4, UPDATE_WORLD_DATA = 5, UPDATE_PLAYER_ITEMS = 6, DISARM_TRAP = 7, EXAMINE_MOB = 8, LEVEL_UP = 9, ACTIVATE_BLESSING = 10, UPDATE_STATS = 11;
	
	public static int[] potionValues = null;
	public static int[] potionDurations = null;
	
	public static int[][] mushroomColors = null;
	
	public static String currentDisadvantage = null;
	
	public static String currentTask = null;
	public static int currentTaskID = -1;
	public static int currentTaskAmount = 0;
	public static int progress = 0;
	public static int tasksCompleted = 0;
	public static int rewardLevels = 0;
	
	public static boolean enderDragonKilled = false;
	public static boolean trapsDisabled = false;
	
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
							
						case ACTIVATE_BLESSING:
							
							String blessing = EntityStatHelper.getStat(player, "Blessing");
							boolean blessingActive = EntityStatHelper.hasStat(player, "BlessingActive") ? Boolean.parseBoolean(EntityStatHelper.getStat(player, "BlessingActive")) : false;
							
							if(!EntityStatHelper.hasStat(player, "BlessingCooldown")) {EntityStatHelper.giveStat(player, "BlessingCooldown", 0);}
							
							if(EntityStatHelper.getStat(player, "BlessingCooldown").equals("0") || Integer.parseInt(EntityStatHelper.getStat(player, "BlessingCooldown")) <= player.worldObj.getWorldTime()) {
								if(!blessingActive) {
									if(blessing != null) {
										if(blessing.equals("Berserker")) {
											if(EntityStatHelper.hasStat(player, "BlessingCounter") && Integer.parseInt(EntityStatHelper.getStat(player, "BlessingCounter")) > 0) {
												blessingActive =  true;
												PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7cYou enter berserk mode."}), (Player) player);
											} else {
												PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7cYou have no berserk counters."}), (Player) player);
											}
										} else if(blessing.equals("Mechanic")) {
											x = inputStream.readInt();
											y = inputStream.readInt();
											z = inputStream.readInt();
											
											TileEntity te = player.worldObj.getBlockTileEntity(x, y, z);
											
											if(te != null && te instanceof TileEntityTrap) {
												PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7e\u00A7oYou disarm and salvage the trap."}), (Player) player);
												player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, x + 0.5F, y, z + 0.5F, new ItemStack(ModjamMod.itemTrap.itemID, 1, player.worldObj.getBlockMetadata(x, y, z) % BlockTrap.trapTypes)));
												player.worldObj.setBlockToAir(x, y, z);
											
												EntityStatHelper.giveStat(player, "BlessingCooldown", (int) ((player.worldObj.getWorldTime() / 24000) + 24000));
											} else {
												PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7e\u00A7oNo selected trap."}), (Player) player);
											}
										}
									}
								} else {
									blessingActive = false;
									
									if(blessing != null) {
										if(blessing.equals("Berserker")) {
											PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7cYou calm down."}), (Player) player);
											EntityStatHelper.giveStat(player, "BlessingCooldown", (int) (player.worldObj.getWorldTime() + 1200));
										}
									}
									
									EntityStatHelper.giveStat(player, "BlessingTimer", 0);
								}
							} else {
								PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7cBlessing is on cooldown. (" + (Integer.parseInt(EntityStatHelper.getStat(player, "BlessingCooldown")) - player.worldObj.getWorldTime())/20 + "s)"}), (Player) player);
							}
							
							EntityStatHelper.giveStat(player, "BlessingActive", blessingActive);
							
						return;
							
						case DISARM_TRAP:
							
							int blockX = inputStream.readInt();
							int blockY = inputStream.readInt();
							int blockZ = inputStream.readInt();
							
							boolean mechanic = inputStream.readBoolean();
							
							blessing = null;
							if(player.getEntityData().hasKey("Blessing")) {blessing = player.getEntityData().getString("Blessing");}
							boolean scout = blessing != null && blessing.equals("Scout");
							
							TileEntity te = player.worldObj.getBlockTileEntity(blockX, blockY, blockZ);
							
							boolean yours = (te == null || !(te instanceof TileEntityTrap)) ? false : player.getEntityName().equals(((TileEntityTrap) te).placedBy);
							
							if(yours || (mechanic ? ModjamMod.r.nextInt(4) != 0 : ModjamMod.r.nextInt(4) == 0)) {
								boolean salvage = yours || (mechanic ? ModjamMod.r.nextBoolean() : (ModjamMod.r.nextInt(4) == 0));
								PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7e\u00A7o" + (!salvage ? "You disarmed the trap." : "You disarm and salvage the trap.")}), (Player) player);
								if(salvage) {player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, blockX + 0.5F, blockY, blockZ + 0.5F, new ItemStack(ModjamMod.itemTrap.itemID, 1, player.worldObj.getBlockMetadata(blockX, blockY, blockZ) % BlockTrap.trapTypes)));}
								player.worldObj.setBlockToAir(blockX, blockY, blockZ);
							} else {
								int trapType = player.worldObj.getBlockMetadata(blockX, blockY, blockZ);
					    		
					    		if(trapType % BlockTrap.trapTypes == 0) {
					    			player.attackEntityFrom(DamageSource.cactus, 4.0F + (scout ? 1 : 0));
					    			if(ModjamMod.r.nextInt(16 - (scout ? 4 : 0)) == 0) {((EntityLivingBase) player).addPotionEffect(new PotionEffect(Potion.poison.id, 100 + (scout ? 25 : 0), 1));}
					    		} else if(trapType % BlockTrap.trapTypes == 1) {
					    			if(!player.isBurning()) {player.setFire(5 + (scout ? 1 : 0));}
					    		} else if(trapType % BlockTrap.trapTypes == 2) {
					    			player.addPotionEffect(new PotionEffect(Potion.blindness.id, 100 + (scout ? 25 : 0), 1));
					    			player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100 + (scout ? 25 : 0), 1));
					    		}
								
								player.worldObj.setBlockToAir(blockX, blockY, blockZ);
								
								PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7c\u00A7oYou failed to disarm the trap."}), (Player) player);
								if(CommonTickHandler.worldData.getDisadvantage().equals("Explosive Traps")) {player.worldObj.setBlockToAir(blockX, blockY, blockZ); player.worldObj.createExplosion(null, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, 1.33F, true);}
								player.triggerAchievement(ModjamMod.whoops);
							}
							
							return;
							
						case EXAMINE_MOB:
							
							int dimension = inputStream.readInt();
							int entityID = inputStream.readInt();
							
							WorldServer server = null;
							
							for(WorldServer s : MinecraftServer.getServer().worldServers) {if(s.provider.dimensionId == dimension) {server = s; break;}}
							
							if(server != null) {
								Entity entity = server.getEntityByID(entityID);
								
								if(entity != null) {
									String blessing2 = entity.getEntityData().hasKey("Blessing") ? entity.getEntityData().getString("Blessing") : null;
									
									if(blessing2 != null) {
										PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7eYou notice " + entity.getTranslatedEntityName() + "\u00A7e is using Blessing of the " + blessing2 + "."}), (Player) player);
									} else {
										PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7eThere doesn't seem to be anything special about " + (entity instanceof EntityPlayer ? "" : "this ") + entity.getTranslatedEntityName() + "\u00A7e."}), (Player) player);
									}
								}
							}
							
							return;
						
						default: return;
					}
				} else if (side == Side.CLIENT) {
					EntityPlayer player = (EntityPlayer) playerEntity;
					
					if(potionValues == null) {potionValues = new int[12];}
					if(potionDurations == null) {potionDurations = new int[12];}
					if(mushroomColors == null) {mushroomColors = new int[13][2];}
					
					switch(type) {
						case UPDATE_BLESSING: player.getEntityData().setString("Blessing", inputStream.readUTF()); return;
						case UPDATE_POTION_KNOWLEDGE: int[] potionKnowledge = new int[12]; for(int i = 0; i < 12; i++) {potionKnowledge[i] = inputStream.readInt();} player.getEntityData().setIntArray("PotionKnowledge", potionKnowledge); return;
						
						case SEND_MESSAGE:
							for(String s : inputStream.readUTF().split("@")) {Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(s);}
						return;
						
						case LEVEL_UP: player.addExperienceLevel(inputStream.readInt()); return;
						
						case UPDATE_STATS:
							int num = inputStream.readInt();
							
							for(int i = 0; i < num; i++) {
								EntityStatHelper.giveStat(player, inputStream.readUTF(), inputStream.readUTF());
							}
						return;
						
						case UPDATE_WORLD_DATA: 
							
							for(int i = 0; i < 12; i++) {potionValues[i] = inputStream.readInt();} 
							for(int i = 0; i < 12; i++) {potionDurations[i] = inputStream.readInt();} 
							currentDisadvantage = inputStream.readUTF(); 
							currentTask = inputStream.readUTF(); 
							currentTaskID = inputStream.readInt(); 
							currentTaskAmount = inputStream.readInt(); 
							progress = inputStream.readInt(); 
							tasksCompleted = inputStream.readInt();
							enderDragonKilled = inputStream.readBoolean();
							trapsDisabled = !inputStream.readBoolean();
							rewardLevels = inputStream.readInt();
							
							for(int i = 0; i < 13; i++) {
								for(int i2 = 0; i2 < 2; i2++) {mushroomColors[i][i2] = inputStream.readInt();}
							}
							
							return;
						
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
                	} else if(data[i] instanceof int[][]) {
                		int[][] values = (int[][]) data[i];
                		
                		for(int i2 = 0; i2 < values.length; i2++) {
                			for(int i3 = 0; i3 < values[i2].length; i3++) {
                				outputStream.writeInt(values[i2][i3]);
                			}
                		}
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
