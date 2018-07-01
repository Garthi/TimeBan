package com.garthi.timebanmod.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

/**
 * @author Martin "Garth" Zander <garth@new-crusader.de>
 * @package BanMod
 */
public class ListBanCommand extends CommandBase
{
    private static final String COMMAND_NAME = "listban";
    
    @Override
    public String getName()
    {
        return COMMAND_NAME;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return String.format("/%s", COMMAND_NAME);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (sender instanceof EntityPlayer) {
            sender.sendMessage(new TextComponentString(
                    EntityPlayer.EnumChatVisibility.SYSTEM + "Liste der gebannten spieler"
            ));
            // TODO show time banned player
            // TODO show banned players from origin
        } else {
            sender.sendMessage(new TextComponentString(
                    EntityPlayer.EnumChatVisibility.SYSTEM + "This command is not available inside the console."
            ));
        }
    }
}
