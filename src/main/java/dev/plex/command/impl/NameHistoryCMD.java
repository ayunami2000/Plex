package dev.plex.command.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import dev.plex.command.PlexCommand;
import dev.plex.command.annotation.CommandParameters;
import dev.plex.command.annotation.CommandPermissions;
import dev.plex.rank.enums.Rank;
import dev.plex.util.AshconInfo;
import dev.plex.util.MojangUtils;
import dev.plex.util.PlexLog;
import dev.plex.util.PlexUtils;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@CommandParameters(name = "namehistory", description = "Get the name history of a player", usage = "/<command> <player>", aliases = "nh")
@CommandPermissions(level = Rank.OP, permission = "plex.namehistory")
public class NameHistoryCMD extends PlexCommand
{
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy 'at' hh:mm:ss a");

    @Override
    protected Component execute(@NotNull CommandSender sender, @Nullable Player playerSender, String[] args)
    {
        if (args.length != 1)
        {
            return usage(getUsage());
        }
        String username = args[0];

        AshconInfo info = MojangUtils.getInfo(username);
        if (info == null)
        {
            return tl("nameHistoryDoesntExist");
        }
        PlexLog.debug("NameHistory UUID: " + info.getUuid());
        PlexLog.debug("NameHistory Size: " + info.getUsernameHistories().length);
        List<Component> historyList = Lists.newArrayList();
        Arrays.stream(info.getUsernameHistories()).forEach(history ->
        {
            if (history.getLocalDateTime() != null)
            {
                historyList.add(
                        Component.text(history.getUsername()).color(NamedTextColor.GOLD)
                                .append(Component.space())
                                .append(Component.text("-").color(NamedTextColor.DARK_GRAY))
                                .append(Component.space())
                                .append(Component.text(DATE_FORMAT.format(history.getLocalDateTime())).color(NamedTextColor.GOLD)));
            }
            else
            {
                historyList.add(
                        Component.text(history.getUsername()).color(NamedTextColor.GOLD)
                                .append(Component.space()));
            }
        });
        send(sender, Component.text("Name History (" + username + ")").color(NamedTextColor.GOLD));
        send(sender, Component.text("-----------------------------").color(NamedTextColor.GOLD).decoration(TextDecoration.STRIKETHROUGH, true));
        historyList.forEach(component -> send(sender, component));
        return null;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException
    {
        return args.length == 1 ? PlexUtils.getPlayerNameList() : ImmutableList.of();
    }
}