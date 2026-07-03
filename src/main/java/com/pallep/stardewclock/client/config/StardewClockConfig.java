package com.pallep.stardewclock.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class StardewClockConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "stardewclock.json";

    public boolean showTime = true;
    public boolean showCoordinates = true;
    public Anchor anchor = Anchor.TOP_RIGHT;
    public int offsetX = 8;
    public int offsetY = 8;

    public static StardewClockConfig load() {
        Path path = configPath();
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                StardewClockConfig config = GSON.fromJson(reader, StardewClockConfig.class);
                if (config != null) {
                    config.clamp();
                    return config;
                }
            } catch (IOException | RuntimeException ignored) {
            }
        }

        StardewClockConfig config = new StardewClockConfig();
        config.save();
        return config;
    }

    public void save() {
        clamp();
        try {
            Files.createDirectories(configPath().getParent());
            try (Writer writer = Files.newBufferedWriter(configPath())) {
                GSON.toJson(this, writer);
            }
        } catch (IOException ignored) {
        }
    }

    public void clamp() {
        if (anchor == null) {
            anchor = Anchor.TOP_RIGHT;
        }
        offsetX = Math.max(0, Math.min(200, offsetX));
        offsetY = Math.max(0, Math.min(200, offsetY));
    }

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    public enum Anchor {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT;

        public Anchor next() {
            Anchor[] values = values();
            return values[(ordinal() + 1) % values.length];
        }

        public String translationKey() {
            return "stardewclock.anchor." + name().toLowerCase(Locale.ROOT);
        }
    }
}
