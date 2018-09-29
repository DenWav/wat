package com.demonwav.wat.bind;

import com.demonwav.wat.bind.struct.StructPlayer;
import org.bukkit.entity.Player;

import static com.demonwav.wat.bind.NativeLocation.loadLocationStruct;

public final class NativePlayer {
    private NativePlayer() {}

    public static StructPlayer loadPlayerStruct(final Player player) {
        if (player == null) {
            return null;
        }

        final var struct = new StructPlayer();

        struct.setDisplayName(player.getDisplayName());
        struct.setPlayerListName(player.getPlayerListName());
        struct.setCompassTarget(loadLocationStruct(player.getCompassTarget()));
        struct.setSneaking(player.isSneaking());
        struct.setSprinting(player.isSprinting());
        struct.setSleepingIgnored(player.isSleepingIgnored());
        struct.setTotalExperience(player.getTotalExperience());
        struct.setLevel(player.getLevel());
        struct.setSaturation(player.getSaturation());
        struct.setFoodLevel(player.getFoodLevel());
        struct.setBedSpawnLocation(loadLocationStruct(player.getBedSpawnLocation()));
        struct.setUuid(player.getUniqueId());

        return struct;
    }

    public static void sendRawMessage(final byte[] uuid, final String message) {
        NativeUtil.getPlayer(uuid).sendRawMessage(message);
    }

    public static void kick(final byte[] uuid, final String message) {
        NativeUtil.getPlayer(uuid).kickPlayer(message);
    }

    public static void saveData(final byte[] uuid) {
        NativeUtil.getPlayer(uuid).saveData();
    }

    public static void loadData(final byte[] uuid) {
        NativeUtil.getPlayer(uuid).loadData();
    }
}
