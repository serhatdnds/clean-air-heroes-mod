package com.cleanairheroes.world.region;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import java.util.Random;

public class OdesaRegion extends BaseRegion {
    
    private static final int SEA_LEVEL = 62;
    private static final int CITY_ELEVATION = 85; // Odesa sits on elevated coastal bluff
    private static final int PORT_AREA_SIZE = 300;
    private static final int INDUSTRIAL_ZONE_START = 400;
    private static final int CITY_CENTER_X = 0;
    private static final int CITY_CENTER_Z = 200;
    
    public OdesaRegion() {
        super("odesa_ukraine", "Odesa, Ukraine");
    }
    
    @Override
    public void generateTerrain(ChunkGenerator generator, int chunkX, int chunkZ) {
        Random random = new Random((long)chunkX * 892374651L + (long)chunkZ * 456789123L);
        
        int worldX = chunkX * 16;
        int worldZ = chunkZ * 16;
        
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int realX = worldX + x;
                int realZ = worldZ + z;
                
                generateOdesaTerrain(realX, realZ, random);
            }
        }
    }
    
    private void generateOdesaTerrain(int x, int z, Random random) {
        if (z < -50) {
            generateBlackSeaWaters(x, z, random);
        } else if (z < 100) {
            generateMassivePortComplex(x, z, random);
        } else if (z < 350) {
            generateHistoricCity(x, z, random);
        } else if (z < 600) {
            generateIndustrialDistrict(x, z, random);
        } else {
            generateSuburbanOutskirts(x, z, random);
        }
    }
    
    private void generateBlackSeaWaters(int x, int z, Random random) {
        if (z < -100) {
            setBlockHeight(x, z, SEA_LEVEL - 20);
            setWaterColumn(x, z, SEA_LEVEL - 20, SEA_LEVEL);
        } else {
            double coastalSlope = (z + 100) / 50.0;
            int depth = SEA_LEVEL - 5 + (int)(coastalSlope * 15);
            setBlockHeight(x, z, depth);
            
            if (depth < SEA_LEVEL) {
                setWaterColumn(x, z, depth, SEA_LEVEL);
            }
        }
    }
    
    private void generateMassivePortComplex(int x, int z, Random random) {
        if (Math.abs(x) < PORT_AREA_SIZE && z < 50) {
            generatePortFacilities(x, z, random);
        } else if (Math.abs(x - 200) < 50 && z < 80) {
            generateOilTerminal(x, z, random);
        } else if (Math.abs(x + 200) < 50 && z < 80) {
            generateContainerTerminal(x, z, random);
        } else {
            generatePortBuffer(x, z, random);
        }
    }
    
    private void generatePortFacilities(int x, int z, Random random) {
        int baseHeight = SEA_LEVEL + 2;
        setBlockHeight(x, z, baseHeight);
        setConcreteLayer(x, z, baseHeight);
        
        // Major breakwaters
        if ((Math.abs(x) == 250 && z < 0) || (z == -10 && Math.abs(x) < 250)) {
            for (int y = 0; y < 15; y++) {
                setBlock(x, baseHeight + y, z, "stone_bricks");
            }
        }
        
        // Berths and quays
        if (x % 80 == 0 && z % 40 == 0) {
            generatePortCrane(x, z, baseHeight);
        }
        
        // Jetties dividing harbors
        if (x % 120 == 0 && z > -50 && z < 20) {
            for (int dz = 0; dz < 70; dz++) {
                setBlock(x, baseHeight + 1, z - 50 + dz, "concrete");
            }
        }
    }
    
    private void generateOilTerminal(int x, int z, Random random) {
        int baseHeight = SEA_LEVEL + 3;
        setBlockHeight(x, z, baseHeight);
        setConcreteLayer(x, z, baseHeight);
        
        // OMTP Oil District with 671,000 m³ capacity
        if (x % 30 == 0 && z % 30 == 0) {
            generateOilTank(x, z, baseHeight, 15 + random.nextInt(5));
        }
        
        // 6 berths for oil tankers
        if (z == 0 && x % 50 == 0 && Math.abs(x - 200) < 150) {
            generateOilBerth(x, z, baseHeight);
        }
        
        // Pipeline network
        if (x % 10 == 0 || z % 10 == 0) {
            setBlock(x, baseHeight + 1, z, "iron_block");
        }
    }
    
    private void generateContainerTerminal(int x, int z, Random random) {
        int baseHeight = SEA_LEVEL + 2;
        setBlockHeight(x, z, baseHeight);
        setConcreteLayer(x, z, baseHeight);
        
        // Container Terminal Odesa (CTO) - largest in Ukraine
        if (x % 20 == 0 && z % 20 == 0) {
            generateContainerStack(x, z, baseHeight, random.nextInt(6) + 2);
        }
        
        // Container cranes
        if (x % 60 == 0 && z % 40 == 0) {
            generateContainerCrane(x, z, baseHeight);
        }
        
        // Rail connections
        if (z % 30 == 15) {
            generateRailLine(x, z, baseHeight);
        }
    }
    
    private void generateHistoricCity(int x, int z, Random random) {
        double distanceFromCenter = Math.sqrt(
            Math.pow(x - CITY_CENTER_X, 2) + 
            Math.pow(z - CITY_CENTER_Z, 2)
        );
        
        // Elevated coastal bluff terrain
        double heightNoise = getPerlinNoise(x * 0.01, z * 0.01) * 8;
        int baseHeight = CITY_ELEVATION + (int)heightNoise;
        setBlockHeight(x, z, baseHeight);
        
        if (distanceFromCenter < 100) {
            generateCityCenter(x, z, baseHeight, random);
        } else if (distanceFromCenter < 200) {
            generateResidentialArea(x, z, baseHeight, random);
        } else {
            generateParkAndGreenSpace(x, z, baseHeight, random);
        }
    }
    
    private void generateCityCenter(int x, int z, int baseHeight, Random random) {
        if ((x % 30 == 0 || x % 30 == 15) && (z % 30 == 0 || z % 30 == 15)) {
            if (random.nextDouble() < 0.8) {
                // UNESCO World Heritage historic buildings
                int buildingHeight = 15 + random.nextInt(20);
                generateHistoricBuilding(x, z, baseHeight, buildingHeight, random);
            }
        }
        
        // Grid street pattern
        if ((x % 30 == 7 || x % 30 == 22) || (z % 30 == 7 || z % 30 == 22)) {
            generateWideStreet(x, z, baseHeight);
        }
    }
    
    private void generateIndustrialDistrict(int x, int z, Random random) {
        int baseHeight = CITY_ELEVATION - 10 + random.nextInt(5);
        setBlockHeight(x, z, baseHeight);
        
        // Most factories lie north of port along waterfront
        if (z < 500 && Math.abs(x) < 300) {
            generateWaterfrontIndustry(x, z, baseHeight, random);
        } else {
            generateWesternIndustry(x, z, baseHeight, random);
        }
    }
    
    private void generateWaterfrontIndustry(int x, int z, int baseHeight, Random random) {
        if (x % 80 == 0 && z % 80 == 0) {
            if (random.nextDouble() < 0.7) {
                // Machine tools, cranes, plows manufacturing
                generateMachineToolFactory(x, z, baseHeight, random);
            }
        }
        
        if (x % 100 == 50 && z % 100 == 50) {
            generateChemicalPlant(x, z, baseHeight, random);
        }
    }
    
    private void generateWesternIndustry(int x, int z, int baseHeight, Random random) {
        if (x % 120 == 0 && z % 120 == 0) {
            if (random.nextDouble() < 0.6) {
                // Oil refining, jute processing, food processing
                String[] industryTypes = {"oil_refinery", "jute_mill", "food_processing"};
                String type = industryTypes[random.nextInt(industryTypes.length)];
                generateIndustrialComplex(x, z, baseHeight, type, random);
            }
        }
    }
    
    @Override
    public void generateStructures(ChunkGenerator generator, int chunkX, int chunkZ) {
        Random random = new Random((long)chunkX * 345678901L + (long)chunkZ * 678901234L);
        
        // UNESCO World Heritage historic center
        if (chunkX == 0 && chunkZ == 12) {
            generateHistoricCenter(chunkX * 16, chunkZ * 16);
        }
        
        // Odesa Opera House
        if (chunkX == 1 && chunkZ == 13) {
            generateOperaHouse(chunkX * 16, chunkZ * 16);
        }
        
        // Potemkin Stairs
        if (chunkX == -1 && chunkZ == 11) {
            generatePotemkinStairs(chunkX * 16, chunkZ * 16);
        }
        
        // University complex
        if (chunkX == 2 && chunkZ == 14) {
            generateUniversityComplex(chunkX * 16, chunkZ * 16);
        }
        
        // Air quality monitoring stations
        if (chunkX % 5 == 0 && chunkZ % 5 == 0) {
            generateMonitoringStation(chunkX * 16 + 8, chunkZ * 16 + 8, random);
        }
    }
    
    private void generateHistoricCenter(int x, int z) {
        // 19th-century urban planning grid
        for (int dx = 0; dx < 200; dx++) {
            for (int dz = 0; dz < 200; dz++) {
                int realX = x + dx;
                int realZ = z + dz;
                int baseHeight = CITY_ELEVATION + 5;
                
                setBlockHeight(realX, realZ, baseHeight);
                
                if (dx % 40 == 0 || dz % 40 == 0) {
                    setBlock(realX, baseHeight, realZ, "stone_bricks");
                } else if (dx % 40 < 35 && dz % 40 < 35 && (dx % 40 > 5) && (dz % 40 > 5)) {
                    generateHistoricBuilding(realX, realZ, baseHeight, 12 + (dx + dz) % 8, new Random());
                }
            }
        }
    }
    
    private void generatePotemkinStairs(int x, int z) {
        // 192 steps connecting port to city
        for (int step = 0; step < 192; step++) {
            int stepZ = z - step;
            int stepY = SEA_LEVEL + 5 + step / 3;
            
            for (int dx = 0; dx < 30; dx++) {
                setBlock(x + dx, stepY, stepZ, "stone_bricks");
                setBlock(x + dx, stepY + 1, stepZ, "air");
            }
        }
    }
    
    private void generateMonitoringStation(int x, int z, Random random) {
        int baseHeight = getHeightAt(x, z) + 1;
        
        // Small building housing air quality equipment
        for (int dx = 0; dx < 5; dx++) {
            for (int dz = 0; dz < 5; dz++) {
                if (dx == 0 || dx == 4 || dz == 0 || dz == 4) {
                    setBlock(x + dx, baseHeight, z + dz, "white_concrete");
                    setBlock(x + dx, baseHeight + 1, z + dz, "white_concrete");
                    setBlock(x + dx, baseHeight + 2, z + dz, "white_concrete");
                }
            }
        }
        
        // Monitoring equipment on top
        setBlock(x + 2, baseHeight + 3, z + 2, "observer");
        setBlock(x + 2, baseHeight + 4, z + 2, "lightning_rod");
    }
    
    @Override
    public PollutionSource[] getPollutionSources() {
        return new PollutionSource[] {
            // Major port emissions
            new PollutionSource(new BlockPos(0, SEA_LEVEL + 2, 0), "SHIP_EMISSIONS", 250),
            new PollutionSource(new BlockPos(-200, SEA_LEVEL + 3, 0), "SHIP_EMISSIONS", 200),
            new PollutionSource(new BlockPos(200, SEA_LEVEL + 3, 0), "SHIP_EMISSIONS", 200),
            
            // Oil terminal (OMTP)
            new PollutionSource(new BlockPos(200, SEA_LEVEL + 3, 40), "INDUSTRIAL", 180),
            
            // Container operations
            new PollutionSource(new BlockPos(-200, SEA_LEVEL + 2, 40), "INDUSTRIAL", 120),
            
            // Waterfront industry
            new PollutionSource(new BlockPos(-150, CITY_ELEVATION - 5, 450), "INDUSTRIAL", 200),
            new PollutionSource(new BlockPos(0, CITY_ELEVATION - 5, 450), "INDUSTRIAL", 220),
            new PollutionSource(new BlockPos(150, CITY_ELEVATION - 5, 450), "INDUSTRIAL", 200),
            
            // Western industrial plants
            new PollutionSource(new BlockPos(-300, CITY_ELEVATION - 8, 550), "INDUSTRIAL", 180),
            new PollutionSource(new BlockPos(300, CITY_ELEVATION - 8, 550), "INDUSTRIAL", 180),
            
            // Urban traffic
            new PollutionSource(new BlockPos(0, CITY_ELEVATION, 200), "TRAFFIC", 150),
            new PollutionSource(new BlockPos(-100, CITY_ELEVATION, 250), "TRAFFIC", 100),
            new PollutionSource(new BlockPos(100, CITY_ELEVATION, 250), "TRAFFIC", 100),
            
            // Heating systems
            new PollutionSource(new BlockPos(0, CITY_ELEVATION + 5, 300), "HEATING", 120)
        };
    }
}