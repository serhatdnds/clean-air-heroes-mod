package com.cleanairheroes.center;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;

import com.cleanairheroes.data.PlayerData;
import com.cleanairheroes.data.PlayerProgress;
import com.cleanairheroes.story.RegionalTransitionStory;

import java.util.*;

public class EnvironmentalProtectionCenter {
    
    public static class RegionalAirQualityData {
        public final String region;
        public final double currentAQI;
        public final String qualityLevel;
        public final double improvementPercentage;
        public final List<String> majorSources;
        public final int activeMissions;
        public final boolean isCompleted;
        
        public RegionalAirQualityData(String region, double currentAQI, String qualityLevel, 
                                    double improvementPercentage, List<String> majorSources, 
                                    int activeMissions, boolean isCompleted) {
            this.region = region;
            this.currentAQI = currentAQI;
            this.qualityLevel = qualityLevel;
            this.improvementPercentage = improvementPercentage;
            this.majorSources = majorSources;
            this.activeMissions = activeMissions;
            this.isCompleted = isCompleted;
        }
    }
    
    public static class AchievementGallery {
        public final String region;
        public final List<String> beforeImages;
        public final List<String> afterImages;
        public final List<String> keyAchievements;
        public final double pollutionReduction;
        public final int completedMissions;
        
        public AchievementGallery(String region, List<String> beforeImages, List<String> afterImages,
                                List<String> keyAchievements, double pollutionReduction, int completedMissions) {
            this.region = region;
            this.beforeImages = beforeImages;
            this.afterImages = afterImages;
            this.keyAchievements = keyAchievements;
            this.pollutionReduction = pollutionReduction;
            this.completedMissions = completedMissions;
        }
    }
    
