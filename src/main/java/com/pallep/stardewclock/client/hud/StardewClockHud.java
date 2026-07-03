package com.pallep.stardewclock.client.hud;

import com.pallep.stardewclock.client.StardewClockClient;
import com.pallep.stardewclock.client.config.StardewClockConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

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

    private StardewClockHud() {
    }

    public static void render(GuiGraphicsExtractor context, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) {
            return;
        }

        StardewClockConfig config = StardewClockClient.CONFIG;
        if (config == null) {
            return;
        }
        int visibleRows = countRows(config);
        if (visibleRows == 0) {
            return;
        }

        int x = resolveX(context.guiWidth(), config, PANEL_WIDTH);
        int y = resolveY(context.guiHeight(), config, PANEL_HEIGHT);

        drawAtlasRegion(context, x, y, 0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        drawClockArrow(context, client.level, x, y);
        drawPeriodSlot(context, client.level, x, y);
        drawWeatherSlot(context, client.level, x, y);

        Font font = client.font;
        if (config.showTime) {
            drawCompactText(context, font, formatTime(client.level), x + 34, y + 5.5F, 34);
        }
        if (config.showCoordinates) {
            BlockPos pos = client.player.blockPosition();
            drawCompactText(context, font, pos.getX() + " " + pos.getY() + " " + pos.getZ(), x + 28.5F, y + 29, 37);
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

    private static void drawCompactText(GuiGraphicsExtractor context, Font font, String text, float x, float y, int maxWidth) {
        int width = font.width(text);
        float scale = Math.max(0.45F, Math.min(1.0F, maxWidth / (float) width));
        context.pose().pushMatrix();
        context.pose().translate(x, y);
        context.pose().scale(scale, scale);
        context.text(font, text, 0, 0, TEXT_COLOR, false);
        context.pose().popMatrix();
    }

    private static void drawClockArrow(GuiGraphicsExtractor context, ClientLevel world, int x, int y) {
        context.pose().pushMatrix();
        context.pose().translate(x + 21.5F, y + 20.0F);
        context.pose().rotate((float) Math.toRadians(arrowAngle(world)));
        context.pose().translate(-3.5F, -17.5F);
        drawAtlasRegion(context, 0, 0, 72, 0, ARROW_WIDTH, ARROW_HEIGHT);
        context.pose().popMatrix();
    }

    private static float arrowAngle(ClientLevel world) {
        long ticks = Math.floorMod(world.getOverworldClockTime(), 24000L);
        if (ticks >= 18000L && ticks < 23000L) {
            return 360.0F;
        }
        if (ticks >= 23000L || ticks < 4000L) {
            return 180.0F;
        }
        return 180.0F + ticks / 18000.0F * 180.0F;
    }

    private static void drawPeriodSlot(GuiGraphicsExtractor context, ClientLevel world, int x, int y) {
        switch (period(world)) {
            case MORNING -> drawAtlasIcon(context, x + 29, y + 16, 106, 9);
            case DAY -> drawAtlasIcon(context, x + 29, y + 16, 80, 9);
            case EVENING -> drawAtlasIcon(context, x + 29, y + 16, 119, 9);
            case NIGHT -> drawAtlasIcon(context, x + 29, y + 16, 93, 9);
        }
    }

    private static void drawWeatherSlot(GuiGraphicsExtractor context, ClientLevel world, int x, int y) {
        if (world.isThundering()) {
            drawAtlasIcon(context, x + 53, y + 16, 106, 0);
        } else if (world.isRaining()) {
            drawAtlasIcon(context, x + 53, y + 16, 80, 0);
        } else {
            drawAtlasIcon(context, x + 53, y + 16, 93, 0);
        }
    }

    private static void drawAtlasIcon(GuiGraphicsExtractor context, int x, int y, int u, int v) {
        drawAtlasRegion(context, x, y, u, v, SMALL_ICON_WIDTH, SMALL_ICON_HEIGHT);
    }

    private static void drawAtlasRegion(GuiGraphicsExtractor context, int x, int y, int u, int v, int width, int height) {
        context.blit(RenderPipelines.GUI_TEXTURED, ATLAS, x, y, u, v, width, height, ATLAS_WIDTH, ATLAS_HEIGHT);
    }

    private static String formatTime(ClientLevel world) {
        long ticks = Math.floorMod(world.getOverworldClockTime(), 24000L);
        int hour = (int) ((ticks / 1000L + 6L) % 24L);
        int minute = (int) ((ticks % 1000L) * 60L / 1000L);
        return String.format(Locale.ROOT, "%02d:%02d", hour, minute);
    }

    private static Period period(ClientLevel world) {
        long ticks = Math.floorMod(world.getOverworldClockTime(), 24000L);
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
