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

public class UniversityProfessorEntity extends VillagerEntity {
    
    private boolean hasIntroduced = false;
    private String currentRegion = "odesa";
    
    public UniversityProfessorEntity(EntityType<? extends VillagerEntity> entityType, World world) {
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
            shareResearchKnowledge(player);
        }
    }
    
    private void introduceToPlayer(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🎓 University Professor").formatted(Formatting.DARK_PURPLE, Formatting.BOLD), false);
        
        switch (currentRegion) {
            case "odesa":
                player.sendMessage(Text.literal("Welcome! I'm Professor Oksana Kovalenko").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("from Odesa National University's").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("Marine Environmental Research Institute.").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("My team studies the impact of port").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("emissions on Black Sea marine ecosystems.").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("We've published 47 papers on regional").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("pollution patterns and mitigation strategies.").formatted(Formatting.YELLOW), false);
                break;
                
            case "varna":
                player.sendMessage(Text.literal("Greetings! Professor Dimitar Petrov from").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("the Bulgarian Academy of Sciences,").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("Environmental Chemistry Division.").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("We're conducting long-term air quality").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("studies along the Bulgarian coast.").formatted(Formatting.YELLOW), false);
                break;
                
            case "trabzon":
                player.sendMessage(Text.literal("Hello! I'm Professor Elif Özdemir from").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("Karadeniz Technical University's").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("Environmental Engineering Faculty.").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("My research focuses on urban air quality").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("in mountainous coastal environments.").formatted(Formatting.YELLOW), false);
                break;
                
            default:
                player.sendMessage(Text.literal("I lead environmental research initiatives").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("at the regional university.").formatted(Formatting.AQUA), false);
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("I'd love to share our research findings!").formatted(Formatting.GREEN), false);
    }
    
    private void shareResearchKnowledge(ServerPlayerEntity player) {
        var playerProgress = CleanAirHeroes.getLevelManager().getPlayerProgress(player);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("📚 Latest Research Findings").formatted(Formatting.GOLD, Formatting.BOLD), false);
        
        switch (currentRegion) {
            case "odesa":
                player.sendMessage(Text.literal("🌊 Black Sea Port Impact Study:").formatted(Formatting.BLUE, Formatting.BOLD), false);
                player.sendMessage(Text.literal("• Ship emissions account for 34% of coastal NO2").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("• Marine biodiversity decreased 18% since 2010").formatted(Formatting.RED), false);
                player.sendMessage(Text.literal("• Phytoplankton blooms increased 45%").formatted(Formatting.RED), false);
                player.sendMessage(Text.literal("• Port wind patterns affect 12km inland").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
                
                player.sendMessage(Text.literal("🔬 Innovative Solutions Tested:").formatted(Formatting.GREEN, Formatting.BOLD), false);
                player.sendMessage(Text.literal("• Bio-remediation: 67% pollution reduction").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("• Smart port scheduling: 28% emission cut").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("• Green shipping corridors: 15% improvement").formatted(Formatting.YELLOW), false);
                break;
                
            case "varna":
                player.sendMessage(Text.literal("🏖️ Coastal Air Quality Analysis:").formatted(Formatting.BLUE, Formatting.BOLD), false);
                player.sendMessage(Text.literal("• Tourist season increases pollution 40%").formatted(Formatting.RED), false);
                player.sendMessage(Text.literal("• Sea breeze carries pollutants 8km inland").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("• Archaeological sites show damage").formatted(Formatting.RED), false);
                player.sendMessage(Text.literal("• Winter months: 60% pollution reduction").formatted(Formatting.GREEN), false);
                break;
                
            case "trabzon":
                player.sendMessage(Text.literal("🏔️ Mountain Urban Pollution Study:").formatted(Formatting.BLUE, Formatting.BOLD), false);
                player.sendMessage(Text.literal("• Topography traps 78% more pollutants").formatted(Formatting.RED), false);
                player.sendMessage(Text.literal("• Street canyons create 3x concentration").formatted(Formatting.RED), false);
                player.sendMessage(Text.literal("• Altitude affects dispersion patterns").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("• Rain events clear 85% of particles").formatted(Formatting.GREEN), false);
                break;
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("📖 Educational Resources:").formatted(Formatting.YELLOW, Formatting.BOLD), false);
        
        // Check player's progress for advanced resources
        int playerScore = getCurrentRegionScore(player);
        
        if (playerScore >= 50) {
            player.sendMessage(Text.literal("✓ Advanced Research Access").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("• Detailed pollution modeling data").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• Peer-reviewed publication database").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• International collaboration networks").formatted(Formatting.WHITE), false);
        } else {
            player.sendMessage(Text.literal("🔒 Advanced Research (50+ points needed)").formatted(Formatting.RED), false);
        }
        
        if (playerScore >= 30) {
            player.sendMessage(Text.literal("✓ Student Resources").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("• Environmental monitoring techniques").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• Data analysis methodologies").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• Field research protocols").formatted(Formatting.WHITE), false);
        } else {
            player.sendMessage(Text.literal("🔒 Student Resources (30+ points needed)").formatted(Formatting.RED), false);
        }
        
        // Always available basic knowledge
        player.sendMessage(Text.literal("✓ Public Education Materials").formatted(Formatting.GREEN), false);
        player.sendMessage(Text.literal("• Air quality awareness guides").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("• Community action handbooks").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("• School environmental programs").formatted(Formatting.WHITE), false);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Provide academic insight
        player.sendMessage(Text.literal("🧠 Academic Perspective:").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), false);
        String insight = getAcademicInsight();
        player.sendMessage(Text.literal(insight).formatted(Formatting.WHITE), false);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Special research opportunity
        if (playerScore >= 70) {
            player.sendMessage(Text.literal("🎯 Research Collaboration Offer:").formatted(Formatting.GOLD, Formatting.BOLD), false);
            player.sendMessage(Text.literal("Join our field research team for advanced").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("data collection and analysis work!").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("Reward: Co-author research publication").formatted(Formatting.GOLD), false);
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Use '/cleanair research' for academic missions!").formatted(Formatting.GREEN), false);
    }
    
    private String getAcademicInsight() {
        switch (currentRegion) {
            case "odesa": return "Marine ecosystem health reflects overall port sustainability.";
            case "varna": return "Historical pollution data reveals seasonal intervention windows.";
            case "trabzon": return "Topographical modeling predicts optimal monitoring locations.";
            default: return "Interdisciplinary approaches yield the most effective solutions.";
        }
    }
    
    private int getCurrentRegionScore(ServerPlayerEntity player) {
        // This would integrate with the actual level/achievement system
        return 72; // Sample: player has excellent academic progress
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
            case "odesa": return Text.literal("Prof. Oksana Kovalenko");
            case "varna": return Text.literal("Prof. Dimitar Petrov");
            case "trabzon": return Text.literal("Prof. Elif Özdemir");
            default: return Text.literal("University Professor");
        }
    }
}