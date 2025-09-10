package com.cleanairheroes.client.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import com.cleanairheroes.data.PlayerData;
import com.cleanairheroes.data.PlayerProgress;

public class AchievementGalleryScreen extends Screen {
    
    private static final int GUI_WIDTH = 1200;
    private static final int GUI_HEIGHT = 800;
    
    private PlayerProgress playerProgress;
    private int centerX;
    private int centerY;
    private int scrollOffset = 0;
    private int maxScroll = 0;
    private int animationTicks = 0;
    private int selectedCategory = 0; // 0: All, 1: Environmental, 2: Technical, 3: Social
    
    public AchievementGalleryScreen() {
        super(Text.literal("Achievement Gallery"));
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
        calculateScrollLimit();
    }
    
    private void initializeButtons() {
        // Category filter buttons
        int categoryButtonWidth = 180;
        int categoryY = centerY + 60;
        
        String[] categories = {"All Achievements", "Environmental Impact", "Technical Mastery", "Social Leadership"};
        
        for (int i = 0; i < categories.length; i++) {
            final int categoryIndex = i;
            this.addDrawableChild(ButtonWidget.builder(Text.literal(categories[i]), button -> {
                selectedCategory = categoryIndex;
                scrollOffset = 0;
                calculateScrollLimit();
            }).dimensions(centerX + 40 + i * (categoryButtonWidth + 10), categoryY, categoryButtonWidth, 25).build());
        }
        
        // Scroll buttons
        this.addDrawableChild(ButtonWidget.builder(Text.literal("↑ Scroll Up"), button -> {
            scrollOffset = Math.max(0, scrollOffset - 120);
        }).dimensions(centerX + GUI_WIDTH - 120, centerY + 100, 100, 25).build());
        
        this.addDrawableChild(ButtonWidget.builder(Text.literal("↓ Scroll Down"), button -> {
            scrollOffset = Math.min(maxScroll, scrollOffset + 120);
        }).dimensions(centerX + GUI_WIDTH - 120, centerY + 130, 100, 25).build());
        
        // Back button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("← Back to Protection Center"), button -> {
            if (this.client != null) {
                this.client.setScreen(new ProtectionCenterScreen());
            }
        }).dimensions(centerX + 40, centerY + GUI_HEIGHT - 40, 250, 30).build());
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.animationTicks++;
        
        // Render gradient background
        renderGradientBackground(context);
        
        // Main gallery background
        context.fill(centerX, centerY, centerX + GUI_WIDTH, centerY + GUI_HEIGHT, 0xF0F8F9FA);
        context.drawBorder(centerX, centerY, GUI_WIDTH, GUI_HEIGHT, 0xFF2C3E50);
        
        // Header
        renderGalleryHeader(context);
        
        // Achievement grid
        renderAchievementGrid(context, mouseX, mouseY);
        
        // Statistics sidebar
        renderStatisticsSidebar(context);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderGradientBackground(DrawContext context) {
        // Deep blue to light blue gradient
        for (int y = 0; y < this.height; y++) {
            float factor = (float) y / this.height;
            int red = (int) (25 + (52 - 25) * factor);
            int green = (int) (60 + (152 - 60) * factor);
            int blue = (int) (80 + (219 - 80) * factor);
            int color = 0xFF000000 | (red << 16) | (green << 8) | blue;
            context.fill(0, y, this.width, y + 1, color);
        }
    }
    
    private void renderGalleryHeader(DrawContext context) {
        int headerHeight = 50;
        
        // Header background
        context.fill(centerX + 10, centerY + 10, centerX + GUI_WIDTH - 10, centerY + headerHeight, 0xFF2C3E50);
        
        // Main title
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("🏆 ACHIEVEMENT GALLERY").formatted(Formatting.BOLD, Formatting.GOLD),
            centerX + GUI_WIDTH / 2, centerY + 25, 0xFFFFFF);
        
        // Category indicator
        String[] categoryNames = {"All", "Environmental", "Technical", "Social"};
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("Category: " + categoryNames[selectedCategory]).formatted(Formatting.ITALIC),
            centerX + GUI_WIDTH / 2, centerY + 40, 0xFFBDC3C7);
    }
    
    private void renderAchievementGrid(DrawContext context, int mouseX, int mouseY) {
        int gridStartY = centerY + 100;
        int gridWidth = GUI_WIDTH - 180; // Leave space for sidebar
        int achievementWidth = 280;
        int achievementHeight = 140;
        int spacing = 15;
        int cols = 3;
        
        AchievementInfo[] achievements = getFilteredAchievements();
        
        for (int i = 0; i < achievements.length; i++) {
            int row = i / cols;
            int col = i % cols;
            
            int x = centerX + 20 + col * (achievementWidth + spacing);
            int y = gridStartY + row * (achievementHeight + spacing) - scrollOffset;
            
            // Skip if outside visible area
            if (y + achievementHeight < gridStartY || y > centerY + GUI_HEIGHT - 100) {
                continue;
            }
            
            // Check if mouse is hovering
            boolean isHovered = mouseX >= x && mouseX <= x + achievementWidth &&
                              mouseY >= y && mouseY <= y + achievementHeight;
            
            renderAchievement(context, x, y, achievementWidth, achievementHeight, achievements[i], isHovered);
        }
    }
    
    private void renderAchievement(DrawContext context, int x, int y, int width, int height, AchievementInfo achievement, boolean isHovered) {
        boolean isUnlocked = playerProgress != null && isAchievementUnlocked(achievement);
        
        // Background with unlock status
        int backgroundColor = isUnlocked ? 
            (isHovered ? 0xFFFFFFFF : 0xFFF8F9FA) : 
            (isHovered ? 0xFF34495E : 0xFF2C3E50);
        
        int borderColor = isUnlocked ? 
            (isHovered ? achievement.rarity.color : 0xFFBDC3C7) : 
            0xFF7F8C8D;
        
        context.fill(x, y, x + width, y + height, backgroundColor);
        context.drawBorder(x, y, width, height, borderColor);
        
        // Rarity stripe
        context.fill(x, y, x + width, y + 5, achievement.rarity.color);
        
        // Achievement icon
        String icon = isUnlocked ? achievement.icon : "🔒";
        int iconColor = isUnlocked ? achievement.rarity.color : 0xFF95A5A6;
        
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(icon),
            x + 15, y + 20, iconColor);
        
        // Achievement title
        int titleColor = isUnlocked ? 0xFF2C3E50 : 0xFF7F8C8D;
        context.drawTextWithShadow(this.textRenderer,
            Text.literal(achievement.title).formatted(Formatting.BOLD),
            x + 50, y + 20, titleColor);
        
        // Achievement description
        int descColor = isUnlocked ? 0xFF5D6D7E : 0xFF95A5A6;
        String[] descLines = wrapText(achievement.description, width - 60);
        for (int i = 0; i < Math.min(descLines.length, 3); i++) {
            context.drawTextWithShadow(this.textRenderer,
                Text.literal(descLines[i]),
                x + 15, y + 45 + i * 12, descColor);
        }
        
        // Progress bar for progressive achievements
        if (isUnlocked && achievement.hasProgress && playerProgress != null) {
            int progress = getAchievementProgress(achievement);
            int maxProgress = achievement.maxProgress;
            
            if (maxProgress > 0) {
                int progressWidth = width - 30;
                int progressHeight = 8;
                int progressY = y + height - 25;
                
                // Progress bar background
                context.fill(x + 15, progressY, x + 15 + progressWidth, progressY + progressHeight, 0xFF34495E);
                
                // Progress bar fill
                int fillWidth = (int) ((float) progress / maxProgress * progressWidth);
                context.fill(x + 15, progressY, x + 15 + fillWidth, progressY + progressHeight, achievement.rarity.color);
                
                // Progress text
                context.drawTextWithShadow(this.textRenderer,
                    Text.literal(progress + "/" + maxProgress),
                    x + width - 60, y + height - 20, 0xFF2C3E50);
            }
        }
        
        // Unlock date for unlocked achievements
        if (isUnlocked && achievement.unlockDate != null) {
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("Unlocked: " + achievement.unlockDate).formatted(Formatting.ITALIC),
                x + 15, y + height - 15, 0xFF85929E);
        }
        
        // Hover glow effect
        if (isHovered && isUnlocked) {
            int glowAlpha = (int) (32 + 16 * Math.sin(animationTicks * 0.3));
            int glowColor = (glowAlpha << 24) | (achievement.rarity.color & 0x00FFFFFF);
            context.fill(x - 3, y - 3, x + width + 3, y + height + 3, glowColor);
        }
    }
    
    private void renderStatisticsSidebar(DrawContext context) {
        int sidebarX = centerX + GUI_WIDTH - 160;
        int sidebarY = centerY + 100;
        int sidebarWidth = 140;
        int sidebarHeight = GUI_HEIGHT - 150;
        
        // Sidebar background
        context.fill(sidebarX, sidebarY, sidebarX + sidebarWidth, sidebarY + sidebarHeight, 0xFF34495E);
        
        // Title
        context.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("Statistics").formatted(Formatting.BOLD),
            sidebarX + sidebarWidth / 2, sidebarY + 15, 0xFFFFFF);
        
        if (playerProgress != null) {
            // Achievement statistics
            AchievementInfo[] allAchievements = getAllAchievements();
            int totalAchievements = allAchievements.length;
            int unlockedAchievements = 0;
            int[] rarityCount = new int[AchievementRarity.values().length];
            
            for (AchievementInfo achievement : allAchievements) {
                if (isAchievementUnlocked(achievement)) {
                    unlockedAchievements++;
                    rarityCount[achievement.rarity.ordinal()]++;
                }
            }
            
            // Progress percentage
            int percentage = (int) ((float) unlockedAchievements / totalAchievements * 100);
            
            // Stats display
            String[] statLabels = {"Total Progress", "Common", "Rare", "Epic", "Legendary"};
            String[] statValues = {
                percentage + "% (" + unlockedAchievements + "/" + totalAchievements + ")",
                String.valueOf(rarityCount[0]),
                String.valueOf(rarityCount[1]),
                String.valueOf(rarityCount[2]),
                String.valueOf(rarityCount[3])
            };
            int[] statColors = {0xFFF1C40F, 0xFF95A5A6, 0xFF3498DB, 0xFF9B59B6, 0xFFE67E22};
            
            for (int i = 0; i < statLabels.length; i++) {
                int yOffset = 40 + i * 30;
                
                context.drawTextWithShadow(this.textRenderer,
                    Text.literal(statLabels[i]),
                    sidebarX + 10, sidebarY + yOffset, 0xFFBDC3C7);
                
                context.drawTextWithShadow(this.textRenderer,
                    Text.literal(statValues[i]).formatted(Formatting.BOLD),
                    sidebarX + 10, sidebarY + yOffset + 12, statColors[i]);
            }
            
            // Recent achievements
            context.drawTextWithShadow(this.textRenderer,
                Text.literal("Recent").formatted(Formatting.BOLD),
                sidebarX + 10, sidebarY + 200, 0xFFFFFF);
            
            // Show last 3 achievements
            AchievementInfo[] recentAchievements = getRecentAchievements(3);
            for (int i = 0; i < recentAchievements.length; i++) {
                context.drawTextWithShadow(this.textRenderer,
                    Text.literal(recentAchievements[i].icon + " " + recentAchievements[i].title),
                    sidebarX + 10, sidebarY + 220 + i * 25, recentAchievements[i].rarity.color);
            }
        }
    }
    
    private AchievementInfo[] getFilteredAchievements() {
        AchievementInfo[] allAchievements = getAllAchievements();
        
        if (selectedCategory == 0) {
            return allAchievements;
        }
        
        AchievementCategory targetCategory = AchievementCategory.values()[selectedCategory - 1];
        return java.util.Arrays.stream(allAchievements)
            .filter(achievement -> achievement.category == targetCategory)
            .toArray(AchievementInfo[]::new);
    }
    
    private AchievementInfo[] getAllAchievements() {
        return new AchievementInfo[] {
            // Environmental Impact Achievements
            new AchievementInfo("🌱", "First Steps", "Complete your first environmental mission", 
                AchievementCategory.ENVIRONMENTAL, AchievementRarity.COMMON, false, 0, 0, "2024-03-15"),
            new AchievementInfo("🌊", "Pollution Buster", "Reduce 100kg of pollution", 
                AchievementCategory.ENVIRONMENTAL, AchievementRarity.RARE, true, 500, 1000, null),
            new AchievementInfo("🌍", "Global Guardian", "Complete all 5 regions", 
                AchievementCategory.ENVIRONMENTAL, AchievementRarity.LEGENDARY, false, 0, 0, null),
            new AchievementInfo("💨", "Air Quality Hero", "Maintain excellent AQI for 30 days", 
                AchievementCategory.ENVIRONMENTAL, AchievementRarity.EPIC, true, 15, 30, null),
            
            // Technical Mastery Achievements
            new AchievementInfo("⚙️", "Tech Rookie", "Install your first equipment", 
                AchievementCategory.TECHNICAL, AchievementRarity.COMMON, false, 0, 0, "2024-03-16"),
            new AchievementInfo("🔧", "Equipment Master", "Install 50 pieces of equipment", 
                AchievementCategory.TECHNICAL, AchievementRarity.RARE, true, 25, 50, null),
            new AchievementInfo("🏭", "Industrial Expert", "Complete all thermal plant upgrades", 
                AchievementCategory.TECHNICAL, AchievementRarity.EPIC, false, 0, 0, null),
            new AchievementInfo("🚢", "Maritime Specialist", "Master ship emission control", 
                AchievementCategory.TECHNICAL, AchievementRarity.RARE, false, 0, 0, null),
            
            // Social Leadership Achievements
            new AchievementInfo("👥", "Community Leader", "Organize 5 community events", 
                AchievementCategory.SOCIAL, AchievementRarity.RARE, true, 2, 5, null),
            new AchievementInfo("🎓", "Education Champion", "Complete all educational programs", 
                AchievementCategory.SOCIAL, AchievementRarity.EPIC, false, 0, 0, null),
            new AchievementInfo("🏆", "Global Influencer", "Reach top 100 on leaderboard", 
                AchievementCategory.SOCIAL, AchievementRarity.LEGENDARY, false, 0, 0, null),
            new AchievementInfo("🤝", "Partnership Builder", "Form 10 international partnerships", 
                AchievementCategory.SOCIAL, AchievementRarity.EPIC, true, 4, 10, null)
        };
    }
    
    private boolean isAchievementUnlocked(AchievementInfo achievement) {
        if (playerProgress == null) return false;
        
        // Simulate achievement unlock logic based on player progress
        switch (achievement.title) {
            case "First Steps":
                return playerProgress.getCompletedMissionsCount() > 0;
            case "Pollution Buster":
                return playerProgress.getTotalPollutionReduced() >= 100;
            case "Global Guardian":
                return playerProgress.getUnlockedRegions().size() >= 5;
            case "Tech Rookie":
                return playerProgress.getEquipmentInstalled() > 0;
            case "Equipment Master":
                return playerProgress.getEquipmentInstalled() >= 50;
            default:
                return Math.random() > 0.7; // Simulate some achievements being unlocked
        }
    }
    
    private int getAchievementProgress(AchievementInfo achievement) {
        if (playerProgress == null) return 0;
        
        switch (achievement.title) {
            case "Pollution Buster":
                return Math.min((int) playerProgress.getTotalPollutionReduced(), achievement.maxProgress);
            case "Air Quality Hero":
                return Math.min(15, achievement.maxProgress); // Simulate 15 days progress
            case "Equipment Master":
                return Math.min(playerProgress.getEquipmentInstalled(), achievement.maxProgress);
            case "Community Leader":
                return Math.min(2, achievement.maxProgress); // Simulate 2 events organized
            case "Partnership Builder":
                return Math.min(4, achievement.maxProgress); // Simulate 4 partnerships
            default:
                return 0;
        }
    }
    
    private AchievementInfo[] getRecentAchievements(int count) {
        // Simulate recent achievements
        return new AchievementInfo[] {
            new AchievementInfo("🌱", "First Steps", "Complete your first environmental mission", 
                AchievementCategory.ENVIRONMENTAL, AchievementRarity.COMMON, false, 0, 0, "2024-03-15"),
            new AchievementInfo("⚙️", "Tech Rookie", "Install your first equipment", 
                AchievementCategory.TECHNICAL, AchievementRarity.COMMON, false, 0, 0, "2024-03-16"),
            new AchievementInfo("🌊", "Pollution Buster", "Reduce 100kg of pollution", 
                AchievementCategory.ENVIRONMENTAL, AchievementRarity.RARE, true, 500, 1000, "2024-03-20")
        };
    }
    
    private void calculateScrollLimit() {
        AchievementInfo[] achievements = getFilteredAchievements();
        int rows = (achievements.length + 2) / 3; // 3 columns
        int totalHeight = rows * (140 + 15); // achievement height + spacing
        int visibleHeight = GUI_HEIGHT - 200; // Subtract header and footer space
        
        maxScroll = Math.max(0, totalHeight - visibleHeight);
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
    
    // Helper classes
    private enum AchievementCategory {
        ENVIRONMENTAL, TECHNICAL, SOCIAL
    }
    
    private enum AchievementRarity {
        COMMON(0xFF95A5A6),
        RARE(0xFF3498DB),
        EPIC(0xFF9B59B6),
        LEGENDARY(0xFFE67E22);
        
        public final int color;
        
        AchievementRarity(int color) {
            this.color = color;
        }
    }
    
    private static class AchievementInfo {
        public final String icon;
        public final String title;
        public final String description;
        public final AchievementCategory category;
        public final AchievementRarity rarity;
        public final boolean hasProgress;
        public final int currentProgress;
        public final int maxProgress;
        public final String unlockDate;
        
        public AchievementInfo(String icon, String title, String description, 
                             AchievementCategory category, AchievementRarity rarity,
                             boolean hasProgress, int currentProgress, int maxProgress, String unlockDate) {
            this.icon = icon;
            this.title = title;
            this.description = description;
            this.category = category;
            this.rarity = rarity;
            this.hasProgress = hasProgress;
            this.currentProgress = currentProgress;
            this.maxProgress = maxProgress;
            this.unlockDate = unlockDate;
        }
    }
}