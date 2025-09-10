package com.cleanairheroes.pollution;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PollutionManager {
    
    private final Map<ChunkPos, PollutionData> pollutionMap = new ConcurrentHashMap<>();
    private final Map<String, List<PollutionSource>> regionSources = new HashMap<>();
    private final Map<UUID, PlayerExposure> playerExposure = new HashMap<>();
    
    private static final double DIFFUSION_RATE = 0.15;
    private static final double DECAY_RATE = 0.02;
    private static final double WIND_EFFECT = 0.1;
    private static final int UPDATE_RADIUS = 10;
    
    public enum PollutionType {
        PM25("PM2.5", 0xFF8B4513, 15.0),
        PM10("PM10", 0xFF696969, 45.0),
        NO2("NO2", 0xFF8B0000, 40.0),
        SO2("SO2", 0xFFFFFF00, 20.0),
        CO("CO", 0xFF000000, 10.0),
        O3("Ozone", 0xFF87CEEB, 100.0);
        
        private final String name;
        private final int particleColor;
        private final double safeLimit;
        
        PollutionType(String name, int particleColor, double safeLimit) {
            this.name = name;
            this.particleColor = particleColor;
            this.safeLimit = safeLimit;
        }
    }
    
    public static class PollutionData {
        private final Map<PollutionType, Double> levels = new EnumMap<>(PollutionType.class);
        private double totalAQI = 0;
        private long lastUpdate = 0;
        
        public PollutionData() {
            for (PollutionType type : PollutionType.values()) {
                levels.put(type, 0.0);
            }
        }
        
        public void addPollution(PollutionType type, double amount) {
            levels.put(type, Math.min(500, levels.get(type) + amount));
            recalculateAQI();
        }
        
        public void reducePollution(PollutionType type, double amount) {
            levels.put(type, Math.max(0, levels.get(type) - amount));
            recalculateAQI();
        }
        
        private void recalculateAQI() {
            double maxAQI = 0;
            for (Map.Entry<PollutionType, Double> entry : levels.entrySet()) {
                double concentration = entry.getValue();
                double safeLimit = entry.getKey().safeLimit;
                double typeAQI = (concentration / safeLimit) * 50;
                maxAQI = Math.max(maxAQI, typeAQI);
            }
            totalAQI = maxAQI;
        }
        
        public double getAQI() {
            return totalAQI;
        }
        
        public String getAQICategory() {
            if (totalAQI <= 50) return "Good";
            if (totalAQI <= 100) return "Moderate";
            if (totalAQI <= 150) return "Unhealthy for Sensitive Groups";
            if (totalAQI <= 200) return "Unhealthy";
            if (totalAQI <= 300) return "Very Unhealthy";
            return "Hazardous";
        }
        
        public int getAQIColor() {
            if (totalAQI <= 50) return 0xFF00FF00;
            if (totalAQI <= 100) return 0xFFFFFF00;
            if (totalAQI <= 150) return 0xFFFFA500;
            if (totalAQI <= 200) return 0xFFFF0000;
            if (totalAQI <= 300) return 0xFF8B008B;
            return 0xFF800000;
        }
    }
    
    public static class PollutionSource {
        public final BlockPos position;
        public final String sourceType;
        public final Map<PollutionType, Double> emissions;
        public final double radius;
        public boolean active = true;
        
        public PollutionSource(BlockPos pos, String type, double radius) {
            this.position = pos;
            this.sourceType = type;
            this.radius = radius;
            this.emissions = new EnumMap<>(PollutionType.class);
            initializeEmissions();
        }
        
        private void initializeEmissions() {
            switch (sourceType) {
                case "SHIP_EMISSIONS":
                    emissions.put(PollutionType.SO2, 15.0);
                    emissions.put(PollutionType.NO2, 12.0);
                    emissions.put(PollutionType.PM10, 8.0);
                    emissions.put(PollutionType.PM25, 5.0);
                    break;
                    
                case "INDUSTRIAL":
                    emissions.put(PollutionType.PM10, 20.0);
                    emissions.put(PollutionType.PM25, 15.0);
                    emissions.put(PollutionType.SO2, 18.0);
                    emissions.put(PollutionType.NO2, 10.0);
                    emissions.put(PollutionType.CO, 8.0);
                    break;
                    
                case "TRAFFIC":
                    emissions.put(PollutionType.NO2, 25.0);
                    emissions.put(PollutionType.CO, 20.0);
                    emissions.put(PollutionType.PM25, 10.0);
                    emissions.put(PollutionType.O3, 5.0);
                    break;
                    
                case "POWER_PLANT":
                    emissions.put(PollutionType.SO2, 30.0);
                    emissions.put(PollutionType.NO2, 25.0);
                    emissions.put(PollutionType.PM10, 20.0);
                    emissions.put(PollutionType.PM25, 15.0);
                    emissions.put(PollutionType.CO, 10.0);
                    break;
                    
                case "MINING_DUST":
                    emissions.put(PollutionType.PM10, 35.0);
                    emissions.put(PollutionType.PM25, 25.0);
                    emissions.put(PollutionType.SO2, 5.0);
                    break;
                    
                case "HEATING":
                    emissions.put(PollutionType.PM25, 12.0);
                    emissions.put(PollutionType.CO, 15.0);
                    emissions.put(PollutionType.SO2, 8.0);
                    emissions.put(PollutionType.NO2, 6.0);
                    break;
                    
                default:
                    emissions.put(PollutionType.PM10, 10.0);
                    emissions.put(PollutionType.PM25, 5.0);
            }
        }
    }
    
    public static class PlayerExposure {
        public double totalExposure = 0;
        public long exposureTime = 0;
        public Map<PollutionType, Double> typeExposure = new EnumMap<>(PollutionType.class);
        
        public PlayerExposure() {
            for (PollutionType type : PollutionType.values()) {
                typeExposure.put(type, 0.0);
            }
        }
    }
    
    public void initialize(MinecraftServer server) {
        loadPollutionSources();
    }
    
    private void loadPollutionSources() {
        regionSources.put("varna", Arrays.asList(
            new PollutionSource(new BlockPos(0, 65, 0), "SHIP_EMISSIONS", 150),
            new PollutionSource(new BlockPos(-100, 65, 0), "SHIP_EMISSIONS", 120),
            new PollutionSource(new BlockPos(100, 65, 0), "SHIP_EMISSIONS", 120),
            new PollutionSource(new BlockPos(0, 70, 450), "INDUSTRIAL", 200),
            new PollutionSource(new BlockPos(-150, 70, 450), "INDUSTRIAL", 180),
            new PollutionSource(new BlockPos(150, 70, 450), "INDUSTRIAL", 180),
            new PollutionSource(new BlockPos(0, 67, 100), "TRAFFIC", 100),
            new PollutionSource(new BlockPos(50, 67, 150), "HEATING", 80)
        ));
        
        regionSources.put("zonguldak", Arrays.asList(
            new PollutionSource(new BlockPos(0, 67, 0), "SHIP_EMISSIONS", 100),
            new PollutionSource(new BlockPos(-128, 130, 800), "POWER_PLANT", 500),
            new PollutionSource(new BlockPos(192, 140, 720), "INDUSTRIAL", 400),
            new PollutionSource(new BlockPos(0, 150, 900), "MINING_DUST", 300),
            new PollutionSource(new BlockPos(-200, 150, 900), "MINING_DUST", 250),
            new PollutionSource(new BlockPos(200, 150, 900), "MINING_DUST", 250),
            new PollutionSource(new BlockPos(0, 82, 50), "TRAFFIC", 150),
            new PollutionSource(new BlockPos(0, 97, 100), "HEATING", 100)
        ));
    }
    
    public void tick(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            updatePollutionSources(world);
            diffusePollution(world);
            applyDecay(world);
            updateVisualEffects(world);
            trackPlayerExposure(world);
        }
    }
    
    private void updatePollutionSources(ServerWorld world) {
        String regionName = getCurrentRegion(world);
        List<PollutionSource> sources = regionSources.get(regionName);
        
        if (sources == null) return;
        
        for (PollutionSource source : sources) {
            if (!source.active) continue;
            
            ChunkPos chunkPos = new ChunkPos(source.position);
            PollutionData data = pollutionMap.computeIfAbsent(chunkPos, k -> new PollutionData());
            
            for (Map.Entry<PollutionType, Double> emission : source.emissions.entrySet()) {
                double amount = emission.getValue() * getTimeOfDayModifier(world) * getWeatherModifier(world);
                data.addPollution(emission.getKey(), amount * 0.1);
            }
            
            spreadPollution(world, source.position, source.emissions, source.radius);
        }
    }
    
    private void spreadPollution(ServerWorld world, BlockPos center, Map<PollutionType, Double> emissions, double radius) {
        int chunkRadius = (int) Math.ceil(radius / 16);
        ChunkPos centerChunk = new ChunkPos(center);
        
        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                ChunkPos targetChunk = new ChunkPos(centerChunk.x + dx, centerChunk.z + dz);
                double distance = Math.sqrt(dx * dx + dz * dz) * 16;
                
                if (distance <= radius) {
                    double factor = 1.0 - (distance / radius);
                    factor *= factor;
                    
                    PollutionData data = pollutionMap.computeIfAbsent(targetChunk, k -> new PollutionData());
                    
                    for (Map.Entry<PollutionType, Double> emission : emissions.entrySet()) {
                        data.addPollution(emission.getKey(), emission.getValue() * factor * 0.05);
                    }
                }
            }
        }
    }
    
    private void diffusePollution(ServerWorld world) {
        Map<ChunkPos, PollutionData> newData = new HashMap<>();
        
        for (Map.Entry<ChunkPos, PollutionData> entry : pollutionMap.entrySet()) {
            ChunkPos pos = entry.getKey();
            PollutionData data = entry.getValue();
            
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0) continue;
                    
                    ChunkPos neighbor = new ChunkPos(pos.x + dx, pos.z + dz);
                    PollutionData neighborData = newData.computeIfAbsent(neighbor, k -> new PollutionData());
                    
                    for (PollutionType type : PollutionType.values()) {
                        double amount = data.levels.get(type) * DIFFUSION_RATE / 8;
                        neighborData.addPollution(type, amount);
                    }
                }
            }
        }
        
        for (Map.Entry<ChunkPos, PollutionData> entry : newData.entrySet()) {
            PollutionData existing = pollutionMap.get(entry.getKey());
            if (existing != null) {
                for (PollutionType type : PollutionType.values()) {
                    existing.addPollution(type, entry.getValue().levels.get(type));
                }
            } else {
                pollutionMap.put(entry.getKey(), entry.getValue());
            }
        }
    }
    
    private void applyDecay(ServerWorld world) {
        pollutionMap.values().forEach(data -> {
            for (PollutionType type : PollutionType.values()) {
                data.reducePollution(type, data.levels.get(type) * DECAY_RATE);
            }
        });
        
        pollutionMap.entrySet().removeIf(entry -> entry.getValue().getAQI() < 1.0);
    }
    
    private void updateVisualEffects(ServerWorld world) {
        Random random = new Random();
        
        for (Map.Entry<ChunkPos, PollutionData> entry : pollutionMap.entrySet()) {
            ChunkPos chunkPos = entry.getKey();
            PollutionData data = entry.getValue();
            
            if (data.getAQI() < 50) continue;
            
            int particleCount = (int)(data.getAQI() / 10);
            int centerX = chunkPos.getStartX() + 8;
            int centerZ = chunkPos.getStartZ() + 8;
            
            for (int i = 0; i < particleCount; i++) {
                double x = centerX + random.nextGaussian() * 8;
                double y = 64 + random.nextDouble() * 50;
                double z = centerZ + random.nextGaussian() * 8;
                
                int color = data.getAQIColor();
                float r = ((color >> 16) & 0xFF) / 255f;
                float g = ((color >> 8) & 0xFF) / 255f;
                float b = (color & 0xFF) / 255f;
                
                world.spawnParticles(
                    new DustParticleEffect(new Vector3f(r, g, b), 2.0f),
                    x, y, z,
                    1,
                    0.5, 0.5, 0.5,
                    0.01
                );
            }
        }
    }
    
    private void trackPlayerExposure(ServerWorld world) {
        world.getPlayers().forEach(player -> {
            ChunkPos chunkPos = new ChunkPos(player.getBlockPos());
            PollutionData data = pollutionMap.get(chunkPos);
            
            if (data != null && data.getAQI() > 50) {
                UUID playerId = player.getUuid();
                PlayerExposure exposure = playerExposure.computeIfAbsent(playerId, k -> new PlayerExposure());
                
                exposure.totalExposure += data.getAQI() * 0.05;
                exposure.exposureTime++;
                
                for (Map.Entry<PollutionType, Double> level : data.levels.entrySet()) {
                    exposure.typeExposure.merge(level.getKey(), level.getValue() * 0.05, Double::sum);
                }
                
                if (exposure.exposureTime % 200 == 0 && data.getAQI() > 100) {
                    player.sendMessage(
                        net.minecraft.text.Text.literal("⚠ Air Quality: " + data.getAQICategory() + " (AQI: " + String.format("%.0f", data.getAQI()) + ")")
                            .formatted(net.minecraft.util.Formatting.YELLOW),
                        true
                    );
                }
            }
        });
    }
    
    private double getTimeOfDayModifier(World world) {
        long timeOfDay = world.getTimeOfDay() % 24000;
        if (timeOfDay >= 6000 && timeOfDay <= 10000) {
            return 1.5;
        } else if (timeOfDay >= 16000 && timeOfDay <= 20000) {
            return 1.3;
        }
        return 1.0;
    }
    
    private double getWeatherModifier(World world) {
        if (world.isRaining()) return 0.7;
        if (world.isThundering()) return 0.5;
        return 1.0;
    }
    
    private String getCurrentRegion(World world) {
        return "varna";
    }
    
    public PollutionData getPollutionAt(BlockPos pos) {
        return pollutionMap.get(new ChunkPos(pos));
    }
    
    public double getAQIAt(BlockPos pos) {
        PollutionData data = getPollutionAt(pos);
        return data != null ? data.getAQI() : 0;
    }
    
    public void addPollutionSource(BlockPos pos, String type, double radius) {
        String region = "current";
        List<PollutionSource> sources = regionSources.computeIfAbsent(region, k -> new ArrayList<>());
        sources.add(new PollutionSource(pos, type, radius));
    }
    
    public void removePollutionSource(BlockPos pos) {
        for (List<PollutionSource> sources : regionSources.values()) {
            sources.removeIf(source -> source.position.equals(pos));
        }
    }
    
    public void reducePollutionInRadius(BlockPos center, double radius, double reductionPercent) {
        int chunkRadius = (int) Math.ceil(radius / 16);
        ChunkPos centerChunk = new ChunkPos(center);
        
        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                ChunkPos targetChunk = new ChunkPos(centerChunk.x + dx, centerChunk.z + dz);
                double distance = Math.sqrt(dx * dx + dz * dz) * 16;
                
                if (distance <= radius) {
                    PollutionData data = pollutionMap.get(targetChunk);
                    if (data != null) {
                        double factor = 1.0 - (distance / radius);
                        for (PollutionType type : PollutionType.values()) {
                            data.reducePollution(type, data.levels.get(type) * reductionPercent * factor);
                        }
                    }
                }
            }
        }
    }
}