package com.garthi.timebanmod;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLLog;

/**
 * @author Martin "Garth" Zander <garth@new-crusader.de>
 * @package BanMod
 */
public class TimeBanCommand extends CommandBase
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
        return String.format("/%s <name>", COMMAND_NAME);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
            throws CommandException
    {
        if (args != null && args.length < 1) {
            return;
        }

        try {
            if (sender instanceof EntityPlayer) {
                assert args != null;
                sender.sendMessage(new TextComponentString("Hello " + args[0]));
            } else {
                assert sender != null;
                sender.sendMessage(new TextComponentString(
                        EntityPlayer.EnumChatVisibility.SYSTEM + "This command is not available inside the console."
                ));
            }
        } catch (NullPointerException e) {
            FMLLog.log.catching(e);
        }
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 4;
    }
}
