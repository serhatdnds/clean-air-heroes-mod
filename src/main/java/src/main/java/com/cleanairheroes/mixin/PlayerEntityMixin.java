package com.cleanairheroes.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cleanairheroes.pollution.PollutionManager;
import com.cleanairheroes.data.PlayerData;
import com.cleanairheroes.data.PlayerProgress;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void onPlayerTick(CallbackInfo info) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        if (!player.getWorld().isClient && player instanceof ServerPlayerEntity serverPlayer) {
            // Check for pollution exposure every 20 ticks (1 second)
            if (player.age % 20 == 0) {
                checkPollutionExposure(serverPlayer);
            }
            
            // Update player progress every 100 ticks (5 seconds)
            if (player.age % 100 == 0) {
                updatePlayerProgress(serverPlayer);
            }
        }
    }
    
    private void checkPollutionExposure(ServerPlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        PollutionManager pollutionManager = PollutionManager.getInstance();
        
        if (pollutionManager != null) {
            float pollutionLevel = pollutionManager.getPollutionLevel(player.getWorld(), playerPos);
            
            if (pollutionLevel > 75.0f) {
                // High pollution exposure
                PlayerProgress progress = PlayerData.getPlayerProgress(player);
                progress.addPollutionExposure(pollutionLevel);
                
                // Apply minor damage or effects for educational purposes
                if (pollutionLevel > 100.0f && player.age % 200 == 0) {
                    player.sendMessage(net.minecraft.text.Text.literal("⚠️ High pollution exposure! Consider using protective equipment."), true);
                }
            }
        }
    }
    
    private void updatePlayerProgress(ServerPlayerEntity player) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        progress.updatePlayTime();
        
        // Check for nearby environmental equipment and give score
        BlockPos playerPos = player.getBlockPos();
        boolean nearEquipment = false;
        
        for (int x = -3; x <= 3; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos checkPos = playerPos.add(x, y, z);
                    if (isEnvironmentalEquipment(player.getWorld().getBlockState(checkPos).getBlock())) {
                        nearEquipment = true;
                        break;
                    }
                }
                if (nearEquipment) break;
            }
            if (nearEquipment) break;
        }
        
        if (nearEquipment && player.age % 400 == 0) {
            progress.addScore(1); // Small score for being near environmental equipment
        }
    }
    
    private boolean isEnvironmentalEquipment(net.minecraft.block.Block block) {
        return block == com.cleanairheroes.block.ModBlocks.AIR_QUALITY_MONITOR ||
               block == com.cleanairheroes.block.ModBlocks.INDUSTRIAL_FILTER ||
               block == com.cleanairheroes.block.ModBlocks.SOLAR_PANEL_HEATING ||
               block == com.cleanairheroes.block.ModBlocks.EV_CHARGING_STATION;
    }
}