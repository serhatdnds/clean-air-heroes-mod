package com.cleanairheroes.world.region;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import java.util.Random;

public class ZonguldakRegion extends BaseRegion {
    
    private static final int SEA_LEVEL = 62;
    private static final int MOUNTAIN_BASE = 150;
    private static final int MOUNTAIN_PEAK = 380; 
    private static final int MINE_DEPTH = 30;
    
    public ZonguldakRegion() {
        super("zonguldak_turkey", "Zonguldak, Turkey");
    }
    
    @Override
    public void generateTerrain(ChunkGenerator generator, int chunkX, int chunkZ) {
        Random random = new Random((long)chunkX * 748392019L + (long)chunkZ * 918273645L);
        
        int worldX = chunkX * 16;
        int worldZ = chunkZ * 16;
        
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = worldX + x;
                int realZ = worldZ + z;
                
                generateZonguldakTerrain(realX, realZ, random);
            }
        }
    }
    
    private void generateZonguldakTerrain(int x, int z, Random random) {
        if (z < -100) {
            generateBlackSeaCoast(x, z, random);
        } else if (z < 100) {
            generateCityAndPort(x, z, random);
        } else if (z < 600) {
            generatePonticMountains(x, z, random);
        } else {
            generateMiningArea(x, z, random);
        }
    }
    
    private void generateBlackSeaCoast(int x, int z, Random random) {
        if (z < -150) {
            setBlockHeight(x, z, SEA_LEVEL - 15);
            setWaterColumn(x, z, SEA_LEVEL - 15, SEA_LEVEL);
        } else {
            double slope = (z + 150) / 50.0;
            int coastHeight = (int)(SEA_LEVEL + slope * 8 + random.nextDouble() * 2);
            setBlockHeight(x, z, coastHeight);
            
            if (coastHeight <= SEA_LEVEL + 2) {
                setSandLayer(x, z, coastHeight - 2, coastHeight);
            }
        }
    }
    
    private void generateCityAndPort(int x, int z, Random random) {
        double terraceLevel = Math.floor((z + 100) / 50.0);
        int baseHeight = SEA_LEVEL + 5 + (int)(terraceLevel * 15);
        
        double noise = getPerlinNoise(x * 0.02, z * 0.02) * 3;
        baseHeight += (int)noise;
        
        setBlockHeight(x, z, baseHeight);
        
        if (x >= -100 && x <= 100 && z < 0) {
            generatePortArea(x, z, baseHeight, random);
        } else {
            generateUrbanTerrace(x, z, baseHeight, random);
        }
    }
    
    private void generatePortArea(int x, int z, int baseHeight, Random random) {
        setConcreteLayer(x, z, baseHeight);
        
        if (x % 40 == 0 && z % 40 == 0) {
            generateCoalLoader(x, z, baseHeight);
        }
        
        if (x % 60 == 20 && z % 60 == 20) {
            generateCoalPile(x, z, baseHeight, random);
        }
    }
    
    private void generateUrbanTerrace(int x, int z, int baseHeight, Random random) {
        if ((x % 25 == 0 || x % 25 == 12) && (z % 25 == 0 || z % 25 == 12)) {
            if (random.nextDouble() < 0.6) {
                int buildingHeight = 8 + random.nextInt(15);
                generateBuilding(x, z, baseHeight, buildingHeight, random);
            }
        }
        
        if ((x % 25 == 6 || x % 25 == 18) || (z % 25 == 6 || z % 25 == 18)) {
            generateSteepRoad(x, z, baseHeight);
        }
    }
    
    private void generatePonticMountains(int x, int z, Random random) {
        double distance = (z - 100) / 500.0;
        double mountainHeight = MOUNTAIN_BASE + (MOUNTAIN_PEAK - MOUNTAIN_BASE) * distance;
        
        double ridgeNoise = getPerlinNoise(x * 0.005, z * 0.01) * 50;
        double detailNoise = getPerlinNoise(x * 0.02, z * 0.02) * 20;
        
        int finalHeight = (int)(mountainHeight + ridgeNoise + detailNoise);
        setBlockHeight(x, z, finalHeight);
        
        if (finalHeight > 200) {
            setStoneLayer(x, z, finalHeight - 10, finalHeight);
        }
        
        if (finalHeight < 250 && random.nextDouble() < 0.3) {
            generateDenseForest(x, z, finalHeight, random);
        }
    }
    
    private void generateMiningArea(int x, int z, Random random) {
        int baseHeight = MOUNTAIN_BASE + random.nextInt(30);
        
        if (isMineLocation(x, z)) {
            generateMineEntrance(x, z, baseHeight, random);
        } else if (isProcessingArea(x, z)) {
            generateProcessingFacility(x, z, baseHeight, random);
        } else {
            setBlockHeight(x, z, baseHeight);
            
            if (random.nextDouble() < 0.05) {
                generateMineVentShaft(x, z, baseHeight);
            }
        }
    }
    
    private boolean isMineLocation(int x, int z) {
        return (x % 200 >= -10 && x % 200 <= 10) && 
               (z % 200 >= -10 && z % 200 <= 10);
    }
    
    private boolean isProcessingArea(int x, int z) {
        return (x % 150 >= -20 && x % 150 <= 20) && 
               (z % 150 >= -20 && z % 150 <= 20);
    }
    
    private void generateMineEntrance(int x, int z, int baseHeight, Random random) {
        for (int dx = -10; dx <= 10; dx++) {
            for (int dz = -10; dz <= 10; dz++) {
                setBlockHeight(x + dx, z + dz, baseHeight);
                setBlock(x + dx, baseHeight, z + dz, "coal_ore");
            }
        }
        
        generateMineShaft(x, z, baseHeight - MINE_DEPTH, baseHeight);
        
        for (int i = 0; i < 4; i++) {
            int towerX = x + (i % 2) * 20 - 10;
            int towerZ = z + (i / 2) * 20 - 10;
            generateMineTower(towerX, towerZ, baseHeight);
        }
    }
    
    private void generateProcessingFacility(int x, int z, int baseHeight, Random random) {
        for (int dx = -20; dx <= 20; dx++) {
            for (int dz = -20; dz <= 20; dz++) {
                setBlockHeight(x + dx, z + dz, baseHeight);
                setConcreteLayer(x + dx, z + dz, baseHeight);
            }
        }
        
        generateConveyor(x - 15, z, baseHeight, x + 15, z, baseHeight + 5);
        
        generateIndustrialBuilding(x, z - 10, baseHeight, 30, 20, 15);
        
        generateSmokestack(x + 10, z + 10, baseHeight);
    }
    
    @Override
    public void generateStructures(ChunkGenerator generator, int chunkX, int chunkZ) {
        Random random = new Random((long)chunkX * 567890123L + (long)chunkZ * 234567890L);
        
        if (chunkX == -8 && chunkZ == 50) {
            generateCatalAgziPowerPlant(chunkX * 16, chunkZ * 16);
        }
        
        if (chunkX == 12 && chunkZ == 45) {
            generateEregliSteelMill(chunkX * 16, chunkZ * 16);
        }
        
        if (Math.abs(chunkX) < 3 && chunkZ == 40) {
            generateCoalGeopark(chunkX * 16, chunkZ * 16, random);
        }
    }
    
    private void generateCatalAgziPowerPlant(int x, int z) {
        int baseHeight = MOUNTAIN_BASE - 20;
        
        for (int i = 0; i < 4; i++) {
            int unitX = x + i * 40;
            generatePowerPlantUnit(unitX, z, baseHeight);
            generateCoolingTower(unitX + 15, z + 30, baseHeight);
        }
        
        for (int i = 0; i < 2; i++) {
            generateMegaSmokestack(x + 80 + i * 30, z + 60, baseHeight, 100);
        }
    }
    
    private void generateEregliSteelMill(int x, int z) {
        int baseHeight = MOUNTAIN_BASE - 10;
        
        for (int dx = 0; dx < 200; dx++) {
            for (int dz = 0; dz < 150; dz++) {
                setBlockHeight(x + dx, z + dz, baseHeight);
                setConcreteLayer(x + dx, z + dz, baseHeight);
            }
        }
        
        generateBlastFurnace(x + 50, z + 50, baseHeight);
        generateBlastFurnace(x + 100, z + 50, baseHeight);
        
        generateRollingMill(x + 30, z + 100, baseHeight);
        
        generateCokeOven(x + 150, z + 30, baseHeight);
    }
    
    @Override
    public PollutionSource[] getPollutionSources() {
        return new PollutionSource[] {
            new PollutionSource(0, SEA_LEVEL + 5, 0, PollutionType.SHIP_EMISSIONS, 100),
            new PollutionSource(-128, MOUNTAIN_BASE - 20, 800, PollutionType.POWER_PLANT, 500),
            new PollutionSource(192, MOUNTAIN_BASE - 10, 720, PollutionType.INDUSTRIAL, 400),
            new PollutionSource(0, MOUNTAIN_BASE, 900, PollutionType.MINING_DUST, 300),
            new PollutionSource(-200, MOUNTAIN_BASE, 900, PollutionType.MINING_DUST, 250),
            new PollutionSource(200, MOUNTAIN_BASE, 900, PollutionType.MINING_DUST, 250),
            new PollutionSource(0, SEA_LEVEL + 20, 50, PollutionType.TRAFFIC, 150),
            new PollutionSource(0, SEA_LEVEL + 35, 100, PollutionType.HEATING, 100)
        };
    }
}