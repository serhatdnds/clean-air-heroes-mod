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

public class ElectricBusEntity extends MobEntity {
    
    private int batteryLevel = 85; // Percentage
    private int passengerCount = 0;
    private boolean isInService = true;
    private String busRoute = "City Center Loop";
    private double co2Savings = 0.0; // kg CO2 saved vs diesel bus
    
    public ElectricBusEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
        // Randomize initial state
        this.batteryLevel = 60 + (int)(Math.random() * 40); // 60-100%
        this.passengerCount = (int)(Math.random() * 45); // 0-45 passengers
        
        String[] routes = {"City Center Loop", "University Route", "Shopping District", "Port Connection", "Residential Circuit"};
        this.busRoute = routes[(int)(Math.random() * routes.length)];
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new LookAroundGoal(this));
        this.goalSelector.add(2, new WanderAroundGoal(this, 0.4)); // Moderate speed like urban transit
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
        showBusStatus(player);
        offerTransitImprovement(player);
    }
    
    private void showBusStatus(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🚌 Electric Bus System").formatted(Formatting.GREEN, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Route: " + busRoute).formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("Vehicle ID: " + getBusNumber()).formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Battery and operational status
        player.sendMessage(Text.literal("🔋 Operational Status:").formatted(Formatting.YELLOW, Formatting.BOLD), false);
        
        Formatting batteryColor = batteryLevel >= 70 ? Formatting.GREEN : batteryLevel >= 30 ? Formatting.YELLOW : Formatting.RED;
        player.sendMessage(Text.literal("Battery level: " + batteryLevel + "%").formatted(batteryColor), false);
        
        String serviceStatus = isInService ? "IN SERVICE" : "OUT OF SERVICE";
        Formatting serviceColor = isInService ? Formatting.GREEN : Formatting.RED;
        player.sendMessage(Text.literal("Service status: " + serviceStatus).formatted(serviceColor, Formatting.BOLD), false);
        
        player.sendMessage(Text.literal("Current passengers: " + passengerCount + "/50").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Range remaining: " + (batteryLevel * 3) + " km").formatted(Formatting.AQUA), false);
        
        // Environmental impact
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🌱 Environmental Impact:").formatted(Formatting.GREEN, Formatting.BOLD), false);
        double dailySavings = calculateDailyCO2Savings();
        player.sendMessage(Text.literal("Daily CO2 savings: " + String.format("%.1f kg", dailySavings)).formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("Lifetime CO2 avoided: " + String.format("%.1f kg", co2Savings)).formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("Equivalent trees planted: " + (int)(co2Savings / 22)).formatted(Formatting.GREEN), false);
        
        // Route efficiency
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("📊 Route Efficiency:").formatted(Formatting.BLUE, Formatting.BOLD), false);
        double efficiency = calculateRouteEfficiency();
        player.sendMessage(Text.literal("Energy efficiency: " + String.format("%.1f kWh/km", efficiency)).formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("Average speed: " + (15 + (int)(Math.random() * 10)) + " km/h").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Daily ridership: " + (250 + (int)(Math.random() * 200)) + " passengers").formatted(Formatting.WHITE), false);
    }
    
    private void offerTransitImprovement(ServerPlayerEntity player) {
        var progress = CleanAirHeroes.getLevelManager().getPlayerProgress(player);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        if (batteryLevel < 30) {
            player.sendMessage(Text.literal("⚠️ Low Battery Alert").formatted(Formatting.RED, Formatting.BOLD), false);
            player.sendMessage(Text.literal("This bus needs charging soon").formatted(Formatting.YELLOW), false);
            
            if (progress.getRegionScore(progress.getCurrentRegion()) >= 30) {
                player.sendMessage(Text.literal("🔌 Install fast charging station?").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("Right-click again to install charging infrastructure").formatted(Formatting.GREEN), false);
                installChargingInfrastructure(player);
            }
        } else if (progress.getRegionScore(progress.getCurrentRegion()) >= 50) {
            player.sendMessage(Text.literal("🚀 Transit Optimization Available").formatted(Formatting.GREEN, Formatting.BOLD), false);
            player.sendMessage(Text.literal("Upgrade options:").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("• Install smart route optimization").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• Add real-time passenger info").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• Integrate renewable energy charging").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("Right-click again to implement upgrades").formatted(Formatting.GREEN), false);
            
            implementTransitUpgrades(player);
        } else {
            player.sendMessage(Text.literal("📈 Transit System Status").formatted(Formatting.BLUE, Formatting.BOLD), false);
            player.sendMessage(Text.literal("Electric buses reduce urban pollution by 85%").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("compared to diesel equivalents").formatted(Formatting.GREEN), false);
            
            if (progress.getRegionScore(progress.getCurrentRegion()) < 30) {
                player.sendMessage(Text.literal("🔒 More actions available at 30+ points").formatted(Formatting.GRAY), false);
            }
        }
    }
    
    private void installChargingInfrastructure(ServerPlayerEntity player) {
        if (batteryLevel < 30) {
            batteryLevel = 95; // Fully charged
            
            player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("🔧 Installing Fast Charging Station...").formatted(Formatting.YELLOW, Formatting.BOLD), false);
            player.sendMessage(Text.literal("Setting up high-power charging equipment...").formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("Connecting to electrical grid...").formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("Testing charging protocols...").formatted(Formatting.GRAY), false);
            player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("⚡ Charging Station Online!").formatted(Formatting.GREEN, Formatting.BOLD), false);
            player.sendMessage(Text.literal("Bus battery recharged to 95%").formatted(Formatting.GREEN), false);
            
            // Award points for infrastructure
            var progress = CleanAirHeroes.getLevelManager().getPlayerProgress(player);
            progress.addScore(progress.getCurrentRegion(), 15);
            progress.addEquipmentInstalled(1);
            progress.addPollutionReduced(50.0); // Long-term benefit
            
            player.sendMessage(Text.literal("+15 points for charging infrastructure").formatted(Formatting.GOLD), false);
        }
    }
    
    private void implementTransitUpgrades(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🚀 Implementing Smart Transit Upgrades...").formatted(Formatting.YELLOW, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Installing route optimization AI...").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("Setting up passenger information displays...").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("Integrating solar charging stations...").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("Configuring mobile app integration...").formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("✅ Smart Transit System Activated!").formatted(Formatting.GREEN, Formatting.BOLD), false);
        
        // Improve efficiency
        double improvementAmount = 45.0;
        co2Savings += improvementAmount;
        
        player.sendMessage(Text.literal("System improvements:").formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("• 25% increase in ridership efficiency").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("• 15% reduction in energy consumption").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("• Real-time passenger optimization").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("• Solar-powered charging network").formatted(Formatting.GREEN), false);
        
        // Award significant points for system upgrade
        var progress = CleanAirHeroes.getLevelManager().getPlayerProgress(player);
        progress.addScore(progress.getCurrentRegion(), 30);
        progress.addPollutionReduced(improvementAmount * 7); // Weekly impact
        progress.addEquipmentInstalled(4); // Multiple systems
        
        player.sendMessage(Text.literal("+30 points for smart transit systems").formatted(Formatting.GOLD), false);
    }
    
    private String getBusNumber() {
        return "EB-" + String.format("%04d", Math.abs(this.getId()) % 10000);
    }
    
    private double calculateDailyCO2Savings() {
        // Average electric bus saves about 1.6 kg CO2 per km compared to diesel
        double avgDailyKm = 150; // Typical urban bus daily distance
        return avgDailyKm * 1.6 * (passengerCount / 30.0); // Adjust for occupancy
    }
    
    private double calculateRouteEfficiency() {
        // Typical electric bus efficiency: 1.2-2.0 kWh/km
        double baseEfficiency = 1.2;
        double loadFactor = 0.3 + (passengerCount / 50.0) * 0.5; // More passengers = higher efficiency per person
        return baseEfficiency / loadFactor;
    }
    
    @Override
    public boolean cannotDespawn() {
        return true;
    }
    
    @Override
    public Text getName() {
        return Text.literal("Electric Bus " + getBusNumber());
    }
    
    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        return false;
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Slowly drain battery during operation
        if (isInService && getWorld().getTime() % 6000 == 0) { // Every 5 minutes
            batteryLevel = Math.max(0, batteryLevel - 1);
            if (batteryLevel <= 0) {
                isInService = false;
            }
        }
        
        // Accumulate CO2 savings
        if (isInService && getWorld().getTime() % 1200 == 0) { // Every minute
            co2Savings += calculateDailyCO2Savings() / 24 / 60; // Per minute savings
        }
    }
}