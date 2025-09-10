package com.cleanairheroes.achievement;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.nbt.NbtCompound;

import java.util.*;

public class CleanAirHeroSystem {
    
    private final Map<UUID, PlayerAchievementData> playerData = new HashMap<>();
    private final Map<String, Achievement> achievements = new HashMap<>();
    private final ScoringSystem scoringSystem = new ScoringSystem();
    private final CertificationSystem certificationSystem = new CertificationSystem();
    
    public CleanAirHeroSystem() {
        initializeAchievements();
    }
    
    public static class PlayerAchievementData {
        public int totalCleanAirPoints = 0;
        public Map<String, Integer> regionScores = new HashMap<>();
        public Set<String> completedMissions = new HashSet<>();
        public Set<String> earnedAchievements = new HashSet<>();
        public Set<String> earnedCertificates = new HashSet<>();
        public Map<String, Integer> statisticCounters = new HashMap<>();
        public long playStartTime = System.currentTimeMillis();
        public int globalRanking = 0;
        public CleanAirHeroLevel heroLevel = CleanAirHeroLevel.ENVIRONMENTAL_TRAINEE;
        
        public PlayerAchievementData() {
            // Initialize region scores
            regionScores.put("varna_bulgaria", 0);
            regionScores.put("zonguldak_turkey", 0);
            regionScores.put("odesa_ukraine", 0);
            regionScores.put("trabzon_turkey", 0);
            regionScores.put("southeast_romania", 0);
            
            // Initialize statistic counters
            statisticCounters.put("ships_filtered", 0);
            statisticCounters.put("buses_electrified", 0);
            statisticCounters.put("homes_converted", 0);
            statisticCounters.put("factories_upgraded", 0);
            statisticCounters.put("trees_planted", 0);
            statisticCounters.put("monitoring_stations", 0);
            statisticCounters.put("pollution_reduced_tons", 0);
            statisticCounters.put("mini_games_completed", 0);
        }
    }
    
    public enum CleanAirHeroLevel {
        ENVIRONMENTAL_TRAINEE("Environmental Trainee", 0, "🌱", "Just started your environmental journey"),
        POLLUTION_FIGHTER("Pollution Fighter", 100, "⚔️", "Actively fighting against air pollution"),
        AIR_QUALITY_SPECIALIST("Air Quality Specialist", 250, "🔬", "Expert in air quality management"),
        CLEAN_AIR_CHAMPION("Clean Air Champion", 400, "🏆", "Leading the fight for clean air"),
        ENVIRONMENTAL_HERO("Environmental Hero", 600, "🌟", "Hero-level environmental protection"),
        CLEAN_AIR_LEGEND("Clean Air Legend", 850, "👑", "Legendary clean air advocate");
        
        public final String displayName;
        public final int requiredPoints;
        public final String icon;
        public final String description;
        
        CleanAirHeroLevel(String displayName, int requiredPoints, String icon, String description) {
            this.displayName = displayName;
            this.requiredPoints = requiredPoints;
            this.icon = icon;
            this.description = description;
        }
        
        public static CleanAirHeroLevel fromPoints(int points) {
            CleanAirHeroLevel[] levels = values();
            for (int i = levels.length - 1; i >= 0; i--) {
                if (points >= levels[i].requiredPoints) {
                    return levels[i];
                }
            }
            return ENVIRONMENTAL_TRAINEE;
        }
    }
    
    public static class Achievement {
        public final String id;
        public final String name;
        public final String description;
        public final String category;
        public final int points;
        public final String icon;
        public final AchievementType type;
        public final Map<String, Integer> requirements;
        
