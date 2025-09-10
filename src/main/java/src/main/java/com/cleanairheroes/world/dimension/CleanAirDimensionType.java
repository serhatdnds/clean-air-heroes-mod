package com.cleanairheroes.world.dimension;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import com.cleanairheroes.CleanAirHeroes;

public class CleanAirDimensionType {
    
    public static final RegistryKey<DimensionType> CLEAN_AIR_WORLD_TYPE = RegistryKey.of(
        RegistryKeys.DIMENSION_TYPE, 
        new Identifier(CleanAirHeroes.MOD_ID, "clean_air_world")
    );
    
    public static void register() {
        CleanAirHeroes.LOGGER.info("Registering Clean Air Heroes dimension types");
    }
}