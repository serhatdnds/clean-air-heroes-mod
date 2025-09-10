package com.cleanairheroes.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AirQualityHud {
    
    private static final int HUD_WIDTH = 180;
    private static final int HUD_HEIGHT = 65;
    private static final int MARGIN = 10;
    
    // Sample data - would come from pollution manager
    private double currentAQI = 85.0;
    private String aqiCategory = "Moderate";
    private int aqiColor = 0xFFFFFF00; // Yellow for moderate
    private boolean showHud = true;
    private long lastUpdate = System.currentTimeMillis();
    
    public void render(DrawContext drawContext, int screenWidth, int screenHeight) {
        if (!showHud) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        
        // Update AQI data periodically
        updateAQIData(client);
        
        // Position in top-right corner
        int x = screenWidth - HUD_WIDTH - MARGIN;
        int y = MARGIN;
        
        // Render background panel with AQI-based border color
        drawContext.fill(x, y, x + HUD_WIDTH, y + HUD_HEIGHT, 0xAA000000);
        drawContext.drawBorder(x, y, HUD_WIDTH, HUD_HEIGHT, aqiColor);
        
        // Title with air quality icon
        String aqiIcon = getAQIIcon(currentAQI);
        drawContext.drawText(client.textRenderer,
            Text.literal(aqiIcon + " Air Quality").formatted(Formatting.AQUA, Formatting.BOLD),
            x + 5, y + 5, 0xFFFFFF, true);
        
        // AQI value
        drawContext.drawText(client.textRenderer,
            Text.literal("AQI: " + String.format("%.1f", currentAQI)).formatted(Formatting.WHITE),
            x + 5, y + 18, 0xFFFFFF, true);
        
        // AQI category with appropriate color
        Formatting categoryFormatting = getAQIFormatting(currentAQI);
        drawContext.drawText(client.textRenderer,
            Text.literal(aqiCategory).formatted(categoryFormatting, Formatting.BOLD),
            x + 5, y + 30, 0xFFFFFF, true);
        
        // Health recommendation
        String healthTip = getHealthRecommendation(currentAQI);
        drawContext.drawText(client.textRenderer,
            Text.literal(healthTip).formatted(Formatting.GRAY),
            x + 5, y + 42, 0xAAAAAAA, false);
        
        // Quick check hint
        drawContext.drawText(client.textRenderer,
            Text.literal("Press Q for details").formatted(Formatting.GRAY),
            x + 5, y + HUD_HEIGHT - 12, 0xAAAAAAA, false);
    }
    
    private void updateAQIData(MinecraftClient client) {
        // Update every 5 seconds
        if (System.currentTimeMillis() - lastUpdate > 5000) {
            if (client.player != null) {
                // This would get real data from pollution manager
                // For now, simulate slight variations
                currentAQI += (Math.random() - 0.5) * 10;
                currentAQI = Math.max(0, Math.min(500, currentAQI)); // Clamp between 0-500
                
                updateAQICategory();
                lastUpdate = System.currentTimeMillis();
            }
        }
    }
    
    private void updateAQICategory() {
        if (currentAQI <= 50) {
            aqiCategory = "Good";
            aqiColor = 0xFF00FF00; // Green
        } else if (currentAQI <= 100) {
            aqiCategory = "Moderate";
            aqiColor = 0xFFFFFF00; // Yellow
        } else if (currentAQI <= 150) {
            aqiCategory = "Unhealthy for Sensitive";
            aqiColor = 0xFFFFA500; // Orange
        } else if (currentAQI <= 200) {
            aqiCategory = "Unhealthy";
            aqiColor = 0xFFFF0000; // Red
        } else if (currentAQI <= 300) {
            aqiCategory = "Very Unhealthy";
            aqiColor = 0xFF8B008B; // Dark Magenta
        } else {
            aqiCategory = "Hazardous";
            aqiColor = 0xFF800000; // Maroon
        }
    }
    
    private String getAQIIcon(double aqi) {
        if (aqi <= 50) return "🟢";
        if (aqi <= 100) return "🟡";
        if (aqi <= 150) return "🟠";
        if (aqi <= 200) return "🔴";
        if (aqi <= 300) return "🟣";
        return "🔴";
    }
    
    private Formatting getAQIFormatting(double aqi) {
        if (aqi <= 50) return Formatting.GREEN;
        if (aqi <= 100) return Formatting.YELLOW;
        if (aqi <= 150) return Formatting.GOLD;
        if (aqi <= 200) return Formatting.RED;
        return Formatting.DARK_RED;
    }
    
    private String getHealthRecommendation(double aqi) {
        if (aqi <= 50) return "Air quality is good";
        if (aqi <= 100) return "Acceptable air quality";
        if (aqi <= 150) return "Sensitive groups caution";
        if (aqi <= 200) return "Everyone should limit exposure";
        if (aqi <= 300) return "Avoid outdoor activities";
        return "Emergency conditions";
    }
    
    public void setAQI(double aqi) {
        this.currentAQI = aqi;
        updateAQICategory();
    }
    
    public void setVisible(boolean visible) {
        this.showHud = visible;
    }
    
    public boolean isVisible() {
        return showHud;
    }
    
    public double getCurrentAQI() {
        return currentAQI;
    }
    
    public String getCurrentCategory() {
        return aqiCategory;
    }
}