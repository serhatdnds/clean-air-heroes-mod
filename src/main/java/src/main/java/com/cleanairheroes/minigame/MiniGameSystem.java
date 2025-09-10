package com.cleanairheroes.minigame;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;

import com.cleanairheroes.data.PlayerData;
import com.cleanairheroes.data.PlayerProgress;

import java.util.*;

public class MiniGameSystem {
    
    public enum MiniGameType {
        // Varna Mini Games
        SHIP_EMISSION_CONTROL,
        GREEN_TRANSPORT_PUZZLE,
        AIR_QUALITY_TRACKING,
        
        // Zonguldak Mini Games
        MINE_DUST_CONTROL,
        THERMAL_PLANT_MODERNIZATION,
        TREE_PLANTING_SYSTEM,
        
        // Odesa Mini Games
        SHIP_TRAFFIC_MANAGEMENT,
        FACTORY_TRANSFORMATION,
        CITY_GREEN_PLANNING,
        
        // Trabzon Mini Games
        SMART_TRAFFIC_OPTIMIZATION,
        HOME_ENERGY_CONVERSION,
        RECYCLING_CENTER_OPERATION,
        
        // Romania Mini Games
        INDUSTRIAL_MODERNIZATION,
        PUBLIC_TRANSPORT_DESIGN,
        SUSTAINABLE_FARMING,
        POWER_PLANT_CONVERSION
    }
    
    public static class MiniGameResult {
        public final boolean success;
        public final int score;
        public final String feedback;
        public final List<String> rewards;
        
        public MiniGameResult(boolean success, int score, String feedback, List<String> rewards) {
            this.success = success;
            this.score = score;
            this.feedback = feedback;
            this.rewards = rewards;
        }
    }
    
    public static void startMiniGame(ServerPlayerEntity player, MiniGameType gameType) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        String region = progress.getCurrentRegion();
        
        // Validate mini game is available for current region
        if (!isGameAvailableForRegion(gameType, region)) {
            player.sendMessage(Text.literal("⚠️ This mini-game is not available in your current region!").formatted(Formatting.RED), false);
            return;
        }
        
