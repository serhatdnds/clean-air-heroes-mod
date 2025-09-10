package com.cleanairheroes.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cleanairheroes.data.ClientPlayerData;

@Environment(EnvType.CLIENT)
@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void onClientPlayerTick(CallbackInfo info) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        
        // Update client-side pollution visualization every 40 ticks (2 seconds)
        if (player.age % 40 == 0) {
            updatePollutionVisualization(player);
        }
    }
    
    private void updatePollutionVisualization(ClientPlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        
        // Simulate pollution particle spawning based on ClientPlayerData
        int aqi = ClientPlayerData.getAQI();
        
        if (aqi > 100) {
            // Spawn pollution particles around player for high AQI
            for (int i = 0; i < Math.min(5, aqi / 20); i++) {
                double x = playerPos.getX() + (Math.random() - 0.5) * 10;
                double y = playerPos.getY() + Math.random() * 5;
                double z = playerPos.getZ() + (Math.random() - 0.5) * 10;
                
                if (player.getWorld() != null) {
                    player.getWorld().addParticle(
                        net.minecraft.particle.ParticleTypes.SMOKE,
                        x, y, z,
                        0.0, 0.01, 0.0
                    );
                }
            }
        }
    }
}