    public static void openProtectionCenter(ServerPlayerEntity player) {
        player.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.MASTER, 0.7f, 1.2f);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("╔══════════════════════════════════════╗").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("║     🌍 ENVIRONMENTAL PROTECTION CENTER     ║").formatted(Formatting.AQUA, Formatting.BOLD), false);
        player.sendMessage(Text.literal("║         Global Air Quality Hub          ║").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("╚══════════════════════════════════════╝").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        showMainMenu(player);
    }
    
    private static void showMainMenu(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("📋 CENTER SERVICES:").formatted(Formatting.YELLOW, Formatting.BOLD), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        player.sendMessage(Text.literal("1. 🗺️  Regional Air Quality Map").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("   View real-time air quality data from all regions").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        player.sendMessage(Text.literal("2. 🏆  Achievement Gallery").formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("   Browse your environmental impact success stories").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        player.sendMessage(Text.literal("3. ✈️  Travel Hub").formatted(Formatting.BLUE), false);
        player.sendMessage(Text.literal("   Fast travel between unlocked regions").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        player.sendMessage(Text.literal("4. 📊  Global Impact Report").formatted(Formatting.LIGHT_PURPLE), false);
        player.sendMessage(Text.literal("   Your total environmental contribution").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        player.sendMessage(Text.literal("5. 📢  International Leaderboard").formatted(Formatting.RED), false);
        player.sendMessage(Text.literal("   Compare your progress with other Clean Air Heroes").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        player.sendMessage(Text.literal("Commands:").formatted(Formatting.AQUA, Formatting.BOLD), false);
        player.sendMessage(Text.literal("/cleanair center map     - View air quality map").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("/cleanair center gallery - Browse achievements").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("/cleanair center travel  - Access travel hub").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("/cleanair center report  - View global impact").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("/cleanair center ranking - View leaderboard").formatted(Formatting.WHITE), false);
    }
    
    public static void showAirQualityMap(ServerPlayerEntity player) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🗺️ BLACK SEA REGION AIR QUALITY MAP").formatted(Formatting.BLUE, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Real-time monitoring across 5 regions").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        List<RegionalAirQualityData> regionData = generateRegionalData(progress);
        
        for (RegionalAirQualityData data : regionData) {
            showRegionalData(player, data, progress.getCurrentRegion().equals(data.region));
            player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        }
        
        // Summary statistics
        player.sendMessage(Text.literal("📈 REGIONAL SUMMARY:").formatted(Formatting.YELLOW, Formatting.BOLD), false);
        double avgImprovement = regionData.stream().mapToDouble(d -> d.improvementPercentage).average().orElse(0);
        int totalMissions = regionData.stream().mapToInt(d -> d.activeMissions).sum();
        long completedRegions = regionData.stream().mapToLong(d -> d.isCompleted ? 1 : 0).sum();
        
        player.sendMessage(Text.literal("• Average air quality improvement: " + String.format("%.1f%%", avgImprovement)).formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("• Total active missions: " + totalMissions).formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("• Regions completed: " + completedRegions + "/5").formatted(Formatting.GOLD), false);
    }
    
    private static void showRegionalData(ServerPlayerEntity player, RegionalAirQualityData data, boolean isCurrent) {
        String regionDisplay = data.region.toUpperCase();
        if (isCurrent) {
            regionDisplay = "📍 " + regionDisplay + " (CURRENT)";
        }
        
        Formatting nameColor = isCurrent ? Formatting.YELLOW : Formatting.WHITE;
        Formatting aqiColor = getAQIColor(data.currentAQI);
        
        player.sendMessage(Text.literal(regionDisplay).formatted(nameColor, Formatting.BOLD), false);
        player.sendMessage(Text.literal("  AQI: " + String.format("%.1f", data.currentAQI) + " (" + data.qualityLevel + ")").formatted(aqiColor), false);
        
        if (data.improvementPercentage > 0) {
            player.sendMessage(Text.literal("  Improvement: +" + String.format("%.1f%%", data.improvementPercentage)).formatted(Formatting.GREEN), false);
        } else {
            player.sendMessage(Text.literal("  Status: Baseline measurements").formatted(Formatting.GRAY), false);
        }
        
        if (!data.majorSources.isEmpty()) {
            player.sendMessage(Text.literal("  Main sources: " + String.join(", ", data.majorSources)).formatted(Formatting.RED), false);
        }
        
        if (data.activeMissions > 0) {
            player.sendMessage(Text.literal("  Active missions: " + data.activeMissions).formatted(Formatting.BLUE), false);
        }
        
        if (data.isCompleted) {
            player.sendMessage(Text.literal("  Status: ✅ COMPLETED").formatted(Formatting.GREEN, Formatting.BOLD), false);
        }
    }
    
    public static void showAchievementGallery(ServerPlayerEntity player) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🏆 YOUR ENVIRONMENTAL ACHIEVEMENT GALLERY").formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Visual documentation of your Clean Air Hero journey").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        List<AchievementGallery> galleries = generateAchievementGalleries(progress);
        
        if (galleries.isEmpty()) {
            player.sendMessage(Text.literal("📸 No achievements documented yet.").formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("Complete missions to build your gallery!").formatted(Formatting.AQUA), false);
            return;
        }
        
        for (AchievementGallery gallery : galleries) {
            showRegionalGallery(player, gallery);
            player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        }
        
        // Gallery statistics
        double totalReduction = galleries.stream().mapToDouble(g -> g.pollutionReduction).sum();
        int totalMissions = galleries.stream().mapToInt(g -> g.completedMissions).sum();
        
        player.sendMessage(Text.literal("📊 GALLERY STATISTICS:").formatted(Formatting.AQUA, Formatting.BOLD), false);
        player.sendMessage(Text.literal("• Total pollution reduced: " + String.format("%.1f kg", totalReduction)).formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("• Missions documented: " + totalMissions).formatted(Formatting.BLUE), false);
        player.sendMessage(Text.literal("• Regions documented: " + galleries.size() + "/5").formatted(Formatting.GOLD), false);
    }
    
    private static void showRegionalGallery(ServerPlayerEntity player, AchievementGallery gallery) {
        player.sendMessage(Text.literal("📂 " + gallery.region.toUpperCase() + " ACHIEVEMENTS").formatted(Formatting.YELLOW, Formatting.BOLD), false);
        
        // Before/After comparison
        if (!gallery.beforeImages.isEmpty() && !gallery.afterImages.isEmpty()) {
            player.sendMessage(Text.literal("📷 TRANSFORMATION DOCUMENTATION:").formatted(Formatting.BLUE), false);
            for (int i = 0; i < Math.min(gallery.beforeImages.size(), gallery.afterImages.size()); i++) {
                player.sendMessage(Text.literal("  Before: " + gallery.beforeImages.get(i)).formatted(Formatting.RED), false);
                player.sendMessage(Text.literal("  After:  " + gallery.afterImages.get(i)).formatted(Formatting.GREEN), false);
            }
        }
        
        // Key achievements
        if (!gallery.keyAchievements.isEmpty()) {
            player.sendMessage(Text.literal("🌟 KEY ACHIEVEMENTS:").formatted(Formatting.GOLD), false);
            for (String achievement : gallery.keyAchievements) {
                player.sendMessage(Text.literal("  ✓ " + achievement).formatted(Formatting.GREEN), false);
            }
        }
        
        // Impact statistics
        player.sendMessage(Text.literal("📈 ENVIRONMENTAL IMPACT:").formatted(Formatting.LIGHT_PURPLE), false);
        player.sendMessage(Text.literal("  Pollution reduced: " + String.format("%.1f kg", gallery.pollutionReduction)).formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("  Missions completed: " + gallery.completedMissions).formatted(Formatting.BLUE), false);
    }
    
    public static void showTravelHub(ServerPlayerEntity player) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("✈️ CLEAN AIR HEROES TRAVEL HUB").formatted(Formatting.BLUE, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Fast travel between regions you've unlocked").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        String currentRegion = progress.getCurrentRegion();
        Set<String> unlockedRegions = progress.getUnlockedRegions();
        
        player.sendMessage(Text.literal("📍 Current Location: " + currentRegion.toUpperCase()).formatted(Formatting.YELLOW, Formatting.BOLD), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        player.sendMessage(Text.literal("🗺️ AVAILABLE DESTINATIONS:").formatted(Formatting.GREEN, Formatting.BOLD), false);
        
        String[] allRegions = {"varna", "zonguldak", "odesa", "trabzon", "romania"};
        for (String region : allRegions) {
            if (unlockedRegions.contains(region)) {
                if (region.equals(currentRegion)) {
                    player.sendMessage(Text.literal("  📍 " + region.toUpperCase() + " (Current Location)").formatted(Formatting.YELLOW), false);
                } else {
                    player.sendMessage(Text.literal("  ✈️  " + region.toUpperCase() + " - Available").formatted(Formatting.GREEN), false);
                    player.sendMessage(Text.literal("      Use '/cleanair teleport " + region + "' to travel").formatted(Formatting.GRAY), false);
                }
            } else {
                player.sendMessage(Text.literal("  🔒 " + region.toUpperCase() + " - Locked").formatted(Formatting.RED), false);
                if (progress.canTravelToRegion(region)) {
                    player.sendMessage(Text.literal("      Complete current region to unlock").formatted(Formatting.YELLOW), false);
                } else {
                    player.sendMessage(Text.literal("      Not yet available").formatted(Formatting.GRAY), false);
                }
            }
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Show transition story availability
        String nextRegion = progress.getNextRegion();
        if (nextRegion != null && progress.getRegionScore(currentRegion) >= 70) {
            player.sendMessage(Text.literal("📖 STORY TRANSITION AVAILABLE:").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), false);
            player.sendMessage(Text.literal("Experience the story journey to " + nextRegion.toUpperCase()).formatted(Formatting.LIGHT_PURPLE), false);
            player.sendMessage(Text.literal("Use '/cleanair story transition' for the full narrative").formatted(Formatting.AQUA), false);
        }
    }
    
    public static void showGlobalImpactReport(ServerPlayerEntity player) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("📊 GLOBAL ENVIRONMENTAL IMPACT REPORT").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Your contribution to global air quality improvement").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Personal statistics
        player.sendMessage(Text.literal("👤 PERSONAL ACHIEVEMENTS:").formatted(Formatting.YELLOW, Formatting.BOLD), false);
        player.sendMessage(Text.literal("• Title: " + progress.getPlayerTitle()).formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("• Total Clean Air Points: " + progress.getTotalScore()).formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("• Missions Completed: " + progress.getCompletedMissionsCount()).formatted(Formatting.BLUE), false);
        player.sendMessage(Text.literal("• Play Time: " + progress.getTotalPlayTimeMinutes() + " minutes").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Environmental impact
        player.sendMessage(Text.literal("🌍 ENVIRONMENTAL IMPACT:").formatted(Formatting.GREEN, Formatting.BOLD), false);
        player.sendMessage(Text.literal("• Pollution Reduced: " + String.format("%.1f kg", progress.getTotalPollutionReduced())).formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("• Trees Equivalent: " + (int)(progress.getTotalPollutionReduced() / 22) + " trees").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("• Equipment Installed: " + progress.getEquipmentInstalled()).formatted(Formatting.BLUE), false);
        player.sendMessage(Text.literal("• Education Sessions: " + progress.getEducationSessionsGiven()).formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Regional breakdown
        player.sendMessage(Text.literal("🗺️ REGIONAL CONTRIBUTIONS:").formatted(Formatting.AQUA, Formatting.BOLD), false);
        String[] regions = {"varna", "zonguldak", "odesa", "trabzon", "romania"};
        for (String region : regions) {
            int score = progress.getRegionScore(region);
            boolean completed = score >= 70;
            String status = completed ? "✅ Completed" : (score > 0 ? "⏳ In Progress" : "🔒 Locked");
            
            player.sendMessage(Text.literal("• " + region.toUpperCase() + ": " + score + " points - " + status)
                .formatted(completed ? Formatting.GREEN : (score > 0 ? Formatting.YELLOW : Formatting.RED)), false);
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Global ranking simulation
        int simulatedRank = Math.max(1, 1000 - progress.getTotalScore() * 2);
        player.sendMessage(Text.literal("🏆 GLOBAL RANKING:").formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.sendMessage(Text.literal("• World Rank: #" + simulatedRank + " of 10,000+ Clean Air Heroes").formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("• Percentile: Top " + String.format("%.1f%%", (simulatedRank / 10000.0) * 100)).formatted(Formatting.YELLOW), false);
    }
    
    // Helper methods for data generation
    private static List<RegionalAirQualityData> generateRegionalData(PlayerProgress progress) {
        List<RegionalAirQualityData> data = new ArrayList<>();
        
        // Varna data
        boolean varnaCompleted = progress.getRegionScore("varna") >= 70;
        data.add(new RegionalAirQualityData("varna", varnaCompleted ? 65 : 95, 
            varnaCompleted ? "Moderate" : "Unhealthy", 
            varnaCompleted ? 35.0 : 0.0, 
            Arrays.asList("Port emissions", "Marine traffic"), 
            varnaCompleted ? 0 : 4, varnaCompleted));
        
        // Zonguldak data
        boolean zonguldakCompleted = progress.getRegionScore("zonguldak") >= 70;
        data.add(new RegionalAirQualityData("zonguldak", zonguldakCompleted ? 70 : 145, 
            zonguldakCompleted ? "Moderate" : "Very Unhealthy", 
            zonguldakCompleted ? 52.0 : 0.0, 
            Arrays.asList("Coal mining", "Thermal plant"), 
            zonguldakCompleted ? 0 : 5, zonguldakCompleted));
        
        // Add other regions similarly...
        
        return data;
    }
    
    private static List<AchievementGallery> generateAchievementGalleries(PlayerProgress progress) {
        List<AchievementGallery> galleries = new ArrayList<>();
        
        // Only show galleries for regions with some progress
        if (progress.getRegionScore("varna") > 0) {
            galleries.add(new AchievementGallery("varna",
                Arrays.asList("Varna port - heavy black smoke", "City center - visible smog"),
                Arrays.asList("Varna port - clean blue skies", "City center - clear air"),
                Arrays.asList("Ship emission controls installed", "Green transport network established", "Air quality monitoring active"),
                progress.getTotalPollutionReduced() * 0.2, // 20% from Varna
                (int)(progress.getCompletedMissionsCount() * 0.2)));
        }
        
        // Add other regions similarly...
        
        return galleries;
    }
    
    private static Formatting getAQIColor(double aqi) {
        if (aqi <= 50) return Formatting.GREEN;
        if (aqi <= 100) return Formatting.YELLOW;
        if (aqi <= 150) return Formatting.GOLD;
        if (aqi <= 200) return Formatting.RED;
        return Formatting.DARK_RED;
    }
}