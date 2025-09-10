package com.cleanairheroes.command;

import com.cleanairheroes.CleanAirHeroes;
import com.cleanairheroes.rewards.RewardSystem;
import com.cleanairheroes.minigame.MiniGameSystem;
import com.cleanairheroes.story.RegionalTransitionStory;
import com.cleanairheroes.center.EnvironmentalProtectionCenter;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ModCommands {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        
        // Main clean air heroes command
        dispatcher.register(CommandManager.literal("cleanair")
            .then(CommandManager.literal("start")
                .executes(ModCommands::startGame))
            .then(CommandManager.literal("profile")
                .executes(ModCommands::showProfile))
            .then(CommandManager.literal("travel")
                .executes(ModCommands::showTravelHub))
            .then(CommandManager.literal("mission")
                .then(CommandManager.literal("start")
                    .then(CommandManager.argument("missionId", StringArgumentType.string())
                        .executes(ModCommands::startMission)))
                .then(CommandManager.literal("complete")
                    .then(CommandManager.argument("missionId", StringArgumentType.string())
                        .executes(ModCommands::completeMission)))
                .then(CommandManager.literal("progress")
                    .executes(ModCommands::showMissionProgress)))
            .then(CommandManager.literal("pollution")
                .then(CommandManager.literal("check")
                    .executes(ModCommands::checkPollution))
                .then(CommandManager.literal("monitor")
                    .executes(ModCommands::startPollutionMonitoring)))
            .then(CommandManager.literal("minigame")
                .then(CommandManager.argument("gameType", StringArgumentType.string())
                    .executes(ModCommands::startMiniGame)))
            .then(CommandManager.literal("teleport")
                .then(CommandManager.argument("region", StringArgumentType.string())
                    .executes(ModCommands::teleportToRegion)))
            .then(CommandManager.literal("achievements")
                .executes(ModCommands::showAchievements))
            .then(CommandManager.literal("certificates")
                .executes(ModCommands::showCertificates))
            .then(CommandManager.literal("rewards")
                .executes(ModCommands::showRewards))
            .then(CommandManager.literal("center")
                .executes(ModCommands::openProtectionCenter)
                .then(CommandManager.literal("map")
                    .executes(ModCommands::showAirQualityMap))
                .then(CommandManager.literal("gallery")
                    .executes(ModCommands::showAchievementGallery))
                .then(CommandManager.literal("travel")
                    .executes(ModCommands::showTravelHub))
                .then(CommandManager.literal("report")
                    .executes(ModCommands::showGlobalReport)))
            .then(CommandManager.literal("story")
                .then(CommandManager.literal("transition")
                    .executes(ModCommands::showStoryTransition)))
            .then(CommandManager.literal("help")
                .executes(ModCommands::showHelp))
        );
        
        // Quick access commands
        dispatcher.register(CommandManager.literal("cah")
            .redirect(dispatcher.getRoot().getChild("cleanair")));
            
        // Debug commands (admin only)
        dispatcher.register(CommandManager.literal("cahdebug")
            .requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.literal("setpoints")
                .then(CommandManager.argument("points", IntegerArgumentType.integer())
                    .executes(ModCommands::debugSetPoints)))
            .then(CommandManager.literal("completemission")
                .then(CommandManager.argument("missionId", StringArgumentType.string())
                    .executes(ModCommands::debugCompleteMission)))
            .then(CommandManager.literal("unlockregion")
                .then(CommandManager.argument("region", StringArgumentType.string())
                    .executes(ModCommands::debugUnlockRegion))));
    }
    
    private static int startGame(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("══════════════════════════════════").formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("  🌍 CLEAN AIR HEROES 🌍").formatted(Formatting.GREEN, Formatting.BOLD), false);
        player.sendMessage(Text.literal("  Environmental Engineering Adventure").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("══════════════════════════════════").formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Welcome, Environmental Engineer!").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("Your mission: Fight air pollution across").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("5 real-world regions using science and").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("engineering to become a Clean Air Hero!").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("🎯 Starting Location: Varna, Bulgaria").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("📊 Your mission begins at the port...").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Commands:").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("• /cleanair mission progress - View current missions").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("• /cleanair pollution check - Check air quality").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("• /cleanair profile - View your progress").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("• /cleanair help - Full command list").formatted(Formatting.WHITE), false);
        
        // Initialize player in the game
        CleanAirHeroes.getLevelManager().startLevel(player, 0);
        
        return 1;
    }
    
    private static int showProfile(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        
        // This would integrate with the achievement system
        player.sendMessage(Text.literal("Opening your Clean Air Hero profile...").formatted(Formatting.GREEN), false);
        
        return 1;
    }
    
    private static int showTravelHub(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        
        player.sendMessage(Text.literal("🌍 ENVIRONMENTAL PROTECTION CENTER").formatted(Formatting.GREEN, Formatting.BOLD), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Available Regions:").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("🔓 Varna, Bulgaria - Port pollution control").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("🔒 Zonguldak, Turkey - Coal mining cleanup").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("🔒 Odesa, Ukraine - Maritime optimization").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("🔒 Trabzon, Turkey - Traffic management").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("🔒 Southeast Romania - Industrial transformation").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Complete missions to unlock new regions!").formatted(Formatting.YELLOW), false);
        
        return 1;
    }
    
    private static int startMission(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String missionId = StringArgumentType.getString(context, "missionId");
        
        player.sendMessage(Text.literal("Starting mission: " + missionId).formatted(Formatting.GREEN), false);
        
        // Integrate with mission system
        // Mission mission = MissionRegistry.getMission(missionId);
        // if (mission != null) {
        //     mission.start(player);
        // }
        
        return 1;
    }
    
    private static int completeMission(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String missionId = StringArgumentType.getString(context, "missionId");
        
        player.sendMessage(Text.literal("Mission completed: " + missionId).formatted(Formatting.GREEN), false);
        
        // This would integrate with level manager
        CleanAirHeroes.getLevelManager().completeMission(player, missionId);
        
        // Check for rewards after mission completion
        RewardSystem.checkAndAwardLevelRewards(player);
        
        return 1;
    }
    
    private static int showMissionProgress(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        
        player.sendMessage(Text.literal("📋 CURRENT MISSIONS").formatted(Formatting.AQUA, Formatting.BOLD), false);
        player.sendMessage(Text.literal(""), false);
        
        // Get current level and show missions
        var currentLevel = CleanAirHeroes.getLevelManager().getCurrentLevel(player);
        if (currentLevel != null) {
            player.sendMessage(Text.literal("Region: " + currentLevel.getDisplayName()).formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal(""), false);
            
            // Show mission progress (this would come from actual mission data)
            player.sendMessage(Text.literal("🚢 Port Cleanup: ██░░░░ 30%").formatted(Formatting.BLUE), false);
            player.sendMessage(Text.literal("🚌 Green Transport: ░░░░░░ 0%").formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("🏠 Smart Heating: ░░░░░░ 0%").formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("🏭 Industrial Filters: ░░░░░░ 0%").formatted(Formatting.GRAY), false);
        } else {
            player.sendMessage(Text.literal("No active missions. Use /cleanair start to begin!").formatted(Formatting.YELLOW), false);
        }
        
        return 1;
    }
    
    private static int checkPollution(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        
        // Get pollution data from pollution manager
        double aqi = CleanAirHeroes.getPollutionManager().getAQIAt(player.getBlockPos());
        
        String aqiCategory;
        Formatting aqiColor;
        
        if (aqi <= 50) {
            aqiCategory = "Good";
            aqiColor = Formatting.GREEN;
        } else if (aqi <= 100) {
            aqiCategory = "Moderate";
            aqiColor = Formatting.YELLOW;
        } else if (aqi <= 150) {
            aqiCategory = "Unhealthy for Sensitive Groups";
            aqiColor = Formatting.GOLD;
        } else if (aqi <= 200) {
            aqiCategory = "Unhealthy";
            aqiColor = Formatting.RED;
        } else {
            aqiCategory = "Very Unhealthy";
            aqiColor = Formatting.DARK_RED;
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🌪️ AIR QUALITY READING").formatted(Formatting.BLUE, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Location: " + player.getBlockPos().getX() + ", " + player.getBlockPos().getZ()).formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("AQI: " + String.format("%.1f", aqi)).formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Status: " + aqiCategory).formatted(aqiColor), false);
        player.sendMessage(Text.literal(""), false);
        
        if (aqi > 100) {
            player.sendMessage(Text.literal("⚠️ Poor air quality detected!").formatted(Formatting.RED), false);
            player.sendMessage(Text.literal("Consider installing pollution control measures.").formatted(Formatting.YELLOW), false);
        } else if (aqi > 50) {
            player.sendMessage(Text.literal("⚠️ Moderate pollution levels.").formatted(Formatting.YELLOW), false);
        } else {
            player.sendMessage(Text.literal("✅ Good air quality!").formatted(Formatting.GREEN), false);
        }
        
        return 1;
    }
    
    private static int startPollutionMonitoring(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        
        player.sendMessage(Text.literal("🔍 Starting pollution monitoring mode...").formatted(Formatting.BLUE), false);
        player.sendMessage(Text.literal("Walk around to collect air quality data!").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("Use /cleanair pollution check for detailed readings").formatted(Formatting.GRAY), false);
        
        return 1;
    }
    
    private static int startMiniGame(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String gameType = StringArgumentType.getString(context, "gameType");
        
        switch (gameType.toLowerCase()) {
            case "ship":
                player.sendMessage(Text.literal("🚢 Starting Ship Emission Control mini-game...").formatted(Formatting.BLUE), false);
                // Start ship emission control game
                break;
            case "transport":
                player.sendMessage(Text.literal("🚌 Starting Green Transport Network puzzle...").formatted(Formatting.GREEN), false);
                // Start transport puzzle
                break;
            case "monitor":
                player.sendMessage(Text.literal("📊 Starting Air Quality Monitoring game...").formatted(Formatting.AQUA), false);
                // Start monitoring game
                break;
            case "factory":
                player.sendMessage(Text.literal("🏭 Starting Factory Emission puzzle...").formatted(Formatting.RED), false);
                // Start factory puzzle
                break;
            default:
                player.sendMessage(Text.literal("Available mini-games:").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("• ship - Ship Emission Control").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("• transport - Green Transport Network").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("• monitor - Air Quality Monitoring").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("• factory - Factory Emission Control").formatted(Formatting.WHITE), false);
                break;
        }
        
        return 1;
    }
    
    private static int teleportToRegion(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String region = StringArgumentType.getString(context, "region");
        
        switch (region.toLowerCase()) {
            case "varna":
                player.sendMessage(Text.literal("🚢 Teleporting to Varna, Bulgaria...").formatted(Formatting.BLUE), true);
                break;
            case "zonguldak":
                player.sendMessage(Text.literal("⛏️ Teleporting to Zonguldak, Turkey...").formatted(Formatting.GRAY), true);
                break;
            case "odesa":
                player.sendMessage(Text.literal("🏭 Teleporting to Odesa, Ukraine...").formatted(Formatting.YELLOW), true);
                break;
            case "trabzon":
                player.sendMessage(Text.literal("🏔️ Teleporting to Trabzon, Turkey...").formatted(Formatting.GREEN), true);
                break;
            case "romania":
                player.sendMessage(Text.literal("🌍 Teleporting to Southeast Romania...").formatted(Formatting.RED), true);
                break;
            case "center":
                player.sendMessage(Text.literal("🌍 Teleporting to Environmental Protection Center...").formatted(Formatting.GOLD), true);
                break;
            default:
                player.sendMessage(Text.literal("Available regions:").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("• varna, zonguldak, odesa, trabzon, romania, center").formatted(Formatting.WHITE), false);
                return 0;
        }
        
        return 1;
    }
    
    private static int showAchievements(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        
        player.sendMessage(Text.literal("🏆 ACHIEVEMENTS").formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Regional Mastery:").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("🔓 Varna Port Master (50 pts)").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("🔒 Zonguldak Mining Hero (50 pts)").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("🔒 Odesa Port Champion (50 pts)").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("🔒 Trabzon Traffic Expert (50 pts)").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("🔒 Romania Coordinator (75 pts)").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Progress: 1/25 achievements unlocked").formatted(Formatting.YELLOW), false);
        
        return 1;
    }
    
    private static int showCertificates(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        
        player.sendMessage(Text.literal("📜 CERTIFICATES").formatted(Formatting.YELLOW, Formatting.BOLD), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Professional Certifications:").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("🔒 Port Emission Control Specialist").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("🔒 Industrial Pollution Expert").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("🔒 Smart Traffic Systems").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("🔒 Renewable Energy Advocate").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("🔒 Air Quality Monitoring").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("🔒 Regional Environmental Coordinator").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("🌟 Ultimate Goal: CLEAN AIR HERO").formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Complete all regions with 70+ scores to earn!").formatted(Formatting.YELLOW), false);
        
        return 1;
    }
    
    private static int showHelp(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🌍 CLEAN AIR HEROES - COMMAND HELP").formatted(Formatting.GREEN, Formatting.BOLD), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Game Management:").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("• /cleanair start - Start your environmental journey").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("• /cleanair profile - View your hero profile").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("• /cleanair travel - Open travel hub").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Mission System:").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("• /cleanair mission progress - Current missions").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("• /cleanair mission start <id> - Start specific mission").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("• /cleanair mission complete <id> - Complete mission").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Environmental Tools:").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("• /cleanair pollution check - Check air quality").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("• /cleanair pollution monitor - Start monitoring mode").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Mini-Games:").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("• /cleanair minigame <type> - Start mini-game").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("  Types: ship, transport, monitor, factory").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Progress Tracking:").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("• /cleanair achievements - View achievements").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("• /cleanair certificates - View certificates").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("• /cleanair rewards - View reward progress").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Environmental Center:").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("• /cleanair center - Open Protection Center").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("• /cleanair center map - Regional air quality map").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("• /cleanair center gallery - Achievement gallery").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("• /cleanair center travel - Travel hub").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("• /cleanair story transition - Story journeys").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Quick Access: /cah <command>").formatted(Formatting.YELLOW), false);
        
        return 1;
    }
    
    // Debug commands
    private static int debugSetPoints(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        int points = IntegerArgumentType.getInteger(context, "points");
        
        player.sendMessage(Text.literal("DEBUG: Setting Clean Air Points to " + points).formatted(Formatting.GOLD), false);
        
        return 1;
    }
    
    private static int debugCompleteMission(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String missionId = StringArgumentType.getString(context, "missionId");
        
        player.sendMessage(Text.literal("DEBUG: Force completing mission " + missionId).formatted(Formatting.GOLD), false);
        
        return 1;
    }
    
    private static int debugUnlockRegion(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        String region = StringArgumentType.getString(context, "region");
        
        player.sendMessage(Text.literal("DEBUG: Unlocking region " + region).formatted(Formatting.GOLD), false);
        
        return 1;
    }
    
    private static int showRewards(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        RewardSystem.showRewardProgress(player);
        return 1;
    }
    
    private static int openProtectionCenter(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        EnvironmentalProtectionCenter.openProtectionCenter(player);
        return 1;
    }
    
    private static int showAirQualityMap(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        EnvironmentalProtectionCenter.showAirQualityMap(player);
        return 1;
    }
    
    private static int showAchievementGallery(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        EnvironmentalProtectionCenter.showAchievementGallery(player);
        return 1;
    }
    
    private static int showTravelHub(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        EnvironmentalProtectionCenter.showTravelHub(player);
        return 1;
    }
    
    private static int showGlobalReport(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        EnvironmentalProtectionCenter.showGlobalImpactReport(player);
        return 1;
    }
    
    private static int showStoryTransition(CommandContext<ServerCommandSource> context) {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        RegionalTransitionStory.showAvailableTransitions(player);
        return 1;
    }
}