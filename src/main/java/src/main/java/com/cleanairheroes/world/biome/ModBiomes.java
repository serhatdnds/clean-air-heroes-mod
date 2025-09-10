package com.cleanairheroes.world.biome;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import com.cleanairheroes.CleanAirHeroes;

public class ModBiomes {
    
    // Biome Registry Keys
    public static final RegistryKey<Biome> VARNA_PORT_BIOME = RegistryKey.of(RegistryKeys.BIOME, 
        new Identifier(CleanAirHeroes.MOD_ID, "varna_port"));
    public static final RegistryKey<Biome> ZONGULDAK_MINING_BIOME = RegistryKey.of(RegistryKeys.BIOME, 
        new Identifier(CleanAirHeroes.MOD_ID, "zonguldak_mining"));
    public static final RegistryKey<Biome> ODESA_INDUSTRIAL_BIOME = RegistryKey.of(RegistryKeys.BIOME, 
        new Identifier(CleanAirHeroes.MOD_ID, "odesa_industrial"));
    public static final RegistryKey<Biome> TRABZON_MOUNTAIN_BIOME = RegistryKey.of(RegistryKeys.BIOME, 
        new Identifier(CleanAirHeroes.MOD_ID, "trabzon_mountain"));
    public static final RegistryKey<Biome> ROMANIA_PLAINS_BIOME = RegistryKey.of(RegistryKeys.BIOME, 
        new Identifier(CleanAirHeroes.MOD_ID, "romania_plains"));
    
    // Biome creation methods
    public static Biome createVarnaPortBiome() {
        SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.VILLAGER, 5, 2, 4));
        spawnBuilder.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.COD, 15, 4, 8));
        spawnBuilder.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.SALMON, 25, 6, 10));
        
        GenerationSettings.LookupBackedBuilder generationBuilder = new GenerationSettings.LookupBackedBuilder(null, null);
        
        return new Biome.Builder()
            .precipitation(true)
            .downfall(0.6f)
            .temperature(0.7f)
            .generationSettings(generationBuilder.build())
            .spawnSettings(spawnBuilder.build())
            .effects(new BiomeEffects.Builder()
                .waterColor(0x3F76E4)      // Blue water for port
                .waterFogColor(0x050533)   // Deep blue fog
                .skyColor(0x78A7FF)        // Light blue sky
                .grassColor(0x79C05A)      // Green grass
                .foliageColor(0x59AE30)    // Green foliage
                .fogColor(0xC0D8FF)        // Light fog
                .moodSound(BiomeMoodSound.CAVE)
                .build())
            .build();
    }
    
    public static Biome createZonguldakMiningBiome() {
        SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.VILLAGER, 3, 1, 3));
        spawnBuilder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ZOMBIE, 95, 4, 4));
        
        GenerationSettings.LookupBackedBuilder generationBuilder = new GenerationSettings.LookupBackedBuilder(null, null);
        
        return new Biome.Builder()
            .precipitation(true)
            .downfall(0.4f)
            .temperature(0.5f)
            .generationSettings(generationBuilder.build())
            .spawnSettings(spawnBuilder.build())
            .effects(new BiomeEffects.Builder()
                .waterColor(0x4C6B73)      // Dark water
                .waterFogColor(0x0C1618)   // Very dark fog
                .skyColor(0x6B6B6B)        // Gray sky (polluted)
                .grassColor(0x5C6B2E)      // Brownish grass
                .foliageColor(0x4A5A1E)    // Dark foliage
                .fogColor(0x9C9C9C)        // Gray fog (mining dust)
                .moodSound(BiomeMoodSound.CAVE)
                .build())
            .build();
    }
    
    public static Biome createOdesaIndustrialBiome() {
        SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.VILLAGER, 8, 3, 6));
        spawnBuilder.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.COD, 10, 2, 4));
        
        GenerationSettings.LookupBackedBuilder generationBuilder = new GenerationSettings.LookupBackedBuilder(null, null);
        
        return new Biome.Builder()
            .precipitation(true)
            .downfall(0.5f)
            .temperature(0.6f)
            .generationSettings(generationBuilder.build())
            .spawnSettings(spawnBuilder.build())
            .effects(new BiomeEffects.Builder()
                .waterColor(0x4A6741)      // Slightly polluted water
                .waterFogColor(0x0C1012)   // Dark water fog
                .skyColor(0x7A8CA8)        // Slightly gray sky
                .grassColor(0x6BA86F)      // Moderate green grass
                .foliageColor(0x5A9C42)    // Green foliage
                .fogColor(0xB8C6D6)        // Light industrial fog
                .moodSound(BiomeMoodSound.CAVE)
                .build())
            .build();
    }
    
    public static Biome createTrabzonMountainBiome() {
        SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.VILLAGER, 4, 2, 4));
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.SHEEP, 12, 4, 6));
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.COW, 8, 2, 4));
        
        GenerationSettings.LookupBackedBuilder generationBuilder = new GenerationSettings.LookupBackedBuilder(null, null);
        
        return new Biome.Builder()
            .precipitation(true)
            .downfall(0.8f)
            .temperature(0.3f)  // Cooler mountain climate
            .generationSettings(generationBuilder.build())
            .spawnSettings(spawnBuilder.build())
            .effects(new BiomeEffects.Builder()
                .waterColor(0x3D57D6)      // Clear mountain water
                .waterFogColor(0x050533)   
                .skyColor(0x87CEEB)        // Clear mountain sky
                .grassColor(0x7CBD6B)      // Mountain grass
                .foliageColor(0x68B55C)    // Rich green foliage
                .fogColor(0xDDE8F0)        // Clear mountain air
                .moodSound(BiomeMoodSound.CAVE)
                .build())
            .build();
    }
    
    public static Biome createRomaniaPlainsBiome() {
        SpawnSettings.Builder spawnBuilder = new SpawnSettings.Builder();
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.VILLAGER, 6, 2, 5));
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.HORSE, 5, 2, 6));
        spawnBuilder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.COW, 10, 4, 8));
        
        GenerationSettings.LookupBackedBuilder generationBuilder = new GenerationSettings.LookupBackedBuilder(null, null);
        
        return new Biome.Builder()
            .precipitation(true)
            .downfall(0.7f)
            .temperature(0.6f)
            .generationSettings(generationBuilder.build())
            .spawnSettings(spawnBuilder.build())
            .effects(new BiomeEffects.Builder()
                .waterColor(0x627DAB)      // Clear plains water
                .waterFogColor(0x050533)   
                .skyColor(0x87CEEB)        // Clear sky
                .grassColor(0x91BD59)      // Rich plains grass
                .foliageColor(0x77AB2F)    // Green foliage
                .fogColor(0xE6F2FF)        // Clear air
                .moodSound(BiomeMoodSound.CAVE)
                .build())
            .build();
    }
}