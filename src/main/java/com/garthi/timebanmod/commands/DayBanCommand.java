package com.garthi.timebanmod.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLLog;

/**
 * @author Martin "Garth" Zander <garth@new-crusader.de>
 * @package BanMod
 */
public class DayBanCommand extends AbstractCommand
{
    protected static final String COMMAND_NAME = "dayban";

    @Override
    public String getName()
    {
        return COMMAND_NAME;
    }
    
    @Override
    public String getUsage(ICommandSender sender)
    {
        return String.format("/%s <name> <days>", COMMAND_NAME);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        setType(DayBanCommand.TYPE_DAY_BAN);
        super.execute(server, sender, args);
    }
}
