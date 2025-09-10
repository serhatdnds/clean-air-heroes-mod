package com.cleanairheroes.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import com.cleanairheroes.data.PlayerData;
import com.cleanairheroes.data.PlayerProgress;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AirQualityDashboard extends Screen {
    
    private static final int GUI_WIDTH = 900;
    private static final int GUI_HEIGHT = 700;
    
    private PlayerProgress playerProgress;
    private int centerX;
    private int centerY;
    private int animationTicks = 0;
    
    // Pollutant data simulation
    private Map<String, PollutantData> pollutantData = new HashMap<>();
    private List<Float> trendData = new ArrayList<>();
    
    public AirQualityDashboard() {
        super(Text.literal("Air Quality Monitoring Dashboard"));
        initializePollutantData();
        initializeTrendData();
    }
    
    @Override
    protected void init() {
        super.init();
        
        this.centerX = (this.width - GUI_WIDTH) / 2;
        this.centerY = (this.height - GUI_HEIGHT) / 2;
        
        if (this.client != null && this.client.player != null) {
            this.playerProgress = PlayerData.getPlayerProgress(this.client.player);
        }
        
        // Back button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("← Back"), button -> {
            if (this.client != null) {
                this.client.setScreen(new MissionControlScreen());
            }
        }).dimensions(centerX + 20, centerY + GUI_HEIGHT - 40, 80, 20).build());
        
        // Refresh button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🔄 Refresh"), button -> {
            updatePollutantData();
            updateTrendData();
        }).dimensions(centerX + GUI_WIDTH - 100, centerY + GUI_HEIGHT - 40, 80, 20).build());
    }
    
    private void initializePollutantData() {
        pollutantData.put("PM2.5", new PollutantData("PM2.5", "μg/m³", 25.5, 25.0, 0xFFE74C3C, "🔴"));
        pollutantData.put("PM10", new PollutantData("PM10", "μg/m³", 42.3, 50.0, 0xFFF39C12, "🟠"));
        pollutantData.put("NO2", new PollutantData("NO2", "μg/m³", 38.7, 40.0, 0xFFF1C40F, "🟡"));
        pollutantData.put("SO2", new PollutantData("SO2", "μg/m³", 15.2, 20.0, 0xFF9B59B6, "🟣"));
        pollutantData.put("CO", new PollutantData("CO", "mg/m³", 8.4, 10.0, 0xFF8B4513, "🟤"));
        pollutantData.put("O3", new PollutantData("O3", "μg/m³", 85.1, 120.0, 0xFF3498DB, "🔵"));
    }
    
    private void initializeTrendData() {
        trendData.clear();
        Random random = new Random();
        for (int i = 0; i < 24; i++) {
            trendData.add(50 + random.nextFloat() * 40); // Values between 50-90
        }
    }
    
    private void updatePollutantData() {
        Random random = new Random();
        for (PollutantData data : pollutantData.values()) {
            // Add small random variation
            double variation = (random.nextDouble() - 0.5) * 5;
            data.currentValue = Math.max(0, data.currentValue + variation);
        }
    }
    
    private void updateTrendData() {
        // Shift data left and add new value
        if (trendData.size() >= 24) {
            trendData.remove(0);
        }
        Random random = new Random();
        trendData.add(50 + random.nextFloat() * 40);
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.animationTicks++;
        
        // Render dark background
        context.fill(0, 0, this.width, this.height, 0x80000000);
        
        // Main dashboard background
        context.fill(centerX, centerY, centerX + GUI_WIDTH, centerY + GUI_HEIGHT, 0xFF1A252F);
        context.drawBorder(centerX, centerY, GUI_WIDTH, GUI_HEIGHT, 0xFF3498DB);
        
        // Header
        renderHeader(context);
        
        // Pollutant metric cards
        renderMetricCards(context);
        
        // Trend graphs
        renderTrendGraphs(context);
        
        // Regional comparison
        renderRegionalComparison(context);
        
        super.render(context, mouseX, mouseY, delta);
        
        // Render tooltips
        renderTooltips(context, mouseX, mouseY);
    }
    
    private void renderHeader(DrawContext context) {
        int headerY = centerY + 10;
        
        // Header background
        context.fill(centerX + 10, headerY, centerX + GUI_WIDTH - 10, headerY + 50, 0xFF2C3E50);
        
        // Title
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("📊 AIR QUALITY MONITORING DASHBOARD").formatted(Formatting.BOLD, Formatting.AQUA),
            centerX + GUI_WIDTH / 2, headerY + 10, 0xFFFFFF);
        
        // Real-time clock
        LocalDateTime now = LocalDateTime.now();
        String timeStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("📅 " + timeStr).formatted(Formatting.GRAY),
            centerX + GUI_WIDTH - 200, headerY + 30, 0xFFFFFF);
        
        // Update indicator
        float pulse = (float) Math.sin(animationTicks * 0.2) * 0.5f + 0.5f;
        int pulseColor = (int) (255 * pulse) << 8; // Green pulse
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("🔴 LIVE").formatted(Formatting.RED),
            centerX + 20, headerY + 30, pulseColor | 0xFF000000);
    }
    
    private void renderMetricCards(DrawContext context) {
        int cardsY = centerY + 80;
        int cardWidth = 280;
        int cardHeight = 180;
        int spacing = 20;
        
        String[] pollutants = {"PM2.5", "PM10", "NO2", "SO2", "CO", "O3"};
        
        for (int i = 0; i < pollutants.length; i++) {
            int row = i / 3;
            int col = i % 3;
            int cardX = centerX + 20 + col * (cardWidth + spacing);
            int cardY = cardsY + row * (cardHeight + spacing);
            
            renderPollutantCard(context, cardX, cardY, cardWidth, cardHeight, pollutants[i]);
        }
    }
    
    private void renderPollutantCard(DrawContext context, int x, int y, int width, int height, String pollutant) {
        PollutantData data = pollutantData.get(pollutant);
        if (data == null) return;
        
        // Card background with pollutant-specific color
        int backgroundColor = (data.color & 0x00FFFFFF) | 0x20000000; // Semi-transparent
        context.fill(x, y, x + width, y + height, backgroundColor);
        context.drawBorder(x, y, width, height, data.color);
        
        // Pollutant icon and name
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(data.icon + " " + pollutant).formatted(Formatting.BOLD),
            x + 10, y + 10, 0xFFFFFF);
        
        // Current value (large)
        String valueText = String.format("%.1f", data.currentValue);
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(valueText).formatted(Formatting.BOLD),
            x + 10, y + 35, data.color);
        
        // Unit
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(data.unit),
            x + 10, y + 55, 0xFFBDC3C7);
        
        // Status indicator
        boolean isHigh = data.currentValue > data.threshold;
        String status = isHigh ? "⚠️ HIGH" : "✅ OK";
        Formatting statusFormat = isHigh ? Formatting.RED : Formatting.GREEN;
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(status).formatted(statusFormat, Formatting.BOLD),
            x + 10, y + 75, 0xFFFFFF);
        
        // Mini trend graph
        renderMiniTrend(context, x + 10, y + 100, width - 20, 60, data);
        
        // Threshold line
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Limit: " + String.format("%.1f", data.threshold)),
            x + 10, y + height - 20, 0xFF95A5A6);
    }
    
    private void renderMiniTrend(DrawContext context, int x, int y, int width, int height, PollutantData data) {
        // Background
        context.fill(x, y, x + width, y + height, 0xFF34495E);
        
        // Generate mini trend data
        Random random = new Random(data.pollutant.hashCode());
        List<Float> miniTrend = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            float variation = (random.nextFloat() - 0.5f) * 20;
            miniTrend.add((float) Math.max(0, data.currentValue + variation));
        }
        
        // Draw trend line
        if (miniTrend.size() > 1) {
            float maxValue = Collections.max(miniTrend);
            float minValue = Collections.min(miniTrend);
            float range = maxValue - minValue;
            if (range == 0) range = 1; // Prevent division by zero
            
            for (int i = 0; i < miniTrend.size() - 1; i++) {
                float x1 = x + (i * width) / (float) (miniTrend.size() - 1);
                float y1 = y + height - ((miniTrend.get(i) - minValue) / range) * height;
                float x2 = x + ((i + 1) * width) / (float) (miniTrend.size() - 1);
                float y2 = y + height - ((miniTrend.get(i + 1) - minValue) / range) * height;
                
                // Draw line (simplified as points for now)
                context.fill((int) x1, (int) y1 - 1, (int) x1 + 2, (int) y1 + 1, data.color);
            }
        }
    }
    
    private void renderTrendGraphs(DrawContext context) {
        int graphY = centerY + 460;
        int graphWidth = GUI_WIDTH - 40;
        int graphHeight = 120;
        
        // Graph background
        context.fill(centerX + 20, graphY, centerX + 20 + graphWidth, graphY + graphHeight, 0xFF2C3E50);
        context.drawBorder(centerX + 20, graphY, graphWidth, graphHeight, 0xFF3498DB);
        
        // Graph title
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("📈 24-Hour Trend Analysis").formatted(Formatting.BOLD, Formatting.YELLOW),
            centerX + 30, graphY + 10, 0xFFFFFF);
        
        // Draw grid
        drawGrid(context, centerX + 30, graphY + 30, graphWidth - 20, graphHeight - 50);
        
        // Draw trend lines for each pollutant
        drawTrendLines(context, centerX + 30, graphY + 30, graphWidth - 20, graphHeight - 50);
    }
    
    private void drawGrid(DrawContext context, int x, int y, int width, int height) {
        int gridColor = 0xFF34495E;
        
        // Horizontal grid lines
        for (int i = 0; i <= 4; i++) {
            int lineY = y + (i * height) / 4;
            context.fill(x, lineY, x + width, lineY + 1, gridColor);
        }
        
        // Vertical grid lines (hours)
        for (int i = 0; i <= 6; i++) {
            int lineX = x + (i * width) / 6;
            context.fill(lineX, y, lineX + 1, y + height, gridColor);
            
            // Hour labels
            String hourLabel = String.format("%02d:00", i * 4);
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(hourLabel),
                lineX + 5, y + height + 5, 0xFF95A5A6);
        }
    }
    
    private void drawTrendLines(DrawContext context, int x, int y, int width, int height) {
        if (trendData.size() < 2) return;
        
        float maxValue = Collections.max(trendData);
        float minValue = Collections.min(trendData);
        float range = maxValue - minValue;
        if (range == 0) range = 1;
        
        // Draw main trend line
        for (int i = 0; i < trendData.size() - 1; i++) {
            float x1 = x + (i * width) / (float) (trendData.size() - 1);
            float y1 = y + height - ((trendData.get(i) - minValue) / range) * height;
            float x2 = x + ((i + 1) * width) / (float) (trendData.size() - 1);
            float y2 = y + height - ((trendData.get(i + 1) - minValue) / range) * height;
            
            // Draw thick line
            for (int thickness = -1; thickness <= 1; thickness++) {
                context.fill((int) x1 + thickness, (int) y1 - 1, (int) x2 + thickness, (int) y1 + 1, 0xFF3498DB);
            }
        }
        
        // Draw data points
        for (int i = 0; i < trendData.size(); i++) {
            float pointX = x + (i * width) / (float) (trendData.size() - 1);
            float pointY = y + height - ((trendData.get(i) - minValue) / range) * height;
            
            context.fill((int) pointX - 2, (int) pointY - 2, (int) pointX + 2, (int) pointY + 2, 0xFF2ECC71);
        }
    }
    
    private void renderRegionalComparison(DrawContext context) {
        int compY = centerY + GUI_HEIGHT - 160;
        int compHeight = 100;
        
        // Background
        context.fill(centerX + 20, compY, centerX + GUI_WIDTH - 20, compY + compHeight, 0xFF2C3E50);
        context.drawBorder(centerX + 20, compY, GUI_WIDTH - 40, compHeight, 0xFF3498DB);
        
        // Title
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("🗺️ Regional Air Quality Comparison").formatted(Formatting.BOLD, Formatting.AQUA),
            centerX + 30, compY + 10, 0xFFFFFF);
        
        // Region mini cards
        String[] regions = {"Varna", "Zonguldak", "Odesa", "Trabzon", "Romania"};
        int[] aqiValues = {65, 145, 78, 92, 58};
        
        for (int i = 0; i < regions.length; i++) {
            int cardX = centerX + 40 + i * 160;
            int cardY = compY + 35;
            int cardWidth = 140;
            int cardHeight = 50;
            
            // Card background
            int aqiColor = getAQIColor(aqiValues[i]);
            context.fill(cardX, cardY, cardX + cardWidth, cardY + cardHeight, (aqiColor & 0x00FFFFFF) | 0x30000000);
            context.drawBorder(cardX, cardY, cardWidth, cardHeight, aqiColor);
            
            // Region name
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal(regions[i]),
                cardX + cardWidth / 2, cardY + 5, 0xFFFFFF);
            
            // AQI value
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("AQI: " + aqiValues[i]).formatted(Formatting.BOLD),
                cardX + cardWidth / 2, cardY + 20, aqiColor);
            
            // Status
            String status = getAQIStatus(aqiValues[i]);
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal(status),
                cardX + cardWidth / 2, cardY + 35, aqiColor);
        }
    }
    
    private void renderTooltips(DrawContext context, int mouseX, int mouseY) {
        // Check if mouse is over a pollutant card
        int cardsY = centerY + 80;
        int cardWidth = 280;
        int cardHeight = 180;
        int spacing = 20;
        
        String[] pollutants = {"PM2.5", "PM10", "NO2", "SO2", "CO", "O3"};
        
        for (int i = 0; i < pollutants.length; i++) {
            int row = i / 3;
            int col = i % 3;
            int cardX = centerX + 20 + col * (cardWidth + spacing);
            int cardY = cardsY + row * (cardHeight + spacing);
            
            if (mouseX >= cardX && mouseX <= cardX + cardWidth &&
                mouseY >= cardY && mouseY <= cardY + cardHeight) {
                
                PollutantData data = pollutantData.get(pollutants[i]);
                if (data != null) {
                    String tooltip = pollutants[i] + " - " + data.getDescription() +
                                   "\nCurrent: " + String.format("%.1f", data.currentValue) + " " + data.unit +
                                   "\nThreshold: " + String.format("%.1f", data.threshold) + " " + data.unit +
                                   "\nHealth Impact: " + data.getHealthImpact();
                    
                    context.drawTooltip(this.textRenderer, Text.literal(tooltip), mouseX, mouseY);
                }
                break;
            }
        }
    }
    
    private int getAQIColor(int aqi) {
        if (aqi <= 50) return 0xFF2ECC71;
        if (aqi <= 100) return 0xFFF1C40F;
        if (aqi <= 150) return 0xFFF39C12;
        if (aqi <= 200) return 0xFFE74C3C;
        return 0xFF8B008B;
    }
    
    private String getAQIStatus(int aqi) {
        if (aqi <= 50) return "Good";
        if (aqi <= 100) return "Moderate";
        if (aqi <= 150) return "Unhealthy";
        if (aqi <= 200) return "Very Unhealthy";
        return "Hazardous";
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
    
    // Helper class for pollutant data
    private static class PollutantData {
        public final String pollutant;
        public final String unit;
        public double currentValue;
        public final double threshold;
        public final int color;
        public final String icon;
        
        public PollutantData(String pollutant, String unit, double currentValue, double threshold, int color, String icon) {
            this.pollutant = pollutant;
            this.unit = unit;
            this.currentValue = currentValue;
            this.threshold = threshold;
            this.color = color;
            this.icon = icon;
        }
        
        public String getDescription() {
            return switch (pollutant) {
                case "PM2.5" -> "Fine particulate matter";
                case "PM10" -> "Coarse particulate matter";
                case "NO2" -> "Nitrogen dioxide from vehicles";
                case "SO2" -> "Sulfur dioxide from industry";
                case "CO" -> "Carbon monoxide";
                case "O3" -> "Ground-level ozone";
                default -> "Air pollutant";
            };
        }
        
        public String getHealthImpact() {
            boolean isHigh = currentValue > threshold;
            return switch (pollutant) {
                case "PM2.5", "PM10" -> isHigh ? "Respiratory irritation" : "Minimal health impact";
                case "NO2" -> isHigh ? "Breathing difficulties" : "Low health risk";
                case "SO2" -> isHigh ? "Eye and throat irritation" : "Safe levels";
                case "CO" -> isHigh ? "Reduced oxygen delivery" : "No immediate concern";
                case "O3" -> isHigh ? "Chest pain, coughing" : "Acceptable exposure";
                default -> "Monitor exposure";
            };
        }
    }
}