package com.cleanairheroes.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import com.cleanairheroes.data.PlayerData;
import com.cleanairheroes.data.PlayerProgress;

public class TravelHubScreen extends Screen {
    
    private static final int GUI_WIDTH = 1400;
    private static final int GUI_HEIGHT = 900;
    
    private PlayerProgress playerProgress;
    private int centerX;
    private int centerY;
    private int animationTicks = 0;
    private int selectedRegion = -1;
    private float mapRotation = 0.0f;
    
    public TravelHubScreen() {
        super(Text.literal("Global Travel Hub"));
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
        // Region travel buttons positioned on the map
        RegionInfo[] regions = getRegionInfo();
        
        for (int i = 0; i < regions.length; i++) {
            final int regionIndex = i;
            final RegionInfo region = regions[i];
            
            boolean isUnlocked = playerProgress != null && 
                               playerProgress.getUnlockedRegions().contains(region.name.toLowerCase());
            
            int buttonX = centerX + region.mapX;
            int buttonY = centerY + region.mapY;
            
            Text buttonText = isUnlocked ? 
                Text.literal("🌍 " + region.name) : 
                Text.literal("🔒 " + region.name);
            
            this.addDrawableChild(ButtonWidget.builder(buttonText, button -> {
                if (isUnlocked) {
                    selectedRegion = regionIndex;
                    // Implement travel logic here
                }
            }).dimensions(buttonX, buttonY, 180, 30).build());
        }
        
        // Quick travel options
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🏠 Return to Base"), button -> {
            // Implement return to spawn logic
        }).dimensions(centerX + 40, centerY + GUI_HEIGHT - 100, 200, 30).build());
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("📍 Set Waypoint"), button -> {
            // Implement waypoint setting
        }).dimensions(centerX + 260, centerY + GUI_HEIGHT - 100, 200, 30).build());
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🗺️ Full Map View"), button -> {
            if (this.client != null) {
                this.client.setScreen(new AirQualityDashboard());
            }
        }).dimensions(centerX + 480, centerY + GUI_HEIGHT - 100, 200, 30).build());
        
        // Back button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("← Back to Protection Center"), button -> {
            if (this.client != null) {
                this.client.setScreen(new ProtectionCenterScreen());
            }
        }).dimensions(centerX + GUI_WIDTH - 250, centerY + GUI_HEIGHT - 50, 230, 30).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.animationTicks++;
        this.mapRotation += 0.1f;
        
        // Render space background
        renderSpaceBackground(context);
        
        // Main hub background
        context.fill(centerX, centerY, centerX + GUI_WIDTH, centerY + GUI_HEIGHT, 0xF0101522);
        context.drawBorder(centerX, centerY, GUI_WIDTH, GUI_HEIGHT, 0xFF1E3A8A);
        
        // Header
        renderHubHeader(context);
        
        // Interactive world map
        renderWorldMap(context, mouseX, mouseY);
        
        // Region information panel
        renderRegionInfoPanel(context);
        
        // Travel statistics
        renderTravelStatistics(context);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderSpaceBackground(DrawContext context) {
        // Dark space gradient
        for (int y = 0; y < this.height; y++) {
            float factor = (float) y / this.height;
            int blue = (int) (10 + (25 - 10) * factor);
            int color = 0xFF000000 | (5 << 16) | (10 << 8) | blue;
            context.fill(0, y, this.width, y + 1, color);
        }
        
        // Animated stars
        for (int i = 0; i < 100; i++) {
            int starX = (int) ((i * 127) % this.width);
            int starY = (int) ((i * 211 + animationTicks * 0.5) % this.height);
            int brightness = (int) (128 + 127 * Math.sin(animationTicks * 0.01 + i));
            
            if (brightness > 200) {
                context.fill(starX, starY, starX + 1, starY + 1, 0xFF000000 | (brightness << 16) | (brightness << 8) | brightness);
            }
        }
    }
    
    private void renderHubHeader(DrawContext context) {
        int headerHeight = 80;
        
        // Header background with glow effect
        int glowAlpha = (int) (32 + 16 * Math.sin(animationTicks * 0.1));
        context.fill(centerX + 20, centerY + 20, centerX + GUI_WIDTH - 20, centerY + headerHeight, 
                    (glowAlpha << 24) | 0x001E3A8A);
        context.fill(centerX + 25, centerY + 25, centerX + GUI_WIDTH - 25, centerY + headerHeight - 5, 0xFF1E3A8A);
        
        // Animated globe icon
        float pulseScale = 1.0f + 0.2f * (float) Math.sin(animationTicks * 0.15);
        
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("🌍").formatted(Formatting.BLUE),
            centerX + GUI_WIDTH / 2 - 200, centerY + 45, 0xFFFFFF);
        
        // Main title with animation
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("GLOBAL TRAVEL HUB").formatted(Formatting.BOLD, Formatting.AQUA),
            centerX + GUI_WIDTH / 2, centerY + 40, 0xFFFFFF);
        
        // Subtitle
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("Instant Transportation to Clean Air Mission Sites").formatted(Formatting.ITALIC),
            centerX + GUI_WIDTH / 2, centerY + 65, 0xFF94A3B8);
        
        // Status indicator
        String status = "🟢 ALL SYSTEMS OPERATIONAL";
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal(status).formatted(Formatting.GREEN),
            centerX + GUI_WIDTH / 2 + 200, centerY + 45, 0xFFFFFF);
    }
    
    private void renderWorldMap(DrawContext context, int mouseX, int mouseY) {
        int mapStartY = centerY + 120;
        int mapHeight = 500;
        int mapWidth = GUI_WIDTH - 300; // Leave space for info panel
        
        // Map background
        context.fill(centerX + 20, mapStartY, centerX + mapWidth, mapStartY + mapHeight, 0xFF0F172A);
        context.drawBorder(centerX + 20, mapStartY, mapWidth - 20, mapHeight, 0xFF1E293B);
        
        // World map grid
        renderMapGrid(context, centerX + 20, mapStartY, mapWidth - 20, mapHeight);
        
        // Region markers
        RegionInfo[] regions = getRegionInfo();
        for (int i = 0; i < regions.length; i++) {
            renderRegionMarker(context, regions[i], centerX + regions[i].mapX, mapStartY + regions[i].mapY, 
                             mouseX, mouseY, i == selectedRegion);
        }
        
        // Flight paths between unlocked regions
        renderFlightPaths(context, centerX + 20, mapStartY);
        
        // Map legend
        renderMapLegend(context, centerX + 20, mapStartY + mapHeight + 10);
    }
    
    private void renderMapGrid(DrawContext context, int x, int y, int width, int height) {
        // Longitude lines
        for (int i = 0; i <= 12; i++) {
            int lineX = x + (i * width / 12);
            int alpha = i % 3 == 0 ? 64 : 32;
            context.fill(lineX, y, lineX + 1, y + height, (alpha << 24) | 0x00FFFFFF);
        }
        
        // Latitude lines
        for (int i = 0; i <= 8; i++) {
            int lineY = y + (i * height / 8);
            int alpha = i % 2 == 0 ? 64 : 32;
            context.fill(x, lineY, x + width, lineY + 1, (alpha << 24) | 0x00FFFFFF);
        }
    }
    
    private void renderRegionMarker(DrawContext context, RegionInfo region, int x, int y, 
                                  int mouseX, int mouseY, boolean isSelected) {
        boolean isUnlocked = playerProgress != null && 
                           playerProgress.getUnlockedRegions().contains(region.name.toLowerCase());
        boolean isHovered = mouseX >= x - 15 && mouseX <= x + 15 && mouseY >= y - 15 && mouseY <= y + 15;
        
        // Marker background
        int markerSize = isSelected ? 20 : (isHovered ? 18 : 15);
        int markerColor = isUnlocked ? region.color : 0xFF6B7280;
        
        // Pulsing effect for unlocked regions
        if (isUnlocked) {
            float pulse = 1.0f + 0.3f * (float) Math.sin(animationTicks * 0.2);
            markerSize = (int) (markerSize * pulse);
        }
        
        // Marker circle
        context.fill(x - markerSize/2, y - markerSize/2, x + markerSize/2, y + markerSize/2, markerColor);
        context.drawBorder(x - markerSize/2, y - markerSize/2, markerSize, markerSize, 0xFFFFFFFF);
        
        // Region icon
        String icon = isUnlocked ? region.icon : "🔒";
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal(icon),
            x, y - 5, 0xFFFFFFFF);
        
        // Region name on hover
        if (isHovered) {
            int nameWidth = this.textRenderer.getWidth(region.name) + 20;
            context.fill(x - nameWidth/2, y + 20, x + nameWidth/2, y + 40, 0xFF1F2937);
            context.drawBorder(x - nameWidth/2, y + 20, nameWidth, 20, region.color);
            
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal(region.name),
                x, y + 25, 0xFFFFFF);
                
            // Show unlock status
            String statusText = isUnlocked ? "UNLOCKED" : "LOCKED";
            int statusColor = isUnlocked ? 0xFF10B981 : 0xFFEF4444;
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal(statusText).formatted(Formatting.BOLD),
                x, y + 45, statusColor);
        }
    }
    
    private void renderFlightPaths(DrawContext context, int baseX, int baseY) {
        if (playerProgress == null) return;
        
        RegionInfo[] regions = getRegionInfo();
        
        // Draw paths between consecutive unlocked regions
        for (int i = 0; i < regions.length - 1; i++) {
            boolean currentUnlocked = playerProgress.getUnlockedRegions().contains(regions[i].name.toLowerCase());
            boolean nextUnlocked = playerProgress.getUnlockedRegions().contains(regions[i + 1].name.toLowerCase());
            
            if (currentUnlocked && nextUnlocked) {
                int x1 = baseX + regions[i].mapX;
                int y1 = baseY + regions[i].mapY;
                int x2 = baseX + regions[i + 1].mapX;
                int y2 = baseY + regions[i + 1].mapY;
                
                // Animated dashed line
                drawAnimatedPath(context, x1, y1, x2, y2, 0xFF10B981);
            }
        }
    }
    
    private void drawAnimatedPath(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        int dx = x2 - x1;
        int dy = y2 - y1;
        int distance = (int) Math.sqrt(dx * dx + dy * dy);
        
        for (int i = 0; i < distance; i += 10) {
            float progress = (float) i / distance;
            int x = x1 + (int) (dx * progress);
            int y = y1 + (int) (dy * progress);
            
            // Animated dash effect
            if ((i + animationTicks / 2) % 20 < 10) {
                context.fill(x - 1, y - 1, x + 1, y + 1, color);
            }
        }
    }
    
    private void renderMapLegend(DrawContext context, int x, int y) {
        String[] legendItems = {"🌍 Unlocked Region", "🔒 Locked Region", "✈️ Flight Path", "📍 Current Location"};
        int[] legendColors = {0xFF10B981, 0xFF6B7280, 0xFF10B981, 0xFFF59E0B};
        
        for (int i = 0; i < legendItems.length; i++) {
            int itemX = x + i * 200;
            
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(legendItems[i]),
                itemX, y, legendColors[i]);
        }
    }
    
    private void renderRegionInfoPanel(DrawContext context) {
        int panelX = centerX + GUI_WIDTH - 280;
        int panelY = centerY + 120;
        int panelWidth = 260;
        int panelHeight = 500;
        
        // Panel background
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF1F2937);
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFF374151);
        
        // Panel title
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("Region Information").formatted(Formatting.BOLD),
            panelX + panelWidth / 2, panelY + 20, 0xFFFFFF);
        
        if (selectedRegion >= 0 && selectedRegion < getRegionInfo().length) {
            RegionInfo region = getRegionInfo()[selectedRegion];
            renderSelectedRegionInfo(context, panelX + 20, panelY + 50, panelWidth - 40, region);
        } else {
            // Show general travel information
            renderGeneralTravelInfo(context, panelX + 20, panelY + 50, panelWidth - 40);
        }
    }
    
    private void renderSelectedRegionInfo(DrawContext context, int x, int y, int width, RegionInfo region) {
        boolean isUnlocked = playerProgress != null && 
                           playerProgress.getUnlockedRegions().contains(region.name.toLowerCase());
        
        // Region icon and name
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(region.icon + " " + region.name).formatted(Formatting.BOLD),
            x, y, region.color);
        
        // Status
        String status = isUnlocked ? "ACCESSIBLE" : "LOCKED";
        int statusColor = isUnlocked ? 0xFF10B981 : 0xFFEF4444;
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Status: " + status),
            x, y + 20, statusColor);
        
        // Description
        String[] descLines = wrapText(region.description, width);
        for (int i = 0; i < Math.min(descLines.length, 8); i++) {
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(descLines[i]),
                x, y + 50 + i * 12, 0xFFD1D5DB);
        }
        
        if (isUnlocked) {
            // Mission progress
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("Mission Progress:").formatted(Formatting.BOLD),
                x, y + 180, 0xFFFFFF);
            
            // Simulate mission data
            String[] missions = {"Air Quality Monitoring", "Equipment Installation", "Community Engagement"};
            boolean[] completed = {true, true, false};
            
            for (int i = 0; i < missions.length; i++) {
                String status_icon = completed[i] ? "✅" : "⏳";
                int mission_color = completed[i] ? 0xFF10B981 : 0xFFF59E0B;
                
                context.drawTextWithShadow(this.textRenderer,
                    Text.literal(status_icon + " " + missions[i]),
                    x + 10, y + 200 + i * 15, mission_color);
            }
            
            // Travel time
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("Travel Time: " + region.travelTime).formatted(Formatting.ITALIC),
                x, y + 270, 0xFF94A3B8);
            
            // Last visit
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("Last Visit: " + region.lastVisit).formatted(Formatting.ITALIC),
                x, y + 290, 0xFF94A3B8);
        } else {
            // Unlock requirements
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("Unlock Requirements:").formatted(Formatting.BOLD),
                x, y + 180, 0xFFFFFF);
            
            String[] requirements = region.unlockRequirements;
            for (int i = 0; i < requirements.length; i++) {
                context.drawTextWithShadow(this.textRenderer,
                    Text.literal("• " + requirements[i]),
                    x + 10, y + 200 + i * 15, 0xFFEF4444);
            }
        }
    }
    
    private void renderGeneralTravelInfo(DrawContext context, int x, int y, int width) {
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Welcome to the Global Travel Hub!").formatted(Formatting.BOLD),
            x, y, 0xFFFFFF);
        
        String[] infoLines = {
            "",
            "Select a region marker to view",
            "detailed information and travel",
            "options.",
            "",
            "Unlocked regions are shown in",
            "color and can be accessed",
            "instantly.",
            "",
            "Complete missions to unlock",
            "new regions and expand your",
            "environmental impact."
        };
        
        for (int i = 0; i < infoLines.length; i++) {
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(infoLines[i]),
                x, y + 20 + i * 12, 0xFFD1D5DB);
        }
    }
    
    private void renderTravelStatistics(DrawContext context) {
        int statsY = centerY + 650;
        int statsHeight = 100;
        
        // Stats background
        context.fill(centerX + 20, statsY, centerX + GUI_WIDTH - 300, statsY + statsHeight, 0xFF374151);
        context.drawBorder(centerX + 20, statsY, GUI_WIDTH - 320, statsHeight, 0xFF4B5563);
        
        // Title
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Travel Statistics").formatted(Formatting.BOLD),
            centerX + 40, statsY + 15, 0xFFFFFF);
        
        if (playerProgress != null) {
            // Statistics grid
            String[] statLabels = {"Total Trips", "Distance Traveled", "Regions Unlocked", "Time Saved"};
            String[] statValues = {
                String.valueOf(playerProgress.getTotalTrips()),
                playerProgress.getTotalDistance() + " km",
                playerProgress.getUnlockedRegions().size() + "/5",
                playerProgress.getTimeSaved() + " hours"
            };
            int[] statColors = {0xFF3B82F6, 0xFF10B981, 0xFFF59E0B, 0xFFEF4444};
            
            for (int i = 0; i < statLabels.length; i++) {
                int statX = centerX + 40 + i * 200;
                
                context.drawTextWithShadow(this.textRenderer,
                    Text.literal(statValues[i]).formatted(Formatting.BOLD),
                    statX, statsY + 35, statColors[i]);
                
                context.drawTextWithShadow(this.textRenderer,
                    Text.literal(statLabels[i]),
                    statX, statsY + 50, 0xFFD1D5DB);
            }
        }
    }
    
    private RegionInfo[] getRegionInfo() {
        return new RegionInfo[] {
            new RegionInfo("Varna, Bulgaria", "🇧🇬", 
                "Major Black Sea port city focusing on maritime pollution control and green shipping initiatives.",
                0xFF3B82F6, 300, 180, "Instant", "2 hours ago",
                new String[]{"Complete tutorial missions"}),
            
            new RegionInfo("Zonguldak, Turkey", "🇹🇷", 
                "Coal mining region transitioning to clean energy with thermal plant modernization projects.",
                0xFFEF4444, 350, 220, "5 minutes", "1 day ago",
                new String[]{"Complete Varna missions", "Install 10 equipment pieces"}),
            
            new RegionInfo("Odesa, Ukraine", "🇺🇦", 
                "UNESCO heritage port implementing advanced air quality monitoring and research programs.",
                0xFFF59E0B, 340, 200, "3 minutes", "3 days ago",
                new String[]{"Complete Zonguldak missions", "Achieve AQI improvement"}),
            
            new RegionInfo("Trabzon, Turkey", "🇹🇷", 
                "Mountain city addressing traffic pollution through smart transportation and urban planning.",
                0xFF10B981, 360, 210, "7 minutes", "1 week ago",
                new String[]{"Complete Odesa missions", "Form partnerships"}),
            
            new RegionInfo("Southeast Romania", "🇷🇴", 
                "Multi-city region coordinating international environmental policies and green corridors.",
                0xFF8B5CF6, 320, 170, "15 minutes", "Never",
                new String[]{"Complete all previous regions", "Reach expert level"})
        };
    }
    
    private String[] wrapText(String text, int maxWidth) {
        int charWidth = 6;
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
    
    // Helper class for region information
    private static class RegionInfo {
        public final String name;
        public final String icon;
        public final String description;
        public final int color;
        public final int mapX;
        public final int mapY;
        public final String travelTime;
        public final String lastVisit;
        public final String[] unlockRequirements;
        
        public RegionInfo(String name, String icon, String description, int color, 
                         int mapX, int mapY, String travelTime, String lastVisit, 
                         String[] unlockRequirements) {
            this.name = name;
            this.icon = icon;
            this.description = description;
            this.color = color;
            this.mapX = mapX;
            this.mapY = mapY;
            this.travelTime = travelTime;
            this.lastVisit = lastVisit;
            this.unlockRequirements = unlockRequirements;
        }
    }
}