package com.garthi.timebanmod.utilities;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.FMLLog;

/**
 * @author Martin "Garth" Zander <garth@new-crusader.de>
 * @package BanMod
 */
public class BanCommandProfileLookupCallback implements ProfileLookupCallback
{
    private ICommandSender sender;
    private int banTime;
    private String banUnit;
    
    public BanCommandProfileLookupCallback(ICommandSender sender, int banTime, String banUnit)
    {
        this.sender = sender;
        this.banTime = banTime;
        this.banUnit = banUnit;
    }
    
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
            EntityPlayerMP entityPlayerMP;
            entityPlayerMP = (EntityPlayerMP)sender.getEntityWorld().getPlayerEntityByName(gameProfile.getName());
            if (entityPlayerMP != null) {
                entityPlayerMP.connection.disconnect(new TextComponentTranslation(
                        "time.ban.command.time.ban.message",
                        sender.getDisplayName().getFormattedText(),
                        String.valueOf(banTime),
                        banUnit
                ));
            }

            // feedback
            sender.sendMessage(
                    new TextComponentTranslation("time.ban.command.time.ban.success.message", gameProfile.getName())
            );
        } catch (Exception e) {
            FMLLog.log.catching(e);
            sender.sendMessage(
                    new TextComponentTranslation("time.ban.command.time.ban.fail.message_1", gameProfile.getName())
            );
        }
    }
    public void onProfileLookupFailed(GameProfile gameProfile, Exception p_onProfileLookupFailed_2_)
    {
        // feedback for fail
        sender.sendMessage(new TextComponentTranslation("time.ban.command.time.ban.fail.message_2", gameProfile.getName()));
    }
}
