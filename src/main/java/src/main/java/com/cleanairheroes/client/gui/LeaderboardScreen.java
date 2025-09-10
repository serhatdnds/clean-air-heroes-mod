package com.cleanairheroes.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import com.cleanairheroes.data.PlayerData;
import com.cleanairheroes.data.PlayerProgress;

public class LeaderboardScreen extends Screen {
    
    private static final int GUI_WIDTH = 1400;
    private static final int GUI_HEIGHT = 900;
    
    private PlayerProgress playerProgress;
    private int centerX;
    private int centerY;
    private int animationTicks = 0;
    private int selectedCategory = 0; // 0: Overall, 1: Environmental, 2: Technical, 3: Regional, 4: Social
    private int scrollOffset = 0;
    private float rankAnimations[] = new float[50]; // For rank change animations
    
    public LeaderboardScreen() {
        super(Text.literal("Global Clean Air Heroes Leaderboard"));
    }
    
    @Override
    protected void init() {
        super.init();
        
        this.centerX = (this.width - GUI_WIDTH) / 2;
        this.centerY = (this.height - GUI_HEIGHT) / 2;
        
        if (this.client != null && this.client.player != null) {
            this.playerProgress = PlayerData.getPlayerProgress(this.client.player);
        }
        
        // Initialize rank animations
        for (int i = 0; i < rankAnimations.length; i++) {
            rankAnimations[i] = (float) Math.random();
        }
        
        initializeButtons();
    }
    
