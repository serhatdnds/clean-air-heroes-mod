package com.cleanairheroes.mixin;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cleanairheroes.pollution.PollutionManager;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    
    @Inject(method = "tick", at = @At("TAIL"))
    private void onWorldTick(CallbackInfo info) {
        ServerWorld world = (ServerWorld) (Object) this;
        
        // Update pollution simulation every 100 ticks (5 seconds)
        if (world.getTime() % 100 == 0) {
            PollutionManager pollutionManager = PollutionManager.getInstance();
            if (pollutionManager != null) {
                pollutionManager.updatePollution(world);
            }
        }
    }
}