package com.cleanairheroes.world.region;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import java.util.Random;

public class SoutheastRomaniaRegion extends BaseRegion {
    
    private static final int SEA_LEVEL = 62;
    private static final int CONSTANTA_PORT_ZONE = 300;
    private static final int GALATI_INDUSTRIAL_ZONE = 600;
    private static final int BRASOV_MOUNTAIN_BASE = 200;
    private static final int BRASOV_MOUNTAIN_PEAK = 400;
    
    // City centers based on real geographic distribution
    private static final int CONSTANTA_X = -800, CONSTANTA_Z = 0;
    private static final int GALATI_X = 0, GALATI_Z = 400;
    private static final int BRASOV_X = 600, BRASOV_Z = 800;
    
    public SoutheastRomaniaRegion() {
        super("southeast_romania", "Southeast Romania (ATG)");
    }
    
    @Override
    public void generateTerrain(ChunkGenerator generator, int chunkX, int chunkZ) {
        Random random = new Random((long)chunkX * 678901234L + (long)chunkZ * 345678901L);
        
        int worldX = chunkX * 16;
        int worldZ = chunkZ * 16;
        
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = worldX + x;
                int realZ = worldZ + z;
                
                generateSoutheastRomaniaTerrain(realX, realZ, random);
            }
        }
    }
    
    private void generateSoutheastRomaniaTerrain(int x, int z, Random random) {
        double distanceToConstanta = Math.sqrt(Math.pow(x - CONSTANTA_X, 2) + Math.pow(z - CONSTANTA_Z, 2));
        double distanceToGalati = Math.sqrt(Math.pow(x - GALATI_X, 2) + Math.pow(z - GALATI_Z, 2));
        double distanceToBrasov = Math.sqrt(Math.pow(x - BRASOV_X, 2) + Math.pow(z - BRASOV_Z, 2));
        
        if (distanceToConstanta < 400) {
            generateConstantaArea(x, z, distanceToConstanta, random);
        } else if (distanceToGalati < 350) {
            generateGalatiArea(x, z, distanceToGalati, random);
        } else if (distanceToBrasov < 300) {
            generateBrasovArea(x, z, distanceToBrasov, random);
        } else {
            generateRuralArea(x, z, random);
        }
    }
    
    private void generateConstantaArea(int x, int z, double distanceFromCenter, Random random) {
        if (z < -50) {
            generateBlackSeaCoast(x, z, random);
        } else if (distanceFromCenter < 150) {
            generateConstantaPort(x, z, random);
        } else if (distanceFromCenter < 250) {
            generateConstantaCity(x, z, random);
        } else {
            generateConstantaIndustrial(x, z, random);
        }
    }
    
    private void generateConstantaPort(int x, int z, Random random) {
        int baseHeight = SEA_LEVEL + 3;
        setBlockHeight(x, z, baseHeight);
        setConcreteLayer(x, z, baseHeight);
        
        // 92+ million tons cargo capacity
        if (x % 60 == 0 && z % 40 == 0) {
            generateMegaPortCrane(x, z, baseHeight);
        }
        
        // Diesel import facilities (4.4 million tons in 2024)
        if (Math.abs(x - CONSTANTA_X) < 80 && Math.abs(z - CONSTANTA_Z) < 80) {
            if (x % 30 == 0 && z % 30 == 0) {
                generateDieselTank(x, z, baseHeight, random);
            }
        }
        
        // Container yards
        if (x % 40 == 20 && z % 40 == 20) {
            generateContainerYard(x, z, baseHeight, random);
        }
        
        // Grain export facilities
        if (x % 80 == 0 && z % 60 == 30) {
            generateGrainSilo(x, z, baseHeight);
        }
    }
    
    private void generateGalatiArea(int x, int z, double distanceFromCenter, Random random) {
        if (distanceFromCenter < 100) {
            generateGalatiSteelWorks(x, z, random);
        } else if (distanceFromCenter < 200) {
            generateGalatiIndustrialCity(x, z, random);
        } else {
            generateDanubeRiverside(x, z, random);
        }
    }
    
    private void generateGalatiSteelWorks(int x, int z, Random random) {
        int baseHeight = SEA_LEVEL + 15 + random.nextInt(5);
        setBlockHeight(x, z, baseHeight);
        setConcreteLayer(x, z, baseHeight);
        
        // Liberty Galati steel complex (historically 8.2 million tons capacity)
        if (x % 80 == 0 && z % 80 == 0) {
            generateBlastFurnaceComplex(x, z, baseHeight);
        }
        
        // Basic oxygen furnace (BOF) areas
        if (x % 100 == 50 && z % 100 == 50) {
            generateBOFFurnace(x, z, baseHeight);
        }
        
        // Electric arc furnace (EAF) facilities
        if (x % 120 == 0 && z % 60 == 0) {
            generateEAFFurnace(x, z, baseHeight);
        }
        
        // Rolling mills
        if (x % 150 == 75 && z % 100 == 0) {
            generateRollingMill(x, z, baseHeight);
        }
        
        // Waste disposal areas (cyanide lakes near Danube)
        if (Math.abs(x - GALATI_X + 200) < 50 && Math.abs(z - GALATI_Z - 100) < 50) {
            generateContaminatedLake(x, z, baseHeight - 5);
        }
    }
    
    private void generateBrasovArea(int x, int z, double distanceFromCenter, Random random) {
        // Mountain city with better air quality
        double mountainHeight = BRASOV_MOUNTAIN_BASE + 
            (BRASOV_MOUNTAIN_PEAK - BRASOV_MOUNTAIN_BASE) * 
            Math.min(1.0, distanceFromCenter / 200.0);
        
        double terrainNoise = getPerlinNoise(x * 0.01, z * 0.01) * 20;
        int baseHeight = (int)(mountainHeight + terrainNoise);
        setBlockHeight(x, z, baseHeight);
        
        if (distanceFromCenter < 100) {
            generateBrasovCityCenter(x, z, baseHeight, random);
        } else if (distanceFromCenter < 200) {
            generateBrasovSuburbs(x, z, baseHeight, random);
        } else {
            generateCarpathianForest(x, z, baseHeight, random);
        }
    }
    
    private void generateBrasovCityCenter(int x, int z, int baseHeight, Random random) {
        if ((x % 25 == 0 || x % 25 == 12) && (z % 25 == 0 || z % 25 == 12)) {
            if (random.nextDouble() < 0.7) {
                int buildingHeight = 12 + random.nextInt(18);
                generateAlpineBuilding(x, z, baseHeight, buildingHeight, random);
            }
        }
        
        // Better urban planning with more green spaces
        if ((x % 25 == 6) || (z % 25 == 6)) {
            generateCleanStreet(x, z, baseHeight);
        }
        
        // Parks and green spaces
        if (x % 50 == 25 && z % 50 == 25) {
            generateUrbanPark(x, z, baseHeight, random);
        }
    }
    
    private void generateRuralArea(int x, int z, Random random) {
        int baseHeight = SEA_LEVEL + 20 + random.nextInt(15);
        setBlockHeight(x, z, baseHeight);
        
        // Agricultural areas
        if (x % 100 < 80 && z % 100 < 80) {
            generateSustainableFarm(x, z, baseHeight, random);
        }
        
        // Small rural settlements
        if (x % 200 == 0 && z % 200 == 0 && random.nextDouble() < 0.3) {
            generateRuralVillage(x, z, baseHeight, random);
        }
        
        // Natural vegetation
        if (random.nextDouble() < 0.05) {
            generateTree(x, z, baseHeight);
        }
    }
    
    @Override
    public void generateStructures(ChunkGenerator generator, int chunkX, int chunkZ) {
        Random random = new Random((long)chunkX * 987654321L + (long)chunkZ * 123456789L);
        
        // Constanta structures
        if (isInConstantaArea(chunkX, chunkZ)) {
            generateConstantaStructures(chunkX, chunkZ, random);
        }
        
        // Galati structures
        if (isInGalatiArea(chunkX, chunkZ)) {
            generateGalatiStructures(chunkX, chunkZ, random);
        }
        
        // Brasov structures
        if (isInBrasovArea(chunkX, chunkZ)) {
            generateBrasovStructures(chunkX, chunkZ, random);
        }
        
        // Regional monitoring network
        if (chunkX % 8 == 0 && chunkZ % 8 == 0) {
            generateRegionalMonitoringCenter(chunkX * 16 + 8, chunkZ * 16 + 8, random);
        }
    }
    
    private void generateConstantaStructures(int chunkX, int chunkZ, Random random) {
        int x = chunkX * 16;
        int z = chunkZ * 16;
        
        // Major port infrastructure
        if (chunkX == -50 && chunkZ == 0) {
            generateConstantaMainPort(x, z);
        }
        
        // Oil refinery complex
        if (chunkX == -48 && chunkZ == 2) {
            generateOilRefineryComplex(x, z);
        }
        
        // Historic lighthouse
        if (chunkX == -51 && chunkZ == -1) {
            generateConstantaLighthouse(x, z);
        }
    }
    
    private void generateGalatiStructures(int chunkX, int chunkZ, Random random) {
        int x = chunkX * 16;
        int z = chunkZ * 16;
        
        // Main steel plant complex
        if (chunkX == 0 && chunkZ == 25) {
            generateMainSteelPlant(x, z);
        }
        
        // Coke ovens
        if (chunkX == 1 && chunkZ == 26) {
            generateCokeOvenBattery(x, z);
        }
        
        // Environmental monitoring for pollution
        if (chunkX == -1 && chunkZ == 24) {
            generatePollutionMonitoringCenter(x, z);
        }
    }
    
    private void generateBrasovStructures(int chunkX, int chunkZ, Random random) {
        int x = chunkX * 16;
        int z = chunkZ * 16;
        
        // Clean technology center
        if (chunkX == 37 && chunkZ == 50) {
            generateCleanTechCenter(x, z);
        }
        
        // Mountain resort with clean air
        if (chunkX == 38 && chunkZ == 52) {
            generateMountainResort(x, z);
        }
        
        // Renewable energy facility
        if (chunkX == 40 && chunkZ == 48) {
            generateRenewableEnergyPlant(x, z);
        }
    }
    
    private void generateRegionalMonitoringCenter(int x, int z, Random random) {
        int baseHeight = getHeightAt(x, z) + 1;
        
        // Comprehensive monitoring system for the entire region
        for (int dx = 0; dx < 8; dx++) {
            for (int dz = 0; dz < 8; dz++) {
                if (dx == 0 || dx == 7 || dz == 0 || dz == 7) {
                    setBlock(x + dx, baseHeight, z + dz, "light_gray_concrete");
                    setBlock(x + dx, baseHeight + 1, z + dz, "light_gray_concrete");
                    setBlock(x + dx, baseHeight + 2, z + dz, "light_gray_concrete");
                }
            }
        }
        
        // Advanced monitoring equipment
        setBlock(x + 3, baseHeight + 3, z + 3, "observer");
        setBlock(x + 4, baseHeight + 3, z + 4, "observer");
        setBlock(x + 3, baseHeight + 4, z + 3, "lightning_rod");
        setBlock(x + 4, baseHeight + 4, z + 4, "lightning_rod");
        
        // Communication antenna
        for (int y = 0; y < 15; y++) {
            setBlock(x + 3, baseHeight + 5 + y, z + 3, "iron_bars");
        }
    }
    
    private boolean isInConstantaArea(int chunkX, int chunkZ) {
        return Math.abs(chunkX + 50) < 25 && Math.abs(chunkZ) < 25;
    }
    
    private boolean isInGalatiArea(int chunkX, int chunkZ) {
        return Math.abs(chunkX) < 22 && Math.abs(chunkZ - 25) < 22;
    }
    
    private boolean isInBrasovArea(int chunkX, int chunkZ) {
        return Math.abs(chunkX - 37) < 18 && Math.abs(chunkZ - 50) < 18;
    }
    
    @Override
    public PollutionSource[] getPollutionSources() {
        return new PollutionSource[] {
            // Constanta port and industrial pollution
            new PollutionSource(new BlockPos(CONSTANTA_X, SEA_LEVEL + 3, CONSTANTA_Z), "SHIP_EMISSIONS", 300),
            new PollutionSource(new BlockPos(CONSTANTA_X + 50, SEA_LEVEL + 5, CONSTANTA_Z + 50), "INDUSTRIAL", 250),
            new PollutionSource(new BlockPos(CONSTANTA_X - 50, SEA_LEVEL + 5, CONSTANTA_Z + 50), "INDUSTRIAL", 200),
            
            // Galati steel works - major pollution source
            new PollutionSource(new BlockPos(GALATI_X, SEA_LEVEL + 15, GALATI_Z), "INDUSTRIAL", 500),
            new PollutionSource(new BlockPos(GALATI_X + 100, SEA_LEVEL + 15, GALATI_Z), "INDUSTRIAL", 400),
            new PollutionSource(new BlockPos(GALATI_X - 100, SEA_LEVEL + 15, GALATI_Z), "INDUSTRIAL", 400),
            new PollutionSource(new BlockPos(GALATI_X, SEA_LEVEL + 20, GALATI_Z + 150), "INDUSTRIAL", 350),
            
            // Regional traffic pollution
            new PollutionSource(new BlockPos(CONSTANTA_X, SEA_LEVEL + 10, CONSTANTA_Z + 100), "TRAFFIC", 180),
            new PollutionSource(new BlockPos(GALATI_X, SEA_LEVEL + 18, GALATI_Z + 100), "TRAFFIC", 200),
            
            // Agricultural emissions
            new PollutionSource(new BlockPos(-200, SEA_LEVEL + 25, 200), "AGRICULTURAL", 100),
            new PollutionSource(new BlockPos(200, SEA_LEVEL + 25, 200), "AGRICULTURAL", 100),
            new PollutionSource(new BlockPos(0, SEA_LEVEL + 25, 600), "AGRICULTURAL", 120),
            
            // Coal power plants (being phased out)
            new PollutionSource(new BlockPos(-400, SEA_LEVEL + 20, 300), "POWER_PLANT", 300),
            new PollutionSource(new BlockPos(400, SEA_LEVEL + 20, 300), "POWER_PLANT", 280),
            
            // Brasov - much cleaner due to mountain location and better planning
            new PollutionSource(new BlockPos(BRASOV_X, BRASOV_MOUNTAIN_BASE + 20, BRASOV_Z), "TRAFFIC", 80),
            new PollutionSource(new BlockPos(BRASOV_X, BRASOV_MOUNTAIN_BASE + 15, BRASOV_Z + 50), "HEATING", 60)
        };
    }
}