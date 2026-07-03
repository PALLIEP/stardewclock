package com.pallep.stardewclock.client.gui;

import com.pallep.stardewclock.client.StardewClockClient;
import com.pallep.stardewclock.client.config.StardewClockConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class StardewClockConfigScreen extends Screen {
    private final Screen parent;
    private final StardewClockConfig config;

    public StardewClockConfigScreen(Screen parent) {
        super(Component.translatable("screen.stardewclock.config"));
        this.parent = parent;
        this.config = StardewClockClient.CONFIG;
    }

    @Override
    protected void init() {
        int center = width / 2;
        int y = height / 6;
        addRenderableWidget(Button.builder(toggleText("show_time", config.showTime), button -> {
            config.showTime = !config.showTime;
            button.setMessage(toggleText("show_time", config.showTime));
        }).bounds(center - 155, y, 150, 20).build());
        addRenderableWidget(Button.builder(toggleText("show_coordinates", config.showCoordinates), button -> {
            config.showCoordinates = !config.showCoordinates;
            button.setMessage(toggleText("show_coordinates", config.showCoordinates));
        }).bounds(center + 5, y, 150, 20).build());

        y += 24;
        addRenderableWidget(Button.builder(Component.translatable(config.anchor.translationKey()), button -> {
            config.anchor = config.anchor.next();
            button.setMessage(Component.translatable(config.anchor.translationKey()));
        }).bounds(center - 155, y, 310, 20).build());

        y += 28;
        addRenderableWidget(new OffsetSlider(center - 155, y, 310, 20, true));
        y += 24;
        addRenderableWidget(new OffsetSlider(center - 155, y, 310, 20, false));

        y += 34;
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose()).bounds(center - 100, y, 200, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0x80000000);
        context.centeredText(font, title, width / 2, 20, 0xFFFFFF);
        super.extractRenderState(context, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        config.save();
        minecraft.setScreenAndShow(parent);
    }

    private Component toggleText(String key, boolean enabled) {
        return Component.translatable("option.stardewclock." + key)
                .append(": ")
                .append(enabled ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF);
    }

    private final class OffsetSlider extends AbstractSliderButton {
        private final boolean xOffset;

        private OffsetSlider(int x, int y, int width, int height, boolean xOffset) {
            super(x, y, width, height, Component.empty(), (xOffset ? config.offsetX : config.offsetY) / 200.0D);
            this.xOffset = xOffset;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            int value = (int) Math.round(this.value * 200.0D);
            setMessage(Component.translatable("option.stardewclock." + (xOffset ? "offset_x" : "offset_y")).append(": " + value));
        }

        @Override
        protected void applyValue() {
            int value = (int) Math.round(this.value * 200.0D);
            if (xOffset) {
                config.offsetX = value;
            } else {
                config.offsetY = value;
            }
        }
    }
}
