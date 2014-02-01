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

public class CommandCurrentDisadvantage implements ICommand {

	public int compareTo(Object arg0) {return 0;}

	public String getCommandName() {return "currentDisadvantage";}

	public String getCommandUsage(ICommandSender icommandsender) {return "commands.currentDisadvantage.usage";}

	public List getCommandAliases() {return null;}

	public void processCommand(ICommandSender icommandsender, String[] astring) {
		if(icommandsender instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) icommandsender;
			
			int index = -1;
			for(int i = 0; i < FyresWorldData.validDisadvantages.length; i++) {if(FyresWorldData.validDisadvantages[i].equals(CommonTickHandler.worldData.getDisadvantage())) {index = i; break;}}
			PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7eWorld disadvantage: " + CommonTickHandler.worldData.getDisadvantage() + (index == -1 ? "" : " (" + FyresWorldData.disadvantageDescriptions[index] + ")")}), (Player) entityplayer);
		}
	}

	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {return true;}

	public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring) {return null;}

	public boolean isUsernameIndex(String[] astring, int i) {return false;}
	
	public int getRequiredPermissionLevel() {return 0;}
	
}
