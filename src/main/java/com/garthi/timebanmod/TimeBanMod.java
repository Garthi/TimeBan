package com.garthi.timebanmod;

import com.garthi.timebanmod.commands.DayBanCommand;
import com.garthi.timebanmod.commands.ListBanCommand;
import com.garthi.timebanmod.commands.TimeBanCommand;
import com.garthi.timebanmod.utilities.ConfigHelper;
import com.garthi.timebanmod.utilities.NotLoadedException;
import net.minecraft.command.CommandBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

@Mod(modid = TimeBanMod.MODID, name = TimeBanMod.NAME, version = TimeBanMod.VERSION)
public class TimeBanMod
{
    static final String MODID = "timebanmod";
    static final String NAME = "Time Ban Mod";
    static final String VERSION = "0.7";
    
    /** Commands **/
    private static CommandBase TIME_BAN_COMMAND = new TimeBanCommand();
    private static CommandBase DAY_BAN_COMMAND = new DayBanCommand();
    //private static CommandBase LIST_BAN_COMMAND = new ListBanCommand();
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        ConfigHelper.init(event.getSuggestedConfigurationFile());
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @EventHandler
    public void registerCommands(FMLServerStartingEvent event)
    {
        event.registerServerCommand(TIME_BAN_COMMAND);
        event.registerServerCommand(DAY_BAN_COMMAND);
        //event.registerServerCommand(LIST_BAN_COMMAND);
    }
    
    @SubscribeEvent
    public void onPlayerConnectedToServer(FMLNetworkEvent.ServerConnectionFromClientEvent event)
    {
        if (isPlayerBanned(event)) {
            kickPlayer(event.getManager(), ((NetHandlerPlayServer)event.getHandler()).player);
        }
    }
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(LivingDeathEvent event)
    {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            // is not a player
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP)event.getEntityLiving();

        try {
            if (!ConfigHelper.player(player.getName()).isDeathBanTimeActive()) {
                // death ban is not active
                return;
            }
        } catch (NotLoadedException e) {
            FMLLog.log.error(e.getMessage());
        }
        
        FMLLog.log.info("player is death: {}", player.getName());
        
        // change game mode
        player.setGameType(GameType.SURVIVAL);
        
        // drop all Items
        InventoryHelper.dropInventoryItems(player.world, player, player.inventory);

        // remove player experience
        player.addExperienceLevel(-30);

        // set player to World Spawn
        BlockPos blockPos = player.getEntityWorld().getSpawnPoint();
        player.setPositionAndUpdate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        
        // kick and ban player
        try {
            int banTime = ConfigHelper.player(player.getName()).getDeathBanTime();
            ConfigHelper.player(player.getName()).add(banTime);
            player.connection.disconnect(new TextComponentTranslation("time.ban.custom.death.message", banTime));
        } catch (NotLoadedException e) {
            FMLLog.log.error(e.getMessage());
        }
    }
    
    @SubscribeEvent
    public void renderName(PlayerEvent.NameFormat event)
    {
        if (event.getEntityPlayer().getName().equals("Balui")) {
            event.setDisplayname("Der unfähige Feuermagier");
        }
    }

    private Boolean isPlayerBanned(FMLNetworkEvent.ServerConnectionFromClientEvent event)
    {
        if (event.isLocal()) {
            return false;
        }
        EntityPlayerMP player = ((NetHandlerPlayServer)event.getHandler()).player;

        FMLLog.log.info("Check if the player \"{}\" is banned", player.getName());
        
        try {
            return ConfigHelper.player(player.getName()).isBanned();
        } catch (NotLoadedException e) {
            FMLLog.log.error(e.getMessage());
        }
        
        return false;
    }
    
    private void kickPlayer(NetworkManager networkManager, EntityPlayerMP player)
    {
        String reasonCustomMessage = null;
        
        try {
            reasonCustomMessage = ConfigHelper.player(player.getName()).reason();
        } catch (NotLoadedException e) {
            FMLLog.log.error(e.getMessage());
        }

        ITextComponent reason;
        if (reasonCustomMessage == null) {
            reason = new TextComponentTranslation("time.ban.default.message");
        } else {
            reason = new TextComponentTranslation("time.ban.custom.message", reasonCustomMessage);
        }
        
        // log ban
        FMLLog.log.info("Rejecting connection: {}", reason.getFormattedText());

        // disconnect player (with log message)
        networkManager.closeChannel(reason);
        //player.connection.disconnect(reason);
    }
}
