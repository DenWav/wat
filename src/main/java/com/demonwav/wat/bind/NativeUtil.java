package com.demonwav.wat.bind;

import java.nio.ByteBuffer;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class NativeUtil {
    private NativeUtil() {}

    public static byte[] getBytesFromUUID(final UUID uuid) {
        final var bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return bb.array();
    }

    public static UUID getUUIDFromBytes(final byte[] bytes) {
        final var byteBuffer = ByteBuffer.wrap(bytes);
        final long high = byteBuffer.getLong();
        final long low = byteBuffer.getLong();

        return new UUID(high, low);
    }

    public static Player getPlayer(final byte[] uuidBytes) {
        final var uuid = getUUIDFromBytes(uuidBytes);
        return Bukkit.getPlayer(uuid);
    }
}
