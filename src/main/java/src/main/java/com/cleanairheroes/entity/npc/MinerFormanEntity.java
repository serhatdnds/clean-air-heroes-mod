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

public class MinerFormanEntity extends VillagerEntity {
    
    private boolean hasIntroduced = false;
    private String currentRegion = "zonguldak";
    
    public MinerFormanEntity(EntityType<? extends VillagerEntity> entityType, World world) {
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
            discussMiningOperations(player);
        }
    }
    
    private void introduceToPlayer(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("⛏️ Mining Foreman").formatted(Formatting.GRAY, Formatting.BOLD), false);
        
        switch (currentRegion) {
            case "zonguldak":
                player.sendMessage(Text.literal("Well hello there! I'm Foreman Hasan Koç").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("from the Çatalağzı Coal Complex.").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("Been working these mines for 25 years,").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("and I've seen the dust problems get worse.").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("The company's trying new dust suppression").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("systems, but we need help implementing them.").formatted(Formatting.YELLOW), false);
                break;
                
            case "romania":
                player.sendMessage(Text.literal("Good day! I'm Foreman Ion Popescu").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("from the Hunedoara mining district.").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("We're dealing with legacy pollution").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("from decades of intensive mining operations.").formatted(Formatting.YELLOW), false);
                break;
                
            default:
                player.sendMessage(Text.literal("I oversee mining operations in this area.").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("We're always looking for ways to reduce").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("our environmental impact.").formatted(Formatting.YELLOW), false);
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Maybe you can help us find solutions!").formatted(Formatting.GREEN), false);
    }
    
    private void discussMiningOperations(ServerPlayerEntity player) {
        var playerProgress = CleanAirHeroes.getLevelManager().getPlayerProgress(player);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("⚡ Current Mining Status").formatted(Formatting.GOLD, Formatting.BOLD), false);
        
        switch (currentRegion) {
            case "zonguldak":
                player.sendMessage(Text.literal("📊 Çatalağzı Operations:").formatted(Formatting.BLUE, Formatting.BOLD), false);
                player.sendMessage(Text.literal("• Daily coal extraction: 2,400 tons").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("• Dust emission: 45 kg/hour (CRITICAL)").formatted(Formatting.RED), false);
                player.sendMessage(Text.literal("• Workers affected: 850+ miners").formatted(Formatting.RED), false);
                player.sendMessage(Text.literal("• Safety equipment: 78% compliance").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
                
                player.sendMessage(Text.literal("🔧 Improvement Projects:").formatted(Formatting.GREEN, Formatting.BOLD), false);
                player.sendMessage(Text.literal("• Water sprinkler system installation").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("• Conveyor belt enclosure project").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("• Air filtration in worker areas").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("• Regular health monitoring program").formatted(Formatting.WHITE), false);
                break;
                
            case "romania":
                player.sendMessage(Text.literal("📊 Hunedoara District:").formatted(Formatting.BLUE, Formatting.BOLD), false);
                player.sendMessage(Text.literal("• Legacy contamination: 15 km radius").formatted(Formatting.RED), false);
                player.sendMessage(Text.literal("• Remediation progress: 23% complete").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("• Affected communities: 8 villages").formatted(Formatting.RED), false);
                player.sendMessage(Text.literal("• Restoration budget: €12M allocated").formatted(Formatting.GREEN), false);
                break;
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("💪 How You Can Help:").formatted(Formatting.YELLOW, Formatting.BOLD), false);
        
        // Check player's progress for available help
        int playerScore = getCurrentRegionScore(player);
        
        if (playerScore >= 30) {
            player.sendMessage(Text.literal("✓ Dust Control Mission Available").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("• Install suppression systems").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• Monitor air quality improvements").formatted(Formatting.WHITE), false);
        } else {
            player.sendMessage(Text.literal("🔒 Build reputation first (30+ points needed)").formatted(Formatting.RED), false);
        }
        
        if (playerScore >= 50) {
            player.sendMessage(Text.literal("✓ Worker Safety Initiative").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("• Provide protective equipment").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• Set up health monitoring stations").formatted(Formatting.WHITE), false);
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Provide practical advice
        player.sendMessage(Text.literal("💡 Practical Tip:").formatted(Formatting.AQUA, Formatting.BOLD), false);
        String tip = getMiningTip();
        player.sendMessage(Text.literal(tip).formatted(Formatting.WHITE), false);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Use '/cleanair mining' for specialized missions!").formatted(Formatting.GREEN), false);
    }
    
    private String getMiningTip() {
        switch (currentRegion) {
            case "zonguldak": return "Water trucks can reduce dust by 60% during transport.";
            case "romania": return "Plant vegetation on slopes to prevent soil erosion.";
            default: return "Regular equipment maintenance reduces emissions significantly.";
        }
    }
    
    private int getCurrentRegionScore(ServerPlayerEntity player) {
        // This would integrate with the actual level/achievement system
        return 65; // Sample: player has good progress
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
            case "zonguldak": return Text.literal("Foreman Hasan Koç");
            case "romania": return Text.literal("Foreman Ion Popescu");
            default: return Text.literal("Mining Foreman");
        }
    }
}