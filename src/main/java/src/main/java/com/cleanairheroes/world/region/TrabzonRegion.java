package com.cleanairheroes.world.region;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import java.util.Random;

public class TrabzonRegion extends BaseRegion {
    
    private static final int SEA_LEVEL = 62;
    private static final int PONTIC_MOUNTAINS_BASE = 200;
    private static final int PONTIC_MOUNTAINS_PEAK = 600; // Kaçkar Peak area representation
    private static final int CITY_TERRACE_HEIGHT = 120;
    private static final int HARBOR_AREA = 100;
    
    public TrabzonRegion() {
        super("trabzon_turkey", "Trabzon, Turkey - DOKA");
    }
    
    @Override
    public void generateTerrain(ChunkGenerator generator, int chunkX, int chunkZ) {
        Random random = new Random((long)chunkX * 567123489L + (long)chunkZ * 789456123L);
        
        int worldX = chunkX * 16;
        int worldZ = chunkZ * 16;
        
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = worldX + x;
                int realZ = worldZ + z;
                
                generateTrabzonTerrain(realX, realZ, random);
            }
        }
    }
    
    private void generateTrabzonTerrain(int x, int z, Random random) {
        if (z < -50) {
            generateBlackSeaCoast(x, z, random);
        } else if (z < 150) {
            generateHarborAndLowerCity(x, z, random);
        } else if (z < 400) {
            generateMountainsideCity(x, z, random);
        } else if (z < 800) {
            generatePonticMountains(x, z, random);
        } else {
            generateHighMountainArea(x, z, random);
        }
    }
    
    private void generateBlackSeaCoast(int x, int z, Random random) {
        if (z < -100) {
            // Sea floor below 2000m as mentioned in research
            setBlockHeight(x, z, SEA_LEVEL - 25);
            setWaterColumn(x, z, SEA_LEVEL - 25, SEA_LEVEL);
        } else {
            // Steep coastal area
            double slope = (z + 100) / 50.0;
            int coastHeight = SEA_LEVEL + (int)(slope * 12) + random.nextInt(3);
            setBlockHeight(x, z, coastHeight);
            
            if (coastHeight <= SEA_LEVEL + 2) {
                setSandLayer(x, z, coastHeight - 1, coastHeight);
            }
        }
    }
    
    private void generateHarborAndLowerCity(int x, int z, Random random) {
        if (Math.abs(x) < HARBOR_AREA && z < 50) {
            generateHistoricHarbor(x, z, random);
        } else {
            // City built on triangle of tableland between two deep ravines
            generateTablelandTerrace(x, z, random);
        }
    }
    
    private void generateHistoricHarbor(int x, int z, Random random) {
        int baseHeight = SEA_LEVEL + 2;
        setBlockHeight(x, z, baseHeight);
        setConcreteLayer(x, z, baseHeight);
        
        // Ancient Roman-built harbor remains
        if (Math.abs(x) < 20 && Math.abs(z) < 20) {
            generateAncientHarborRuins(x, z, baseHeight);
        }
        
        // Modern port facilities
        if (x % 40 == 0 && z % 30 == 0) {
            generateSmallPortCrane(x, z, baseHeight);
        }
        
        // Fishing boats area
        if (x % 25 == 0 && z % 25 == 0 && random.nextDouble() < 0.4) {
            generateFishingBoat(x, z, baseHeight);
        }
    }
    
    private void generateTablelandTerrace(int x, int z, Random random) {
        // Ascending terrain as described in research
        double distanceFromHarbor = Math.max(0, z - 50);
        int terraceHeight = SEA_LEVEL + 15 + (int)(distanceFromHarbor * 0.15);
        
        // Create deep ravines on east and west
        if (Math.abs(x) > 150 && Math.abs(x) < 200) {
            terraceHeight -= 30 + random.nextInt(10);
            generateRavine(x, z, terraceHeight);
        } else {
            double tableNoise = getPerlinNoise(x * 0.02, z * 0.02) * 5;
            terraceHeight += (int)tableNoise;
            setBlockHeight(x, z, terraceHeight);
            
            generateUrbanTerrace(x, z, terraceHeight, random);
        }
    }
    
    private void generateMountainsideCity(int x, int z, Random random) {
        // Steep terrain climbing into mountains
        double mountainProgress = (z - 150) / 250.0;
        int baseHeight = CITY_TERRACE_HEIGHT + (int)(mountainProgress * (PONTIC_MOUNTAINS_BASE - CITY_TERRACE_HEIGHT));
        
        // Add steep slope variation
        double slopeNoise = getPerlinNoise(x * 0.01, z * 0.005) * 25;
        baseHeight += (int)slopeNoise;
        
        setBlockHeight(x, z, baseHeight);
        
        if (mountainProgress < 0.7) {
            generateMountainUrbanArea(x, z, baseHeight, random);
        } else {
            generateMountainForest(x, z, baseHeight, random);
        }
    }
    
    private void generatePonticMountains(int x, int z, Random random) {
        double mountainProgress = (z - 400) / 400.0;
        int mountainHeight = PONTIC_MOUNTAINS_BASE + (int)(mountainProgress * (PONTIC_MOUNTAINS_PEAK - PONTIC_MOUNTAINS_BASE));
        
        // Dense ridge system
        double ridgeNoise = getPerlinNoise(x * 0.003, z * 0.008) * 80;
        double detailNoise = getPerlinNoise(x * 0.02, z * 0.02) * 30;
        
        mountainHeight += (int)(ridgeNoise + detailNoise);
        setBlockHeight(x, z, mountainHeight);
        
        if (mountainHeight > 300) {
            setStoneLayer(x, z, mountainHeight - 15, mountainHeight);
        }
        
        // Dense forest coverage as mentioned in research
        if (mountainHeight < 450 && random.nextDouble() < 0.4) {
            generateDenseBlackSeaForest(x, z, mountainHeight, random);
        }
    }
    
    private void generateHighMountainArea(int x, int z, Random random) {
        // Approaching Kaçkar Peak area (3971m represented)
        double peakProgress = (z - 800) / 200.0;
        int peakHeight = PONTIC_MOUNTAINS_PEAK + (int)(peakProgress * 200);
        
        double alpineNoise = getPerlinNoise(x * 0.002, z * 0.002) * 100;
        peakHeight += (int)alpineNoise;
        
        setBlockHeight(x, z, peakHeight);
        setStoneLayer(x, z, peakHeight - 20, peakHeight);
        
        // Alpine meadows and rocky peaks
        if (random.nextDouble() < 0.1) {
            generateAlpineMeadow(x, z, peakHeight);
        }
        
        if (peakHeight > 700 && random.nextDouble() < 0.05) {
            generateRockyPeak(x, z, peakHeight);
        }
    }
    
    private void generateUrbanTerrace(int x, int z, int baseHeight, Random random) {
        // Confusing layout built on mountainside as described
        if ((x % 20 + z % 20) % 15 < 8 && random.nextDouble() < 0.6) {
            // Irregular building placement due to terrain
            int buildingHeight = 8 + random.nextInt(12);
            generateTerracedBuilding(x, z, baseHeight, buildingHeight, random);
        }
        
        // Narrow, twisting streets
        if (getTwistingStreetPattern(x, z)) {
            generateNarrowStreet(x, z, baseHeight);
        }
    }
    
    private void generateMountainUrbanArea(int x, int z, int baseHeight, Random random) {
        // Sparse development climbing the mountainside
        if (x % 40 == 0 && z % 30 == 0 && random.nextDouble() < 0.3) {
            generateMountainHouse(x, z, baseHeight, random);
        }
        
        // Winding mountain roads
        if (isMountainRoad(x, z)) {
            generateWindingRoad(x, z, baseHeight);
        }
        
        // Forest patches
        if (random.nextDouble() < 0.2) {
            generateTree(x, z, baseHeight);
        }
    }
    
    private boolean getTwistingStreetPattern(int x, int z) {
        // Create irregular street pattern following terrain
        return ((x + z) % 25 < 3) || (Math.abs(x) % 15 < 2 && z % 20 < 2);
    }
    
    private boolean isMountainRoad(int x, int z) {
        // Serpentine roads following contour lines
        double contourLevel = Math.floor((x * x + z * z) / 10000.0);
        return (contourLevel % 3) < 0.5;
    }
    
    @Override
    public void generateStructures(ChunkGenerator generator, int chunkX, int chunkZ) {
        Random random = new Random((long)chunkX * 123789456L + (long)chunkZ * 456123789L);
        
        // Atatürk Alanı (central square)
        if (chunkX == 0 && chunkZ == 8) {
            generateAtaturkSquare(chunkX * 16, chunkZ * 16);
        }
        
        // Uzun Sokak (main pedestrian street)
        if (chunkX == -1 && chunkZ == 8) {
            generateUzunSokak(chunkX * 16, chunkZ * 16);
        }
        
        // Byzantine walls
        if (Math.abs(chunkX) == 8 && chunkZ > 6 && chunkZ < 12) {
            generateByzantineWalls(chunkX * 16, chunkZ * 16);
        }
        
        // Genoese castle (Leontocastron)
        if (chunkX == 9 && chunkZ == 9) {
            generateGenoeseCastle(chunkX * 16, chunkZ * 16);
        }
        
        // DOKA monitoring stations
        if (chunkX % 4 == 0 && chunkZ % 4 == 0) {
            generateDOKAMonitoringStation(chunkX * 16 + 8, chunkZ * 16 + 8, random);
        }
    }
    
    private void generateAtaturkSquare(int x, int z) {
        int baseHeight = SEA_LEVEL + 25;
        
        // Shady park at the heart of the city
        for (int dx = 0; dx < 80; dx++) {
            for (int dz = 0; dz < 80; dz++) {
                setBlockHeight(x + dx, z + dz, baseHeight);
                setBlock(x + dx, baseHeight, z + dz, "grass_block");
                
                // Trees for shade
                Random random = new Random(dx * 1000L + dz);
                if (random.nextDouble() < 0.08) {
                    generateTree(x + dx, z + dz, baseHeight + 1);
                }
                
                // Pathways
                if (dx == 40 || dz == 40 || (dx + dz) % 20 == 0) {
                    setBlock(x + dx, baseHeight, z + dz, "stone_bricks");
                }
            }
        }
        
        // Central monument
        generateMonument(x + 40, z + 40, baseHeight);
    }
    
    private void generateUzunSokak(int x, int z) {
        int baseHeight = SEA_LEVEL + 27;
        
        // Major pedestrian shopping street
        for (int dx = 0; dx < 200; dx++) {
            for (int dz = 0; dz < 15; dz++) {
                setBlockHeight(x + dx, z + dz, baseHeight);
                setBlock(x + dx, baseHeight, z + dz, "stone_bricks");
                
                // Shop buildings on both sides
                if (dz < 3 || dz > 11) {
                    for (int y = 0; y < 15; y++) {
                        setBlock(x + dx, baseHeight + y, z + dz, "brick_block");
                    }
                }
            }
        }
    }
    
    private void generateByzantineWalls(int x, int z) {
        int wallHeight = getHeightAt(x, z) + 1;
        
        // Walls that date from Byzantine times enclosing city center
        for (int i = 0; i < 16; i++) {
            int wallY = wallHeight + i;
            if (i < 12) {
                setBlock(x, wallY, z + i, "stone_bricks");
                if (i % 3 == 0) {
                    setBlock(x + 1, wallY, z + i, "stone_bricks");
                }
            }
        }
        
        // Defensive towers every 50 blocks
        if (z % 50 == 0) {
            generateDefensiveTower(x, z, wallHeight);
        }
    }
    
    private void generateDOKAMonitoringStation(int x, int z, Random random) {
        int baseHeight = getHeightAt(x, z) + 1;
        
        // DOKA Agency air quality monitoring
        for (int dx = 0; dx < 4; dx++) {
            for (int dz = 0; dz < 4; dz++) {
                if (dx == 0 || dx == 3 || dz == 0 || dz == 3) {
                    setBlock(x + dx, baseHeight, z + dz, "white_concrete");
                    setBlock(x + dx, baseHeight + 1, z + dz, "white_concrete");
                    setBlock(x + dx, baseHeight + 2, z + dz, "white_concrete");
                }
            }
        }
        
        // Monitoring equipment
        setBlock(x + 1, baseHeight + 3, z + 1, "observer");
        setBlock(x + 2, baseHeight + 3, z + 2, "observer");
        setBlock(x + 1, baseHeight + 4, z + 1, "lightning_rod");
    }
    
    @Override
    public PollutionSource[] getPollutionSources() {
        return new PollutionSource[] {
            // Harbor emissions (smaller than other cities)
            new PollutionSource(new BlockPos(0, SEA_LEVEL + 2, 0), "SHIP_EMISSIONS", 80),
            
            // Heavy traffic in narrow streets (major source)
            new PollutionSource(new BlockPos(0, SEA_LEVEL + 25, 100), "TRAFFIC", 200),
            new PollutionSource(new BlockPos(-50, SEA_LEVEL + 30, 150), "TRAFFIC", 180),
            new PollutionSource(new BlockPos(50, SEA_LEVEL + 30, 150), "TRAFFIC", 180),
            new PollutionSource(new BlockPos(0, SEA_LEVEL + 35, 200), "TRAFFIC", 160),
            
            // Industrial facilities
            new PollutionSource(new BlockPos(-100, CITY_TERRACE_HEIGHT, 300), "INDUSTRIAL", 150),
            new PollutionSource(new BlockPos(100, CITY_TERRACE_HEIGHT, 300), "INDUSTRIAL", 150),
            new PollutionSource(new BlockPos(0, CITY_TERRACE_HEIGHT + 20, 350), "INDUSTRIAL", 180),
            
            // Solid fuel heating systems (major issue)
            new PollutionSource(new BlockPos(-80, SEA_LEVEL + 40, 180), "HEATING", 140),
            new PollutionSource(new BlockPos(80, SEA_LEVEL + 40, 180), "HEATING", 140),
            new PollutionSource(new BlockPos(0, SEA_LEVEL + 50, 250), "HEATING", 160),
            new PollutionSource(new BlockPos(-60, SEA_LEVEL + 60, 320), "HEATING", 120),
            new PollutionSource(new BlockPos(60, SEA_LEVEL + 60, 320), "HEATING", 120),
            
            // Reduced green areas contributing to poor air quality
            new PollutionSource(new BlockPos(0, SEA_LEVEL + 45, 400), "HEATING", 100)
        };
    }
}