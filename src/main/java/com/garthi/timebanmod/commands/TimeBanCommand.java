package com.garthi.timebanmod.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLLog;

/**
 * @author Martin "Garth" Zander <garth@new-crusader.de>
 * @package BanMod
 */
public class TimeBanCommand extends AbstractCommand
{
    private static final String COMMAND_NAME = "timeban";

    @Override
    public String getName()
    {
        return COMMAND_NAME;
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return String.format("/%s <name> <minutes>", COMMAND_NAME);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        setType(TimeBanCommand.TYPE_TIME_BAN);
        super.execute(server, sender, args);
    }
}
