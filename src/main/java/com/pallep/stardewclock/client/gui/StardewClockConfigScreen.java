package com.pallep.stardewclock.client.gui;

import com.pallep.stardewclock.client.StardewClockClient;
import com.pallep.stardewclock.client.config.StardewClockConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class StardewClockConfigScreen extends Screen {
    private final Screen parent;
    private final StardewClockConfig config;

    public StardewClockConfigScreen(Screen parent) {
        super(Text.translatable("screen.stardewclock.config"));
        this.parent = parent;
        this.config = StardewClockClient.CONFIG;
    }

    @Override
    protected void init() {
        int center = width / 2;
        int y = height / 6;
        addDrawableChild(ButtonWidget.builder(toggleText("show_time", config.showTime), button -> {
            config.showTime = !config.showTime;
            button.setMessage(toggleText("show_time", config.showTime));
        }).dimensions(center - 155, y, 150, 20).build());
        addDrawableChild(ButtonWidget.builder(toggleText("show_coordinates", config.showCoordinates), button -> {
            config.showCoordinates = !config.showCoordinates;
            button.setMessage(toggleText("show_coordinates", config.showCoordinates));
        }).dimensions(center + 5, y, 150, 20).build());

        y += 24;
        addDrawableChild(ButtonWidget.builder(Text.translatable(config.anchor.translationKey()), button -> {
            config.anchor = config.anchor.next();
            button.setMessage(Text.translatable(config.anchor.translationKey()));
        }).dimensions(center - 155, y, 310, 20).build());

        y += 28;
        addDrawableChild(new OffsetSlider(center - 155, y, 310, 20, true));
        y += 24;
        addDrawableChild(new OffsetSlider(center - 155, y, 310, 20, false));

        y += 34;
        addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> close()).dimensions(center - 100, y, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 20, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        config.save();
        client.setScreen(parent);
    }

    private Text toggleText(String key, boolean enabled) {
        return Text.translatable("option.stardewclock." + key)
                .append(": ")
                .append(enabled ? ScreenTexts.ON : ScreenTexts.OFF);
    }

    private final class OffsetSlider extends SliderWidget {
        private final boolean xOffset;

        private OffsetSlider(int x, int y, int width, int height, boolean xOffset) {
            super(x, y, width, height, Text.empty(), (xOffset ? config.offsetX : config.offsetY) / 200.0D);
            this.xOffset = xOffset;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            int value = (int) Math.round(this.value * 200.0D);
            setMessage(Text.translatable("option.stardewclock." + (xOffset ? "offset_x" : "offset_y")).append(": " + value));
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