        // Start the appropriate mini game
        switch (gameType) {
            case SHIP_EMISSION_CONTROL -> startShipEmissionControl(player);
            case GREEN_TRANSPORT_PUZZLE -> startGreenTransportPuzzle(player);
            case AIR_QUALITY_TRACKING -> startAirQualityTracking(player);
            case MINE_DUST_CONTROL -> startMineDustControl(player);
            case THERMAL_PLANT_MODERNIZATION -> startThermalPlantModernization(player);
            case TREE_PLANTING_SYSTEM -> startTreePlantingSystem(player);
            case SHIP_TRAFFIC_MANAGEMENT -> startShipTrafficManagement(player);
            case FACTORY_TRANSFORMATION -> startFactoryTransformation(player);
            case CITY_GREEN_PLANNING -> startCityGreenPlanning(player);
            case SMART_TRAFFIC_OPTIMIZATION -> startSmartTrafficOptimization(player);
            case HOME_ENERGY_CONVERSION -> startHomeEnergyConversion(player);
            case RECYCLING_CENTER_OPERATION -> startRecyclingCenterOperation(player);
            case INDUSTRIAL_MODERNIZATION -> startIndustrialModernization(player);
            case PUBLIC_TRANSPORT_DESIGN -> startPublicTransportDesign(player);
            case SUSTAINABLE_FARMING -> startSustainableFarming(player);
            case POWER_PLANT_CONVERSION -> startPowerPlantConversion(player);
        }
    }
    
    // Varna Mini Games
    private static void startShipEmissionControl(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🚢 SHIP EMISSION CONTROL MINI-GAME").formatted(Formatting.BLUE, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Remove black smoke clouds from ships in Varna port!").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Simulate mini-game mechanics
        player.sendMessage(Text.literal("Phase 1: Install scrubber systems on cargo ships").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("⚙️ Installing marine scrubbers...").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("✓ 3 cargo ships equipped with scrubber systems").formatted(Formatting.GREEN), false);
        
        player.sendMessage(Text.literal("Phase 2: Optimize fuel efficiency").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("🔧 Tuning engine parameters...").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("✓ 25% fuel efficiency improvement achieved").formatted(Formatting.GREEN), false);
        
        player.sendMessage(Text.literal("Phase 3: Monitor emission reductions").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("📊 Measuring SO2 and PM reduction...").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("✓ 60% emission reduction confirmed").formatted(Formatting.GREEN), false);
        
        completeMiniGame(player, new MiniGameResult(
            true, 85, "Excellent work! Varna port emissions significantly reduced.",
            Arrays.asList("Marine Engineer Certificate", "Port Authority Recognition", "25 Clean Air Points")
        ));
    }
    
    private static void startGreenTransportPuzzle(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🚌 GREEN TRANSPORT NETWORK PUZZLE").formatted(Formatting.GREEN, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Design an eco-friendly transport system for Varna!").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        player.sendMessage(Text.literal("Step 1: Electric bus route optimization").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("🔌 Planning 5 electric bus routes...").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("✓ Optimal routes connecting port, center, and beaches").formatted(Formatting.GREEN), false);
        
        player.sendMessage(Text.literal("Step 2: Bicycle infrastructure design").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("🚴 Creating bike lane network...").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("✓ 15km of protected bike lanes designed").formatted(Formatting.GREEN), false);
        
        player.sendMessage(Text.literal("Step 3: Integration with existing transport").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("🚊 Connecting with tram and ferry systems...").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("✓ Seamless multimodal transport network created").formatted(Formatting.GREEN), false);
        
        completeMiniGame(player, new MiniGameResult(
            true, 90, "Perfect! Your green transport network reduces city emissions by 40%.",
            Arrays.asList("Urban Planning Certificate", "Sustainable Transport Expert", "30 Clean Air Points")
        ));
    }
    
    private static void startAirQualityTracking(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("📊 AIR QUALITY MONITORING CHALLENGE").formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Track and analyze pollution levels across Varna!").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        player.sendMessage(Text.literal("Mission: Install 8 monitoring stations strategically").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("📡 Positioning sensors in key locations...").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("✓ Port area: PM2.5 and SO2 monitoring active").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("✓ City center: NO2 and CO tracking online").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("✓ Industrial zone: Heavy metals detection enabled").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("✓ Residential areas: Particulate matter sensors deployed").formatted(Formatting.GREEN), false);
        
        player.sendMessage(Text.literal("Analysis: Data correlation and trend identification").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("📈 Processing 24-hour data cycles...").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("✓ Peak pollution hours identified: 7-9 AM, 5-7 PM").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("✓ Hotspots mapped: Port loading areas, main boulevard").formatted(Formatting.GREEN), false);
        
        completeMiniGame(player, new MiniGameResult(
            true, 88, "Excellent monitoring network! Real-time air quality data now available.",
            Arrays.asList("Environmental Monitoring Expert", "Data Analysis Certification", "28 Clean Air Points")
        ));
    }
    
    // Zonguldak Mini Games
    private static void startMineDustControl(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("⛏️ MINE DUST CONTROL SIMULATION").formatted(Formatting.DARK_RED, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Implement dust suppression in Çatalağzı coal mines!").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        player.sendMessage(Text.literal("Challenge: Reduce PM10 emissions by 70%").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("💧 Installing water spray systems...").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("✓ High-pressure misting at extraction points").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("✓ Conveyor belt enclosure systems").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("✓ Truck loading area dust barriers").formatted(Formatting.GREEN), false);
        
        player.sendMessage(Text.literal("Worker Safety: Personal protection enhancement").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("🦺 Upgrading safety equipment...").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("✓ 850 miners equipped with advanced respirators").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("✓ Underground air filtration systems installed").formatted(Formatting.GREEN), false);
        
        completeMiniGame(player, new MiniGameResult(
            true, 92, "Outstanding! Mine dust emissions reduced by 75%.",
            Arrays.asList("Mining Safety Expert", "Industrial Hygiene Certificate", "35 Clean Air Points")
        ));
    }
    
    // Continue with other mini games...
    private static void startThermalPlantModernization(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("⚡ THERMAL PLANT MODERNIZATION PUZZLE").formatted(Formatting.DARK_BLUE, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Upgrade Çatalağzı Power Plant with modern filters!").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        player.sendMessage(Text.literal("Phase 1: Flue Gas Desulfurization (FGD) installation").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("🔧 Installing limestone-based FGD system...").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("✓ 95% SO2 removal efficiency achieved").formatted(Formatting.GREEN), false);
        
        player.sendMessage(Text.literal("Phase 2: Selective Catalytic Reduction (SCR)").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("⚗️ Installing NOx reduction catalysts...").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("✓ 85% NOx emission reduction confirmed").formatted(Formatting.GREEN), false);
        
        player.sendMessage(Text.literal("Phase 3: Electrostatic precipitators upgrade").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("⚡ Enhancing particulate capture...").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("✓ 99.5% ash particle removal efficiency").formatted(Formatting.GREEN), false);
        
        completeMiniGame(player, new MiniGameResult(
            true, 94, "Exceptional modernization! Power plant now meets EU standards.",
            Arrays.asList("Power Plant Engineer", "Clean Technology Specialist", "40 Clean Air Points")
        ));
    }
    
    // Helper methods
    private static boolean isGameAvailableForRegion(MiniGameType gameType, String region) {
        return switch (region) {
            case "varna" -> Arrays.asList(
                MiniGameType.SHIP_EMISSION_CONTROL,
                MiniGameType.GREEN_TRANSPORT_PUZZLE,
                MiniGameType.AIR_QUALITY_TRACKING
            ).contains(gameType);
            
            case "zonguldak" -> Arrays.asList(
                MiniGameType.MINE_DUST_CONTROL,
                MiniGameType.THERMAL_PLANT_MODERNIZATION,
                MiniGameType.TREE_PLANTING_SYSTEM
            ).contains(gameType);
            
            case "odesa" -> Arrays.asList(
                MiniGameType.SHIP_TRAFFIC_MANAGEMENT,
                MiniGameType.FACTORY_TRANSFORMATION,
                MiniGameType.CITY_GREEN_PLANNING
            ).contains(gameType);
            
            case "trabzon" -> Arrays.asList(
                MiniGameType.SMART_TRAFFIC_OPTIMIZATION,
                MiniGameType.HOME_ENERGY_CONVERSION,
                MiniGameType.RECYCLING_CENTER_OPERATION
            ).contains(gameType);
            
            case "romania" -> Arrays.asList(
                MiniGameType.INDUSTRIAL_MODERNIZATION,
                MiniGameType.PUBLIC_TRANSPORT_DESIGN,
                MiniGameType.SUSTAINABLE_FARMING,
                MiniGameType.POWER_PLANT_CONVERSION
            ).contains(gameType);
            
            default -> false;
        };
    }
    
    private static void completeMiniGame(ServerPlayerEntity player, MiniGameResult result) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        
        // Play completion sound
        if (result.success) {
            player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1.0f, 1.2f);
        } else {
            player.playSound(SoundEvents.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 0.8f);
        }
        
        // Show results
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🏁 MINI-GAME COMPLETED!").formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Score: " + result.score + "/100").formatted(result.success ? Formatting.GREEN : Formatting.RED), false);
        player.sendMessage(Text.literal(result.feedback).formatted(Formatting.AQUA), false);
        
        if (result.success) {
            player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("🎁 Rewards Earned:").formatted(Formatting.YELLOW, Formatting.BOLD), false);
            for (String reward : result.rewards) {
                player.sendMessage(Text.literal("• " + reward).formatted(Formatting.GREEN), false);
                
                // Apply actual rewards
                if (reward.contains("Points")) {
                    int points = Integer.parseInt(reward.replaceAll("[^0-9]", ""));
                    progress.addScore(progress.getCurrentRegion(), points);
                }
            }
            
            // Track mini-game completion
            progress.addCompletedMission("minigame_" + System.currentTimeMillis());
            PlayerData.updatePlayer(player, progress);
            
            player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("Your environmental expertise grows stronger!").formatted(Formatting.GREEN, Formatting.ITALIC), false);
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
    }
    
    // Placeholder implementations for remaining mini-games
    private static void startTreePlantingSystem(ServerPlayerEntity player) {
        // Implementation for tree planting mini-game
        completeMiniGame(player, new MiniGameResult(true, 87, "Great reforestation effort!", Arrays.asList("Forest Restoration Expert", "20 Clean Air Points")));
    }
    
    private static void startShipTrafficManagement(ServerPlayerEntity player) {
        // Implementation for ship traffic management
        completeMiniGame(player, new MiniGameResult(true, 91, "Excellent port optimization!", Arrays.asList("Port Management Expert", "32 Clean Air Points")));
    }
    
    private static void startFactoryTransformation(ServerPlayerEntity player) {
        // Implementation for factory transformation
        completeMiniGame(player, new MiniGameResult(true, 89, "Successful industrial modernization!", Arrays.asList("Industrial Transformation Expert", "30 Clean Air Points")));
    }
    
    private static void startCityGreenPlanning(ServerPlayerEntity player) {
        // Implementation for city green planning
        completeMiniGame(player, new MiniGameResult(true, 93, "Beautiful green city design!", Arrays.asList("Urban Green Planner", "38 Clean Air Points")));
    }
    
    private static void startSmartTrafficOptimization(ServerPlayerEntity player) {
        // Implementation for smart traffic optimization
        completeMiniGame(player, new MiniGameResult(true, 86, "Smart traffic system deployed!", Arrays.asList("Traffic Systems Expert", "28 Clean Air Points")));
    }
    
    private static void startHomeEnergyConversion(ServerPlayerEntity player) {
        // Implementation for home energy conversion
        completeMiniGame(player, new MiniGameResult(true, 88, "Successful energy transition!", Arrays.asList("Renewable Energy Specialist", "29 Clean Air Points")));
    }
    
    private static void startRecyclingCenterOperation(ServerPlayerEntity player) {
        // Implementation for recycling center operation
        completeMiniGame(player, new MiniGameResult(true, 85, "Efficient waste management!", Arrays.asList("Waste Management Expert", "26 Clean Air Points")));
    }
    
    private static void startIndustrialModernization(ServerPlayerEntity player) {
        // Implementation for industrial modernization
        completeMiniGame(player, new MiniGameResult(true, 92, "Complete industrial transformation!", Arrays.asList("Industrial Modernization Leader", "35 Clean Air Points")));
    }
    
    private static void startPublicTransportDesign(ServerPlayerEntity player) {
        // Implementation for public transport design
        completeMiniGame(player, new MiniGameResult(true, 90, "Excellent transport network!", Arrays.asList("Transport Network Designer", "33 Clean Air Points")));
    }
    
    private static void startSustainableFarming(ServerPlayerEntity player) {
        // Implementation for sustainable farming
        completeMiniGame(player, new MiniGameResult(true, 87, "Sustainable agriculture achieved!", Arrays.asList("Sustainable Agriculture Expert", "30 Clean Air Points")));
    }
    
    private static void startPowerPlantConversion(ServerPlayerEntity player) {
        // Implementation for power plant conversion
        completeMiniGame(player, new MiniGameResult(true, 94, "Complete energy transformation!", Arrays.asList("Energy Transformation Leader", "42 Clean Air Points")));
    }
}