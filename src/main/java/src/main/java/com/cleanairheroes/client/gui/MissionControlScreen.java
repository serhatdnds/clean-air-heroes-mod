package com.cleanairheroes.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import com.cleanairheroes.CleanAirHeroes;
import com.cleanairheroes.data.PlayerData;
import com.cleanairheroes.data.PlayerProgress;

public class MissionControlScreen extends Screen {
    
    private static final Identifier BACKGROUND_TEXTURE = new Identifier(CleanAirHeroes.MOD_ID, "textures/gui/mission_control_bg.png");
    private static final int GUI_WIDTH = 1024;
    private static final int GUI_HEIGHT = 768;
    
    private PlayerProgress playerProgress;
    private int centerX;
    private int centerY;
    
    // Button references
    private ButtonWidget miniGameButton;
    private ButtonWidget centerButton;
    private ButtonWidget travelButton;
    private ButtonWidget[] regionButtons = new ButtonWidget[5];
    
    // Animation states
    private int animationTicks = 0;
    private float pulseAnimation = 0.0f;
    
    public MissionControlScreen() {
        super(Text.literal("Mission Control Center"));
    }
    
    @Override
    protected void init() {
        super.init();
        
        this.centerX = (this.width - GUI_WIDTH) / 2;
        this.centerY = (this.height - GUI_HEIGHT) / 2;
        
        // Get player progress
        if (this.client != null && this.client.player != null) {
            this.playerProgress = PlayerData.getPlayerProgress(this.client.player);
        }
        
        initializeButtons();
    }
    
    private void initializeButtons() {
        // Quick action buttons (right panel)
        int rightPanelX = centerX + 704; // 320 (left) + 384 (center) = 704
        int buttonY = centerY + 200;
        
        this.miniGameButton = ButtonWidget.builder(Text.literal("🎮 Start Mini-Game"), button -> {
            if (this.client != null) {
                this.client.setScreen(new MiniGameSelectionScreen());
            }
        }).dimensions(rightPanelX, buttonY, 280, 40).build();
        
        this.centerButton = ButtonWidget.builder(Text.literal("🌍 Protection Center"), button -> {
            if (this.client != null) {
                this.client.setScreen(new ProtectionCenterScreen());
            }
        }).dimensions(rightPanelX, buttonY + 50, 280, 40).build();
        
        this.travelButton = ButtonWidget.builder(Text.literal("✈️ Travel Hub"), button -> {
            if (this.client != null) {
                this.client.setScreen(new TravelHubScreen());
            }
        }).dimensions(rightPanelX, buttonY + 100, 280, 40).build();
        
        // Region buttons (center panel)
        int mapCenterX = centerX + 512; // 320 + 192 (half of 384)
        int mapCenterY = centerY + 380;
        
        // Varna (bottom left)
        this.regionButtons[0] = ButtonWidget.builder(Text.literal("🏖️"), button -> selectRegion("varna"))
            .dimensions(mapCenterX - 120, mapCenterY + 60, 40, 40).build();
        
        // Zonguldak (bottom center)
        this.regionButtons[1] = ButtonWidget.builder(Text.literal("⛏️"), button -> selectRegion("zonguldak"))
            .dimensions(mapCenterX - 40, mapCenterY + 80, 40, 40).build();
        
        // Odesa (top left)
        this.regionButtons[2] = ButtonWidget.builder(Text.literal("🚢"), button -> selectRegion("odesa"))
            .dimensions(mapCenterX - 100, mapCenterY - 40, 40, 40).build();
        
        // Trabzon (bottom right)
        this.regionButtons[3] = ButtonWidget.builder(Text.literal("🏔️"), button -> selectRegion("trabzon"))
            .dimensions(mapCenterX + 40, mapCenterY + 60, 40, 40).build();
        
        // Romania (top right)
        this.regionButtons[4] = ButtonWidget.builder(Text.literal("🏛️"), button -> selectRegion("romania"))
            .dimensions(mapCenterX + 80, mapCenterY - 20, 40, 40).build();
        
        // Add all buttons
        this.addDrawableChild(miniGameButton);
        this.addDrawableChild(centerButton);
        this.addDrawableChild(travelButton);
        
        for (ButtonWidget regionButton : regionButtons) {
            this.addDrawableChild(regionButton);
        }
    }
    
