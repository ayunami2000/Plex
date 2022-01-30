package dev.plex.command.impl;

import dev.plex.command.PlexCommand;
import dev.plex.command.annotation.CommandParameters;
import dev.plex.command.annotation.CommandPermissions;
import dev.plex.rank.enums.Rank;
import dev.plex.util.PlexUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandParameters(name = "opall", description = "Op everyone on the server", aliases = "opa")
@CommandPermissions(level = Rank.NONOP)
public class OpAllCMD extends PlexCommand
{
    @Override
    public Component execute(CommandSender sender, String[] args)
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            player.setOp(true);
        }
        PlexUtils.broadcast(tl("oppedAllPlayers", sender.getName()));
        return null;
    }

}