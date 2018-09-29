package com.demonwav.wat.event;

import com.demonwav.wat.bind.struct.StructPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static com.demonwav.wat.bind.NativePlayer.loadPlayerStruct;

public final class PlayerJoinListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) throws Exception {
        onPlayerJoin0(loadPlayerStruct(event.getPlayer()), event.getJoinMessage());
    }

    private static native void onPlayerJoin0(final StructPlayer player, final String message) throws Exception;
}
