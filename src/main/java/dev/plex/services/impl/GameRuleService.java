package dev.plex.services.impl;

import dev.plex.services.AbstractService;
import dev.plex.util.PlexLog;
import dev.plex.util.PlexUtils;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class GameRuleService extends AbstractService
{
    public GameRuleService()
    {
        super(false, true);
    }

    @Override
    public void run()
    {
        for (World world : Bukkit.getWorlds())
        {
            PlexUtils.commitGlobalGameRules(world);
            PlexLog.log("Set global gamerules for world: " + world.getName());
        }
        for (String world : plugin.config.getConfigurationSection("worlds").getKeys(false))
        {
            World bukkitWorld = Bukkit.getWorld(world);
            if (bukkitWorld != null)
            {
                PlexUtils.commitSpecificGameRules(bukkitWorld);
                PlexLog.log("Set specific gamerules for world: " + world.toLowerCase(Locale.ROOT));
            }
        }
    }

    @Override
    public int repeatInSeconds()
    {
        return 0;
    }
}
