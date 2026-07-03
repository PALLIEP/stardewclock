package com.pallep.stardewclock.client.integration;

import com.pallep.stardewclock.client.gui.StardewClockConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class StardewClockModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return StardewClockConfigScreen::new;
    }
}
