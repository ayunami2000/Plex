package dev.plex.rank.enums;

import com.google.common.collect.Lists;
import java.util.List;
import org.bukkit.ChatColor;

public enum Rank
{
    IMPOSTOR(-1, ChatColor.AQUA + "an " + ChatColor.YELLOW + "Impostor", "Impostor", ChatColor.YELLOW + "[Imp]"),
    NONOP(0, "a " + ChatColor.WHITE + "Non-Op", "Non-Op", ChatColor.WHITE + ""),
    OP(1, "an " + ChatColor.GREEN + "Operator", "Operator", ChatColor.GREEN + "[OP]"),
    ADMIN(2, "an " + ChatColor.DARK_GREEN + "Admin", "Admin", ChatColor.DARK_GREEN + "[Admin]"),
    SENIOR_ADMIN(3, "a " + ChatColor.GOLD + "Senior Admin", "Senior Admin", ChatColor.GOLD + "[SrA]"),
    EXECUTIVE(4, "an " + ChatColor.RED + "Executive", "Executive", ChatColor.RED + "[Exec]");

    private final int level;
    private String loginMessage;
    private String readable;
    private String prefix;
    private List<String> permissions;

    Rank(int level, String loginMessage, String readable, String prefix)
    {
        this.level = level;
        this.loginMessage = loginMessage;
        this.readable = readable;
        this.prefix = prefix;
        this.permissions = Lists.newArrayList();
    }

    public String getPrefix()
    {
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public String getLoginMSG()
    {
        return ChatColor.translateAlternateColorCodes('&', loginMessage);
    }

    public int getLevel()
    {
        return level;
    }

    public String getReadableString()
    {
        return readable;
    }

    public void setLoginMessage(String msg)
    {
        this.loginMessage = msg;
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    public void setHumanReadableString(String readable)
    {
        this.readable = readable;
    }

    public boolean isAtLeast(Rank rank)
    {
        return getLevel() >= rank.getLevel();
    }

    public List<String> getPermissions()
    {
        return permissions;
    }

    public void setPermissions(List<String> permissions)
    {
        this.permissions = permissions;
    }
}
