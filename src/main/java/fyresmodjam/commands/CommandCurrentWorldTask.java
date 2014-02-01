package fyresmodjam.commands;

import java.util.List;


import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import fyresmodjam.handlers.CommonTickHandler;
import fyresmodjam.handlers.PacketHandler;
import fyresmodjam.worldgen.FyresWorldData;

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
			
			String name = CommonTickHandler.worldData.currentTask.equals("Kill") ? FyresWorldData.validMobNames[CommonTickHandler.worldData.currentTaskID] : new ItemStack(Item.itemsList[CommonTickHandler.worldData.currentTaskID], 1).getDisplayName();
			
			if(CommonTickHandler.worldData.currentTaskAmount > 1) {
				if(name.contains("Block")) {if(name.contains("Block")) {name = name.replace("Block", "Blocks").replace("block", "blocks");}}
				else {name += "s";}
			}
			
			PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7eWorld goal: " + CommonTickHandler.worldData.currentTask + " " + CommonTickHandler.worldData.currentTaskAmount + " " + name + ". (" + CommonTickHandler.worldData.progress + " " + CommonTickHandler.worldData.currentTask + "ed)"}), (Player) entityplayer);
			PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7f   Reward: " + CommonTickHandler.worldData.rewardLevels + " Experience Levels"}), (Player) entityplayer);
			PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7eGoals completed: " + CommonTickHandler.worldData.tasksCompleted}), (Player) entityplayer);
		}
	}

	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {return true;}

	public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring) {return null;}

	public boolean isUsernameIndex(String[] astring, int i) {return false;}
	
	public int getRequiredPermissionLevel() {return 0;}
	
}
