package com.demonwav.wat.bind;

import com.demonwav.wat.Wat;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.apache.commons.lang.SystemUtils;

/**
 * This class simply exists for any classes containing native methods to call this in their {@literal <clinit>} so class load order doesn't matter.
 */
final class NativeLoader {

    static {
        final Path outputPath;
        final InputStream source;

        final var dir = Wat.Instance.get().getDataFolder().toPath();
        try {
            Files.createDirectories(dir);
        } catch (final IOException e) {
            throw new LinkageError("Failed to create plugin directory " + dir, e);
        }

        if (SystemUtils.IS_OS_WINDOWS) {
            outputPath = dir.resolve("wat.dll");
            source = NativeInit.class.getResourceAsStream("/wat.dll");
        } else if (SystemUtils.IS_OS_LINUX) {
            outputPath = dir.resolve("libwat.so");
            source = NativeInit.class.getResourceAsStream("/libwat.so");
        } else if (SystemUtils.IS_OS_MAC) {
            outputPath = dir.resolve("libwat.dylib");
            source = NativeInit.class.getResourceAsStream("/libwat.dylib");
        } else {
            throw new LinkageError("Unsupported operating system: " + SystemUtils.OS_NAME);
        }

        try (source) {
            Files.copy(source, outputPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            throw new LinkageError("Failed to extract native library", e);
        }

        System.load(outputPath.toAbsolutePath().toString());
    }

    private NativeLoader() {}

    static void init() {}
}
