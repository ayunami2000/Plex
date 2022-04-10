package dev.plex.player;

import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import dev.plex.Plex;
import dev.plex.punishment.Punishment;
import dev.plex.punishment.extra.Note;
import dev.plex.rank.enums.Rank;
import dev.plex.storage.StorageType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import dev.plex.util.adapter.LocalDateTimeSerializer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@Getter
@Setter
@Entity(value = "players", useDiscriminator = false)
public class PlexPlayer
{
    @Setter(AccessLevel.NONE)
    @Id
    private String id;

    @Setter(AccessLevel.NONE)
    @Indexed(options = @IndexOptions(unique = true))
    private UUID uuid;

    @Indexed
    private String name;
    private transient Player player;

    private String loginMessage;
    private String prefix;

    private boolean vanished;
    private boolean commandSpy;

    // These fields are transient so MongoDB doesn't automatically drop them in.
    private transient boolean frozen;
    private transient boolean muted;
    private transient boolean lockedUp;

    private boolean adminActive;

    private long coins;

    private String rank;

    private List<String> ips = Lists.newArrayList();
    private List<Punishment> punishments = Lists.newArrayList();
    private List<Note> notes = Lists.newArrayList();

    public PlexPlayer()
    {
    }

    public PlexPlayer(UUID playerUUID)
    {
        this.uuid = playerUUID;

        this.id = uuid.toString().substring(0, 8);

        this.name = "";
        this.player = Bukkit.getPlayer(name);

        this.loginMessage = "";
        this.prefix = "";

        this.vanished = false;
        this.commandSpy = false;

        this.coins = 0;

        this.rank = "";
        this.loadPunishments();
    }

    public String displayName()
    {
        return PlainTextComponentSerializer.plainText().serialize(player.displayName());
    }

    public Rank getRankFromString()
    {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if (rank.isEmpty() || !isAdminActive())
        {
            if (player.isOp())
            {
                return Rank.OP;
            }
            else
            {
                return Rank.NONOP;
            }
        }
        else
        {
            return Rank.valueOf(rank.toUpperCase());
        }
    }

    public void loadPunishments()
    {
        if (Plex.get().getStorageType() != StorageType.MONGODB)
        {
            this.setPunishments(Plex.get().getSqlPunishment().getPunishments(this.getUuid()).stream().filter(punishment -> punishment.getPunished().equals(this.getUuid())).collect(Collectors.toList()));
        }
    }

    public CompletableFuture<List<Note>> loadNotes()
    {
        if (Plex.get().getStorageType() != StorageType.MONGODB)
        {
            return Plex.get().getSqlNotes().getNotes(this.getUuid());
        }
        return null;
    }

    public String toJSON()
    {
        return new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer()).create().toJson(this);
    }
}