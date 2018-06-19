package com.garthi.timebanmod.commands;

import com.garthi.timebanmod.utilities.BanCommandProfileLookupCallback;
import com.mojang.authlib.Agent;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLLog;

/**
 * @author Martin "Garth" Zander <garth@new-crusader.de>
 * @package BanMod
 */
abstract class AbstractCommand extends CommandBase
{
    protected  static int TYPE_TIME_BAN = 1;
    protected  static int TYPE_DAY_BAN = 2;
    
    protected static String COMMAND_NAME = "/ban";

    private int type;

    @Override
    public String getName()
    {
        return COMMAND_NAME;
    }

    private int getType()
    {
        return type;
    }

    protected void setType(int type)
    {
        this.type = type;
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
            throws CommandException
    {
        if (args == null || args.length < 2) {
            sender.sendMessage(
                    new TextComponentTranslation("time.ban.command.time.ban.help", COMMAND_NAME, getBanUnit())
            );
            return;
        }

        if (args[1] == null || Integer.parseInt(args[1]) < 1) {
            sender.sendMessage(
                    new TextComponentTranslation("time.ban.command.time.ban.help", COMMAND_NAME, getBanUnit())
            );
            return;
        }

        int banTime = Integer.parseInt(args[1]);
        String playerName = args[0];

        if (getType() == AbstractCommand.TYPE_DAY_BAN) {
            banTime = banTime * 60 * 24;
        }

        executeBanCommand(server, sender, playerName, banTime);
    }
    
    private String getBanUnit()
    {
        if (getType() == AbstractCommand.TYPE_DAY_BAN) {
            return new TextComponentTranslation("time.ban.command.time.ban.message.hour").getFormattedText();
        } else if (getType() == AbstractCommand.TYPE_TIME_BAN) {
            return new TextComponentTranslation("time.ban.command.time.ban.message.minutes").getFormattedText();
        }
        
        return "";
    }
    
    private void executeBanCommand(MinecraftServer server, ICommandSender sender, String playerName, int banTime)
    {
        try {
            if (sender instanceof EntityPlayer) {
                try {
                    BanCommandProfileLookupCallback callback;
                    callback = new BanCommandProfileLookupCallback(sender, banTime, getBanUnit());
                    
                    String[] players = new String[]{playerName};
                    
                    assert server.getServer() != null;
                    server.getServer().getGameProfileRepository().findProfilesByNames(
                            players, Agent.MINECRAFT, callback
                    );
                } catch (NullPointerException e) {
                    FMLLog.log.catching(e);
                }
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
}
