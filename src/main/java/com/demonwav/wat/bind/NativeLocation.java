package com.demonwav.wat.bind;

import com.demonwav.wat.bind.struct.StructLocation;
import org.bukkit.Location;

public final class NativeLocation {
    private NativeLocation() {}

    public static StructLocation loadLocationStruct(final Location location) {
        if (location == null) {
            return null;
        }

        final var struct = new StructLocation();

        if (location.getWorld() != null) {
            struct.setWorldId(location.getWorld().getUID());
        }
        struct.setX(location.getX());
        struct.setY(location.getY());
        struct.setZ(location.getZ());
        struct.setPitch(location.getPitch());
        struct.setYaw(location.getYaw());

        return struct;
    }
}
