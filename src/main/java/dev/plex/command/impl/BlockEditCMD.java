package dev.plex.command.impl;

import dev.plex.command.PlexCommand;
import dev.plex.command.annotation.CommandParameters;
import dev.plex.command.annotation.CommandPermissions;
import dev.plex.listener.impl.BlockListener;
import dev.plex.rank.enums.Rank;
import dev.plex.util.PlexUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@CommandPermissions(level = Rank.ADMIN, permission = "plex.blockedit")
@CommandParameters(name = "blockedit", usage = "/<command> [list | purge | all | <player>]", aliases = "bedit", description = "Prevent players from modifying blocks")
public class BlockEditCMD extends PlexCommand
{
    private final BlockListener bl = new BlockListener();

    @Override
    protected Component execute(@NotNull CommandSender sender, @Nullable Player playerSender, @NotNull String[] args)
    {
        if (args.length == 0)
        {
            return usage();
        }

        if (args[0].equalsIgnoreCase("list"))
        {
            send(sender, "The following have block modification abilities restricted:");
            int count = 0;
            for (String player : bl.blockedPlayers.stream().toList())
            {
                send(sender, "- " + player);
                ++count;
            }
            if (count == 0)
            {
                send(sender, "- none");
            }
            return null;
        }
        else if (args[0].equalsIgnoreCase("purge"))
        {
            PlexUtils.broadcast(componentFromString(sender.getName() + " - Unblocking block modification abilities for all players").color(NamedTextColor.AQUA));
            int count = 0;
            for (String player : bl.blockedPlayers.stream().toList())
            {
                if (bl.blockedPlayers.contains(player))
                {
                    bl.blockedPlayers.remove(player);
                    ++count;
                }
            }
            return messageComponent("unblockedEditsSize", count);
        }
        else if (args[0].equalsIgnoreCase("all"))
        {
            PlexUtils.broadcast(componentFromString(sender.getName() + " - Blocking block modification abilities for all non-admins").color(NamedTextColor.RED));
            int count = 0;
            for (final Player player : Bukkit.getOnlinePlayers())
            {
                if (!silentCheckRank(player, Rank.ADMIN, "plex.blockedit"))
                {
                    bl.blockedPlayers.add(player.getName());
                    ++count;
                }
            }

            return messageComponent("blockedEditsSize", count);
        }

        final Player player = getNonNullPlayer(args[0]);
        if (!bl.blockedPlayers.contains(player.getName()))
        {
            if (silentCheckRank(player, Rank.ADMIN, "plex.blockedit"))
            {
                send(sender, messageComponent("higherRankThanYou"));
                return null;
            }
            PlexUtils.broadcast(messageComponent("blockingEditFor", sender.getName(), player.getName()));
            bl.blockedPlayers.add(player.getName());
            send(player, messageComponent("yourEditsHaveBeenBlocked"));
            send(sender, messageComponent("editsBlocked", player.getName()));
        }
        else
        {
            PlexUtils.broadcast(messageComponent("unblockingEditFor", sender.getName(), player.getName()));
            bl.blockedPlayers.remove(player.getName());
            send(player, messageComponent("yourEditsHaveBeenUnblocked"));
            send(sender, messageComponent("editsUnblocked", player.getName()));
        }
        return null;
    }
}
