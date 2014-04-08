package fyresmodjam.commands;

import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import fyresmodjam.handlers.NewPacketHandler;
import fyresmodjam.tileentities.TileEntityPillar;

public class CommandCurrentBlessing implements ICommand {

	@Override
	public int compareTo(Object arg0) {return 0;}

	@Override
	public String getCommandName() {return "currentBlessing";}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {return "commands.currentBlessing.usage";}

	@Override
	@SuppressWarnings("rawtypes")
	public List getCommandAliases() {return null;}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
		if(icommandsender instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) icommandsender;

			String blessing = entityplayer.getEntityData().hasKey("Blessing") ? entityplayer.getEntityData().getString("Blessing") : null;
			int index = 0;
			for(int i = 0; i < TileEntityPillar.validBlessings.length; i++) {if(TileEntityPillar.validBlessings[i].equals(blessing)) {index = i; break;}}

			NewPacketHandler.SEND_MESSAGE.sendToPlayer(entityplayer, blessing != null ? "\u00A7eCurrent Blessing - \u00A7oBlessing of the " + blessing + ": " + TileEntityPillar.blessingDescriptions[index] : "You don't currently have a blessing.");
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
