package com.cleanairheroes.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import com.cleanairheroes.data.PlayerData;
import com.cleanairheroes.data.PlayerProgress;
import com.cleanairheroes.minigame.MiniGameSystem;

import java.util.*;

public class MiniGameSelectionScreen extends Screen {
    
    private static final int GUI_WIDTH = 960;
    private static final int GUI_HEIGHT = 720;
    
    private PlayerProgress playerProgress;
    private int centerX;
    private int centerY;
    private int animationTicks = 0;
    private int selectedGameIndex = -1;
    
    // Mini-game data
    private List<MiniGameInfo> availableGames = new ArrayList<>();
    
    public MiniGameSelectionScreen() {
        super(Text.literal("Environmental Mini-Games"));
        initializeGameData();
    }
    
    @Override
    protected void init() {
        super.init();
        
        this.centerX = (this.width - GUI_WIDTH) / 2;
        this.centerY = (this.height - GUI_HEIGHT) / 2;
        
        if (this.client != null && this.client.player != null) {
            this.playerProgress = PlayerData.getPlayerProgress(this.client.player);
            updateAvailableGames();
        }
        
        initializeButtons();
    }
    
    private void initializeGameData() {
        // Initialize all mini-games with their info
        availableGames.clear();
        
        // Varna games
        availableGames.add(new MiniGameInfo(
            MiniGameSystem.MiniGameType.SHIP_EMISSION_CONTROL,
            "Ship Emission Control",
            "Remove black smoke clouds from port ships",
            "varna", 3, "🚢", 15, 25,
            "Install scrubber systems and optimize fuel efficiency"
        ));
        
        availableGames.add(new MiniGameInfo(
            MiniGameSystem.MiniGameType.GREEN_TRANSPORT_PUZZLE,
            "Green Transport Network",
            "Design eco-friendly city transport system",
            "varna", 2, "🚌", 20, 30,
            "Plan electric bus routes and bicycle infrastructure"
        ));
        
        availableGames.add(new MiniGameInfo(
            MiniGameSystem.MiniGameType.AIR_QUALITY_TRACKING,
            "Air Quality Monitoring",
            "Track and analyze pollution levels",
            "varna", 2, "📊", 10, 28,
            "Install monitoring stations and analyze data patterns"
        ));
        
        // Zonguldak games
        availableGames.add(new MiniGameInfo(
            MiniGameSystem.MiniGameType.MINE_DUST_CONTROL,
            "Mine Dust Suppression",
            "Control coal mining dust emissions",
            "zonguldak", 4, "⛏️", 25, 35,
            "Install water spray systems and worker protection"
        ));
        
        availableGames.add(new MiniGameInfo(
            MiniGameSystem.MiniGameType.THERMAL_PLANT_MODERNIZATION,
            "Power Plant Upgrade",
            "Modernize thermal plant with filters",
            "zonguldak", 5, "⚡", 30, 40,
            "Install FGD, SCR, and electrostatic precipitators"
        ));
        
        availableGames.add(new MiniGameInfo(
            MiniGameSystem.MiniGameType.TREE_PLANTING_SYSTEM,
            "Reforestation Project",
            "Create natural air filter zones",
            "zonguldak", 2, "🌳", 15, 20,
            "Strategic tree planting and forest development"
        ));
        
        // Odesa games
        availableGames.add(new MiniGameInfo(
            MiniGameSystem.MiniGameType.SHIP_TRAFFIC_MANAGEMENT,
            "Port Traffic Control",
            "Optimize marine traffic for emissions",
            "odesa", 3, "⚓", 20, 32,
            "Schedule ships and manage port operations efficiently"
        ));
        
        availableGames.add(new MiniGameInfo(
            MiniGameSystem.MiniGameType.FACTORY_TRANSFORMATION,
            "Industrial Modernization",
            "Transform factories with clean technology",
            "odesa", 4, "🏭", 25, 30,
            "Upgrade factory systems and reduce emissions"
        ));
        
        availableGames.add(new MiniGameInfo(
            MiniGameSystem.MiniGameType.CITY_GREEN_PLANNING,
            "Urban Green Design",
            "Create green spaces and parks",
            "odesa", 3, "🌳", 18, 38,
            "Design sustainable urban green infrastructure"
        ));
        
        // Trabzon games
        availableGames.add(new MiniGameInfo(
            MiniGameSystem.MiniGameType.SMART_TRAFFIC_OPTIMIZATION,
            "Smart Traffic Systems",
            "Optimize traffic flow in mountain city",
            "trabzon", 3, "🚦", 22, 28,
            "Implement AI traffic control and flow optimization"
        ));
        
        availableGames.add(new MiniGameInfo(
            MiniGameSystem.MiniGameType.HOME_ENERGY_CONVERSION,
            "Home Energy Transition",
            "Convert homes to renewable energy",
            "trabzon", 2, "🏠", 15, 29,
            "Install solar panels and heat pumps"
        ));
        
        availableGames.add(new MiniGameInfo(
            MiniGameSystem.MiniGameType.RECYCLING_CENTER_OPERATION,
            "Waste Management Center",
            "Operate efficient recycling facility",
            "trabzon", 3, "♻️", 20, 26,
            "Sort, process, and manage municipal waste"
        ));
        
        // Romania games
        availableGames.add(new MiniGameInfo(
            MiniGameSystem.MiniGameType.INDUSTRIAL_MODERNIZATION,
            "Regional Industry Upgrade",
            "Transform multi-city industrial sector",
            "romania", 5, "🏗️", 35, 35,
            "Coordinate large-scale industrial transformation"
        ));
        
        availableGames.add(new MiniGameInfo(
            MiniGameSystem.MiniGameType.PUBLIC_TRANSPORT_DESIGN,
            "Transport Network Design",
            "Create integrated regional transport",
            "romania", 4, "🚆", 28, 33,
            "Design multi-modal transport connections"
        ));
        
        availableGames.add(new MiniGameInfo(
            MiniGameSystem.MiniGameType.SUSTAINABLE_FARMING,
            "Agricultural Transformation",
            "Implement sustainable farming practices",
            "romania", 3, "🚜", 18, 30,
            "Reduce agricultural emissions and improve soil"
        ));
        
        availableGames.add(new MiniGameInfo(
            MiniGameSystem.MiniGameType.POWER_PLANT_CONVERSION,
            "Energy System Transformation",
            "Convert coal plants to renewable energy",
            "romania", 5, "🔋", 40, 42,
            "Plan complete energy infrastructure transition"
        ));
    }
    
