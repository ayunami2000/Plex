package dev.plex.cache;

import com.google.common.collect.Maps;
import dev.plex.player.PlexPlayer;
import dev.plex.player.PunishedPlayer;
import java.util.Map;
import java.util.UUID;

/**
 * Cache storage
 */

public class PlayerCache
{
    /**
     * A key/value pair where the key is the unique ID of the Plex Player
     */
    private static final Map<UUID, PlexPlayer> plexPlayerMap = Maps.newHashMap();

    /**
     * A key/value pair where the key is the unique ID of the Punished Player
     */
    private static final Map<UUID, PunishedPlayer> punishedPlayerMap = Maps.newHashMap();

    public static Map<UUID, PunishedPlayer> getPunishedPlayerMap()
    {
        return punishedPlayerMap;
    }

    public static Map<UUID, PlexPlayer> getPlexPlayerMap()
    {
        return plexPlayerMap;
    }

    public static PunishedPlayer getPunishedPlayer(UUID uuid)
    {
        return getPunishedPlayerMap().get(uuid);
    }

    public static PlexPlayer getPlexPlayer(UUID uuid)
    {
        return getPlexPlayerMap().get(uuid);
    }
}
