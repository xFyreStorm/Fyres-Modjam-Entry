package assets.fyresmodjam;

import java.util.List;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CommandCurrentWorldTask implements ICommand {

	public int compareTo(Object arg0) {return 0;}

	public String getCommandName() {return "currentGoal";}

	public String getCommandUsage(ICommandSender icommandsender) {return "commands.currentGoal.usage";}

	public List getCommandAliases() {return null;}

	public void processCommand(ICommandSender icommandsender, String[] astring) {
		if(icommandsender instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) icommandsender;
			
			PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7eWorld goal: " + FyresWorldData.currentTask + " " + FyresWorldData.currentTaskAmount + " " + (FyresWorldData.currentTask.equals("Kill") ? FyresWorldData.validMobNames[FyresWorldData.currentTaskID] : new ItemStack(Item.itemsList[FyresWorldData.currentTaskID], 1).getDisplayName()) + (FyresWorldData.currentTaskAmount > 1 ? "s" : "") + ". (" + FyresWorldData.progress + " " + FyresWorldData.currentTask + "ed)"}), (Player) entityplayer);
			PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7eGoals completed: " + FyresWorldData.tasksCompleted}), (Player) entityplayer);
		}
	}

	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {return true;}

	public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring) {return null;}

	public boolean isUsernameIndex(String[] astring, int i) {return false;}
	
	public int getRequiredPermissionLevel() {return 0;}
	
}
