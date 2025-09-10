package com.cleanairheroes.client.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.client.MinecraftClient;

public class MissionScreen extends Screen {
    
    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 300;
    
    private int panelX;
    private int panelY;
    
    // Sample mission data - would come from mission system
    private final MissionDisplayData[] missions = {
        new MissionDisplayData("Port Cleanup", "Install ship emission filters", 60, 100, true),
        new MissionDisplayData("Green Transport", "Build electric bus network", 0, 100, false),
        new MissionDisplayData("Smart Heating", "Convert to renewable energy", 0, 100, false),
        new MissionDisplayData("Industrial Filters", "Upgrade factory systems", 0, 100, false)
    };
    
    private int currentRegionScore = 60;
    private String currentRegion = "Varna, Bulgaria";
    
    public MissionScreen() {
        super(Text.literal("Clean Air Heroes - Missions"));
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Calculate panel position (centered)
        panelX = (width - PANEL_WIDTH) / 2;
        panelY = (height - PANEL_HEIGHT) / 2;
        
        // Close button
        addDrawableChild(ButtonWidget.builder(Text.literal("Close"), button -> close())
            .dimensions(panelX + PANEL_WIDTH - 60, panelY + PANEL_HEIGHT - 30, 50, 20)
            .build());
        
        // Mission action buttons
        for (int i = 0; i < missions.length; i++) {
            MissionDisplayData mission = missions[i];
            int buttonY = panelY + 80 + (i * 35);
            
            if (mission.isActive && mission.progress < 100) {
                addDrawableChild(ButtonWidget.builder(Text.literal("Continue"), button -> {
                    // Start/continue mission
                    close();
                    if (client != null && client.player != null) {
                        client.player.sendMessage(Text.literal("Continuing mission: " + mission.name)
                            .formatted(Formatting.GREEN), false);
                    }
                }).dimensions(panelX + PANEL_WIDTH - 80, buttonY, 60, 20).build());
            } else if (!mission.isActive && i > 0) {
                // Check if previous mission is complete
                if (missions[i-1].progress >= 100) {
                    addDrawableChild(ButtonWidget.builder(Text.literal("Start"), button -> {
                        // Start new mission
                        mission.isActive = true;
                        init(); // Refresh screen
                    }).dimensions(panelX + PANEL_WIDTH - 80, buttonY, 60, 20).build());
                }
            }
        }
        
        // Travel button (if region score is high enough)
        if (currentRegionScore >= 70) {
            addDrawableChild(ButtonWidget.builder(Text.literal("🚢 Travel to Next Region"), button -> {
                close();
                if (client != null && client.player != null) {
                    client.player.networkHandler.sendCommand("cleanair travel");
                }
            }).dimensions(panelX + 10, panelY + PANEL_HEIGHT - 30, 150, 20).build());
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render background
        renderBackground(context, mouseX, mouseY, delta);
        
        // Render panel background
        context.fill(panelX, panelY, panelX + PANEL_WIDTH, panelY + PANEL_HEIGHT, 0xCC000000);
        context.drawBorder(panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, 0xFF4A90E2);
        
        // Render title
        context.drawCenteredTextWithShadow(textRenderer, 
            Text.literal("🌍 CLEAN AIR HEROES").formatted(Formatting.GOLD, Formatting.BOLD),
            panelX + PANEL_WIDTH / 2, panelY + 10, 0xFFFFFF);
        
        // Render current region info
        context.drawCenteredTextWithShadow(textRenderer,
            Text.literal("Region: " + currentRegion).formatted(Formatting.AQUA),
            panelX + PANEL_WIDTH / 2, panelY + 25, 0xFFFFFF);
        
        context.drawCenteredTextWithShadow(textRenderer,
            Text.literal("Progress: " + currentRegionScore + "/100").formatted(Formatting.YELLOW),
            panelX + PANEL_WIDTH / 2, panelY + 40, 0xFFFFFF);
        
        // Render missions list
        context.drawText(textRenderer,
            Text.literal("Active Missions:").formatted(Formatting.WHITE, Formatting.BOLD),
            panelX + 20, panelY + 60, 0xFFFFFF, false);
        
        for (int i = 0; i < missions.length; i++) {
            renderMission(context, missions[i], i);
        }
        
        // Render next region unlock status
        if (currentRegionScore >= 70) {
            context.drawText(textRenderer,
                Text.literal("✅ Next region unlocked!").formatted(Formatting.GREEN),
                panelX + 20, panelY + PANEL_HEIGHT - 50, 0xFFFFFF, false);
        } else {
            int needed = 70 - currentRegionScore;
            context.drawText(textRenderer,
                Text.literal("🔒 Need " + needed + " more points to unlock next region").formatted(Formatting.RED),
                panelX + 20, panelY + PANEL_HEIGHT - 50, 0xFFFFFF, false);
        }
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderMission(DrawContext context, MissionDisplayData mission, int index) {
        int y = panelY + 80 + (index * 35);
        
        // Mission status icon
        String statusIcon;
        Formatting statusColor;
        
        if (mission.progress >= 100) {
            statusIcon = "✅";
            statusColor = Formatting.GREEN;
        } else if (mission.isActive) {
            statusIcon = "🔄";
            statusColor = Formatting.YELLOW;
        } else {
            statusIcon = "🔒";
            statusColor = Formatting.GRAY;
        }
        
        // Render mission info
        context.drawText(textRenderer,
            Text.literal(statusIcon + " " + mission.name).formatted(statusColor),
            panelX + 20, y, 0xFFFFFF, false);
        
        context.drawText(textRenderer,
            Text.literal(mission.description).formatted(Formatting.GRAY),
            panelX + 25, y + 10, 0xFFFFFF, false);
        
        // Progress bar
        if (mission.isActive || mission.progress > 0) {
            int barWidth = 150;
            int barHeight = 6;
            int barX = panelX + 20;
            int barY = y + 22;
            
            // Background
            context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);
            
            // Progress fill
            int fillWidth = (int) ((double) mission.progress / mission.maxProgress * barWidth);
            int progressColor = mission.progress >= 100 ? 0xFF00FF00 : 0xFF4A90E2;
            context.fill(barX, barY, barX + fillWidth, barY + barHeight, progressColor);
            
            // Progress text
            context.drawText(textRenderer,
                Text.literal(mission.progress + "/" + mission.maxProgress).formatted(Formatting.WHITE),
                barX + barWidth + 10, y + 20, 0xFFFFFF, false);
        }
    }
    
    @Override
    public boolean shouldPause() {
        return false; // Don't pause the game
    }
    
    private static class MissionDisplayData {
        public final String name;
        public final String description;
        public int progress;
        public final int maxProgress;
        public boolean isActive;
        
        public MissionDisplayData(String name, String description, int progress, int maxProgress, boolean isActive) {
            this.name = name;
            this.description = description;
            this.progress = progress;
            this.maxProgress = maxProgress;
            this.isActive = isActive;
        }
    }
}