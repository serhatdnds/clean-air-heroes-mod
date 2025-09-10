package com.cleanairheroes.entity.npc;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Formatting;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;

import com.cleanairheroes.data.PlayerData;
import com.cleanairheroes.data.PlayerProgress;

public class ShipCaptainEntity extends VillagerEntity {
    
    public ShipCaptainEntity(EntityType<? extends VillagerEntity> entityType, World world) {
        super(entityType, world);
        this.setVillagerData(this.getVillagerData().withProfession(VillagerProfession.FISHERMAN));
    }
    
    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.getWorld().isClient) {
            return ActionResult.SUCCESS;
        }
        
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        PlayerProgress progress = PlayerData.getPlayerProgress(serverPlayer);
        
        // Different dialogues based on player progress
        if (progress.getUnlockedRegions().contains("varna")) {
            showVarnaShipCaptainDialogue(serverPlayer, progress);
        } else {
            showInitialDialogue(serverPlayer);
        }
        
        return ActionResult.SUCCESS;
    }
    
    private void showInitialDialogue(ServerPlayerEntity player) {
        player.sendMessage(
            Text.literal("Ship Captain: ").formatted(Formatting.BLUE)
                .append(Text.literal("Welcome to Varna Port! I'm Captain Dimitri.").formatted(Formatting.WHITE)), 
            false
        );
        
        player.sendMessage(
            Text.literal("We've been struggling with ship emissions here. ").formatted(Formatting.GRAY)
                .append(Text.literal("Can you help us implement cleaner shipping practices?").formatted(Formatting.YELLOW)), 
            false
        );
        
        player.sendMessage(
            Text.literal("I can teach you about: ").formatted(Formatting.GREEN)
                .append(Text.literal("⚓ Ship emission controls").formatted(Formatting.AQUA))
                .append(Text.literal(" • ⚡ Shore power systems").formatted(Formatting.AQUA))
                .append(Text.literal(" • 🛥️ Green shipping logistics").formatted(Formatting.AQUA)), 
            false
        );
    }
    
    private void showVarnaShipCaptainDialogue(ServerPlayerEntity player, PlayerProgress progress) {
        int shipMissions = progress.getCompletedMissionsInRegion("varna");
        
        if (shipMissions >= 3) {
            player.sendMessage(
                Text.literal("Captain Dimitri: ").formatted(Formatting.BLUE)
                    .append(Text.literal("Excellent work, Captain! ").formatted(Formatting.GREEN))
                    .append(Text.literal("Our emissions have dropped by 40%!").formatted(Formatting.GOLD)), 
                false
            );
            
            player.sendMessage(
                Text.literal("The port authority is impressed. ").formatted(Formatting.GRAY)
                    .append(Text.literal("You're ready for the next challenge in Zonguldak!").formatted(Formatting.YELLOW)), 
                false
            );
        } else {
            player.sendMessage(
                Text.literal("Captain Dimitri: ").formatted(Formatting.BLUE)
                    .append(Text.literal("Progress is good, but we need more work. ").formatted(Formatting.YELLOW))
                    .append(Text.literal("Focus on the ship emission filters!").formatted(Formatting.AQUA)), 
                false
            );
        }
    }
    
    @Override
    public Text getName() {
        return Text.literal("Captain Dimitri");
    }
}