        public Achievement(String id, String name, String description, String category, 
                          int points, String icon, AchievementType type, Map<String, Integer> requirements) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.category = category;
            this.points = points;
            this.icon = icon;
            this.type = type;
            this.requirements = requirements;
        }
    }
    
    public enum AchievementType {
        MISSION_COMPLETION,
        STATISTIC_MILESTONE,
        REGION_MASTERY,
        GLOBAL_IMPACT,
        SPEED_RUN,
        PERFECT_SCORE
    }
    
    public static class ScoringSystem {
        
        public int calculateMissionScore(String missionId, int objectivesCompleted, int totalObjectives, 
                                       int timeBonus, int accuracyBonus, int efficiencyBonus) {
            int baseScore = (objectivesCompleted * 100) / totalObjectives;
            int bonusScore = timeBonus + accuracyBonus + efficiencyBonus;
            return Math.min(100, baseScore + bonusScore);
        }
        
        public int calculateRegionScore(Map<String, Integer> missionScores) {
            if (missionScores.isEmpty()) return 0;
            
            int totalScore = missionScores.values().stream().mapToInt(Integer::intValue).sum();
            int averageScore = totalScore / missionScores.size();
            
            // Bonus for completing all missions in region
            if (missionScores.size() >= getRequiredMissionsForRegion()) {
                averageScore += 10;
            }
            
            return Math.min(100, averageScore);
        }
        
        public int calculateGlobalRanking(int playerPoints, List<Integer> allPlayerPoints) {
            Collections.sort(allPlayerPoints, Collections.reverseOrder());
            int ranking = 1;
            for (int points : allPlayerPoints) {
                if (points > playerPoints) {
                    ranking++;
                } else {
                    break;
                }
            }
            return ranking;
        }
        
        private int getRequiredMissionsForRegion() {
            return 4; // Most regions have 4-5 main missions
        }
    }
    
    public static class CertificationSystem {
        
        public enum CertificationType {
            PORT_EMISSION_CONTROL("Port Emission Control Specialist", "🚢"),
            INDUSTRIAL_POLLUTION("Industrial Pollution Expert", "🏭"),
            TRAFFIC_MANAGEMENT("Smart Traffic Systems", "🚦"),
            RENEWABLE_ENERGY("Renewable Energy Advocate", "⚡"),
            AIR_QUALITY_MONITORING("Air Quality Monitoring", "📊"),
            REGIONAL_COORDINATOR("Regional Environmental Coordinator", "🌍"),
            CLEAN_AIR_HERO("Clean Air Hero", "🌟");
            
            public final String displayName;
            public final String icon;
            
            CertificationType(String displayName, String icon) {
                this.displayName = displayName;
                this.icon = icon;
            }
        }
        
        public boolean canEarnCertificate(PlayerAchievementData playerData, CertificationType type) {
            switch (type) {
                case PORT_EMISSION_CONTROL:
                    return playerData.statisticCounters.get("ships_filtered") >= 50;
                    
                case INDUSTRIAL_POLLUTION:
                    return playerData.statisticCounters.get("factories_upgraded") >= 30;
                    
                case TRAFFIC_MANAGEMENT:
                    return playerData.statisticCounters.get("buses_electrified") >= 25;
                    
                case RENEWABLE_ENERGY:
                    return playerData.statisticCounters.get("homes_converted") >= 100;
                    
                case AIR_QUALITY_MONITORING:
                    return playerData.statisticCounters.get("monitoring_stations") >= 20;
                    
                case REGIONAL_COORDINATOR:
                    return playerData.regionScores.values().stream().mapToInt(Integer::intValue).sum() >= 400;
                    
                case CLEAN_AIR_HERO:
                    return playerData.totalCleanAirPoints >= 350 && 
                           playerData.regionScores.values().stream().allMatch(score -> score >= 70);
                           
                default:
                    return false;
            }
        }
        
        public String generateCertificate(ServerPlayerEntity player, CertificationType type) {
            String playerName = player.getName().getString();
            long currentTime = System.currentTimeMillis();
            
            StringBuilder certificate = new StringBuilder();
            certificate.append("═══════════════════════════════════\n");
            certificate.append("       🏆 OFFICIAL CERTIFICATE 🏆\n");
            certificate.append("═══════════════════════════════════\n\n");
            certificate.append("This certifies that\n\n");
            certificate.append("        ").append(playerName.toUpperCase()).append("\n\n");
            certificate.append("has successfully completed all requirements\n");
            certificate.append("and demonstrated exceptional expertise as a\n\n");
            certificate.append("    ").append(type.icon).append(" ").append(type.displayName.toUpperCase()).append(" ").append(type.icon).append("\n\n");
            certificate.append("Issued by the Clean Air Heroes Program\n");
            certificate.append("Environmental Protection Agency\n\n");
            certificate.append("Date: ").append(new Date(currentTime)).append("\n");
            certificate.append("Certificate ID: CAH-").append(type.name()).append("-").append(playerName.hashCode()).append("\n");
            certificate.append("═══════════════════════════════════");
            
            return certificate.toString();
        }
    }
    
    private void initializeAchievements() {
        // Mission completion achievements
        addAchievement("varna_master", "Varna Port Master", 
            "Complete all missions in Varna, Bulgaria", "Regional Mastery", 50, "🚢",
            AchievementType.REGION_MASTERY, Map.of("varna_score", 70));
            
        addAchievement("zonguldak_hero", "Zonguldak Mining Hero", 
            "Clean up coal mining pollution in Zonguldak", "Regional Mastery", 50, "⛏️",
            AchievementType.REGION_MASTERY, Map.of("zonguldak_score", 70));
            
        addAchievement("odesa_champion", "Odesa Port Champion", 
            "Transform maritime operations in Odesa", "Regional Mastery", 50, "🏭",
            AchievementType.REGION_MASTERY, Map.of("odesa_score", 70));
            
        addAchievement("trabzon_traffic_expert", "Trabzon Traffic Expert", 
            "Solve traffic pollution in mountainous Trabzon", "Regional Mastery", 50, "🚗",
            AchievementType.REGION_MASTERY, Map.of("trabzon_score", 70));
            
        addAchievement("romania_coordinator", "Romania Regional Coordinator", 
            "Coordinate clean air efforts across Southeast Romania", "Regional Mastery", 75, "🌍",
            AchievementType.REGION_MASTERY, Map.of("romania_score", 70));
        
        // Statistical milestones
        addAchievement("ship_filter_expert", "Ship Filter Expert", 
            "Install emission filters on 100 ships", "Environmental Impact", 30, "🚢",
            AchievementType.STATISTIC_MILESTONE, Map.of("ships_filtered", 100));
            
        addAchievement("green_transport_advocate", "Green Transport Advocate", 
            "Convert 50 vehicles to electric", "Transportation", 25, "🚌",
            AchievementType.STATISTIC_MILESTONE, Map.of("buses_electrified", 50));
            
        addAchievement("home_energy_transformer", "Home Energy Transformer", 
            "Convert 200 homes to clean energy", "Residential", 35, "🏠",
            AchievementType.STATISTIC_MILESTONE, Map.of("homes_converted", 200));
            
        addAchievement("forest_guardian", "Forest Guardian", 
            "Plant 1000 trees for natural air filtering", "Environmental", 40, "🌳",
            AchievementType.STATISTIC_MILESTONE, Map.of("trees_planted", 1000));
            
        addAchievement("pollution_data_master", "Pollution Data Master", 
            "Establish 50 monitoring stations", "Technology", 30, "📊",
            AchievementType.STATISTIC_MILESTONE, Map.of("monitoring_stations", 50));
        
        // Global impact achievements
        addAchievement("tons_reducer", "Mega Pollution Reducer", 
            "Reduce 10,000 tons of harmful emissions", "Global Impact", 100, "🌍",
            AchievementType.GLOBAL_IMPACT, Map.of("pollution_reduced_tons", 10000));
            
        addAchievement("five_region_hero", "Five Region Hero", 
            "Successfully complete missions in all 5 regions", "Global Impact", 150, "🏆",
            AchievementType.REGION_MASTERY, Map.of("regions_completed", 5));
        
        // Speed and efficiency achievements
        addAchievement("speed_cleaner", "Speed Cleaner", 
            "Complete a region in under 2 hours", "Efficiency", 25, "⚡",
            AchievementType.SPEED_RUN, Map.of("fastest_region_minutes", 120));
            
        addAchievement("perfect_scorer", "Perfect Environmental Score", 
            "Achieve 100/100 score in any region", "Excellence", 50, "💯",
            AchievementType.PERFECT_SCORE, Map.of("perfect_region_score", 100));
        
        // Ultimate achievement
        addAchievement("clean_air_legend", "Clean Air Legend", 
            "Reach maximum hero level with 850+ points", "Ultimate", 200, "👑",
            AchievementType.GLOBAL_IMPACT, Map.of("total_points", 850));
    }
    
    private void addAchievement(String id, String name, String description, String category, 
                               int points, String icon, AchievementType type, Map<String, Integer> requirements) {
        achievements.put(id, new Achievement(id, name, description, category, points, icon, type, requirements));
    }
    
    public void updatePlayerProgress(ServerPlayerEntity player, String statistic, int value) {
        PlayerAchievementData data = getPlayerData(player.getUuid());
        
        // Update statistic
        data.statisticCounters.put(statistic, data.statisticCounters.getOrDefault(statistic, 0) + value);
        
        // Check for new achievements
        checkAchievements(player, data);
        
        // Update hero level
        updateHeroLevel(player, data);
    }
    
    public void completeRegion(ServerPlayerEntity player, String regionId, int score) {
        PlayerAchievementData data = getPlayerData(player.getUuid());
        
        data.regionScores.put(regionId, score);
        data.totalCleanAirPoints += score;
        
        // Check for regional achievements
        checkRegionalAchievements(player, data, regionId, score);
        
        // Check for certificates
        checkCertifications(player, data);
        
        // Update global ranking
        updateGlobalRanking(player, data);
        
        // Check for ultimate Clean Air Hero status
        checkCleanAirHeroStatus(player, data);
    }
    
    private void checkAchievements(ServerPlayerEntity player, PlayerAchievementData data) {
        for (Achievement achievement : achievements.values()) {
            if (data.earnedAchievements.contains(achievement.id)) continue;
            
            boolean earned = false;
            
            switch (achievement.type) {
                case STATISTIC_MILESTONE:
                    earned = checkStatisticRequirements(data, achievement.requirements);
                    break;
                case GLOBAL_IMPACT:
                    earned = checkGlobalRequirements(data, achievement.requirements);
                    break;
                case SPEED_RUN:
                    earned = checkSpeedRequirements(data, achievement.requirements);
                    break;
                case PERFECT_SCORE:
                    earned = checkPerfectScoreRequirements(data, achievement.requirements);
                    break;
            }
            
            if (earned) {
                awardAchievement(player, data, achievement);
            }
        }
    }
    
    private void checkRegionalAchievements(ServerPlayerEntity player, PlayerAchievementData data, 
                                         String regionId, int score) {
        String achievementId = getRegionalAchievementId(regionId);
        if (achievementId != null && score >= 70 && !data.earnedAchievements.contains(achievementId)) {
            Achievement achievement = achievements.get(achievementId);
            if (achievement != null) {
                awardAchievement(player, data, achievement);
            }
        }
        
        // Check for five region hero
        long completedRegions = data.regionScores.values().stream().filter(s -> s >= 70).count();
        if (completedRegions >= 5 && !data.earnedAchievements.contains("five_region_hero")) {
            awardAchievement(player, data, achievements.get("five_region_hero"));
        }
    }
    
    private void checkCertifications(ServerPlayerEntity player, PlayerAchievementData data) {
        for (CertificationSystem.CertificationType type : CertificationSystem.CertificationType.values()) {
            if (!data.earnedCertificates.contains(type.name()) && 
                certificationSystem.canEarnCertificate(data, type)) {
                
                awardCertificate(player, data, type);
            }
        }
    }
    
    private void awardAchievement(ServerPlayerEntity player, PlayerAchievementData data, Achievement achievement) {
        data.earnedAchievements.add(achievement.id);
        data.totalCleanAirPoints += achievement.points;
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🏆 ACHIEVEMENT UNLOCKED! 🏆").formatted(Formatting.GOLD, Formatting.BOLD), true);
        player.sendMessage(Text.literal(achievement.icon + " " + achievement.name).formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal(achievement.description).formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("+" + achievement.points + " Clean Air Points").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
    }
    
    private void awardCertificate(ServerPlayerEntity player, PlayerAchievementData data, 
                                CertificationSystem.CertificationType type) {
        data.earnedCertificates.add(type.name());
        
        String certificate = certificationSystem.generateCertificate(player, type);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("📜 CERTIFICATE EARNED! 📜").formatted(Formatting.GOLD, Formatting.BOLD), true);
        player.sendMessage(Text.literal(type.icon + " " + type.displayName).formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Your certificate has been saved to your profile!").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
    }
    
    private void updateHeroLevel(ServerPlayerEntity player, PlayerAchievementData data) {
        CleanAirHeroLevel newLevel = CleanAirHeroLevel.fromPoints(data.totalCleanAirPoints);
        
        if (newLevel != data.heroLevel) {
            CleanAirHeroLevel oldLevel = data.heroLevel;
            data.heroLevel = newLevel;
            
            player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("🌟 LEVEL UP! 🌟").formatted(Formatting.GOLD, Formatting.BOLD), true);
            player.sendMessage(Text.literal("You are now a " + newLevel.icon + " " + newLevel.displayName + "!")
                .formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal(newLevel.description).formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        }
    }
    
    private void checkCleanAirHeroStatus(ServerPlayerEntity player, PlayerAchievementData data) {
        boolean isCleanAirHero = data.totalCleanAirPoints >= 350 && 
                                data.regionScores.values().stream().allMatch(score -> score >= 70);
        
        if (isCleanAirHero && !data.earnedCertificates.contains("CLEAN_AIR_HERO")) {
            awardCertificate(player, data, CertificationSystem.CertificationType.CLEAN_AIR_HERO);
            
            // Special Clean Air Hero announcement
            player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("════════════════════════════════").formatted(Formatting.GOLD), false);
            player.sendMessage(Text.literal("  🌟 CLEAN AIR HERO ACHIEVED! 🌟").formatted(Formatting.GREEN, Formatting.BOLD), false);
            player.sendMessage(Text.literal("════════════════════════════════").formatted(Formatting.GOLD), false);
            player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("You have successfully completed all regions").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("and demonstrated exceptional environmental").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("leadership in the fight against air pollution!").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("Your knowledge and skills can now be used").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("to tackle real-world air quality challenges!").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        }
    }
    
    public void displayPlayerProfile(ServerPlayerEntity player) {
        PlayerAchievementData data = getPlayerData(player.getUuid());
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("═════ CLEAN AIR HERO PROFILE ═════").formatted(Formatting.AQUA, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Player: " + player.getName().getString()).formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("Level: " + data.heroLevel.icon + " " + data.heroLevel.displayName)
            .formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("Total Points: " + data.totalCleanAirPoints).formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("Global Ranking: #" + data.globalRanking).formatted(Formatting.BLUE), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Regional progress
        player.sendMessage(Text.literal("🌍 REGIONAL PROGRESS:").formatted(Formatting.AQUA), false);
        for (Map.Entry<String, Integer> entry : data.regionScores.entrySet()) {
            String regionName = getRegionDisplayName(entry.getKey());
            int score = entry.getValue();
            String status = score >= 70 ? "✅" : (score > 0 ? "🔄" : "🔒");
            player.sendMessage(Text.literal(status + " " + regionName + ": " + score + "/100")
                .formatted(Formatting.WHITE), false);
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Achievements
        player.sendMessage(Text.literal("🏆 ACHIEVEMENTS: " + data.earnedAchievements.size() + "/" + achievements.size())
            .formatted(Formatting.GOLD), false);
        
        // Certificates
        player.sendMessage(Text.literal("📜 CERTIFICATES: " + data.earnedCertificates.size())
            .formatted(Formatting.YELLOW), false);
        
        player.sendMessage(Text.literal("═══════════════════════════════════").formatted(Formatting.AQUA), false);
    }
    
    // Helper methods for requirement checking
    private boolean checkStatisticRequirements(PlayerAchievementData data, Map<String, Integer> requirements) {
        return requirements.entrySet().stream().allMatch(entry -> 
            data.statisticCounters.getOrDefault(entry.getKey(), 0) >= entry.getValue());
    }
    
    private boolean checkGlobalRequirements(PlayerAchievementData data, Map<String, Integer> requirements) {
        for (Map.Entry<String, Integer> req : requirements.entrySet()) {
            switch (req.getKey()) {
                case "total_points":
                    if (data.totalCleanAirPoints < req.getValue()) return false;
                    break;
                case "regions_completed":
                    long completed = data.regionScores.values().stream().filter(s -> s >= 70).count();
                    if (completed < req.getValue()) return false;
                    break;
                default:
                    if (data.statisticCounters.getOrDefault(req.getKey(), 0) < req.getValue()) return false;
            }
        }
        return true;
    }
    
    private boolean checkSpeedRequirements(PlayerAchievementData data, Map<String, Integer> requirements) {
        // This would need to track completion times, simplified for now
        return data.earnedAchievements.size() >= 3; // Has completed multiple achievements quickly
    }
    
    private boolean checkPerfectScoreRequirements(PlayerAchievementData data, Map<String, Integer> requirements) {
        return data.regionScores.values().stream().anyMatch(score -> score >= 100);
    }
    
    private String getRegionalAchievementId(String regionId) {
        switch (regionId) {
            case "varna_bulgaria": return "varna_master";
            case "zonguldak_turkey": return "zonguldak_hero";
            case "odesa_ukraine": return "odesa_champion";
            case "trabzon_turkey": return "trabzon_traffic_expert";
            case "southeast_romania": return "romania_coordinator";
            default: return null;
        }
    }
    
    private String getRegionDisplayName(String regionId) {
        switch (regionId) {
            case "varna_bulgaria": return "Varna, Bulgaria";
            case "zonguldak_turkey": return "Zonguldak, Turkey";
            case "odesa_ukraine": return "Odesa, Ukraine";
            case "trabzon_turkey": return "Trabzon, Turkey";
            case "southeast_romania": return "Southeast Romania";
            default: return regionId;
        }
    }
    
    private void updateGlobalRanking(ServerPlayerEntity player, PlayerAchievementData data) {
        // In a real implementation, this would compare against all players
        List<Integer> allPoints = playerData.values().stream()
            .mapToInt(d -> d.totalCleanAirPoints)
            .boxed()
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        
        data.globalRanking = scoringSystem.calculateGlobalRanking(data.totalCleanAirPoints, allPoints);
    }
    
    public PlayerAchievementData getPlayerData(UUID playerId) {
        return playerData.computeIfAbsent(playerId, k -> new PlayerAchievementData());
    }
    
    public ScoringSystem getScoringSystem() {
        return scoringSystem;
    }
    
    public CertificationSystem getCertificationSystem() {
        return certificationSystem;
    }
    
    public Collection<Achievement> getAllAchievements() {
        return achievements.values();
    }
}