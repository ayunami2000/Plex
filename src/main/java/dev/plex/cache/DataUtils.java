package dev.plex.cache;

import dev.plex.Plex;
import dev.plex.player.PlexPlayer;
import dev.plex.storage.StorageType;
import java.util.UUID;
import org.bukkit.Bukkit;

/**
 * Parent cache class
 */
public class DataUtils
{
    /**
     * Checks if the player has been on the server before
     *
     * @param uuid The unique ID of the player
     * @return true if the player is registered in the database
     */
    public static boolean hasPlayedBefore(UUID uuid)
    {
        if (Plex.get().getStorageType() == StorageType.MONGODB)
        {
            return Plex.get().getMongoPlayerData().exists(uuid);
        }
        else
        {
            return Plex.get().getSqlPlayerData().exists(uuid);
        }
    }

    /**
     * Gets a player from cache or from the database
     *
     * @param uuid The unique ID of the player
     * @return a PlexPlayer object
     * @see PlexPlayer
     */
    public static PlexPlayer getPlayer(UUID uuid)
    {
        if (PlayerCache.getPlexPlayerMap().containsKey(uuid))
        {
            return PlayerCache.getPlexPlayerMap().get(uuid);
        }

        if (Plex.get().getStorageType() == StorageType.MONGODB)
        {
            return Plex.get().getMongoPlayerData().getByUUID(uuid);
        }
        else
        {
            return Plex.get().getSqlPlayerData().getByUUID(uuid);
        }
    }

    /**
     * Gets a player from cache or from the database
     *
     * @param name Username of the player
     * @return a PlexPlayer object
     * @see PlexPlayer
     */
    public static PlexPlayer getPlayer(String name)
    {
        return getPlayer(Bukkit.getPlayer(name).getUniqueId());
    }

    /**
     * Updates a player's information in the database
     *
     * @param plexPlayer The PlexPlayer to update
     * @see PlexPlayer
     */
    public static void update(PlexPlayer plexPlayer)
    {
        if (Plex.get().getStorageType() == StorageType.MONGODB)
        {
            Plex.get().getMongoPlayerData().update(plexPlayer);
        }
        else
        {
            Plex.get().getSqlPlayerData().update(plexPlayer);
        }
    }

    /**
     * Inserts a player's information in the database
     *
     * @param plexPlayer The PlexPlayer to insert
     * @see PlexPlayer
     */
    public static void insert(PlexPlayer plexPlayer)
    {
        if (Plex.get().getStorageType() == StorageType.MONGODB)
        {
            Plex.get().getMongoPlayerData().save(plexPlayer);
        }
        else
        {
            Plex.get().getSqlPlayerData().insert(plexPlayer);
        }
    }

    /*           REDIS METHODS AT ONE POINT FOR BANS, AND JSON METHODS FOR PUNISHMENTS       */

}
