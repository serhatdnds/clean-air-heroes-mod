package com.cleanairheroes.entity.vehicle;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import com.cleanairheroes.CleanAirHeroes;

public class PollutionMonitoringShipEntity extends MobEntity {
    
    private boolean isActive = false;
    private int monitoringRadius = 50;
    private long lastScanTime = 0;
    
    public PollutionMonitoringShipEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new LookAroundGoal(this));
        this.goalSelector.add(2, new WanderAroundGoal(this, 0.3));
    }
    
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.getWorld().isClient) {
            return ActionResult.CONSUME;
        }
        
        if (player instanceof ServerPlayerEntity serverPlayer) {
            handlePlayerInteraction(serverPlayer);
        }
        
        return ActionResult.SUCCESS;
    }
    
    private void handlePlayerInteraction(ServerPlayerEntity player) {
        if (!isActive) {
            activateMonitoring(player);
        } else {
            provideMonitoringData(player);
        }
    }
    
    private void activateMonitoring(ServerPlayerEntity player) {
        isActive = true;
        lastScanTime = getWorld().getTime();
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🛥️ Pollution Monitoring Ship").formatted(Formatting.BLUE, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Status: ACTIVATED").formatted(Formatting.GREEN, Formatting.BOLD), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        player.sendMessage(Text.literal("⚙️ Systems Online:").formatted(Formatting.YELLOW, Formatting.BOLD), false);
        player.sendMessage(Text.literal("• Water quality sensors").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("• Air pollution detectors").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("• Marine life scanners").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("• Data transmission array").formatted(Formatting.GREEN), false);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Monitoring radius: " + monitoringRadius + " blocks").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("Scanning marine environment...").formatted(Formatting.YELLOW), false);
        
        // Award activation points
        var progress = CleanAirHeroes.getLevelManager().getPlayerProgress(player);
        progress.addScore(progress.getCurrentRegion(), 10);
        progress.addEquipmentInstalled(1);
    }
    
    private void provideMonitoringData(ServerPlayerEntity player) {
        long currentTime = getWorld().getTime();
        long timeSinceLastScan = currentTime - lastScanTime;
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("📊 Marine Environment Report").formatted(Formatting.BLUE, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Scan time: " + (timeSinceLastScan / 20) + " seconds ago").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Simulated monitoring data
        double waterQuality = 65 + (Math.random() * 30); // 65-95
        double marineLife = 40 + (Math.random() * 40);   // 40-80
        double pollutionLevel = 30 + (Math.random() * 50); // 30-80
        
        player.sendMessage(Text.literal("🌊 Water Quality Index: " + String.format("%.1f", waterQuality)).formatted(getQualityColor(waterQuality)), false);
        player.sendMessage(Text.literal("🐟 Marine Life Health: " + String.format("%.1f%%", marineLife)).formatted(getHealthColor(marineLife)), false);
        player.sendMessage(Text.literal("💨 Ship Emission Level: " + String.format("%.1f ppm", pollutionLevel)).formatted(getPollutionColor(pollutionLevel)), false);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Detected pollution sources
        player.sendMessage(Text.literal("🚨 Detected Sources:").formatted(Formatting.RED, Formatting.BOLD), false);
        player.sendMessage(Text.literal("• 3 cargo ships (high SO2)").formatted(Formatting.RED), false);
        player.sendMessage(Text.literal("• 1 cruise ship (wastewater)").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("• Port facilities (fuel loading)").formatted(Formatting.YELLOW), false);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Recommendations
        player.sendMessage(Text.literal("💡 Recommendations:").formatted(Formatting.GREEN, Formatting.BOLD), false);
        if (pollutionLevel > 60) {
            player.sendMessage(Text.literal("• Implement ship emission controls").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• Establish green shipping lanes").formatted(Formatting.WHITE), false);
        }
        if (marineLife < 60) {
            player.sendMessage(Text.literal("• Create marine protection zones").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• Monitor fishing activities").formatted(Formatting.WHITE), false);
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Data uploaded to research database").formatted(Formatting.GREEN), false);
        
        // Award monitoring points
        var progress = CleanAirHeroes.getLevelManager().getPlayerProgress(player);
        progress.addScore(progress.getCurrentRegion(), 5);
        progress.addPollutionReduced(1.5);
        
        lastScanTime = currentTime;
    }
    
    private Formatting getQualityColor(double value) {
        if (value >= 80) return Formatting.GREEN;
        if (value >= 60) return Formatting.YELLOW;
        return Formatting.RED;
    }
    
    private Formatting getHealthColor(double value) {
        if (value >= 70) return Formatting.GREEN;
        if (value >= 50) return Formatting.YELLOW;
        return Formatting.RED;
    }
    
    private Formatting getPollutionColor(double value) {
        if (value <= 40) return Formatting.GREEN;
        if (value <= 60) return Formatting.YELLOW;
        return Formatting.RED;
    }
    
    @Override
    public boolean cannotDespawn() {
        return true;
    }
    
    @Override
    public Text getName() {
        return Text.literal("Research Vessel Poseidon");
    }
    
    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }
}