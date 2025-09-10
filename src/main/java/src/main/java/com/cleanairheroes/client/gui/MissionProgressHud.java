package com.cleanairheroes.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MissionProgressHud {
    
    private static final int HUD_WIDTH = 200;
    private static final int HUD_HEIGHT = 80;
    private static final int MARGIN = 10;
    
    // Sample data - would come from actual mission system
    private String currentMissionName = "Port Cleanup";
    private int currentProgress = 60;
    private int maxProgress = 100;
    private boolean showHud = true;
    
    public void render(DrawContext drawContext, int screenWidth, int screenHeight) {
        if (!showHud) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        
        // Position in top-left corner
        int x = MARGIN;
        int y = MARGIN;
        
        // Render background panel
        drawContext.fill(x, y, x + HUD_WIDTH, y + HUD_HEIGHT, 0xAA000000);
        drawContext.drawBorder(x, y, HUD_WIDTH, HUD_HEIGHT, 0xFF4A90E2);
        
        // Title
        drawContext.drawText(client.textRenderer,
            Text.literal("🎯 Current Mission").formatted(Formatting.GOLD, Formatting.BOLD),
            x + 5, y + 5, 0xFFFFFF, true);
        
        // Mission name
        drawContext.drawText(client.textRenderer,
            Text.literal(currentMissionName).formatted(Formatting.WHITE),
            x + 5, y + 18, 0xFFFFFF, true);
        
        // Progress text
        drawContext.drawText(client.textRenderer,
            Text.literal("Progress: " + currentProgress + "/" + maxProgress).formatted(Formatting.YELLOW),
            x + 5, y + 30, 0xFFFFFF, true);
        
        // Progress bar
        int barWidth = HUD_WIDTH - 20;
        int barHeight = 8;
        int barX = x + 10;
        int barY = y + 45;
        
        // Background
        drawContext.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
        
        // Progress fill
        double progressRatio = (double) currentProgress / maxProgress;
        int fillWidth = (int) (barWidth * progressRatio);
        int fillColor = progressRatio >= 1.0 ? 0xFF00FF00 : 0xFF4A90E2;
        
        drawContext.fill(barX, barY, barX + fillWidth, barY + barHeight, fillColor);
        
        // Percentage text
        int percentage = (int) (progressRatio * 100);
        String percentText = percentage + "%";
        int textWidth = client.textRenderer.getWidth(percentText);
        drawContext.drawText(client.textRenderer,
            Text.literal(percentText).formatted(Formatting.WHITE),
            barX + (barWidth - textWidth) / 2, y + 58, 0xFFFFFF, true);
        
        // Mini instructions
        drawContext.drawText(client.textRenderer,
            Text.literal("Press M for details").formatted(Formatting.GRAY),
            x + 5, y + HUD_HEIGHT - 12, 0xAAAAAAA, false);
    }
    
    public void updateMission(String missionName, int progress, int maxProgress) {
        this.currentMissionName = missionName;
        this.currentProgress = progress;
        this.maxProgress = maxProgress;
    }
    
    public void setVisible(boolean visible) {
        this.showHud = visible;
    }
    
    public boolean isVisible() {
        return showHud;
    }
}