    private void initializeButtons() {
        // Category selection buttons
        String[] categories = {"🏆 Overall Ranking", "🌱 Environmental Impact", "⚙️ Technical Mastery", "🌍 Regional Leaders", "👥 Social Engagement"};
        int categoryWidth = 220;
        
        for (int i = 0; i < categories.length; i++) {
            final int categoryIndex = i;
            this.addDrawableChild(ButtonWidget.builder(Text.literal(categories[i]), button -> {
                selectedCategory = categoryIndex;
                scrollOffset = 0;
            }).dimensions(centerX + 40 + i * (categoryWidth + 20), centerY + 60, categoryWidth, 30).build());
        }
        
        // Time period filters
        this.addDrawableChild(ButtonWidget.builder(Text.literal("📅 This Week"), button -> {
            // Implement time filter
        }).dimensions(centerX + 40, centerY + 100, 120, 25).build());
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("📅 This Month"), button -> {
            // Implement time filter
        }).dimensions(centerX + 170, centerY + 100, 120, 25).build());
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("📅 All Time"), button -> {
            // Implement time filter
        }).dimensions(centerX + 300, centerY + 100, 120, 25).build());
        
        // Scroll controls
        this.addDrawableChild(ButtonWidget.builder(Text.literal("↑ Scroll Up"), button -> {
            scrollOffset = Math.max(0, scrollOffset - 120);
        }).dimensions(centerX + GUI_WIDTH - 150, centerY + 140, 120, 25).build());
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("↓ Scroll Down"), button -> {
            scrollOffset = Math.min(2000, scrollOffset + 120);
        }).dimensions(centerX + GUI_WIDTH - 150, centerY + 170, 120, 25).build());
        
        // Player search
        this.addDrawableChild(ButtonWidget.builder(Text.literal("🔍 Find Player"), button -> {
            // Implement player search
        }).dimensions(centerX + GUI_WIDTH - 300, centerY + 100, 120, 25).build());
        
        // Back button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("← Back to Protection Center"), button -> {
            if (this.client != null) {
                this.client.setScreen(new ProtectionCenterScreen());
            }
        }).dimensions(centerX + 40, centerY + GUI_HEIGHT - 50, 250, 30).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.animationTicks++;
        
        // Update rank animations
        for (int i = 0; i < rankAnimations.length; i++) {
            rankAnimations[i] += 0.01f + (float) Math.sin(animationTicks * 0.02 + i) * 0.005f;
            if (rankAnimations[i] > 1.0f) rankAnimations[i] = 0.0f;
        }
        
        // Render championship background
        renderChampionshipBackground(context);
        
        // Main leaderboard interface
        context.fill(centerX, centerY, centerX + GUI_WIDTH, centerY + GUI_HEIGHT, 0xF0F7FAFC);
        context.drawBorder(centerX, centerY, GUI_WIDTH, GUI_HEIGHT, 0xFFD97706);
        
        // Header with crown
        renderLeaderboardHeader(context);
        
        // Top 3 podium
        renderPodium(context);
        
        // Main leaderboard
        renderMainLeaderboard(context, mouseX, mouseY);
        
        // Player position panel
        renderPlayerPosition(context);
        
        // Statistics panel
        renderLeaderboardStats(context);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderChampionshipBackground(DrawContext context) {
        // Golden gradient background
        for (int y = 0; y < this.height; y++) {
            float factor = (float) y / this.height;
            int red = (int) (255 - (255 - 245) * factor);
            int green = (int) (255 - (255 - 245) * factor);
            int blue = (int) (255 - (255 - 220) * factor);
            int color = 0xFF000000 | (red << 16) | (green << 8) | blue;
            context.fill(0, y, this.width, y + 1, color);
        }
        
        // Championship sparkles
        for (int i = 0; i < 80; i++) {
            float sparklePhase = (animationTicks * 0.02f + i * 0.1f) % (2 * Math.PI);
            int sparkleX = (int) ((i * 137 + Math.sin(sparklePhase) * 50) % this.width);
            int sparkleY = (int) ((i * 211 + Math.cos(sparklePhase) * 30) % this.height);
            int sparkleAlpha = (int) (128 + 64 * Math.sin(sparklePhase * 2));
            
            if (sparkleAlpha > 160) {
                context.fill(sparkleX, sparkleY, sparkleX + 2, sparkleY + 2, 
                           (sparkleAlpha << 24) | 0x00FFD700);
            }
        }
    }
    
    private void renderLeaderboardHeader(DrawContext context) {
        int headerHeight = 50;
        
        // Header background with crown effect
        int crownGlow = (int) (48 + 32 * Math.sin(animationTicks * 0.1));
        context.fill(centerX + 10, centerY + 10, centerX + GUI_WIDTH - 10, centerY + headerHeight,
                    (crownGlow << 24) | 0x00D97706);
        context.fill(centerX + 15, centerY + 15, centerX + GUI_WIDTH - 15, centerY + headerHeight - 5, 0xFFD97706);
        
        // Crown icon
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("👑"),
            centerX + 30, centerY + 25, 0xFFFFD700);
        
        // Main title
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("GLOBAL CLEAN AIR HEROES LEADERBOARD").formatted(Formatting.BOLD, Formatting.YELLOW),
            centerX + GUI_WIDTH / 2, centerY + 25, 0xFFFFFF);
        
        // Live status
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("🔴 LIVE RANKINGS").formatted(Formatting.RED, Formatting.BOLD),
            centerX + GUI_WIDTH - 150, centerY + 25, 0xFFFFFF);
        
        // Category indicator
        String[] categoryNames = {"Overall", "Environmental", "Technical", "Regional", "Social"};
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Category: " + categoryNames[selectedCategory] + " | Last Updated: Now").formatted(Formatting.ITALIC),
            centerX + 30, centerY + 40, 0xFFFCD34D);
    }
    
    private void renderPodium(DrawContext context) {
        int podiumY = centerY + 140;
        int podiumHeight = 200;
        
        // Get top 3 players
        LeaderboardEntry[] topPlayers = getTopPlayers(3);
        
        // Podium platforms
        int[] podiumHeights = {80, 120, 60}; // 2nd, 1st, 3rd
        int[] podiumX = {centerX + 200, centerX + 350, centerX + 500};
        int[] podiumColors = {0xFFC0C0C0, 0xFFFFD700, 0xFFCD7F32}; // Silver, Gold, Bronze
        String[] podiumLabels = {"2nd", "1st", "3rd"};
        int[] playerIndices = {1, 0, 2}; // Order for podium display
        
        for (int i = 0; i < 3; i++) {
            int platformHeight = podiumHeights[i];
            int platformY = podiumY + podiumHeight - platformHeight;
            
            // Podium platform
            context.fill(podiumX[i], platformY, podiumX[i] + 100, podiumY + podiumHeight, podiumColors[i]);
            context.drawBorder(podiumX[i], platformY, 100, platformHeight, 0xFF000000);
            
            // Podium rank
            context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal(podiumLabels[i]).formatted(Formatting.BOLD),
                podiumX[i] + 50, platformY + 10, 0xFF000000);
            
            // Player info (if available)
            if (playerIndices[i] < topPlayers.length) {
                LeaderboardEntry player = topPlayers[playerIndices[i]];
                
                // Player name
                context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal(player.playerName).formatted(Formatting.BOLD),
                    podiumX[i] + 50, platformY - 40, 0xFF1F2937);
                
                // Player avatar (simplified)
                context.fill(podiumX[i] + 35, platformY - 60, podiumX[i] + 65, platformY - 30, player.avatarColor);
                context.drawBorder(podiumX[i] + 35, platformY - 60, 30, 30, 0xFF000000);
                
                // Score
                context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal(String.valueOf(player.score)).formatted(Formatting.YELLOW),
                    podiumX[i] + 50, platformY - 20, 0xFFD97706);
                
                // Trophy/medal icon
                String trophy = i == 1 ? "🏆" : (i == 0 ? "🥈" : "🥉");
                context.drawCenteredTextWithShadow(this.textRenderer,
                    Text.literal(trophy),
                    podiumX[i] + 50, platformY - 75, 0xFFFFFF);
            }
        }
    }
    
    private void renderMainLeaderboard(DrawContext context, int mouseX, int mouseY) {
        int leaderboardX = centerX + 40;
        int leaderboardY = centerY + 360;
        int leaderboardWidth = GUI_WIDTH - 320;
        int leaderboardHeight = 400;
        
        // Leaderboard background
        context.fill(leaderboardX, leaderboardY, leaderboardX + leaderboardWidth, leaderboardY + leaderboardHeight, 0xFFFFFFFF);
        context.drawBorder(leaderboardX, leaderboardY, leaderboardWidth, leaderboardHeight, 0xFFE5E7EB);
        
        // Header row
        renderLeaderboardHeader(context, leaderboardX, leaderboardY, leaderboardWidth);
        
        // Player entries
        LeaderboardEntry[] players = getLeaderboardEntries();
        int entryHeight = 40;
        int visibleEntries = (leaderboardHeight - 40) / entryHeight;
        int startIndex = Math.max(0, scrollOffset / entryHeight);
        
        for (int i = 0; i < Math.min(visibleEntries, players.length - startIndex); i++) {
            int playerIndex = startIndex + i;
            if (playerIndex >= players.length) break;
            
            LeaderboardEntry player = players[playerIndex];
            int entryY = leaderboardY + 40 + i * entryHeight;
            
            // Check if this is the current player
            boolean isCurrentPlayer = playerProgress != null && 
                                    player.playerName.equals(this.client.player.getName().getString());
            
            // Check if mouse is hovering
            boolean isHovered = mouseX >= leaderboardX && mouseX <= leaderboardX + leaderboardWidth &&
                              mouseY >= entryY && mouseY <= entryY + entryHeight;
            
            renderLeaderboardEntry(context, leaderboardX, entryY, leaderboardWidth, entryHeight, 
                                 player, playerIndex + 1, isCurrentPlayer, isHovered);
        }
        
        // Scroll indicator
        if (players.length > visibleEntries) {
            int scrollBarHeight = Math.max(20, (visibleEntries * leaderboardHeight) / players.length);
            int scrollBarY = leaderboardY + 40 + (scrollOffset * (leaderboardHeight - 40 - scrollBarHeight)) / 
                           Math.max(1, (players.length - visibleEntries) * entryHeight);
            
            context.fill(leaderboardX + leaderboardWidth - 8, scrollBarY, 
                        leaderboardX + leaderboardWidth - 3, scrollBarY + scrollBarHeight, 0xFFD97706);
        }
    }
    
    private void renderLeaderboardHeader(DrawContext context, int x, int y, int width) {
        // Header background
        context.fill(x, y, x + width, y + 40, 0xFF1F2937);
        
        // Column headers
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Rank").formatted(Formatting.BOLD),
            x + 20, y + 15, 0xFFFFFF);
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Player").formatted(Formatting.BOLD),
            x + 120, y + 15, 0xFFFFFF);
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Score").formatted(Formatting.BOLD),
            x + 350, y + 15, 0xFFFFFF);
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Region").formatted(Formatting.BOLD),
            x + 450, y + 15, 0xFFFFFF);
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Achievements").formatted(Formatting.BOLD),
            x + 580, y + 15, 0xFFFFFF);
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Status").formatted(Formatting.BOLD),
            x + 750, y + 15, 0xFFFFFF);
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Trend").formatted(Formatting.BOLD),
            x + 850, y + 15, 0xFFFFFF);
    }
    
    private void renderLeaderboardEntry(DrawContext context, int x, int y, int width, int height,
                                      LeaderboardEntry entry, int rank, boolean isCurrentPlayer, boolean isHovered) {
        // Entry background
        int backgroundColor = isCurrentPlayer ? 0xFFFEF3C7 : (isHovered ? 0xFFF8FAFC : 0xFFFFFFFF);
        context.fill(x, y, x + width, y + height, backgroundColor);
        
        if (isCurrentPlayer) {
            context.drawBorder(x, y, width, height, 0xFFD97706);
        }
        
        // Rank with animation
        String rankText = String.valueOf(rank);
        int rankColor = 0xFF1F2937;
        
        if (rank <= 3) {
            rankText = (rank == 1 ? "👑" : rank == 2 ? "🥈" : "🥉") + " " + rank;
            rankColor = 0xFFD97706;
        }
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(rankText).formatted(Formatting.BOLD),
            x + 20, y + 15, rankColor);
        
        // Player avatar (simplified colored square)
        int avatarSize = 20;
        context.fill(x + 90, y + 10, x + 90 + avatarSize, y + 10 + avatarSize, entry.avatarColor);
        context.drawBorder(x + 90, y + 10, avatarSize, avatarSize, 0xFF000000);
        
        // Player name
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(entry.playerName).formatted(isCurrentPlayer ? Formatting.BOLD : Formatting.RESET),
            x + 120, y + 15, isCurrentPlayer ? 0xFFD97706 : 0xFF1F2937);
        
        // Score with formatting
        String scoreText = formatScore(entry.score);
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(scoreText).formatted(Formatting.BOLD),
            x + 350, y + 15, 0xFF059669);
        
        // Region
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(entry.region),
            x + 450, y + 15, 0xFF6B7280);
        
        // Achievements count
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(entry.achievements + " 🏆"),
            x + 580, y + 15, 0xFF8B5CF6);
        
        // Online status
        String statusIcon = entry.isOnline ? "🟢" : "⭕";
        String statusText = entry.isOnline ? "Online" : entry.lastSeen;
        int statusColor = entry.isOnline ? 0xFF10B981 : 0xFF6B7280;
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(statusIcon + " " + statusText),
            x + 750, y + 15, statusColor);
        
        // Trend indicator
        String trendIcon = entry.rankChange > 0 ? "↗" : entry.rankChange < 0 ? "↘" : "→";
        String trendText = entry.rankChange != 0 ? Math.abs(entry.rankChange) + "" : "=";
        int trendColor = entry.rankChange > 0 ? 0xFF10B981 : entry.rankChange < 0 ? 0xFFEF4444 : 0xFF6B7280;
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(trendIcon + trendText),
            x + 850, y + 15, trendColor);
        
        // Level badge
        int level = calculateLevel(entry.score);
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Lv." + level),
            x + width - 60, y + 15, 0xFF7C3AED);
    }
    
    private void renderPlayerPosition(DrawContext context) {
        int panelX = centerX + GUI_WIDTH - 260;
        int panelY = centerY + 360;
        int panelWidth = 240;
        int panelHeight = 200;
        
        // Panel background
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF1F2937);
        context.drawBorder(panelX, panelY, panelWidth, panelHeight, 0xFFD97706);
        
        // Title
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Your Position").formatted(Formatting.BOLD),
            panelX + 15, panelY + 15, 0xFFFFFF);
        
        if (playerProgress != null) {
            // Simulate player ranking data
            int playerRank = Math.max(1, 1000 - playerProgress.getTotalScore() * 2);
            int totalPlayers = 15420;
            float percentile = ((float)(totalPlayers - playerRank) / totalPlayers) * 100;
            
            // Current rank
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("Current Rank:").formatted(Formatting.ITALIC),
                panelX + 15, panelY + 40, 0xFFD1D5DB);
            
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("#" + playerRank).formatted(Formatting.BOLD),
                panelX + 15, panelY + 55, 0xFFD97706);
            
            // Percentile
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("Top " + String.format("%.1f", percentile) + "%"),
                panelX + 100, panelY + 55, 0xFF10B981);
            
            // Score
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("Total Score:").formatted(Formatting.ITALIC),
                panelX + 15, panelY + 80, 0xFFD1D5DB);
            
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(formatScore(playerProgress.getTotalScore())).formatted(Formatting.BOLD),
                panelX + 15, panelY + 95, 0xFF059669);
            
            // Next rank requirements
            int nextRankScore = ((1000 - (playerRank - 1)) / 2);
            int scoreNeeded = Math.max(0, nextRankScore - playerProgress.getTotalScore());
            
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("Next Rank:").formatted(Formatting.ITALIC),
                panelX + 15, panelY + 120, 0xFFD1D5DB);
            
            if (scoreNeeded > 0) {
                context.drawTextWithShadow(this.textRenderer,
                    Text.literal("+" + scoreNeeded + " points needed"),
                    panelX + 15, panelY + 135, 0xFFF59E0B);
            } else {
                context.drawTextWithShadow(this.textRenderer,
                    Text.literal("Rank up available!"),
                    panelX + 15, panelY + 135, 0xFF10B981);
            }
            
            // Weekly change
            int weeklyChange = (int) (Math.random() * 20 - 10); // Simulate weekly change
            String changeText = weeklyChange > 0 ? "↗ +" + weeklyChange : weeklyChange < 0 ? "↘ " + weeklyChange : "→ No change";
            int changeColor = weeklyChange > 0 ? 0xFF10B981 : weeklyChange < 0 ? 0xFFEF4444 : 0xFF6B7280;
            
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("This Week:").formatted(Formatting.ITALIC),
                panelX + 15, panelY + 160, 0xFFD1D5DB);
            
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(changeText),
                panelX + 15, panelY + 175, changeColor);
        }
    }
    
    private void renderLeaderboardStats(DrawContext context) {
        int statsX = centerX + GUI_WIDTH - 260;
        int statsY = centerY + 580;
        int statsWidth = 240;
        int statsHeight = 160;
        
        // Stats background
        context.fill(statsX, statsY, statsX + statsWidth, statsY + statsHeight, 0xFF374151);
        context.drawBorder(statsX, statsY, statsWidth, statsHeight, 0xFF4B5563);
        
        // Title
        context.drawTextWithShadow(this.textRenderer,
            Text.literal("Global Statistics").formatted(Formatting.BOLD),
            statsX + 15, statsY + 15, 0xFFFFFF);
        
        // Statistics
        String[] statLabels = {"Total Heroes:", "Active Players:", "Avg. Score:", "Top Region:"};
        String[] statValues = {"15,420", "2,847", "1,247", "Varna"};
        int[] statColors = {0xFF3B82F6, 0xFF10B981, 0xFFD97706, 0xFF8B5CF6};
        
        for (int i = 0; i < statLabels.length; i++) {
            int statY = statsY + 40 + i * 25;
            
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(statLabels[i]),
                statsX + 15, statY, 0xFFD1D5DB);
            
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(statValues[i]).formatted(Formatting.BOLD),
                statsX + 120, statY, statColors[i]);
        }
    }
    
    private LeaderboardEntry[] getTopPlayers(int count) {
        LeaderboardEntry[] allPlayers = getLeaderboardEntries();
        LeaderboardEntry[] topPlayers = new LeaderboardEntry[Math.min(count, allPlayers.length)];
        System.arraycopy(allPlayers, 0, topPlayers, 0, topPlayers.length);
        return topPlayers;
    }
    
    private LeaderboardEntry[] getLeaderboardEntries() {
        // Generate sample leaderboard data
        return new LeaderboardEntry[] {
            new LeaderboardEntry("GreenGuardian2024", 4580, "Varna", 27, true, "", +3, 0xFF10B981),
            new LeaderboardEntry("EcoWarrior", 4445, "Odesa", 23, true, "", -1, 0xFF3B82F6),
            new LeaderboardEntry("CleanAirChamp", 4230, "Zonguldak", 19, false, "2h ago", +2, 0xFFEF4444),
            new LeaderboardEntry("PollutionSlayer", 4156, "Trabzon", 25, true, "", 0, 0xFF8B5CF6),
            new LeaderboardEntry("AirQualityHero", 3980, "Romania", 21, false, "1d ago", -2, 0xFFF59E0B),
            new LeaderboardEntry("EnvironmentalPro", 3875, "Varna", 18, true, "", +1, 0xFF06B6D4),
            new LeaderboardEntry("GreenTechMaster", 3756, "Odesa", 22, false, "5h ago", +4, 0xFF84CC16),
            new LeaderboardEntry("ClimateDefender", 3689, "Zonguldak", 16, true, "", -1, 0xFFF97316),
            new LeaderboardEntry("EcoInnovator", 3567, "Trabzon", 20, false, "3h ago", +2, 0xFFEC4899),
            new LeaderboardEntry("SustainabilityGuru", 3445, "Romania", 24, true, "", 0, 0xFF6366F1),
            new LeaderboardEntry("AirPurifier", 3234, "Varna", 15, false, "1h ago", -3, 0xFF14B8A6),
            new LeaderboardEntry("GreenSolutions", 3156, "Odesa", 17, true, "", +1, 0xFFA855F7),
            new LeaderboardEntry("EcoFighter", 3089, "Zonguldak", 19, false, "6h ago", +2, 0xFFEAB308),
            new LeaderboardEntry("CleanBreather", 2967, "Trabzon", 14, true, "", -1, 0xFFDC2626),
            new LeaderboardEntry("PureAirAdvocate", 2845, "Romania", 16, false, "2d ago", +3, 0xFF059669),
            new LeaderboardEntry("EnvironmentalGuard", 2734, "Varna", 13, true, "", 0, 0xFF7C3AED),
            new LeaderboardEntry("GreenInitiative", 2667, "Odesa", 18, false, "4h ago", -2, 0xFF0EA5E9),
            new LeaderboardEntry("AirQualityAdvocate", 2589, "Zonguldak", 12, true, "", +1, 0xFFBE185D),
            new LeaderboardEntry("EcoProtector", 2456, "Trabzon", 15, false, "8h ago", +2, 0xFF16A34A),
            new LeaderboardEntry("SustainableHero", 2334, "Romania", 11, true, "", -1, 0xFF2563EB)
        };
    }
    
    private String formatScore(int score) {
        if (score >= 1000000) {
            return String.format("%.1fM", score / 1000000.0f);
        } else if (score >= 1000) {
            return String.format("%.1fK", score / 1000.0f);
        } else {
            return String.valueOf(score);
        }
    }
    
    private int calculateLevel(int score) {
        return Math.max(1, (int) Math.sqrt(score / 100.0) + 1);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
    
    // Helper class for leaderboard entries
    private static class LeaderboardEntry {
        public final String playerName;
        public final int score;
        public final String region;
        public final int achievements;
        public final boolean isOnline;
        public final String lastSeen;
        public final int rankChange;
        public final int avatarColor;
        
        public LeaderboardEntry(String playerName, int score, String region, int achievements,
                              boolean isOnline, String lastSeen, int rankChange, int avatarColor) {
            this.playerName = playerName;
            this.score = score;
            this.region = region;
            this.achievements = achievements;
            this.isOnline = isOnline;
            this.lastSeen = lastSeen;
            this.rankChange = rankChange;
            this.avatarColor = avatarColor;
        }
    }
}