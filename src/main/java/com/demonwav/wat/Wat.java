package com.demonwav.wat;

import com.demonwav.wat.bind.NativeInit;
import com.demonwav.wat.bind.NativeLogger;
import com.demonwav.wat.event.PlayerJoinListener;
import org.apache.commons.lang.SystemUtils;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public final class Wat extends JavaPlugin implements Listener {

    private static final Pattern libMatcher;

    static {
        if (SystemUtils.IS_OS_WINDOWS) {
            libMatcher = Pattern.compile(".+\\.dll");
        } else if (SystemUtils.IS_OS_LINUX) {
            libMatcher = Pattern.compile("lib.+\\.so");
        } else if (SystemUtils.IS_OS_MAC) {
            libMatcher = Pattern.compile("lib.+\\.dylib");
        } else {
            throw new RuntimeException("Unsupported operating system: " + SystemUtils.OS_NAME);
        }
    }

    @Override
    public void onEnable() {
        Instance.instance = this;
        NativeLogger.initLoggerInstance(getLogger());

        final var pluginsDir = getDataFolder().toPath().resolve("plugins");
        if (!Files.exists(pluginsDir)) {
            NativeInit.init(new String[0]);
        } else {
            final String[] plugins;
            try {
                plugins = Files.list(pluginsDir)
                        .filter(f -> libMatcher.matcher(f.getFileName().toString()).matches())
                        .map(f -> f.toAbsolutePath().toString())
                        .toArray(String[]::new);
            } catch (final IOException e) {
                throw new RuntimeException("Could not collect plugins in " + pluginsDir, e);
            }

            NativeInit.init(plugins);
        }

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
    }

    @Override
    public void onDisable() {
        NativeInit.teardown();
        NativeLogger.closeLoggerInstance();
    }

    public static final class Instance {
        private static Wat instance = null;
        public static Wat get() {
            return instance;
        }
    }
}
