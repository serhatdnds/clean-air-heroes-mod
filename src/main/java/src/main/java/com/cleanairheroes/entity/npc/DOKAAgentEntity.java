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

public class DOKAAgentEntity extends VillagerEntity {
    
    private boolean hasIntroduced = false;
    private String currentRegion = "trabzon";
    
    public DOKAAgentEntity(EntityType<? extends VillagerEntity> entityType, World world) {
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
            provideDOKAServices(player);
        }
    }
    
    private void introduceToPlayer(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🏛️ DOKA Agency Representative").formatted(Formatting.BLUE, Formatting.BOLD), false);
        
        switch (currentRegion) {
            case "trabzon":
                player.sendMessage(Text.literal("Greetings! I'm Mehmet Çelik from the").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("Trabzon DOKA Environmental Office.").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("We coordinate with international partners").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("to combat traffic pollution in our city.").formatted(Formatting.YELLOW), false);
                break;
                
            case "romania":
                player.sendMessage(Text.literal("Hello! I'm Maria Ionescu from").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("the Romanian Environmental Agency.").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("We work closely with DOKA on").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("cross-border pollution initiatives.").formatted(Formatting.YELLOW), false);
                break;
                
            default:
                player.sendMessage(Text.literal("I represent the official environmental").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("monitoring and coordination agency.").formatted(Formatting.AQUA), false);
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("I can provide official permits and resources!").formatted(Formatting.GREEN), false);
    }
    
    private void provideDOKAServices(ServerPlayerEntity player) {
        var playerProgress = CleanAirHeroes.getLevelManager().getPlayerProgress(player);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🔧 DOKA Services Available").formatted(Formatting.GOLD, Formatting.BOLD), false);
        
        // Check player's achievements for service access
        int completedMissions = getCompletedMissions(player);
        
        if (completedMissions >= 5) {
            player.sendMessage(Text.literal("✓ Advanced Equipment Access").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("• High-tech pollution sensors").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• Emission analysis tools").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• Traffic monitoring devices").formatted(Formatting.WHITE), false);
        } else {
            player.sendMessage(Text.literal("🔒 Advanced Equipment (Complete 5+ missions)").formatted(Formatting.RED), false);
        }
        
        if (completedMissions >= 3) {
            player.sendMessage(Text.literal("✓ Official Documentation").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("• Environmental impact permits").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• Pollution reduction certificates").formatted(Formatting.WHITE), false);
        } else {
            player.sendMessage(Text.literal("🔒 Official Documentation (Complete 3+ missions)").formatted(Formatting.RED), false);
        }
        
        // Always available basic services
        player.sendMessage(Text.literal("✓ Basic Services").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("• Air quality reporting").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("• Emergency response coordination").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("• Public awareness materials").formatted(Formatting.WHITE), false);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Provide region-specific guidance
        switch (currentRegion) {
            case "trabzon":
                player.sendMessage(Text.literal("📋 Priority Areas:").formatted(Formatting.YELLOW, Formatting.BOLD), false);
                player.sendMessage(Text.literal("• Atatürk Square traffic management").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("• Port road emission monitoring").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("• Historic center air quality").formatted(Formatting.WHITE), false);
                break;
                
            case "romania":
                player.sendMessage(Text.literal("📋 Multi-City Coordination:").formatted(Formatting.YELLOW, Formatting.BOLD), false);
                player.sendMessage(Text.literal("• Constanta port collaboration").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("• Galati industrial monitoring").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("• Brasov mountain air quality").formatted(Formatting.WHITE), false);
                break;
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Use '/cleanair doka' for service requests!").formatted(Formatting.GREEN), false);
    }
    
    private int getCompletedMissions(ServerPlayerEntity player) {
        // This would integrate with the actual mission system
        return 7; // Sample: player has good progress
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
            case "trabzon": return Text.literal("Agent Mehmet Çelik");
            case "romania": return Text.literal("Agent Maria Ionescu");
            default: return Text.literal("DOKA Representative");
        }
    }
}