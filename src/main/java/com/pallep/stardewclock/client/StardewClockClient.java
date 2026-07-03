package com.pallep.stardewclock.client;

import com.pallep.stardewclock.client.config.StardewClockConfig;
import com.pallep.stardewclock.client.hud.StardewClockHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public class StardewClockClient implements ClientModInitializer {
    public static final String MOD_ID = "stardewclock";
    public static StardewClockConfig CONFIG;

    @Override
    public void onInitializeClient() {
        CONFIG = StardewClockConfig.load();
        HudElementRegistry.attachElementAfter(VanillaHudElements.BOSS_BAR, id("hud/stardew_clock"), StardewClockHud::render);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
