package dev.plex.listener.impl;

import dev.plex.admin.Admin;
import dev.plex.cache.DataUtils;
import dev.plex.cache.MongoPlayerData;
import dev.plex.cache.PlayerCache;
import dev.plex.cache.SQLPlayerData;
import dev.plex.listener.PlexListener;
import dev.plex.player.PlexPlayer;
import dev.plex.player.PunishedPlayer;
import dev.plex.util.PlexLog;
import java.util.Collections;
import java.util.UUID;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener extends PlexListener
{
    private final MongoPlayerData mongoPlayerData = plugin.getMongoPlayerData() != null ? plugin.getMongoPlayerData() : null;
    private final SQLPlayerData sqlPlayerData = plugin.getSqlPlayerData() != null ? plugin.getSqlPlayerData() : null;

    // setting up a player's data
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerSetup(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        PlexPlayer plexPlayer;

        if (plugin.getSystem().equalsIgnoreCase("ranks"))
        {
            player.setOp(true);
            PlexLog.debug("Automatically opped " + player.getName() + " since ranks are enabled.");
        }
        else if (plugin.getSystem().equalsIgnoreCase("permissions"))
        {
            player.setOp(false);
            PlexLog.debug("Automatically deopped " + player.getName() + " since ranks are disabled.");
        }

        if (!DataUtils.hasPlayedBefore(player.getUniqueId()))
        {
            PlexLog.log("A player with this name has not joined the server before, creating new entry.");
            plexPlayer = new PlexPlayer(player.getUniqueId()); //it doesn't! okay so now create the object
            plexPlayer.setName(player.getName()); //set the name of the player
            plexPlayer.setIps(Collections.singletonList(player.getAddress().getAddress().getHostAddress().trim())); //set the arraylist of ips
            DataUtils.insert(plexPlayer); // insert data in some wack db
        }
        else
        {
            plexPlayer = DataUtils.getPlayer(player.getUniqueId());
        }

        PlayerCache.getPlexPlayerMap().put(player.getUniqueId(), plexPlayer); //put them into the cache
        if (!PlayerCache.getPunishedPlayerMap().containsKey(player.getUniqueId()))
        {
            PlayerCache.getPunishedPlayerMap().put(player.getUniqueId(), new PunishedPlayer(player.getUniqueId()));
        }

        assert plexPlayer != null;

        if (plugin.getRankManager().isAdmin(plexPlayer))
        {
            Admin admin = new Admin(UUID.fromString(plexPlayer.getUuid()));
            admin.setRank(plexPlayer.getRankFromString());

            plugin.getAdminList().addToCache(admin);

            if (!plexPlayer.getLoginMSG().isEmpty())
            {
                event.joinMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(ChatColor.AQUA + player.getName() + " is " + plexPlayer.getLoginMSG()));
            }
            else
            {
                event.joinMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(ChatColor.AQUA + player.getName() + " is " + plexPlayer.getRankFromString().getLoginMSG()));
            }
        }
    }

    // saving the player's data
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerSave(PlayerQuitEvent event)
    {
        PlexPlayer plexPlayer = PlayerCache.getPlexPlayerMap().get(event.getPlayer().getUniqueId()); //get the player because it's literally impossible for them to not have an object

        if (plugin.getRankManager().isAdmin(plexPlayer))
        {
            plugin.getAdminList().removeFromCache(UUID.fromString(plexPlayer.getUuid()));
        }

        if (mongoPlayerData != null) //back to mongo checking
        {
            mongoPlayerData.update(plexPlayer); //update the player's document
        }
        else if (sqlPlayerData != null) //sql checking
        {
            sqlPlayerData.update(plexPlayer);
        }

        PlayerCache.getPlexPlayerMap().remove(event.getPlayer().getUniqueId()); //remove them from cache
    }
}
