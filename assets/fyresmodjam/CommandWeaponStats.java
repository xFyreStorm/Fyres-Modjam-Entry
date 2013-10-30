package assets.fyresmodjam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class CommandWeaponStats implements ICommand {

	public int compareTo(Object arg0) {return 0;}

	public String getCommandName() {return "weaponKnowledge";}

	public String getCommandUsage(ICommandSender icommandsender) {return "commands.weaponKnowledge.usage";}

	public List getCommandAliases() {return null;}

	public void processCommand(ICommandSender icommandsender, String[] astring) {
		int page = astring.length > 0 ? Integer.parseInt(astring[0]) - 1 : 0, maxPage = 0;
		if(icommandsender instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) icommandsender;

			String message = "\u00A7c\u00A7oWeapon kill stats not enabled.";
			
			if(ModjamMod.enableWeaponKillStats) {
				message = "@Weapon Knowledge:"; //\u00A7e\u00A7o
				
				if(entityplayer.getEntityData().hasKey("WeaponStats")) {
					NBTTagCompound killStats = entityplayer.getEntityData().getCompoundTag("WeaponStats");
					
					ArrayList<Object> objects = new ArrayList<Object>();
					objects.addAll(killStats.getTags());
					
					Collections.sort(objects, new Comparator<Object>() {
						public int compare(Object o1, Object o2) {
							int i = 0;
							if(o1 instanceof NBTBase && o2 instanceof NBTBase) {i = ((NBTBase) o1).getName().replace("Kills", "").compareTo(((NBTBase) o2).getName().replace("Kills", ""));}
							return i;
						}
					});
					
					maxPage = Math.max(0, (killStats.getTags().size())/4);
					if(page > maxPage) {page = maxPage;}
					if(page < 0) {page = 0;}
					
					message = "@Weapon Knowledge (page " + (page + 1) + "/" + (maxPage + 1) +  "):";
					
					int count = 0, skip = 0;
					for(Object o : objects) {
						if(skip < page * 4) {skip++; continue;}
						if(o instanceof NBTBase) {
							String mob = ((NBTBase) o).getName().replace("Kills", "");
							int kills = killStats.getInteger(((NBTBase) o).getName());
							
							int last = 0;
							for(int i = 0; i < EntityStatHelper.killCount.length; i++) {
								if(kills >= EntityStatHelper.killCount[i] * 2) {last = i; continue;} else {break;}
							}
							
							message += "@\u00A7b    " + EntityStatHelper.knowledge[last] + " " + mob.toLowerCase() + " user\u00A73 " + (last > 0 ? "+" + EntityStatHelper.damageBonusString[last] + "% damage bonus (" : "(") + kills + " kill(s)" + (last < EntityStatHelper.knowledge.length - 1 ? ", " + (EntityStatHelper.killCount[last + 1] * 2 - kills + " kill(s) to next rank)") : ")");
							count++;
							
							if(count >= 4) {break;}
						}
					}
				} else {
					message += "@    You've yet to learn anything.";
				}
			}
			
			PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {message}), (Player) entityplayer);
		}
	}

	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {return true;}

	public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring) {return null;}

	public boolean isUsernameIndex(String[] astring, int i) {return false;}
	
	public int getRequiredPermissionLevel() {return 0;}
	
}
