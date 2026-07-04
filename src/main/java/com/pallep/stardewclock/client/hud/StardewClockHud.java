package com.pallep.stardewclock.client.hud;

import com.pallep.stardewclock.client.StardewClockClient;
import com.pallep.stardewclock.client.config.StardewClockConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Locale;

public final class StardewClockHud {
    private static final Identifier ATLAS = StardewClockClient.id("textures/gui/stardew_clock.png");
    private static final int ATLAS_WIDTH = 131;
    private static final int ATLAS_HEIGHT = 59;
    private static final int PANEL_WIDTH = 71;
    private static final int PANEL_HEIGHT = 40;
    private static final int ARROW_WIDTH = 7;
    private static final int ARROW_HEIGHT = 19;
    private static final int SMALL_ICON_WIDTH = 12;
    private static final int SMALL_ICON_HEIGHT = 8;
    private static final int TEXT_COLOR = 0xFF33223A;
    private static final int SHADOW_COLOR = 0xFFD27C;

    private StardewClockHud() {
    }

    public static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.hudHidden || client.player == null || client.world == null) {
            return;
        }

        StardewClockConfig config = StardewClockClient.CONFIG;
        int visibleRows = countRows(config);
        if (visibleRows == 0) {
            return;
        }

        int x = resolveX(context.getScaledWindowWidth(), config, PANEL_WIDTH);
        int y = resolveY(context.getScaledWindowHeight(), config, PANEL_HEIGHT);

        context.drawTexture(ATLAS, x, y, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, ATLAS_WIDTH, ATLAS_HEIGHT);
        drawClockArrow(context, client.world, x, y);
        drawPeriodSlot(context, client.world, x, y);
        drawWeatherSlot(context, client.world, x, y);

        TextRenderer textRenderer = client.textRenderer;
        if (config.showTime) {
            drawCompactText(context, textRenderer, formatTime(client.world), x + 34, y + 5.5F, 34);
        }
        if (config.showCoordinates) {
            BlockPos pos = client.player.getBlockPos();
            drawCompactText(context, textRenderer, pos.getX() + " " + pos.getY() + " " + pos.getZ(), x + 28.5F, y + 29, 37);
        }
    }

    private static int countRows(StardewClockConfig config) {
        int rows = 0;
        if (config.showTime) {
            rows++;
        }
        if (config.showCoordinates) {
            rows++;
        }
        return rows;
    }

    private static int resolveX(int screenWidth, StardewClockConfig config, int width) {
        return switch (config.anchor) {
            case TOP_LEFT, BOTTOM_LEFT -> config.offsetX;
            case TOP_RIGHT, BOTTOM_RIGHT -> screenWidth - width - config.offsetX;
        };
    }

    private static int resolveY(int screenHeight, StardewClockConfig config, int height) {
        return switch (config.anchor) {
            case TOP_LEFT, TOP_RIGHT -> config.offsetY;
            case BOTTOM_LEFT, BOTTOM_RIGHT -> screenHeight - height - config.offsetY;
        };
    }

    private static void drawPixelText(DrawContext context, TextRenderer textRenderer, String text, int x, int y) {
        context.drawText(textRenderer, Text.literal(text), x, y, TEXT_COLOR, false);
    }

    private static void drawCompactText(DrawContext context, TextRenderer textRenderer, String text, float x, float y, int maxWidth) {
        int width = textRenderer.getWidth(text);
        float scale = Math.max(0.45F, Math.min(1.0F, maxWidth / (float) width));
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0.0F);
        context.getMatrices().scale(scale, scale, 1.0F);
        context.drawText(textRenderer, Text.literal(text), 0, 0, TEXT_COLOR, false);
        context.getMatrices().pop();
    }

    private static void drawClockArrow(DrawContext context, World world, int x, int y) {
        context.getMatrices().push();
        context.getMatrices().translate(x + 21.5F, y + 20F, 0.0F);
        context.getMatrices().multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Z.rotationDegrees(arrowAngle(world)));
        context.getMatrices().translate(-3.5F, -17.5F, 0.0F);
        context.drawTexture(ATLAS, 0, 0, 72, 0, ARROW_WIDTH, ARROW_HEIGHT, ATLAS_WIDTH, ATLAS_HEIGHT);
        context.getMatrices().pop();
    }

    private static float arrowAngle(World world) {
        long ticks = Math.floorMod(world.getTimeOfDay(), 24000L);
        if (ticks >= 18000L && ticks < 23000L) {
            return 360.0F;
        }
        if (ticks >= 23000L || ticks < 4000L) {
            return 180.0F;
        }
        return 180.0F + ticks / 18000.0F * 180.0F;
    }

    private static void drawPeriodSlot(DrawContext context, World world, int x, int y) {
        switch (period(world)) {
            case MORNING -> drawAtlasIcon(context, x + 29, y + 16, 106, 9);
            case DAY -> drawAtlasIcon(context, x + 29, y + 16, 80, 9);
            case EVENING -> drawAtlasIcon(context, x + 29, y + 16, 119, 9);
            case NIGHT -> drawAtlasIcon(context, x + 29, y + 16, 93, 9);
        }
    }

    private static void drawWeatherSlot(DrawContext context, World world, int x, int y) {
        if (world.isThundering()) {
            drawAtlasIcon(context, x + 53, y + 16, 106, 0);
        } else if (world.isRaining()) {
            drawAtlasIcon(context, x + 53, y + 16, 80, 0);
        } else {
            drawAtlasIcon(context, x + 53, y + 16, 93, 0);
        }
    }

    private static void drawAtlasIcon(DrawContext context, int x, int y, int u, int v) {
        context.drawTexture(ATLAS, x, y, u, v, SMALL_ICON_WIDTH, SMALL_ICON_HEIGHT, ATLAS_WIDTH, ATLAS_HEIGHT);
    }

    private static String formatTime(World world) {
        long ticks = Math.floorMod(world.getTimeOfDay(), 24000L);
        int hour = (int) ((ticks / 1000L + 6L) % 24L);
        int minute = (int) ((ticks % 1000L) * 60L / 1000L);
        return String.format(Locale.ROOT, "%02d:%02d", hour, minute);
    }

    private static Period period(World world) {
        long ticks = Math.floorMod(world.getTimeOfDay(), 24000L);
        int hour = (int) ((ticks / 1000L + 6L) % 24L);
        if (hour >= 5 && hour < 10) {
            return Period.MORNING;
        }
        if (hour >= 10 && hour < 17) {
            return Period.DAY;
        }
        if (hour >= 17 && hour < 20) {
            return Period.EVENING;
        }
        return Period.NIGHT;
    }

    private enum Period {
        MORNING,
        DAY,
        EVENING,
        NIGHT
    }

}