    private void selectRegion(String region) {
        if (this.client != null && this.client.player != null) {
            // Send command to switch to region or show region details
            this.client.player.networkHandler.sendChatCommand("cleanair teleport " + region);
            this.close();
        }
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.animationTicks++;
        this.pulseAnimation = (float) Math.sin(animationTicks * 0.1) * 0.1f + 1.0f;
        
        // Render dark background
        context.fill(0, 0, this.width, this.height, 0x80000000);
        
        // Main GUI background
        context.fill(centerX, centerY, centerX + GUI_WIDTH, centerY + GUI_HEIGHT, 0xFF2C3E50);
        
        // Header
        renderHeader(context);
        
        // Left panel - Missions
        renderMissionsPanel(context);
        
        // Center panel - Regional map
        renderRegionalMap(context);
        
        // Right panel - Quick access
        renderQuickAccessPanel(context);
        
        // Bottom status bar
        renderStatusBar(context);
        
        // Render buttons
        super.render(context, mouseX, mouseY, delta);
        
        // Render tooltips
        renderTooltips(context, mouseX, mouseY);
    }
    
    private void renderHeader(DrawContext context) {
        int headerY = centerY + 10;
        
        // Title background
        context.fill(centerX + 10, headerY, centerX + GUI_WIDTH - 10, headerY + 60, 0xFF34495E);
        
        // Main title
        context.drawCenteredTextWithShadow(this.textRenderer, 
            Text.literal("🌍 CLEAN AIR HEROES").formatted(Formatting.BOLD, Formatting.AQUA),
            centerX + 300, headerY + 15, 0xFFFFFF);
        
        // Player info
        if (playerProgress != null) {
            String playerTitle = playerProgress.getPlayerTitle();
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("Title: " + playerTitle).formatted(Formatting.GOLD),
                centerX + 600, headerY + 15, 0xFFFFFF);
            
            String currentRegion = playerProgress.getCurrentRegion().toUpperCase();
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("Region: " + currentRegion).formatted(Formatting.YELLOW),
                centerX + 600, headerY + 35, 0xFFFFFF);
        }
    }
    
    private void renderMissionsPanel(DrawContext context) {
        int panelX = centerX + 20;
        int panelY = centerY + 80;
        int panelWidth = 280;
        int panelHeight = 560;
        
        // Panel background
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF34495E);
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFF3498DB);
        
        // Panel title
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("📋 ACTIVE MISSIONS").formatted(Formatting.BOLD, Formatting.BLUE),
            panelX + 10, panelY + 10, 0xFFFFFF);
        
        // Mission cards
        if (playerProgress != null) {
            String currentRegion = playerProgress.getCurrentRegion();
            int currentScore = playerProgress.getRegionScore(currentRegion);
            
            // Sample mission cards
            renderMissionCard(context, panelX + 10, panelY + 40, "Port Cleanup", currentScore, 70, 0xFF2ECC71);
            renderMissionCard(context, panelY + 10, panelY + 170, "Air Quality Monitoring", currentScore - 20, 50, 0xFF3498DB);
            renderMissionCard(context, panelX + 10, panelY + 300, "Equipment Installation", currentScore - 30, 40, 0xFFF39C12);
            renderMissionCard(context, panelX + 10, panelY + 430, "Education Campaign", currentScore - 40, 30, 0xFFE74C3C);
        }
    }
    
    private void renderMissionCard(DrawContext context, int x, int y, String missionName, int current, int target, int color) {
        int cardWidth = 260;
        int cardHeight = 120;
        
        // Card background
        context.fill(x, y, x + cardWidth, y + cardHeight, 0xFFFFFFFF);
        context.drawBorder(x, y, cardWidth, cardHeight, 0xFF3498DB);
        
        // Mission name
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(missionName).formatted(Formatting.BOLD),
            x + 10, y + 10, 0xFF2C3E50);
        
        // Progress bar background
        int barX = x + 10;
        int barY = y + 35;
        int barWidth = cardWidth - 20;
        int barHeight = 20;
        
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF95A5A6);
        
        // Progress bar fill
        int progress = Math.min(current, target);
        int fillWidth = (barWidth * progress) / target;
        context.fill(barX, barY, barX + fillWidth, barY + barHeight, color);
        
        // Progress text
        String progressText = progress + "/" + target + " (" + (progress * 100 / target) + "%)";
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal(progressText),
            barX + barWidth / 2, barY + 30, 0xFF2C3E50);
        
        // Mission status
        String status = progress >= target ? "✅ Completed" : "⏳ In Progress";
        Formatting statusColor = progress >= target ? Formatting.GREEN : Formatting.YELLOW;
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(status).formatted(statusColor),
            x + 10, y + 80, 0xFF2C3E50);
    }
    
    private void renderRegionalMap(DrawContext context) {
        int mapX = centerX + 320;
        int mapY = centerY + 80;
        int mapWidth = 384;
        int mapHeight = 560;
        
        // Map background
        context.fill(mapX, mapY, mapX + mapWidth, mapY + mapHeight, 0xFF1A252F);
        context.drawBorder(mapX, mapY, mapWidth, mapHeight, 0xFF3498DB);
        
        // Map title
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("🗺️ BLACK SEA REGION MAP").formatted(Formatting.BOLD, Formatting.AQUA),
            mapX + mapWidth / 2, mapY + 10, 0xFFFFFF);
        
        // Draw simplified map outline
        drawMapOutline(context, mapX, mapY + 40, mapWidth, mapHeight - 80);
        
        // Region status indicators
        if (playerProgress != null) {
            drawRegionStatus(context, mapX, mapY);
        }
    }
    
    private void drawMapOutline(DrawContext context, int x, int y, int width, int height) {
        // Simple Black Sea outline
        int seaColor = 0xFF2980B9;
        int landColor = 0xFF27AE60;
        
        // Sea background
        context.fill(x + 20, y + 20, x + width - 20, y + height - 20, seaColor);
        
        // Land masses (simplified)
        // Bulgaria coast
        context.fill(x + 30, y + height - 60, x + 100, y + height - 20, landColor);
        // Turkey coast
        context.fill(x + 150, y + height - 40, x + 300, y + height - 20, landColor);
        // Ukraine coast
        context.fill(x + 50, y + 30, x + 150, y + 70, landColor);
        // Romania coast
        context.fill(x + 200, y + 30, x + width - 30, y + 70, landColor);
    }
    
    private void drawRegionStatus(DrawContext context, int mapX, int mapY) {
        String[] regions = {"varna", "zonguldak", "odesa", "trabzon", "romania"};
        int[] regionX = {-120, -40, -100, 40, 80};
        int[] regionY = {60, 80, -40, 60, -20};
        
        int mapCenterX = mapX + 192;
        int mapCenterY = mapY + 300;
        
        for (int i = 0; i < regions.length; i++) {
            int score = playerProgress.getRegionScore(regions[i]);
            boolean isCompleted = score >= 70;
            boolean isCurrent = regions[i].equals(playerProgress.getCurrentRegion());
            
            int regionPixelX = mapCenterX + regionX[i];
            int regionPixelY = mapCenterY + regionY[i];
            
            // Region glow effect
            int glowColor;
            if (isCurrent) {
                // Pulsing blue for current region
                int alpha = (int) (128 + 127 * pulseAnimation);
                glowColor = (alpha << 24) | 0x3498DB;
            } else if (isCompleted) {
                glowColor = 0x802ECC71; // Green for completed
            } else {
                glowColor = 0x8095A5A6; // Gray for incomplete
            }
            
            // Draw glow
            context.fill(regionPixelX - 25, regionPixelY - 25, regionPixelX + 25, regionPixelY + 25, glowColor);
            
            // Region score
            if (score > 0) {
                context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal(String.valueOf(score)),
                    regionPixelX, regionPixelY - 35, 0xFFFFFF);
            }
        }
    }
    
    private void renderQuickAccessPanel(DrawContext context) {
        int panelX = centerX + 724;
        int panelY = centerY + 80;
        int panelWidth = 280;
        int panelHeight = 560;
        
        // Panel background
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF34495E);
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFF3498DB);
        
        // Panel title
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("⚡ QUICK ACCESS").formatted(Formatting.BOLD, Formatting.YELLOW),
            panelX + 10, panelY + 10, 0xFFFFFF);
        
        // Air Quality Meter
        renderAirQualityMeter(context, panelX + 10, panelY + 40);
        
        // Equipment slots (rendered above buttons)
        renderEquipmentSlots(context, panelX + 10, panelY + 250);
    }
    
    private void renderAirQualityMeter(DrawContext context, int x, int y) {
        int meterWidth = 260;
        int meterHeight = 150;
        
        // Meter background
        context.fill(x, y, x + meterWidth, y + meterHeight, 0xFF2C3E50);
        context.drawBorder(x, y, meterWidth, meterHeight, 0xFFF1C40F);
        
        // Title
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("🌡️ AIR QUALITY INDEX").formatted(Formatting.BOLD),
            x + meterWidth / 2, y + 10, 0xFFFFFF);
        
        // Simulate AQI value
        double aqi = 75 + Math.sin(animationTicks * 0.05) * 15; // Animated between 60-90
        
        // Circular meter (simplified as rectangle for now)
        int meterCenterX = x + meterWidth / 2;
        int meterCenterY = y + 70;
        int meterRadius = 40;
        
        // Meter background circle
        context.fill(meterCenterX - meterRadius, meterCenterY - meterRadius,
                    meterCenterX + meterRadius, meterCenterY + meterRadius, 0xFF34495E);
        
        // AQI value
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal(String.format("%.0f", aqi)).formatted(Formatting.BOLD),
            meterCenterX, meterCenterY - 5, getAQIColor(aqi));
        
        // AQI category
        String category = getAQICategory(aqi);
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal(category),
            meterCenterX, meterCenterY + 10, getAQIColor(aqi));
    }
    
    private void renderEquipmentSlots(DrawContext context, int x, int y) {
        // Equipment grid (2x3)
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 3; col++) {
                int slotX = x + col * 70;
                int slotY = y + row * 70;
                
                // Slot background
                context.fill(slotX, slotY, slotX + 64, slotY + 64, 0xFF2C3E50);
                context.drawBorder(slotX, slotY, 64, 64, 0xFF95A5A6);
                
                // Sample equipment icons (using text for now)
                String[] equipmentIcons = {"🔬", "🔧", "📡", "⚗️", "🧪", "🔌"};
                int index = row * 3 + col;
                if (index < equipmentIcons.length) {
                    context.drawCenteredTextWithShadow(this.textRenderer,
                        Text.literal(equipmentIcons[index]),
                        slotX + 32, slotY + 28, 0xFFFFFF);
                }
            }
        }
    }
    
    private void renderStatusBar(DrawContext context) {
        int barY = centerY + GUI_HEIGHT - 60;
        
        // Status bar background
        context.fill(centerX, barY, centerX + GUI_WIDTH, centerY + GUI_HEIGHT, 0xFF2C3E50);
        context.drawBorder(centerX, barY, GUI_WIDTH, 60, 0xFF3498DB);
        
        if (playerProgress != null) {
            // Left: Clean Air Points
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("🌟 Clean Air Points: " + playerProgress.getTotalScore()).formatted(Formatting.GOLD),
                centerX + 20, barY + 15, 0xFFFFFF);
            
            // Center: Pollution Reduced
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("♻️ Pollution Reduced: " + String.format("%.1f kg", playerProgress.getTotalPollutionReduced())).formatted(Formatting.GREEN),
                centerX + GUI_WIDTH / 2, barY + 15, 0xFFFFFF);
            
            // Right: Current Title
            String title = playerProgress.getPlayerTitle();
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("🏆 " + title).formatted(Formatting.AQUA),
                centerX + GUI_WIDTH - 300, barY + 15, 0xFFFFFF);
            
            // Additional stats on second line
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("Missions: " + playerProgress.getCompletedMissionsCount()).formatted(Formatting.BLUE),
                centerX + 20, barY + 35, 0xFFFFFF);
            
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Equipment: " + playerProgress.getEquipmentInstalled()).formatted(Formatting.YELLOW),
                centerX + GUI_WIDTH / 2, barY + 35, 0xFFFFFF);
            
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("Playtime: " + playerProgress.getTotalPlayTimeMinutes() + " min").formatted(Formatting.GRAY),
                centerX + GUI_WIDTH - 200, barY + 35, 0xFFFFFF);
        }
    }
    
    private void renderTooltips(DrawContext context, int mouseX, int mouseY) {
        // Check if mouse is over a region button
        for (int i = 0; i < regionButtons.length; i++) {
            ButtonWidget button = regionButtons[i];
            if (mouseX >= button.getX() && mouseX <= button.getX() + button.getWidth() &&
                mouseY >= button.getY() && mouseY <= button.getY() + button.getHeight()) {
                
                String[] regionNames = {"Varna, Bulgaria", "Zonguldak, Turkey", "Odesa, Ukraine", "Trabzon, Turkey", "Southeast Romania"};
                String[] regionDescriptions = {
                    "Port pollution challenges",
                    "Coal mining dust control", 
                    "Marine research cooperation",
                    "Urban traffic optimization",
                    "Multi-city coordination"
                };
                
                if (playerProgress != null) {
                    String[] regions = {"varna", "zonguldak", "odesa", "trabzon", "romania"};
                    int score = playerProgress.getRegionScore(regions[i]);
                    boolean isUnlocked = playerProgress.isRegionUnlocked(regions[i]);
                    
                    context.drawTooltip(this.textRenderer, 
                        Text.literal(regionNames[i] + "\n" + regionDescriptions[i] + 
                                   "\nScore: " + score + "/70" +
                                   "\nStatus: " + (isUnlocked ? "Unlocked" : "Locked")),
                        mouseX, mouseY);
                }
            }
        }
    }
    
    private int getAQIColor(double aqi) {
        if (aqi <= 50) return 0xFF2ECC71; // Green
        if (aqi <= 100) return 0xFFF1C40F; // Yellow
        if (aqi <= 150) return 0xFFF39C12; // Orange
        if (aqi <= 200) return 0xFFE74C3C; // Red
        return 0xFF8B008B; // Purple
    }
    
    private String getAQICategory(double aqi) {
        if (aqi <= 50) return "Good";
        if (aqi <= 100) return "Moderate";
        if (aqi <= 150) return "Unhealthy";
        if (aqi <= 200) return "Very Unhealthy";
        return "Hazardous";
    }
    
    @Override
    public boolean shouldPause() {
        return false; // Don't pause the game
    }
}