    private void updateAvailableGames() {
        if (playerProgress == null) return;
        
        String currentRegion = playerProgress.getCurrentRegion();
        
        for (MiniGameInfo game : availableGames) {
            // Check if game is available in current region and unlocked
            game.isAvailable = game.requiredRegion.equals(currentRegion);
            game.isUnlocked = game.isAvailable && playerProgress.getRegionScore(currentRegion) >= game.requiredScore;
        }
    }
    
    private void initializeButtons() {
        // Back button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("← Back"), button -> {
            if (this.client != null) {
                this.client.setScreen(new MissionControlScreen());
            }
        }).dimensions(centerX + 20, centerY + GUI_HEIGHT - 40, 80, 20).build());
        
        // Start game button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🎮 Start Game"), button -> {
            if (selectedGameIndex >= 0 && selectedGameIndex < availableGames.size()) {
                MiniGameInfo selected = availableGames.get(selectedGameIndex);
                if (selected.isUnlocked && this.client != null && this.client.player != null) {
                    // Start the mini-game
                    MiniGameSystem.startMiniGame(this.client.player, selected.gameType);
                    this.close();
                }
            }
        }).dimensions(centerX + GUI_WIDTH - 120, centerY + GUI_HEIGHT - 40, 100, 20).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.animationTicks++;
        
        // Render dark background
        context.fill(0, 0, this.width, this.height, 0x80000000);
        
        // Main GUI background
        context.fill(centerX, centerY, centerX + GUI_WIDTH, centerY + GUI_HEIGHT, 0xFF1A252F);
        context.drawBorder(centerX, centerY, GUI_WIDTH, GUI_HEIGHT, 0xFF3498DB);
        
        // Header
        renderHeader(context);
        
        // Game grid
        renderGameGrid(context, mouseX, mouseY);
        
        // Game details panel
        renderGameDetails(context);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderHeader(DrawContext context) {
        int headerY = centerY + 10;
        
        // Header background
        context.fill(centerX + 10, headerY, centerX + GUI_WIDTH - 10, headerY + 50, 0xFF2C3E50);
        
        // Title
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("🎮 ENVIRONMENTAL MINI-GAMES").formatted(Formatting.BOLD, Formatting.GREEN),
            centerX + GUI_WIDTH / 2, headerY + 10, 0xFFFFFF);
        
        // Current region indicator
        if (playerProgress != null) {
            String region = playerProgress.getCurrentRegion().toUpperCase();
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("Available in: " + region).formatted(Formatting.AQUA),
                centerX + 20, headerY + 30, 0xFFFFFF);
            
            // Player score
            int score = playerProgress.getRegionScore(playerProgress.getCurrentRegion());
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("Your Score: " + score + " points").formatted(Formatting.GOLD),
                centerX + GUI_WIDTH - 200, headerY + 30, 0xFFFFFF);
        }
    }
    
    private void renderGameGrid(DrawContext context, int mouseX, int mouseY) {
        int gridStartX = centerX + 20;
        int gridStartY = centerY + 80;
        int cardWidth = 220;
        int cardHeight = 140;
        int spacing = 15;
        int cardsPerRow = 4;
        
        for (int i = 0; i < availableGames.size(); i++) {
            MiniGameInfo game = availableGames.get(i);
            
            int row = i / cardsPerRow;
            int col = i % cardsPerRow;
            int cardX = gridStartX + col * (cardWidth + spacing);
            int cardY = gridStartY + row * (cardHeight + spacing);
            
            // Check if mouse is over this card
            boolean isHovered = mouseX >= cardX && mouseX <= cardX + cardWidth &&
                              mouseY >= cardY && mouseY <= cardY + cardHeight;
            
            if (isHovered && game.isAvailable) {
                selectedGameIndex = i;
            }
            
            renderGameCard(context, cardX, cardY, cardWidth, cardHeight, game, isHovered);
        }
    }
    
    private void renderGameCard(DrawContext context, int x, int y, int width, int height, MiniGameInfo game, boolean isHovered) {
        // Determine card state colors
        int backgroundColor;
        int borderColor;
        
        if (!game.isAvailable) {
            backgroundColor = 0xFF2C3E50; // Gray for unavailable
            borderColor = 0xFF95A5A6;
        } else if (!game.isUnlocked) {
            backgroundColor = 0xFF8B4513; // Brown for locked
            borderColor = 0xFFE67E22;
        } else if (isHovered) {
            backgroundColor = 0xFF27AE60; // Green for hovered/available
            borderColor = 0xFF2ECC71;
        } else {
            backgroundColor = 0xFF34495E; // Dark blue for available
            borderColor = 0xFF3498DB;
        }
        
        // Card background
        context.fill(x, y, x + width, y + height, backgroundColor);
        context.drawBorder(x, y, width, height, borderColor);
        
        // Lock overlay for unavailable/locked games
        if (!game.isAvailable || !game.isUnlocked) {
            context.fill(x, y, x + width, y + height, 0x80000000);
            
            // Lock icon
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("🔒").formatted(Formatting.RED),
                x + width / 2, y + height / 2 - 10, 0xFFFFFF);
            
            if (!game.isAvailable) {
                context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("Wrong Region"),
                    x + width / 2, y + height / 2 + 10, 0xFFFF0000);
            } else {
                context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal("Need " + game.requiredScore + " points"),
                    x + width / 2, y + height / 2 + 10, 0xFFFF0000);
            }
        }
        
        // Game preview area (top 100px)
        context.fill(x + 5, y + 5, x + width - 5, y + 100, 0xFF1A252F);
        
        // Game icon (large)
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal(game.icon),
            x + width / 2, y + 35, 0xFFFFFF);
        
        // Difficulty stars
        renderDifficultyStars(context, x + 10, y + 65, game.difficulty);
        
        // Region badge
        String regionBadge = game.requiredRegion.substring(0, 3).toUpperCase();
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(regionBadge).formatted(Formatting.BOLD),
            x + width - 30, y + 10, getRegionColor(game.requiredRegion));
        
        // Game title
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal(game.name),
            x + width / 2, y + 105, 0xFFFFFF);
        
        // Estimated time and reward
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("⏱️ " + game.estimatedMinutes + " min"),
            x + 5, y + height - 25, 0xFF95A5A6);
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("🌟 " + game.rewardPoints + " pts"),
            x + width - 60, y + height - 25, 0xFFF1C40F);
    }
    
    private void renderDifficultyStars(DrawContext context, int x, int y, int difficulty) {
        for (int i = 0; i < 5; i++) {
            String star = i < difficulty ? "⭐" : "☆";
            Formatting color = i < difficulty ? Formatting.YELLOW : Formatting.GRAY;
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(star).formatted(color),
                x + i * 12, y, 0xFFFFFF);
        }
    }
    
    private void renderGameDetails(DrawContext context) {
        int detailsY = centerY + GUI_HEIGHT - 120;
        
        // Details panel background
        context.fill(centerX + 20, detailsY, centerX + GUI_WIDTH - 20, centerY + GUI_HEIGHT - 60, 0xFF2C3E50);
        context.drawBorder(centerX + 20, detailsY, GUI_WIDTH - 40, 60, 0xFF3498DB);
        
        if (selectedGameIndex >= 0 && selectedGameIndex < availableGames.size()) {
            MiniGameInfo selected = availableGames.get(selectedGameIndex);
            
            // Game description
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("Selected: " + selected.name).formatted(Formatting.BOLD, Formatting.YELLOW),
                centerX + 30, detailsY + 10, 0xFFFFFF);
            
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(selected.description),
                centerX + 30, detailsY + 25, 0xFFBDC3C7);
            
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("Strategy: " + selected.strategy),
                centerX + 30, detailsY + 40, 0xFF95A5A6);
            
        } else {
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Hover over a mini-game to see details"),
                centerX + GUI_WIDTH / 2, detailsY + 25, 0xFF95A5A6);
        }
    }
    
    private int getRegionColor(String region) {
        return switch (region) {
            case "varna" -> 0xFF3498DB;      // Blue
            case "zonguldak" -> 0xFF8B4513;  // Brown
            case "odesa" -> 0xFF2ECC71;      // Green
            case "trabzon" -> 0xFFF39C12;    // Orange
            case "romania" -> 0xFF9B59B6;    // Purple
            default -> 0xFF95A5A6;           // Gray
        };
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
    
    // Helper class for mini-game information
    private static class MiniGameInfo {
        public final MiniGameSystem.MiniGameType gameType;
        public final String name;
        public final String description;
        public final String requiredRegion;
        public final int difficulty; // 1-5 stars
        public final String icon;
        public final int estimatedMinutes;
        public final int rewardPoints;
        public final String strategy;
        
        public boolean isAvailable = false;
        public boolean isUnlocked = false;
        public final int requiredScore;
        
        public MiniGameInfo(MiniGameSystem.MiniGameType gameType, String name, String description,
                          String requiredRegion, int difficulty, String icon, int estimatedMinutes,
                          int rewardPoints, String strategy) {
            this.gameType = gameType;
            this.name = name;
            this.description = description;
            this.requiredRegion = requiredRegion;
            this.difficulty = difficulty;
            this.icon = icon;
            this.estimatedMinutes = estimatedMinutes;
            this.rewardPoints = rewardPoints;
            this.strategy = strategy;
            this.requiredScore = difficulty * 10; // Difficulty-based score requirement
        }
    }
}