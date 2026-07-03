package com.pallep.stardewclock.client;

import com.pallep.stardewclock.client.config.StardewClockConfig;
import com.pallep.stardewclock.client.hud.StardewClockHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.util.Identifier;

public class StardewClockClient implements ClientModInitializer {
    public static final String MOD_ID = "stardewclock";
    public static StardewClockConfig CONFIG;

    @Override
    public void onInitializeClient() {
        CONFIG = StardewClockConfig.load();
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> StardewClockHud.render(drawContext));
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
