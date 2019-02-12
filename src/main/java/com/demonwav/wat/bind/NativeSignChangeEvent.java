package com.demonwav.wat.bind;

import com.demonwav.wat.bind.struct.StructSignChangeEvent;
import org.bukkit.event.block.SignChangeEvent;

public final class NativeSignChangeEvent {
    private NativeSignChangeEvent() {}

    public static StructSignChangeEvent loadSignChangeEventStruct(final SignChangeEvent event) {
        if (event == null) {
            return null;
        }

        final var struct = new StructSignChangeEvent();

        struct.setPlayer(NativePlayer.loadPlayerStruct(event.getPlayer()));
        struct.setLines(event.getLines());
        struct.setCancel(event.isCancelled());

        return struct;
    }
}
