package com.cleanairheroes.world.region;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.registry.entry.RegistryEntry;
import java.util.Random;

public class VarnaRegion extends BaseRegion {
    
    private static final int SEA_LEVEL = 62;
    private static final int PLATEAU_HEIGHT = 95; 
    private static final int PORT_ZONE_START_X = -200;
    private static final int PORT_ZONE_END_X = 200;
    private static final int CITY_CENTER_X = 0;
    private static final int CITY_CENTER_Z = 100;
    
    public VarnaRegion() {
        super("varna_bulgaria", "Varna, Bulgaria");
    }
    
    @Override
    public void generateTerrain(ChunkGenerator generator, int chunkX, int chunkZ) {
        Random random = new Random((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);
        
        int worldX = chunkX * 16;
        int worldZ = chunkZ * 16;
        
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = worldX + x;
                int realZ = worldZ + z;
                
                generateVarnaTerrain(realX, realZ, random);
            }
        }
    }
    
    private void generateVarnaTerrain(int x, int z, Random random) {
        if (z < -50) {
            generateBlackSeaCoast(x, z, random);
        } else if (z < 200) {
            generatePortAndCity(x, z, random);
        } else if (z < 400) {
            generateDobrudzhanskoPlateau(x, z, random);
        } else {
            generateIndustrialZone(x, z, random);
        }
    }
    
    private void generateBlackSeaCoast(int x, int z, Random random) {
        if (z < -100) {
            setBlockHeight(x, z, SEA_LEVEL - 10);
            setWaterColumn(x, z, SEA_LEVEL - 10, SEA_LEVEL);
        } else {
            double noise = random.nextDouble() * 3;
            int beachHeight = (int)(SEA_LEVEL + 2 + noise);
            setBlockHeight(x, z, beachHeight);
            setSandLayer(x, z, beachHeight - 3, beachHeight);
        }
    }
    
    private void generatePortAndCity(int x, int z, Random random) {
        if (x >= PORT_ZONE_START_X && x <= PORT_ZONE_END_X && z < 50) {
            generatePort(x, z, random);
        } else {
            double distanceFromCenter = Math.sqrt(
                Math.pow(x - CITY_CENTER_X, 2) + 
                Math.pow(z - CITY_CENTER_Z, 2)
            );
            
            if (distanceFromCenter < 150) {
                generateUrbanArea(x, z, random);
            } else {
                generateSuburbanArea(x, z, random);
            }
        }
    }
    
    private void generatePort(int x, int z, Random random) {
        int baseHeight = SEA_LEVEL + 3;
        setBlockHeight(x, z, baseHeight);
        setConcreteLayer(x, z, baseHeight);
        
        if (x % 50 == 0 && z % 30 == 0) {
            generateCrane(x, z, baseHeight);
        }
        
        if (x % 100 == 0 && z % 100 == 0) {
            generateOilTank(x, z, baseHeight);
        }
        
        if (Math.abs(x) < 50 && z % 40 == 0) {
            generateContainerStack(x, z, baseHeight, random);
        }
    }
    
    private void generateUrbanArea(int x, int z, Random random) {
        int baseHeight = SEA_LEVEL + 5 + random.nextInt(3);
        setBlockHeight(x, z, baseHeight);
        
        if ((x % 20 == 0 || x % 20 == 10) && (z % 20 == 0 || z % 20 == 10)) {
            if (random.nextDouble() < 0.7) {
                int buildingHeight = 10 + random.nextInt(25);
                generateBuilding(x, z, baseHeight, buildingHeight, random);
            }
        }
        
        if ((x % 20 == 5 || x % 20 == 15) || (z % 20 == 5 || z % 20 == 15)) {
            generateRoad(x, z, baseHeight);
        }
    }
    
    private void generateSuburbanArea(int x, int z, Random random) {
        int baseHeight = SEA_LEVEL + 4 + random.nextInt(5);
        setBlockHeight(x, z, baseHeight);
        
        if (x % 30 == 0 && z % 30 == 0 && random.nextDouble() < 0.4) {
            generateHouse(x, z, baseHeight, random);
        }
        
        if (random.nextDouble() < 0.15) {
            generateTree(x, z, baseHeight);
        }
    }
    
    private void generateDobrudzhanskoPlateau(int x, int z, Random random) {
        double plateauNoise = getPerlinNoise(x * 0.01, z * 0.01) * 10;
        int plateauHeight = (int)(PLATEAU_HEIGHT + plateauNoise);
        setBlockHeight(x, z, plateauHeight);
        
        if (random.nextDouble() < 0.05) {
            generateWindTurbine(x, z, plateauHeight);
        }
        
        if (random.nextDouble() < 0.1) {
            generateTree(x, z, plateauHeight);
        }
    }
    
    private void generateIndustrialZone(int x, int z, Random random) {
        int baseHeight = SEA_LEVEL + 8 + random.nextInt(3);
        setBlockHeight(x, z, baseHeight);
        
        if (x % 60 == 0 && z % 60 == 0) {
            if (random.nextDouble() < 0.6) {
                generateFactory(x, z, baseHeight, random);
            }
        }
        
        if (x % 80 == 0 && z % 80 == 0) {
            generateSmokestack(x, z, baseHeight);
        }
    }
    
    @Override
    public void generateStructures(ChunkGenerator generator, int chunkX, int chunkZ) {
        Random random = new Random((long)chunkX * 987654321L + (long)chunkZ * 123456789L);
        
        if (chunkX == 0 && chunkZ == 6) {
            generateDormitionCathedral(chunkX * 16, chunkZ * 16);
        }
        
        if (chunkX == -2 && chunkZ == 4) {
            generateSeaGarden(chunkX * 16, chunkZ * 16);
        }
        
        if (chunkX == 3 && chunkZ == 5) {
            generateRomanBaths(chunkX * 16, chunkZ * 16);
        }
        
        if (chunkX == -1 && chunkZ == 2) {
            generateNavalMuseum(chunkX * 16, chunkZ * 16);
        }
    }
    
    private void generateDormitionCathedral(int x, int z) {
        int baseHeight = SEA_LEVEL + 10;
        for (int dx = 0; dx < 30; dx++) {
            for (int dz = 0; dz < 40; dz++) {
                setBlockHeight(x + dx, z + dz, baseHeight);
                if (dx < 3 || dx > 26 || dz < 3 || dz > 36) {
                    for (int y = 0; y < 25; y++) {
                        setBlock(x + dx, baseHeight + y, z + dz, "stone_bricks");
                    }
                }
            }
        }
        
        for (int i = 0; i < 3; i++) {
            int domeX = x + 10 + i * 7;
            int domeZ = z + 20;
            generateDome(domeX, domeZ, baseHeight + 25, 5, "gold_block");
        }
    }
    
    private void generateSeaGarden(int x, int z) {
        int baseHeight = SEA_LEVEL + 3;
        for (int dx = 0; dx < 200; dx++) {
            for (int dz = 0; dz < 100; dz++) {
                setBlockHeight(x + dx, z + dz, baseHeight);
                setBlock(x + dx, baseHeight, z + dz, "grass_block");
                
                Random random = new Random(dx * 1000L + dz);
                if (random.nextDouble() < 0.02) {
                    generateTree(x + dx, z + dz, baseHeight + 1);
                }
                
                if ((dx % 20 == 0 || dz % 20 == 0) && random.nextDouble() < 0.5) {
                    setBlock(x + dx, baseHeight, z + dz, "gravel");
                }
            }
        }
    }
    
    @Override
    public PollutionSource[] getPollutionSources() {
        return new PollutionSource[] {
            new PollutionSource(0, SEA_LEVEL + 3, 0, PollutionType.SHIP_EMISSIONS, 150),
            new PollutionSource(-100, SEA_LEVEL + 3, 0, PollutionType.SHIP_EMISSIONS, 120),
            new PollutionSource(100, SEA_LEVEL + 3, 0, PollutionType.SHIP_EMISSIONS, 120),
            new PollutionSource(0, SEA_LEVEL + 8, 450, PollutionType.INDUSTRIAL, 200),
            new PollutionSource(-150, SEA_LEVEL + 8, 450, PollutionType.INDUSTRIAL, 180),
            new PollutionSource(150, SEA_LEVEL + 8, 450, PollutionType.INDUSTRIAL, 180),
            new PollutionSource(0, SEA_LEVEL + 5, 100, PollutionType.TRAFFIC, 100),
            new PollutionSource(50, SEA_LEVEL + 5, 150, PollutionType.HEATING, 80)
        };
    }
}