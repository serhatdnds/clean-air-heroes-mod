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

public class EnvironmentalScientistEntity extends VillagerEntity {
    
    public EnvironmentalScientistEntity(EntityType<? extends VillagerEntity> entityType, World world) {
        super(entityType, world);
        this.setVillagerData(this.getVillagerData().withProfession(VillagerProfession.LIBRARIAN));
    }
    
    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.getWorld().isClient) {
            return ActionResult.SUCCESS;
        }
        
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        PlayerProgress progress = PlayerData.getPlayerProgress(serverPlayer);
        
        showScientistDialogue(serverPlayer, progress);
        return ActionResult.SUCCESS;
    }
    
    private void showScientistDialogue(ServerPlayerEntity player, PlayerProgress progress) {
        player.sendMessage(
            Text.literal("Dr. Elena Petrov: ").formatted(Formatting.DARK_PURPLE)
                .append(Text.literal("Greetings! I study air quality patterns across the Black Sea region.").formatted(Formatting.WHITE)), 
            false
        );
        
        if (progress.getTotalScore() > 1000) {
            player.sendMessage(
                Text.literal("Your environmental work is impressive! ").formatted(Formatting.GREEN)
                    .append(Text.literal("The pollution reduction data shows significant improvement.").formatted(Formatting.AQUA)), 
                false
            );
        } else {
            player.sendMessage(
                Text.literal("Air quality science involves: ").formatted(Formatting.GRAY)
                    .append(Text.literal("📊 Data collection • 🔬 Analysis • 📈 Monitoring").formatted(Formatting.YELLOW)), 
                false
            );
        }
    }
    
    @Override
    public Text getName() {
        return Text.literal("Dr. Elena Petrov");
    }
}