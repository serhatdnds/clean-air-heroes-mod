package com.cleanairheroes.world;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;

import com.cleanairheroes.CleanAirHeroes;

public class ModDimensions {
    
    // Dimension Registry Keys
    public static final RegistryKey<World> VARNA_BULGARIA = RegistryKey.of(RegistryKeys.WORLD,
        new Identifier(CleanAirHeroes.MOD_ID, "varna_bulgaria"));
    
    public static final RegistryKey<World> ZONGULDAK_TURKEY = RegistryKey.of(RegistryKeys.WORLD,
        new Identifier(CleanAirHeroes.MOD_ID, "zonguldak_turkey"));
    
    public static final RegistryKey<World> ODESA_UKRAINE = RegistryKey.of(RegistryKeys.WORLD,
        new Identifier(CleanAirHeroes.MOD_ID, "odesa_ukraine"));
    
    public static final RegistryKey<World> TRABZON_TURKEY = RegistryKey.of(RegistryKeys.WORLD,
        new Identifier(CleanAirHeroes.MOD_ID, "trabzon_turkey"));
    
    public static final RegistryKey<World> SOUTHEAST_ROMANIA = RegistryKey.of(RegistryKeys.WORLD,
        new Identifier(CleanAirHeroes.MOD_ID, "southeast_romania"));
    
    public static final RegistryKey<World> ENVIRONMENTAL_PROTECTION_CENTER = RegistryKey.of(RegistryKeys.WORLD,
        new Identifier(CleanAirHeroes.MOD_ID, "protection_center"));
    
    // Dimension Type Registry Keys
    public static final RegistryKey<DimensionType> VARNA_DIMENSION_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
        new Identifier(CleanAirHeroes.MOD_ID, "varna_type"));
    
    public static final RegistryKey<DimensionType> ZONGULDAK_DIMENSION_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
        new Identifier(CleanAirHeroes.MOD_ID, "zonguldak_type"));
    
    public static final RegistryKey<DimensionType> ODESA_DIMENSION_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
        new Identifier(CleanAirHeroes.MOD_ID, "odesa_type"));
    
    public static final RegistryKey<DimensionType> TRABZON_DIMENSION_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
        new Identifier(CleanAirHeroes.MOD_ID, "trabzon_type"));
    
    public static final RegistryKey<DimensionType> ROMANIA_DIMENSION_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
        new Identifier(CleanAirHeroes.MOD_ID, "romania_type"));
    
    public static final RegistryKey<DimensionType> CENTER_DIMENSION_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
        new Identifier(CleanAirHeroes.MOD_ID, "center_type"));
    
    public static void register() {
        CleanAirHeroes.LOGGER.info("Registering dimensions for " + CleanAirHeroes.MOD_ID);
    }
    
    // Utility methods for dimension management
    public static RegistryKey<World> getDimensionForRegion(String regionId) {
        switch (regionId) {
            case "varna_bulgaria":
                return VARNA_BULGARIA;
            case "zonguldak_turkey":
                return ZONGULDAK_TURKEY;
            case "odesa_ukraine":
                return ODESA_UKRAINE;
            case "trabzon_turkey":
                return TRABZON_TURKEY;
            case "southeast_romania":
                return SOUTHEAST_ROMANIA;
            case "protection_center":
                return ENVIRONMENTAL_PROTECTION_CENTER;
            default:
                return World.OVERWORLD;
        }
    }
    
    public static String getRegionDisplayName(RegistryKey<World> dimension) {
        if (dimension == VARNA_BULGARIA) {
            return "Varna, Bulgaria";
        } else if (dimension == ZONGULDAK_TURKEY) {
            return "Zonguldak, Turkey";
        } else if (dimension == ODESA_UKRAINE) {
            return "Odesa, Ukraine";
        } else if (dimension == TRABZON_TURKEY) {
            return "Trabzon, Turkey";
        } else if (dimension == SOUTHEAST_ROMANIA) {
            return "Southeast Romania";
        } else if (dimension == ENVIRONMENTAL_PROTECTION_CENTER) {
            return "Environmental Protection Center";
        }
        return "Unknown Region";
    }
}