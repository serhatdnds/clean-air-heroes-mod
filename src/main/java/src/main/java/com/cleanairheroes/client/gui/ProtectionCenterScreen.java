package com.cleanairheroes.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import com.cleanairheroes.data.PlayerData;
import com.cleanairheroes.data.PlayerProgress;

public class ProtectionCenterScreen extends Screen {
    
    private static final int GUI_WIDTH = 1280;
    private static final int GUI_HEIGHT = 900;
    
    private PlayerProgress playerProgress;
    private int centerX;
    private int centerY;
    private int animationTicks = 0;
    private float logoRotation = 0.0f;
    
    public ProtectionCenterScreen() {
        super(Text.literal("Environmental Protection Center"));
    }
    
    @Override
    protected void init() {
        super.init();
        
        this.centerX = (this.width - GUI_WIDTH) / 2;
        this.centerY = (this.height - GUI_HEIGHT) / 2;
        
        if (this.client != null && this.client.player != null) {
            this.playerProgress = PlayerData.getPlayerProgress(this.client.player);
        }
        
        initializeButtons();
    }
    
    private void initializeButtons() {
        int buttonWidth = 380;
        int buttonHeight = 260;
        int spacing = 20;
        
        // Service cards arranged in 2x3 grid
        int startX = centerX + 40;
        int startY = centerY + 140;
        
        // Row 1
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🗺️ Air Quality Map"), button -> {
            if (this.client != null) {
                this.client.setScreen(new AirQualityDashboard());
            }
        }).dimensions(startX, startY, buttonWidth, buttonHeight).build());
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🏆 Achievement Gallery"), button -> {
            if (this.client != null) {
                this.client.setScreen(new AchievementGalleryScreen());
            }
        }).dimensions(startX + buttonWidth + spacing, startY, buttonWidth, buttonHeight).build());
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("✈️ Travel Hub"), button -> {
            if (this.client != null) {
                this.client.setScreen(new TravelHubScreen());
            }
        }).dimensions(startX + 2 * (buttonWidth + spacing), startY, buttonWidth, buttonHeight).build());
        
        // Row 2
        int row2Y = startY + buttonHeight + spacing;
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("📊 Global Report"), button -> {
            if (this.client != null) {
                this.client.setScreen(new GlobalReportScreen());
            }
        }).dimensions(startX, row2Y, buttonWidth, buttonHeight).build());
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🔬 Research Data"), button -> {
            if (this.client != null) {
                this.client.setScreen(new ResearchDataScreen());
            }
        }).dimensions(startX + buttonWidth + spacing, row2Y, buttonWidth, buttonHeight).build());
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🏅 Leaderboard"), button -> {
            if (this.client != null) {
                this.client.setScreen(new LeaderboardScreen());
            }
        }).dimensions(startX + 2 * (buttonWidth + spacing), row2Y, buttonWidth, buttonHeight).build());
        
        // Back button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("← Back to Mission Control"), button -> {
            if (this.client != null) {
                this.client.setScreen(new MissionControlScreen());
            }
        }).dimensions(centerX + 40, centerY + GUI_HEIGHT - 50, 200, 30).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.animationTicks++;
        this.logoRotation += 0.5f;
        
        // Render gradient background
        renderGradientBackground(context);
        
        // Main center building background
        context.fill(centerX, centerY, centerX + GUI_WIDTH, centerY + GUI_HEIGHT, 0xF0F8FFFF);
        context.drawBorder(centerX, centerY, GUI_WIDTH, GUI_HEIGHT, 0xFF2C3E50);
        
        // Header with logo
        renderCenterHeader(context);
        
        // Service cards with hover effects
        renderServiceCards(context, mouseX, mouseY);
        
        // Statistics footer
        renderStatisticsFooter(context);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderGradientBackground(Context context) {
        // Sky blue to white gradient
        for (int y = 0; y < this.height; y++) {
            float factor = (float) y / this.height;
            int blue = (int) (135 + (255 - 135) * factor);
            int green = (int) (206 + (255 - 206) * factor);
            int color = 0xFF000000 | (255 << 16) | (green << 8) | blue;
            context.fill(0, y, this.width, y + 1, color);
        }
    }
    
    private void renderCenterHeader(DrawContext context) {
        int headerHeight = 120;
        
        // Header background with gradient
        context.fill(centerX + 20, centerY + 20, centerX + GUI_WIDTH - 20, centerY + headerHeight, 0xFF2C3E50);
        
        // Animated logo
        float pulseScale = 1.0f + 0.1f * (float) Math.sin(animationTicks * 0.1);
        
        // World icon (rotating)
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("🌍").formatted(Formatting.BLUE),
            centerX + GUI_WIDTH / 2 - 100, centerY + 40, 0xFFFFFF);
        
        // Main title
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("ENVIRONMENTAL PROTECTION CENTER").formatted(Formatting.BOLD, Formatting.AQUA),
            centerX + GUI_WIDTH / 2, centerY + 40, 0xFFFFFF);
        
        // Subtitle
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("Global Air Quality Hub & Coordination Center").formatted(Formatting.ITALIC),
            centerX + GUI_WIDTH / 2, centerY + 65, 0xFFBDC3C7);
        
        // Status indicator
        String status = "🟢 OPERATIONAL - Real-time monitoring active";
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal(status).formatted(Formatting.GREEN),
            centerX + GUI_WIDTH / 2, centerY + 90, 0xFFFFFF);
    }
    
    private void renderServiceCards(DrawContext context, int mouseX, int mouseY) {
        int buttonWidth = 380;
        int buttonHeight = 260;
        int spacing = 20;
        int startX = centerX + 40;
        int startY = centerY + 140;
        
        ServiceCardInfo[] services = {
            new ServiceCardInfo("🗺️", "Air Quality Map", "View real-time air quality data", "Monitor pollution levels across all regions", 0xFF3498DB),
            new ServiceCardInfo("🏆", "Achievement Gallery", "Browse your environmental impact", "Visual documentation of your success", 0xFFF1C40F),
            new ServiceCardInfo("✈️", "Travel Hub", "Fast travel between regions", "Access unlocked regions instantly", 0xFF2ECC71),
            new ServiceCardInfo("📊", "Global Report", "Your environmental contribution", "Comprehensive impact analysis", 0xFF9B59B6),
            new ServiceCardInfo("🔬", "Research Data", "Scientific research database", "Access latest environmental research", 0xFFE67E22),
            new ServiceCardInfo("🏅", "Leaderboard", "Global Clean Air Heroes ranking", "Compare with environmental heroes worldwide", 0xFFE74C3C)
        };
        
        for (int i = 0; i < services.length; i++) {
            int row = i / 3;
            int col = i % 3;
            int cardX = startX + col * (buttonWidth + spacing);
            int cardY = startY + row * (buttonHeight + spacing);
            
            // Check if mouse is hovering
            boolean isHovered = mouseX >= cardX && mouseX <= cardX + buttonWidth &&
                              mouseY >= cardY && mouseY <= cardY + buttonHeight;
            
            renderServiceCard(context, cardX, cardY, buttonWidth, buttonHeight, services[i], isHovered);
        }
    }
    
    private void renderServiceCard(DrawContext context, int x, int y, int width, int height, ServiceCardInfo service, boolean isHovered) {
        // Card background with hover effect
        int backgroundColor = isHovered ? 0xFFFFFFFF : 0xFFF8F9FA;
        int borderColor = isHovered ? service.accentColor : 0xFFBDC3C7;
        int borderWidth = isHovered ? 3 : 1;
        
        context.fill(x, y, x + width, y + height, backgroundColor);
        
        // Multiple border lines for thickness effect
        for (int i = 0; i < borderWidth; i++) {
            context.drawBorder(x - i, y - i, width + 2 * i, height + 2 * i, borderColor);
        }
        
        // Service icon (large)
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(service.icon),
            x + 20, y + 20, service.accentColor);
        
        // Service title
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(service.title).formatted(Formatting.BOLD),
            x + 20, y + 60, 0xFF2C3E50);
        
        // Service description
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(service.description),
            x + 20, y + 85, 0xFF5D6D7E);
        
        // Detailed description
        String[] detailLines = wrapText(service.details, width - 40);
        for (int i = 0; i < detailLines.length && i < 6; i++) {
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(detailLines[i]),
                x + 20, y + 110 + i * 12, 0xFF85929E);
        }
        
        // Status indicators based on player progress
        if (playerProgress != null) {
            renderServiceStatus(context, x, y + height - 40, width, service, playerProgress);
        }
        
        // Hover glow effect
        if (isHovered) {
            int glowAlpha = (int) (64 + 32 * Math.sin(animationTicks * 0.2));
            int glowColor = (glowAlpha << 24) | (service.accentColor & 0x00FFFFFF);
            context.fill(x - 5, y - 5, x + width + 5, y + height + 5, glowColor);
        }
    }
    
    private void renderServiceStatus(DrawContext context, int x, int y, int width, ServiceCardInfo service, PlayerProgress progress) {
        String statusText = "";
        int statusColor = 0xFF95A5A6;
        
        switch (service.title) {
            case "Air Quality Map" -> {
                int regionsWithData = progress.getUnlockedRegions().size();
                statusText = regionsWithData + "/5 regions monitored";
                statusColor = regionsWithData >= 3 ? 0xFF2ECC71 : 0xFFF39C12;
            }
            case "Achievement Gallery" -> {
                int achievements = progress.getCompletedMissionsCount();
                statusText = achievements + " achievements documented";
                statusColor = achievements >= 10 ? 0xFF2ECC71 : 0xFFF39C12;
            }
            case "Travel Hub" -> {
                int unlockedRegions = progress.getUnlockedRegions().size();
                statusText = unlockedRegions + "/5 destinations unlocked";
                statusColor = unlockedRegions >= 3 ? 0xFF2ECC71 : 0xFFF39C12;
            }
            case "Global Report" -> {
                double impact = progress.getTotalPollutionReduced();
                statusText = String.format("%.1f kg pollution reduced", impact);
                statusColor = impact >= 100 ? 0xFF2ECC71 : 0xFFF39C12;
            }
            case "Research Data" -> {
                statusText = "Live data access available";
                statusColor = 0xFF2ECC71;
            }
            case "Leaderboard" -> {
                int rank = Math.max(1, 1000 - progress.getTotalScore() * 2);
                statusText = "Global rank: #" + rank;
                statusColor = rank <= 100 ? 0xFF2ECC71 : rank <= 500 ? 0xFFF39C12 : 0xFFE74C3C;
            }
        }
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Status: " + statusText),
            x + 20, y, statusColor);
    }
    
    private void renderStatisticsFooter(DrawContext context) {
        int footerY = centerY + GUI_HEIGHT - 120;
        int footerHeight = 70;
        
        // Footer background
        context.fill(centerX + 20, footerY, centerX + GUI_WIDTH - 20, footerY + footerHeight, 0xFF34495E);
        
        if (playerProgress != null) {
            // Statistics in 6 boxes
            int boxWidth = (GUI_WIDTH - 80) / 6;
            String[] statTitles = {"Total Score", "Regions Completed", "Pollution Reduced", "Equipment Installed", "Play Time", "Global Rank"};
            String[] statValues = {
                String.valueOf(playerProgress.getTotalScore()),
                progress.getUnlockedRegions().size() + "/5",
                String.format("%.1f kg", playerProgress.getTotalPollutionReduced()),
                String.valueOf(playerProgress.getEquipmentInstalled()),
                playerProgress.getTotalPlayTimeMinutes() + " min",
                "#" + Math.max(1, 1000 - playerProgress.getTotalScore() * 2)
            };
            int[] statColors = {0xFFF1C40F, 0xFF3498DB, 0xFF2ECC71, 0xFFE67E22, 0xFF9B59B6, 0xFFE74C3C};
            
            for (int i = 0; i < 6; i++) {
                int boxX = centerX + 30 + i * (boxWidth + 10);
                
                // Stat value (large)
                context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal(statValues[i]).formatted(Formatting.BOLD),
                    boxX + boxWidth / 2, footerY + 15, statColors[i]);
                
                // Stat title (small)
                context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal(statTitles[i]),
                    boxX + boxWidth / 2, footerY + 35, 0xFFBDC3C7);
            }
        }
    }
    
    private String[] wrapText(String text, int maxWidth) {
        // Simple text wrapping (basic implementation)
        int charWidth = 6; // Approximate character width
        int maxChars = maxWidth / charWidth;
        
        if (text.length() <= maxChars) {
            return new String[]{text};
        }
        
        String[] words = text.split(" ");
        java.util.List<String> lines = new java.util.ArrayList<>();
        String currentLine = "";
        
        for (String word : words) {
            if ((currentLine + " " + word).length() <= maxChars) {
                currentLine += (currentLine.isEmpty() ? "" : " ") + word;
            } else {
                if (!currentLine.isEmpty()) {
                    lines.add(currentLine);
                }
                currentLine = word;
            }
        }
        
        if (!currentLine.isEmpty()) {
            lines.add(currentLine);
        }
        
        return lines.toArray(new String[0]);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
    
    // Helper class for service card information
    private static class ServiceCardInfo {
        public final String icon;
        public final String title;
        public final String description;
        public final String details;
        public final int accentColor;
        
        public ServiceCardInfo(String icon, String title, String description, String details, int accentColor) {
            this.icon = icon;
            this.title = title;
            this.description = description;
            this.details = details;
            this.accentColor = accentColor;
        }
    }
}