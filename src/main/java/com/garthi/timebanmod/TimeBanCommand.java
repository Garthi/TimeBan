package com.garthi.timebanmod;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
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
        return String.format("/%s <name> <minutes>", COMMAND_NAME);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args)
            throws CommandException
    {
        try {
            if (sender instanceof EntityPlayer) {
                if (args == null || args.length < 2) {
                    sender.sendMessage(new TextComponentTranslation("time.ban.command.time.ban.help", COMMAND_NAME));
                    return;
                }
                
                if (args[1] == null || Integer.parseInt(args[1]) < 1) {
                    sender.sendMessage(new TextComponentTranslation("time.ban.command.time.ban.help", COMMAND_NAME));
                    return;
                }
                
                int banTime = Integer.parseInt(args[1]);
                String playerName = args[0]; 
                
                // get player object
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback()
                {
                    public void onProfileLookupSucceeded(GameProfile gameProfile)
                    {
                        // ban player
                        try {
                            ConfigHelper.player(gameProfile.getName()).add(banTime);
                        } catch (NotLoadedException e) {
                            FMLLog.log.catching(e);
                        }
                        
                        // kick player
                        try {
                            EntityPlayerMP entityPlayerMP = (EntityPlayerMP)sender.getEntityWorld().getPlayerEntityByName(playerName);
                            assert entityPlayerMP != null;
                            entityPlayerMP.connection.disconnect(new TextComponentTranslation(
                                    "time.ban.command.time.ban.message",
                                    sender.getDisplayName().getFormattedText(),
                                    String.valueOf(banTime),
                                    (new TextComponentTranslation("time.ban.command.time.ban.message.minutes")).getFormattedText()
                            ));

                            // feedback
                            sender.sendMessage(new TextComponentTranslation("time.ban.command.time.ban.success.message", gameProfile.getName()));
                        } catch (Exception e) {
                            FMLLog.log.catching(e);
                            sender.sendMessage(new TextComponentTranslation("time.ban.command.time.ban.fail.message", playerName));
                        }
                    }
                    public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_)
                    {
                        // feedback for fail
                        sender.sendMessage(new TextComponentTranslation("time.ban.command.time.ban.fail.message", playerName));
                    }
                };
                
                try {
                    String[] players = new String[]{playerName};
                    assert server.getServer() != null;
                    server.getServer().getGameProfileRepository().findProfilesByNames(players, Agent.MINECRAFT, profilelookupcallback);
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

    @Override
    public int getRequiredPermissionLevel()
    {
        return 4;
    }
}
