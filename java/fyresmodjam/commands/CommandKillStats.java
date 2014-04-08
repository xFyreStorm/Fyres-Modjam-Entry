package fyresmodjam.commands;

import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import fyresmodjam.ModjamMod;
import fyresmodjam.handlers.NewPacketHandler;
import fyresmodjam.misc.EntityStatHelper;

public class CommandKillStats implements ICommand {

	@Override
	public int compareTo(Object arg0) {return 0;}

	@Override
	public String getCommandName() {return "creatureKnowledge";}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {return "commands.creatureKnowledge.usage";}

	@Override
	@SuppressWarnings("rawtypes")
	public List getCommandAliases() {return null;}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
		int page = astring.length > 0 ? Integer.parseInt(astring[0]) - 1 : 0, maxPage = 0;

		if(icommandsender instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) icommandsender;

			String message = "\u00A7c\u00A7oMob kill stats not enabled.";

			if(ModjamMod.enableMobKillStats) {
				message = "@Creature Knowledge:";

				if(entityplayer.getEntityData().hasKey("KillStats")) {
					NBTTagCompound killStats = entityplayer.getEntityData().getCompoundTag("KillStats");

					String trackedMobs = killStats.hasKey("TrackedMobList") ? killStats.getString("TrackedMobList") : "";

					if(trackedMobs != null && trackedMobs.length() > 0) {
						String[] trackedMobList = trackedMobs.split(";");

						maxPage = Math.max(0, (killStats.func_150296_c().size() - 1)/4);
						if(page > maxPage) {page = maxPage;}
						if(page < 0) {page = 0;}

						message = "@Creature Knowledge (page " + (page + 1) + "/" + (maxPage + 1) +  "):";

						int count = 0, skip = 0;
						for(String mob : trackedMobList) {
							if(skip < page * 4) {skip++; continue;}
							
							int kills = killStats.getInteger(mob);

							int last = 0;
							for(int i = 0; i < EntityStatHelper.killCount.length; i++) {
								if(kills >= EntityStatHelper.killCount[i]) {last = i; continue;} else {break;}
							}

							message += "@\u00A7b    " + EntityStatHelper.knowledge[last] + " " + mob.toLowerCase() + " slayer\u00A73 " + (last > 0 ? "+" + EntityStatHelper.damageBonusString[last] + "% damage bonus (" : "(") + kills + " kill(s)" + (last < EntityStatHelper.knowledge.length - 1 ? ", " + (EntityStatHelper.killCount[last + 1] - kills + " kill(s) to next rank)") : ")");
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
