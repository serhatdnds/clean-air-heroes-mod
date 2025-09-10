package com.cleanairheroes.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cleanairheroes.pollution.PollutionManager;

@Mixin(Block.class)
public class BlockMixin {
    
    @Inject(method = "onBlockAdded", at = @At("HEAD"))
    private void onBlockPlaced(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo info) {
        if (!world.isClient) {
            PollutionManager pollutionManager = PollutionManager.getInstance();
            if (pollutionManager != null) {
                pollutionManager.onBlockChanged(world, pos, state);
            }
        }
    }
    
    @Inject(method = "onStateReplaced", at = @At("HEAD"))
    private void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved, CallbackInfo info) {
        if (!world.isClient) {
            PollutionManager pollutionManager = PollutionManager.getInstance();
            if (pollutionManager != null) {
                pollutionManager.onBlockChanged(world, pos, newState);
            }
        }
    }
}