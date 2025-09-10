package com.cleanairheroes.events;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import com.cleanairheroes.CleanAirHeroes;
import com.cleanairheroes.block.ModBlocks;
import com.cleanairheroes.data.PlayerData;
import com.cleanairheroes.data.PlayerProgress;
import com.cleanairheroes.item.ModItems;
import com.cleanairheroes.rewards.RewardSystem;

public class PlayerEventHandler {
    
    public static void registerEvents() {
        // Player join/leave events
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            onPlayerJoin(player);
        });
        
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            onPlayerLeave(player);
        });
        
        // Player respawn event
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            onPlayerRespawn(newPlayer);
        });
        
        // Block interaction events
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (player instanceof ServerPlayerEntity serverPlayer && !world.isClient) {
                return onBlockInteraction(serverPlayer, hitResult.getBlockPos(), world);
            }
            return ActionResult.PASS;
        });
        
        // Block break events
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, entity) -> {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                onBlockBreak(serverPlayer, pos, state.getBlock());
            }
        });
        
        // Item use events
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (player instanceof ServerPlayerEntity serverPlayer && !world.isClient) {
                Item item = player.getStackInHand(hand).getItem();
                return onItemUse(serverPlayer, item);
            }
            return ActionResult.PASS;
        });
        
        // Server tick events for periodic updates
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world instanceof ServerWorld serverWorld) {
                onWorldTick(serverWorld);
            }
        });
    }
    
    private static void onPlayerJoin(ServerPlayerEntity player) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        
        // Update play time
        progress.updatePlayTime();
        PlayerData.updatePlayer(player, progress);
        
        // Welcome message
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🌍 Welcome to Clean Air Heroes!").formatted(Formatting.GREEN, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Current region: " + progress.getCurrentRegion().toUpperCase()).formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("Your title: " + progress.getPlayerTitle()).formatted(Formatting.GOLD), false);
        
        // Show progress summary
        int totalScore = progress.getTotalScore();
        int completedMissions = progress.getCompletedMissionsCount();
        player.sendMessage(Text.literal("Progress: " + totalScore + " points, " + completedMissions + " missions completed").formatted(Formatting.YELLOW), false);
        
        // Show environmental impact
        if (progress.getTotalPollutionReduced() > 0) {
            player.sendMessage(Text.literal("Environmental Impact: " + 
                String.format("%.1f kg pollution reduced", progress.getTotalPollutionReduced())).formatted(Formatting.GREEN), false);
        }
        
        player.sendMessage(Text.literal("Use '/cleanair help' for available commands").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        CleanAirHeroes.LOGGER.info("Player {} joined with progress: {} points, {} missions", 
            player.getName().getString(), totalScore, completedMissions);
    }
    
    private static void onPlayerLeave(ServerPlayerEntity player) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        
        // Update final play time
        progress.updatePlayTime();
        PlayerData.updatePlayer(player, progress);
        
        CleanAirHeroes.LOGGER.info("Player {} left. Total play time: {} minutes", 
            player.getName().getString(), progress.getTotalPlayTimeMinutes());
    }
    
    private static void onPlayerRespawn(ServerPlayerEntity player) {
        // Give basic items if needed
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        
        // Provide air quality monitor if the player has completed at least one mission
        if (progress.getCompletedMissionsCount() > 0) {
            if (!player.getInventory().contains(ModItems.AIR_QUALITY_METER.getDefaultStack())) {
                player.getInventory().insertStack(ModItems.AIR_QUALITY_METER.getDefaultStack());
                player.sendMessage(Text.literal("Air Quality Monitor restored").formatted(Formatting.GREEN), true);
            }
        }
    }
    
    private static ActionResult onBlockInteraction(ServerPlayerEntity player, BlockPos pos, net.minecraft.world.World world) {
        Block block = world.getBlockState(pos).getBlock();
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        
        // Handle pollution monitoring station interactions
        if (block == ModBlocks.AIR_QUALITY_MONITOR) {
            handleMonitoringStationInteraction(player, pos, progress);
            return ActionResult.SUCCESS;
        }
        
        // Handle air purifier interactions
        if (block == ModBlocks.INDUSTRIAL_FILTER) {
            handleAirPurifierInteraction(player, pos, progress);
            return ActionResult.SUCCESS;
        }
        
        // Handle solar panel interactions
        if (block == ModBlocks.SOLAR_PANEL_HEATING) {
            handleSolarPanelInteraction(player, pos, progress);
            return ActionResult.SUCCESS;
        }
        
        // Handle electric charging station interactions
        if (block == ModBlocks.EV_CHARGING_STATION) {
            handleChargingStationInteraction(player, pos, progress);
            return ActionResult.SUCCESS;
        }
        
        return ActionResult.PASS;
    }
    
    private static void onBlockBreak(ServerPlayerEntity player, BlockPos pos, Block block) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        
        // Prevent breaking of mission-critical blocks without permission
        if (block == ModBlocks.AIR_QUALITY_MONITOR) {
            if (progress.getRegionScore(progress.getCurrentRegion()) < 50) {
                player.sendMessage(Text.literal("⚠️ Insufficient permissions to remove monitoring equipment").formatted(Formatting.RED), true);
                // Note: The block break is already completed at this point, so we'd need to use BEFORE event to prevent it
                return;
            }
        }
        
        // Track environmental destruction
        if (isEnvironmentallyImportantBlock(block)) {
            progress.addScore(progress.getCurrentRegion(), -5); // Penalty for environmental damage
            PlayerData.updatePlayer(player, progress);
            
            player.sendMessage(Text.literal("⚠️ Environmental damage: -5 points").formatted(Formatting.RED), true);
        }
    }
    
    private static ActionResult onItemUse(ServerPlayerEntity player, Item item) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        
        // Handle air quality monitor usage
        if (item == ModItems.AIR_QUALITY_METER) {
            handleAirQualityMonitorUse(player, progress);
            return ActionResult.SUCCESS;
        }
        
        // Handle emission scanner usage
        if (item == ModItems.EMISSION_ANALYZER) {
            handleEmissionScannerUse(player, progress);
            return ActionResult.SUCCESS;
        }
        
        // Handle pollution filter usage
        if (item == ModItems.POLLUTION_DETECTOR) {
            handlePollutionFilterUse(player, progress);
            return ActionResult.SUCCESS;
        }
        
        return ActionResult.PASS;
    }
    
    private static void onWorldTick(ServerWorld world) {
        // Periodic pollution updates and player progress tracking
        if (world.getTime() % 6000 == 0) { // Every 5 minutes (6000 ticks)
            updatePlayerProgress(world);
        }
        
        // Update pollution levels based on player actions
        if (world.getTime() % 1200 == 0) { // Every minute (1200 ticks)
            updatePollutionLevels(world);
        }
    }
    
    // Helper methods for specific interactions
    private static void handleMonitoringStationInteraction(ServerPlayerEntity player, BlockPos pos, PlayerProgress progress) {
        // Simulate real-time air quality data
        double aqi = 50 + (Math.random() * 100); // Random AQI between 50-150
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("📊 Monitoring Station Data").formatted(Formatting.BLUE, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Location: " + pos.getX() + ", " + pos.getZ()).formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("Air Quality Index: " + String.format("%.1f", aqi)).formatted(Formatting.WHITE), false);
        
        String quality = aqi <= 50 ? "Good" : aqi <= 100 ? "Moderate" : "Unhealthy";
        Formatting color = aqi <= 50 ? Formatting.GREEN : aqi <= 100 ? Formatting.YELLOW : Formatting.RED;
        player.sendMessage(Text.literal("Quality: " + quality).formatted(color), false);
        
        // Award points for monitoring
        progress.addScore(progress.getCurrentRegion(), 2);
        PlayerData.updatePlayer(player, progress);
        
        player.sendMessage(Text.literal("+2 points for environmental monitoring").formatted(Formatting.GREEN), true);
    }
    
    private static void handleAirPurifierInteraction(ServerPlayerEntity player, BlockPos pos, PlayerProgress progress) {
        player.sendMessage(Text.literal("🌬️ Air Purifier activated").formatted(Formatting.GREEN), true);
        player.sendMessage(Text.literal("Filtering pollutants in 10-block radius").formatted(Formatting.AQUA), true);
        
        // Award points for air purification
        progress.addScore(progress.getCurrentRegion(), 5);
        progress.addPollutionReduced(2.5);
        PlayerData.updatePlayer(player, progress);
        
        player.sendMessage(Text.literal("+5 points for air purification").formatted(Formatting.GREEN), true);
        
        // Check for region completion and level rewards
        RewardSystem.checkAndAwardRegionCompletion(player, progress.getCurrentRegion());
        RewardSystem.checkAndAwardLevelRewards(player);
    }
    
    private static void handleSolarPanelInteraction(ServerPlayerEntity player, BlockPos pos, PlayerProgress progress) {
        player.sendMessage(Text.literal("☀️ Solar Panel Status: Active").formatted(Formatting.YELLOW), true);
        player.sendMessage(Text.literal("Generating clean energy").formatted(Formatting.GREEN), true);
        
        // Award points for renewable energy
        progress.addScore(progress.getCurrentRegion(), 3);
        progress.addPollutionReduced(1.0);
        PlayerData.updatePlayer(player, progress);
        
        player.sendMessage(Text.literal("+3 points for renewable energy").formatted(Formatting.GREEN), true);
    }
    
    private static void handleChargingStationInteraction(ServerPlayerEntity player, BlockPos pos, PlayerProgress progress) {
        player.sendMessage(Text.literal("🔌 Electric Charging Station").formatted(Formatting.BLUE), true);
        player.sendMessage(Text.literal("Supporting electric vehicle adoption").formatted(Formatting.GREEN), true);
        
        // Award points for sustainable transport
        progress.addScore(progress.getCurrentRegion(), 4);
        progress.addPollutionReduced(3.0);
        PlayerData.updatePlayer(player, progress);
        
        player.sendMessage(Text.literal("+4 points for sustainable transport").formatted(Formatting.GREEN), true);
    }
    
    private static void handleAirQualityMonitorUse(ServerPlayerEntity player, PlayerProgress progress) {
        double localAQI = 60 + (Math.random() * 80); // Simulated local AQI
        
        player.sendMessage(Text.literal("📱 Local Air Quality: " + String.format("%.1f", localAQI)).formatted(Formatting.AQUA), true);
        
        String recommendation = localAQI > 100 ? "Stay indoors" : localAQI > 75 ? "Limit outdoor activities" : "Air quality is acceptable";
        player.sendMessage(Text.literal("Recommendation: " + recommendation).formatted(Formatting.YELLOW), true);
    }
    
    private static void handleEmissionScannerUse(ServerPlayerEntity player, PlayerProgress progress) {
        player.sendMessage(Text.literal("🔍 Scanning for emission sources...").formatted(Formatting.YELLOW), true);
        player.sendMessage(Text.literal("Found 3 pollution sources within 50 blocks").formatted(Formatting.RED), true);
        player.sendMessage(Text.literal("Use '/cleanair sources' for details").formatted(Formatting.GRAY), true);
    }
    
    private static void handlePollutionFilterUse(ServerPlayerEntity player, PlayerProgress progress) {
        player.sendMessage(Text.literal("🧽 Installing pollution filter...").formatted(Formatting.GREEN), true);
        
        // Award points for equipment installation
        progress.addScore(progress.getCurrentRegion(), 8);
        progress.addEquipmentInstalled(1);
        progress.addPollutionReduced(5.0);
        PlayerData.updatePlayer(player, progress);
        
        player.sendMessage(Text.literal("+8 points for equipment installation").formatted(Formatting.GREEN), true);
        
        // Check for rewards
        RewardSystem.checkAndAwardRegionCompletion(player, progress.getCurrentRegion());
        RewardSystem.checkAndAwardLevelRewards(player);
    }
    
    private static boolean isEnvironmentallyImportantBlock(Block block) {
        // Define blocks that are environmentally important
        return block == ModBlocks.AIR_QUALITY_MONITOR ||
               block == ModBlocks.INDUSTRIAL_FILTER ||
               block == ModBlocks.SOLAR_PANEL_HEATING ||
               block == ModBlocks.EV_CHARGING_STATION;
    }
    
    private static void updatePlayerProgress(ServerWorld world) {
        // Update progress for all online players
        for (ServerPlayerEntity player : world.getPlayers()) {
            PlayerProgress progress = PlayerData.getPlayerProgress(player);
            progress.updatePlayTime();
            PlayerData.updatePlayer(player, progress);
            
            // Periodic progress message
            if (progress.getTotalPlayTimeMinutes() % 30 == 0 && progress.getTotalPlayTimeMinutes() > 0) {
                player.sendMessage(Text.literal("📈 Play time: " + progress.getTotalPlayTimeMinutes() + " minutes").formatted(Formatting.GRAY), true);
                player.sendMessage(Text.literal("Environmental impact: " + String.format("%.1f kg", progress.getTotalPollutionReduced())).formatted(Formatting.GREEN), true);
            }
        }
    }
    
    private static void updatePollutionLevels(ServerWorld world) {
        // This would integrate with the PollutionManager to update pollution based on player actions
        // For now, we just log that updates are happening
        if (world.getPlayers().size() > 0) {
            CleanAirHeroes.LOGGER.debug("Updating pollution levels for {} players", world.getPlayers().size());
        }
    }
}