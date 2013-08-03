package assets.fyresmodjam;

import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

public class CommandCurrentBlessing implements ICommand {

	public int compareTo(Object arg0) {return 0;}

	public String getCommandName() {return null;}

	public String getCommandUsage(ICommandSender icommandsender) {return "commands.currentBlessing.usage";}

	public List getCommandAliases() {return null;}

	public void processCommand(ICommandSender icommandsender, String[] astring) {}

	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {return false;}

	public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring) {return null;}

	public boolean isUsernameIndex(String[] astring, int i) {return false;}
	
	public int getRequiredPermissionLevel() {return 0;}
	
}
