package com.cleanairheroes.world.region;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public abstract class BaseRegion {
    
    protected final String regionId;
    protected final String displayName;
    protected final Map<String, Integer> heightMap = new HashMap<>();
    protected final Map<String, String> blockMap = new HashMap<>();
    
    public BaseRegion(String regionId, String displayName) {
        this.regionId = regionId;
        this.displayName = displayName;
    }
    
    public abstract void generateTerrain(ChunkGenerator generator, int chunkX, int chunkZ);
    public abstract void generateStructures(ChunkGenerator generator, int chunkX, int chunkZ);
    public abstract PollutionSource[] getPollutionSources();
    
    // Terrain generation helper methods
    protected void setBlockHeight(int x, int z, int height) {
        heightMap.put(x + "," + z, height);
    }
    
    protected int getHeightAt(int x, int z) {
        return heightMap.getOrDefault(x + "," + z, 64);
    }
    
    protected void setBlock(int x, int y, int z, String blockType) {
        blockMap.put(x + "," + y + "," + z, blockType);
    }
    
    protected void setWaterColumn(int x, int z, int startY, int endY) {
        for (int y = startY; y <= endY; y++) {
            setBlock(x, y, z, "water");
        }
    }
    
    protected void setSandLayer(int x, int z, int startY, int endY) {
        for (int y = startY; y <= endY; y++) {
            setBlock(x, y, z, "sand");
        }
    }
    
    protected void setConcreteLayer(int x, int z, int y) {
        setBlock(x, y, z, "gray_concrete");
        setBlock(x, y - 1, z, "gray_concrete");
    }
    
    protected void setStoneLayer(int x, int z, int startY, int endY) {
        for (int y = startY; y <= endY; y++) {
            setBlock(x, y, z, "stone");
        }
    }
    
    // Building generation methods
    protected void generateBuilding(int x, int z, int baseHeight, int height, Random random) {
        String[] buildingMaterials = {"stone_bricks", "brick_block", "smooth_stone", "cobblestone"};
        String material = buildingMaterials[random.nextInt(buildingMaterials.length)];
        
        for (int dx = 0; dx < 8; dx++) {
            for (int dz = 0; dz < 8; dz++) {
                for (int dy = 0; dy < height; dy++) {
                    if (dx == 0 || dx == 7 || dz == 0 || dz == 7 || dy == 0 || dy == height - 1) {
                        setBlock(x + dx, baseHeight + dy, z + dz, material);
                    } else {
                        setBlock(x + dx, baseHeight + dy, z + dz, "air");
                    }
                }
            }
        }
        
        // Windows
        for (int floor = 1; floor < height - 1; floor += 3) {
            setBlock(x + 2, baseHeight + floor, z, "glass");
            setBlock(x + 5, baseHeight + floor, z, "glass");
            setBlock(x + 2, baseHeight + floor, z + 7, "glass");
            setBlock(x + 5, baseHeight + floor, z + 7, "glass");
        }
    }
    
    protected void generateHouse(int x, int z, int baseHeight, Random random) {
        String[] houseMaterials = {"oak_planks", "birch_planks", "brick_block"};
        String material = houseMaterials[random.nextInt(houseMaterials.length)];
        int houseHeight = 4 + random.nextInt(3);
        
        for (int dx = 0; dx < 6; dx++) {
            for (int dz = 0; dz < 6; dz++) {
                for (int dy = 0; dy < houseHeight; dy++) {
                    if (dx == 0 || dx == 5 || dz == 0 || dz == 5 || dy == 0) {
                        setBlock(x + dx, baseHeight + dy, z + dz, material);
                    } else {
                        setBlock(x + dx, baseHeight + dy, z + dz, "air");
                    }
                }
            }
        }
        
        // Roof
        for (int dx = 0; dx < 6; dx++) {
            for (int dz = 0; dz < 6; dz++) {
                setBlock(x + dx, baseHeight + houseHeight, z + dz, "red_terracotta");
            }
        }
    }
    
    protected void generateFactory(int x, int z, int baseHeight, Random random) {
        for (int dx = 0; dx < 20; dx++) {
            for (int dz = 0; dz < 15; dz++) {
                for (int dy = 0; dy < 12; dy++) {
                    if (dx == 0 || dx == 19 || dz == 0 || dz == 14 || dy == 0 || dy == 11) {
                        setBlock(x + dx, baseHeight + dy, z + dz, "stone_bricks");
                    } else {
                        setBlock(x + dx, baseHeight + dy, z + dz, "air");
                    }
                }
            }
        }
        
        // Industrial windows
        for (int dx = 2; dx < 18; dx += 4) {
            for (int dy = 2; dy < 10; dy += 3) {
                setBlock(x + dx, baseHeight + dy, z, "glass");
                setBlock(x + dx, baseHeight + dy, z + 14, "glass");
            }
        }
    }
    
    protected void generateSmokestack(int x, int z, int baseHeight) {
        int stackHeight = 25 + (int)(Math.random() * 15);
        
        for (int dy = 0; dy < stackHeight; dy++) {
            if (dy < 5) {
                // Base
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        setBlock(x + dx, baseHeight + dy, z + dz, "brick_block");
                    }
                }
            } else {
                // Stack
                setBlock(x, baseHeight + dy, z, "brick_block");
                setBlock(x + 1, baseHeight + dy, z, "brick_block");
                setBlock(x, baseHeight + dy, z + 1, "brick_block");
                setBlock(x + 1, baseHeight + dy, z + 1, "brick_block");
            }
        }
    }
    
    protected void generateCrane(int x, int z, int baseHeight) {
        // Base structure
        for (int dy = 0; dy < 3; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    setBlock(x + dx, baseHeight + dy, z + dz, "iron_block");
                }
            }
        }
        
        // Vertical mast
        for (int dy = 3; dy < 20; dy++) {
            setBlock(x, baseHeight + dy, z, "iron_bars");
        }
        
        // Horizontal arm
        for (int dx = 0; dx < 15; dx++) {
            setBlock(x + dx, baseHeight + 18, z, "iron_bars");
        }
    }
    
    protected void generateOilTank(int x, int z, int baseHeight) {
        generateOilTank(x, z, baseHeight, 8);
    }
    
    protected void generateOilTank(int x, int z, int baseHeight, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz <= radius * radius) {
                    for (int dy = 0; dy < radius; dy++) {
                        if (dx * dx + dz * dz == radius * radius || dy == 0 || dy == radius - 1) {
                            setBlock(x + dx, baseHeight + dy, z + dz, "white_concrete");
                        } else {
                            setBlock(x + dx, baseHeight + dy, z + dz, "air");
                        }
                    }
                }
            }
        }
    }
    
    protected void generateContainerStack(int x, int z, int baseHeight, Random random) {
        generateContainerStack(x, z, baseHeight, random.nextInt(4) + 2);
    }
    
    protected void generateContainerStack(int x, int z, int baseHeight, int height) {
        String[] containerColors = {"red_concrete", "blue_concrete", "green_concrete", "yellow_concrete", "orange_concrete"};
        
        for (int dy = 0; dy < height; dy++) {
            String color = containerColors[dy % containerColors.length];
            for (int dx = 0; dx < 8; dx++) {
                for (int dz = 0; dz < 3; dz++) {
                    if (dx == 0 || dx == 7 || dz == 0 || dz == 2) {
                        setBlock(x + dx, baseHeight + dy, z + dz, color);
                    } else {
                        setBlock(x + dx, baseHeight + dy, z + dz, "air");
                    }
                }
            }
        }
    }
    
    protected void generateRoad(int x, int z, int baseHeight) {
        setBlock(x, baseHeight, z, "gray_concrete");
    }
    
    protected void generateTree(int x, int z, int baseHeight) {
        // Simple tree
        for (int dy = 0; dy < 4; dy++) {
            setBlock(x, baseHeight + dy, z, "oak_log");
        }
        
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 3; dy <= 5; dy++) {
                    if (Math.abs(dx) + Math.abs(dz) <= 2 + (5 - dy)) {
                        setBlock(x + dx, baseHeight + dy, z + dz, "oak_leaves");
                    }
                }
            }
        }
    }
    
    protected void generateWindTurbine(int x, int z, int baseHeight) {
        // Tower
        for (int dy = 0; dy < 30; dy++) {
            setBlock(x, baseHeight + dy, z, "white_concrete");
        }
        
        // Nacelle
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                setBlock(x + dx, baseHeight + 30, z + dz, "light_gray_concrete");
            }
        }
        
        // Rotor blades (simplified)
        setBlock(x + 3, baseHeight + 30, z, "white_concrete");
        setBlock(x - 3, baseHeight + 30, z, "white_concrete");
        setBlock(x, baseHeight + 30, z + 3, "white_concrete");
        setBlock(x, baseHeight + 30, z - 3, "white_concrete");
    }
    
    // Utility methods
    protected double getPerlinNoise(double x, double z) {
        // Simplified Perlin noise implementation
        int xi = (int) Math.floor(x);
        int zi = (int) Math.floor(z);
        double xf = x - xi;
        double zf = z - zi;
        
        double u = fade(xf);
        double v = fade(zf);
        
        int a = hash(xi) + zi;
        int b = hash(xi + 1) + zi;
        
        return lerp(v, lerp(u, grad(hash(a), xf, zf), grad(hash(b), xf - 1, zf)),
                       lerp(u, grad(hash(a + 1), xf, zf - 1), grad(hash(b + 1), xf - 1, zf - 1)));
    }
    
    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }
    
    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }
    
    private double grad(int hash, double x, double z) {
        int h = hash & 15;
        double u = h < 8 ? x : z;
        double v = h < 4 ? z : h == 12 || h == 14 ? x : 0;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
    
    private int hash(int x) {
        x = ((x >> 16) ^ x) * 0x45d9f3b;
        x = ((x >> 16) ^ x) * 0x45d9f3b;
        x = (x >> 16) ^ x;
        return x & 0x7fffffff;
    }
    
    // Pollution source class
    public static class PollutionSource {
        public final BlockPos position;
        public final String sourceType;
        public final double radius;
        public boolean active = true;
        
        public PollutionSource(BlockPos pos, String type, double radius) {
            this.position = pos;
            this.sourceType = type;
            this.radius = radius;
        }
    }
    
    // Additional specialized building methods for different regions
    protected void generateHistoricBuilding(int x, int z, int baseHeight, int height, Random random) {
        for (int dx = 0; dx < 12; dx++) {
            for (int dz = 0; dz < 10; dz++) {
                for (int dy = 0; dy < height; dy++) {
                    if (dx == 0 || dx == 11 || dz == 0 || dz == 9 || dy == 0 || dy == height - 1) {
                        setBlock(x + dx, baseHeight + dy, z + dz, "sandstone");
                    }
                }
            }
        }
        
        // Ornate windows
        for (int floor = 2; floor < height - 1; floor += 4) {
            for (int window = 2; window < 10; window += 3) {
                setBlock(x + window, baseHeight + floor, z, "glass");
                setBlock(x + window, baseHeight + floor + 1, z, "glass");
            }
        }
    }
    
    protected void generateDome(int x, int z, int baseHeight, int radius, String material) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double distance = Math.sqrt(dx * dx + dz * dz);
                if (distance <= radius) {
                    int domeHeight = (int) Math.sqrt(radius * radius - distance * distance);
                    for (int dy = 0; dy <= domeHeight; dy++) {
                        double sphereDistance = Math.sqrt(dx * dx + dz * dz + dy * dy);
                        if (Math.abs(sphereDistance - radius) < 0.5) {
                            setBlock(x + dx, baseHeight + dy, z + dz, material);
                        }
                    }
                }
            }
        }
    }
    
    public String getRegionId() {
        return regionId;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}