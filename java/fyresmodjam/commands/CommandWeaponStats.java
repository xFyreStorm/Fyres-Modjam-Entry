package fyresmodjam.commands;

import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import fyresmodjam.ModjamMod;
import fyresmodjam.handlers.NewPacketHandler;
import fyresmodjam.misc.EntityStatHelper;

public class CommandWeaponStats implements ICommand {

	@Override
	public int compareTo(Object arg0) {return 0;}

	@Override
	public String getCommandName() {return "weaponKnowledge";}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {return "commands.weaponKnowledge.usage";}

	@Override
	@SuppressWarnings("rawtypes")
	public List getCommandAliases() {return null;}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
		int page = astring.length > 0 ? Integer.parseInt(astring[0]) - 1 : 0, maxPage = 0;
		if(icommandsender instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) icommandsender;

			String message = "\u00A7c\u00A7oWeapon kill stats not enabled.";

			if(ModjamMod.enableWeaponKillStats) {
				message = "@Weapon Knowledge:";

				if(entityplayer.getEntityData().hasKey("WeaponStats")) {
					NBTTagCompound itemStats = entityplayer.getEntityData().getCompoundTag("WeaponStats");

					String trackedItems = itemStats.hasKey("TrackedItemList") ? itemStats.getString("TrackedItemList") : "";

					if(trackedItems != null && trackedItems.length() > 0) {
						String[] trackedItemList = trackedItems.split(";");

						maxPage = Math.max(0, (itemStats.func_150296_c().size())/4);
						if(page > maxPage) {page = maxPage;}
						if(page < 0) {page = 0;}

						message = "@Weapon Knowledge (page " + (page + 1) + "/" + (maxPage + 1) +  "):";

						int count = 0, skip = 0;
						for(String item : trackedItemList) {
							if(skip < page * 4) {skip++; continue;}
							
							int kills = itemStats.getInteger(item);

							int last = 0;
							for(int i = 0; i < EntityStatHelper.killCount.length; i++) {
								if(kills >= EntityStatHelper.killCount[i] * 2) {last = i; continue;} else {break;}
							}

							message += "@\u00A7b    " + EntityStatHelper.knowledge[last] + " " + item.toLowerCase() + " user\u00A73 " + (last > 0 ? "+" + EntityStatHelper.damageBonusString[last] + "% damage bonus (" : "(") + kills + " kill(s)" + (last < EntityStatHelper.knowledge.length - 1 ? ", " + (EntityStatHelper.killCount[last + 1] * 2 - kills + " kill(s) to next rank)") : ")");
							count++;

							if(count >= 4) {break;}
						}
					}
				} else {
					message += "@    You've yet to learn anything.";
				}
			}

			NewPacketHandler.SEND_MESSAGE.sendToPlayer(entityplayer, message);
		}
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {return true;}

	@Override
	@SuppressWarnings("rawtypes")
	public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring) {return null;}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) {return false;}

	public int getRequiredPermissionLevel() {return 0;}

}
