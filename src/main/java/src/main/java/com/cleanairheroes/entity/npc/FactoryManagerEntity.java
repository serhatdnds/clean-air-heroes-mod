package com.cleanairheroes.entity.npc;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import com.cleanairheroes.CleanAirHeroes;

public class FactoryManagerEntity extends VillagerEntity {
    
    private boolean hasIntroduced = false;
    private String currentRegion = "romania";
    
    public FactoryManagerEntity(EntityType<? extends VillagerEntity> entityType, World world) {
        super(entityType, world);
    }
    
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.25));
        this.goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 0.6));
        this.goalSelector.add(4, new LookAroundGoal(this));
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
        if (!hasIntroduced) {
            introduceToPlayer(player);
            hasIntroduced = true;
        } else {
            discussFactoryOperations(player);
        }
    }
    
    private void introduceToPlayer(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🏭 Factory Manager").formatted(Formatting.DARK_BLUE, Formatting.BOLD), false);
        
        switch (currentRegion) {
            case "romania":
                player.sendMessage(Text.literal("Good afternoon! I'm Manager Andrei Georgescu").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("from the Galați Steel Complex.").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("Our facility is one of the largest").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("steel producers in Southeast Europe.").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("We're investing heavily in emission").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("reduction technology, but need guidance.").formatted(Formatting.YELLOW), false);
                break;
                
            case "zonguldak":
                player.sendMessage(Text.literal("Hello! I'm Manager Fatma Yılmaz").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("from the Catalağzı Thermal Power Plant.").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("We supply power to the entire region,").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("but coal burning creates significant emissions.").formatted(Formatting.YELLOW), false);
                break;
                
            default:
                player.sendMessage(Text.literal("I oversee industrial operations").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("and environmental compliance here.").formatted(Formatting.YELLOW), false);
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Perhaps we can collaborate on solutions!").formatted(Formatting.GREEN), false);
    }
    
    private void discussFactoryOperations(ServerPlayerEntity player) {
        var playerProgress = CleanAirHeroes.getLevelManager().getPlayerProgress(player);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🏗️ Industrial Operations Report").formatted(Formatting.GOLD, Formatting.BOLD), false);
        
        switch (currentRegion) {
            case "romania":
                player.sendMessage(Text.literal("📊 Galați Steel Complex:").formatted(Formatting.BLUE, Formatting.BOLD), false);
                player.sendMessage(Text.literal("• Daily production: 8,500 tons steel").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("• SO2 emissions: 125 kg/hour").formatted(Formatting.RED), false);
                player.sendMessage(Text.literal("• PM10 emissions: 85 kg/hour").formatted(Formatting.RED), false);
                player.sendMessage(Text.literal("• Workforce: 12,000 employees").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("• EU compliance: 67% targets met").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
                
                player.sendMessage(Text.literal("💰 Green Investment Plan:").formatted(Formatting.GREEN, Formatting.BOLD), false);
                player.sendMessage(Text.literal("• €45M allocated for emissions reduction").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("• New filtration system: 40% reduction").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("• Electric arc furnace upgrade planned").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("• Renewable energy transition: 2025").formatted(Formatting.YELLOW), false);
                break;
                
            case "zonguldak":
                player.sendMessage(Text.literal("📊 Catalağzı Power Plant:").formatted(Formatting.BLUE, Formatting.BOLD), false);
                player.sendMessage(Text.literal("• Power output: 1,365 MW capacity").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("• Coal consumption: 4.2M tons/year").formatted(Formatting.RED), false);
                player.sendMessage(Text.literal("• CO2 emissions: 12M tons/year").formatted(Formatting.RED), false);
                player.sendMessage(Text.literal("• Ash production: 420,000 tons/year").formatted(Formatting.RED), false);
                player.sendMessage(Text.literal("• Desulfurization: 95% efficiency").formatted(Formatting.GREEN), false);
                break;
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🎯 Collaboration Opportunities:").formatted(Formatting.YELLOW, Formatting.BOLD), false);
        
        // Check player's progress for available projects
        int playerScore = getCurrentRegionScore(player);
        
        if (playerScore >= 40) {
            player.sendMessage(Text.literal("✓ Emission Monitoring Project").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("• Install real-time monitoring systems").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• Optimize production schedules").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• Reward: Advanced filtration access").formatted(Formatting.GOLD), false);
        } else {
            player.sendMessage(Text.literal("🔒 Emission Monitoring (40+ points needed)").formatted(Formatting.RED), false);
        }
        
        if (playerScore >= 60) {
            player.sendMessage(Text.literal("✓ Technology Upgrade Initiative").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("• Evaluate new emission control tech").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• Coordinate with equipment suppliers").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• Reward: Industry partnership certificate").formatted(Formatting.GOLD), false);
        } else {
            player.sendMessage(Text.literal("🔒 Technology Upgrade (60+ points needed)").formatted(Formatting.RED), false);
        }
        
        // Always available basic cooperation
        player.sendMessage(Text.literal("✓ Basic Consultation").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("• Environmental impact assessments").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("• Best practices sharing").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("• Worker training programs").formatted(Formatting.WHITE), false);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Provide industry insight
        player.sendMessage(Text.literal("💡 Industry Insight:").formatted(Formatting.AQUA, Formatting.BOLD), false);
        String insight = getIndustryInsight();
        player.sendMessage(Text.literal(insight).formatted(Formatting.WHITE), false);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Use '/cleanair industry' for factory missions!").formatted(Formatting.GREEN), false);
    }
    
    private String getIndustryInsight() {
        switch (currentRegion) {
            case "romania": return "Steel production efficiency can reduce emissions by 30%.";
            case "zonguldak": return "Power plant load balancing minimizes coal consumption peaks.";
            default: return "Predictive maintenance reduces both costs and emissions.";
        }
    }
    
    private int getCurrentRegionScore(ServerPlayerEntity player) {
        // This would integrate with the actual level/achievement system
        return 55; // Sample: player has decent progress
    }
    
    public void setCurrentRegion(String region) {
        this.currentRegion = region;
        this.hasIntroduced = false;
    }
    
    @Override
    public boolean cannotDespawn() {
        return true;
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }
    
    @Override
    public Text getName() {
        switch (currentRegion) {
            case "romania": return Text.literal("Manager Andrei Georgescu");
            case "zonguldak": return Text.literal("Manager Fatma Yılmaz");
            default: return Text.literal("Factory Manager");
        }
    }
}