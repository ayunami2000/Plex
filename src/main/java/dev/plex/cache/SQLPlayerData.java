package dev.plex.cache;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import dev.plex.Plex;
import dev.plex.player.PlexPlayer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

/**
 * SQL fetching utilities for players
 */
public class SQLPlayerData
{
    private final String SELECT = "SELECT * FROM `players` WHERE uuid=?";
    private final String UPDATE = "UPDATE `players` SET name=?, login_msg=?, prefix=?, rank=?, ips=?, coins=?, vanished=?, commandspy=? WHERE uuid=?";
    private final String INSERT = "INSERT INTO `players` (`uuid`, `name`, `login_msg`, `prefix`, `rank`, `ips`, `coins`, `vanished`, `commandspy`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

    /**
     * Checks if a player exists in the SQL database
     *
     * @param uuid The unique ID of the player
     * @return true if the player was found in the database
     */
    public boolean exists(UUID uuid)
    {
        try (Connection con = Plex.get().getSqlConnection().getCon())
        {
            PreparedStatement statement = con.prepareStatement(SELECT);
            statement.setString(1, uuid.toString());
            ResultSet set = statement.executeQuery();
            return set.next();
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return false;
    }

    /**
     * Gets the player from cache or from the SQL database
     *
     * @param uuid The unique ID of the player
     * @return a PlexPlayer object
     * @see PlexPlayer
     */
    public PlexPlayer getByUUID(UUID uuid)
    {
        if (PlayerCache.getPlexPlayerMap().containsKey(uuid))
        {
            return PlayerCache.getPlexPlayerMap().get(uuid);
        }

        try (Connection con = Plex.get().getSqlConnection().getCon())
        {
            PreparedStatement statement = con.prepareStatement(SELECT);
            statement.setString(1, uuid.toString());
            ResultSet set = statement.executeQuery();
            PlexPlayer plexPlayer = new PlexPlayer(uuid);
            while (set.next())
            {
                String name = set.getString("name");
                String loginMSG = set.getString("login_msg");
                String prefix = set.getString("prefix");
                String rankName = set.getString("rank").toUpperCase();
                long coins = set.getLong("coins");
                boolean vanished = set.getBoolean("vanished");
                boolean commandspy = set.getBoolean("commandspy");
                List<String> ips = new Gson().fromJson(set.getString("ips"), new TypeToken<List<String>>()
                {
                }.getType());
                plexPlayer.setName(name);
                plexPlayer.setLoginMSG(loginMSG);
                plexPlayer.setPrefix(prefix);
                plexPlayer.setRank(rankName);
                plexPlayer.setIps(ips);
                plexPlayer.setCoins(coins);
                plexPlayer.setVanished(vanished);
                plexPlayer.setCommandSpy(commandspy);
            }
            return plexPlayer;
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return null;
    }

    /**
     * Updates a player's information in the SQL database
     *
     * @param player The PlexPlayer object
     * @see PlexPlayer
     */
    public void update(PlexPlayer player)
    {
        try (Connection con = Plex.get().getSqlConnection().getCon())
        {
            PreparedStatement statement = con.prepareStatement(UPDATE);
            statement.setString(1, player.getName());
            statement.setString(2, player.getLoginMSG());
            statement.setString(3, player.getPrefix());
            statement.setString(4, player.getRank().toLowerCase());
            statement.setString(5, new Gson().toJson(player.getIps()));
            statement.setLong(6, player.getCoins());
            statement.setBoolean(7, player.isVanished());
            statement.setBoolean(8, player.isCommandSpy());
            statement.setString(9, player.getUuid());
            statement.executeUpdate();
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }

    /**
     * Inserts the player's information in the database
     *
     * @param player The PlexPlayer object
     * @see PlexPlayer
     */
    public void insert(PlexPlayer player)
    {
        try (Connection con = Plex.get().getSqlConnection().getCon())
        {
            PreparedStatement statement = con.prepareStatement(INSERT);
            statement.setString(1, player.getUuid());
            statement.setString(2, player.getName());
            statement.setString(3, player.getLoginMSG());
            statement.setString(4, player.getPrefix());
            statement.setString(5, player.getRank().toLowerCase());
            statement.setString(6, new Gson().toJson(player.getIps()));
            statement.setLong(7, player.getCoins());
            statement.setBoolean(8, player.isVanished());
            statement.setBoolean(9, player.isCommandSpy());
            statement.execute();
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }
}
