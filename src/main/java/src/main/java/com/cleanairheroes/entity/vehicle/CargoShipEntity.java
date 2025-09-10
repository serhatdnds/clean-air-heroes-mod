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

public class CargoShipEntity extends MobEntity {
    
    private boolean emissionControlsActive = false;
    private String shipType = "container";
    private double currentEmissions = 85.0; // kg/hour
    
    public CargoShipEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
        // Randomize ship characteristics
        String[] types = {"container", "bulk_carrier", "tanker", "general_cargo"};
        this.shipType = types[(int)(Math.random() * types.length)];
        this.currentEmissions = 60 + (Math.random() * 50); // 60-110 kg/hour
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new LookAroundGoal(this));
        this.goalSelector.add(2, new WanderAroundGoal(this, 0.2)); // Slow movement like a real cargo ship
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
        showShipStatus(player);
        offerEmissionReduction(player);
    }
    
    private void showShipStatus(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🚢 Cargo Ship Inspection").formatted(Formatting.DARK_BLUE, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Vessel: " + getShipName()).formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("Type: " + getShipTypeDisplay()).formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Current emission status
        player.sendMessage(Text.literal("📊 Emission Report:").formatted(Formatting.YELLOW, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Current emissions: " + String.format("%.1f kg/hour", currentEmissions)).formatted(getEmissionColor()), false);
        
        String complianceStatus = currentEmissions <= 70 ? "COMPLIANT" : "EXCEEDS LIMITS";
        Formatting complianceColor = currentEmissions <= 70 ? Formatting.GREEN : Formatting.RED;
        player.sendMessage(Text.literal("IMO compliance: " + complianceStatus).formatted(complianceColor, Formatting.BOLD), false);
        
        if (emissionControlsActive) {
            player.sendMessage(Text.literal("Scrubber system: ACTIVE").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("Fuel efficiency: OPTIMIZED").formatted(Formatting.GREEN), false);
        } else {
            player.sendMessage(Text.literal("Scrubber system: INACTIVE").formatted(Formatting.RED), false);
            player.sendMessage(Text.literal("Fuel efficiency: STANDARD").formatted(Formatting.YELLOW), false);
        }
        
        // Cargo information
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("📦 Cargo Details:").formatted(Formatting.BLUE, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Load capacity: " + getCargoCapacity()).formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Origin port: " + getOriginPort()).formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Destination: " + getDestinationPort()).formatted(Formatting.WHITE), false);
    }
    
    private void offerEmissionReduction(ServerPlayerEntity player) {
        var progress = CleanAirHeroes.getLevelManager().getPlayerProgress(player);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        if (!emissionControlsActive && progress.getRegionScore(progress.getCurrentRegion()) >= 40) {
            player.sendMessage(Text.literal("🔧 Emission Reduction Available").formatted(Formatting.GREEN, Formatting.BOLD), false);
            player.sendMessage(Text.literal("Would you like to help install emission controls?").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("• Install scrubber system (40% emission reduction)").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• Optimize fuel consumption (15% efficiency gain)").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• Crew training on eco-navigation").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("Right-click again to implement measures").formatted(Formatting.GREEN), false);
            
            // Implement emission controls
            implementEmissionControls(player);
        } else if (!emissionControlsActive) {
            player.sendMessage(Text.literal("🔒 Emission Reduction Locked").formatted(Formatting.RED, Formatting.BOLD), false);
            player.sendMessage(Text.literal("Requires 40+ regional points to access").formatted(Formatting.RED), false);
            player.sendMessage(Text.literal("Current score: " + progress.getRegionScore(progress.getCurrentRegion())).formatted(Formatting.YELLOW), false);
        } else {
            player.sendMessage(Text.literal("✅ Emission Controls Active").formatted(Formatting.GREEN, Formatting.BOLD), false);
            player.sendMessage(Text.literal("This vessel is now environmentally compliant").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("Estimated reduction: " + String.format("%.1f kg/hour", getCurrentEmissionReduction())).formatted(Formatting.AQUA), false);
        }
    }
    
    private void implementEmissionControls(ServerPlayerEntity player) {
        if (!emissionControlsActive) {
            emissionControlsActive = true;
            double reductionAmount = currentEmissions * 0.4; // 40% reduction
            currentEmissions *= 0.6; // Reduce to 60% of original
            
            player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("🛠️ Installing Emission Controls...").formatted(Formatting.YELLOW, Formatting.BOLD), false);
            player.sendMessage(Text.literal("Installing scrubber system...").formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("Optimizing engine parameters...").formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("Training crew on eco-procedures...").formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("✅ Installation Complete!").formatted(Formatting.GREEN, Formatting.BOLD), false);
            player.sendMessage(Text.literal("Emission reduction: " + String.format("%.1f kg/hour", reductionAmount)).formatted(Formatting.GREEN), false);
            
            // Award significant points for ship emission control
            var progress = CleanAirHeroes.getLevelManager().getPlayerProgress(player);
            progress.addScore(progress.getCurrentRegion(), 25);
            progress.addPollutionReduced(reductionAmount * 24); // Daily reduction
            progress.addEquipmentInstalled(1);
            
            player.sendMessage(Text.literal("+25 points for maritime emission control").formatted(Formatting.GOLD), false);
        }
    }
    
    private String getShipName() {
        String[] names = {"MV Ocean Pioneer", "MS Baltic Carrier", "MV Sea Harmony", "MS Green Wave", "MV Port Unity"};
        return names[Math.abs(this.getId()) % names.length];
    }
    
    private String getShipTypeDisplay() {
        switch (shipType) {
            case "container": return "Container Ship";
            case "bulk_carrier": return "Bulk Carrier";
            case "tanker": return "Oil Tanker";
            case "general_cargo": return "General Cargo";
            default: return "Cargo Vessel";
        }
    }
    
    private String getCargoCapacity() {
        switch (shipType) {
            case "container": return "18,000 TEU";
            case "bulk_carrier": return "85,000 DWT";
            case "tanker": return "120,000 DWT";
            case "general_cargo": return "25,000 DWT";
            default: return "Unknown";
        }
    }
    
    private String getOriginPort() {
        String[] ports = {"Hamburg", "Rotterdam", "Piraeus", "Istanbul", "Constanta"};
        return ports[Math.abs(this.getId()) % ports.length];
    }
    
    private String getDestinationPort() {
        String[] ports = {"Odesa", "Varna", "Burgas", "Batumi", "Novorossiysk"};
        return ports[Math.abs(this.getId()) % ports.length];
    }
    
    private Formatting getEmissionColor() {
        if (currentEmissions <= 50) return Formatting.GREEN;
        if (currentEmissions <= 70) return Formatting.YELLOW;
        return Formatting.RED;
    }
    
    private double getCurrentEmissionReduction() {
        return emissionControlsActive ? currentEmissions / 0.6 - currentEmissions : 0;
    }
    
    @Override
    public boolean cannotDespawn() {
        return true;
    }
    
    @Override
    public Text getName() {
        return Text.literal(getShipName());
    }
    
    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }
}