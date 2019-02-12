package com.demonwav.wat.event;

import com.demonwav.wat.bind.struct.StructSignChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import static com.demonwav.wat.bind.NativeSignChangeEvent.loadSignChangeEventStruct;

public final class SignChangeListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChange(final SignChangeEvent event) throws Exception {
        final StructSignChangeEvent res = onSignChange0(loadSignChangeEventStruct(event));
        if (res.getLines() != null) {
            final String[] lines = event.getLines();
            System.arraycopy(res.getLines(), 0, lines, 0, lines.length);
        }
        event.setCancelled(res.isCancel());
    }

    private static native StructSignChangeEvent onSignChange0(final StructSignChangeEvent event) throws Exception;
}
