package com.cleanairheroes.mission;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public abstract class MiniGame {
    protected final String gameId;
    protected final String name;
    protected final String description;
    protected final int duration; // in seconds
    protected final Mission.MissionType relatedMission;
    
    protected boolean isActive = false;
    protected boolean isCompleted = false;
    protected int score = 0;
    protected long startTime = 0;
    
    public MiniGame(String gameId, String name, String description, int duration, Mission.MissionType relatedMission) {
        this.gameId = gameId;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.relatedMission = relatedMission;
    }
    
    public abstract void startGame(ServerPlayerEntity player, World world, BlockPos location);
    public abstract void updateGame(ServerPlayerEntity player, World world);
    public abstract void endGame(ServerPlayerEntity player, boolean success);
    public abstract boolean checkWinCondition();
    
    protected boolean isTimeUp() {
        return (System.currentTimeMillis() - startTime) / 1000 >= duration;
    }
    
    protected int getRemainingTime() {
        return Math.max(0, duration - (int)((System.currentTimeMillis() - startTime) / 1000));
    }
    
    // Ship Emission Control Mini-Game
    public static class ShipEmissionControlGame extends MiniGame {
        private List<BlockPos> pollutionSources = new ArrayList<>();
        private int cleanedSources = 0;
        private int totalSources = 5;
        
        public ShipEmissionControlGame() {
            super("ship_emission_control", "Ship Emission Control", 
                  "Click on ships to install emission filters and clear pollution clouds", 
                  60, Mission.MissionType.PORT_CLEANUP);
        }
        
        @Override
        public void startGame(ServerPlayerEntity player, World world, BlockPos location) {
            isActive = true;
            startTime = System.currentTimeMillis();
            score = 0;
            cleanedSources = 0;
            
            // Generate pollution sources around the port
            Random random = new Random();
            pollutionSources.clear();
            
            for (int i = 0; i < totalSources; i++) {
                int x = location.getX() + random.nextInt(200) - 100;
                int z = location.getZ() + random.nextInt(100) - 50;
                pollutionSources.add(new BlockPos(x, location.getY(), z));
                
                // Spawn visual pollution particles at these locations
                spawnPollutionCloud(world, new BlockPos(x, location.getY() + 10, z));
            }
            
            player.sendMessage(Text.literal("=== SHIP EMISSION CONTROL ===").formatted(Formatting.GOLD, Formatting.BOLD), false);
            player.sendMessage(Text.literal("Clear " + totalSources + " pollution sources by installing ship filters!").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("Time limit: " + duration + " seconds").formatted(Formatting.GRAY), false);
        }
        
        @Override
        public void updateGame(ServerPlayerEntity player, World world) {
            if (!isActive || isCompleted) return;
            
            // Send progress update every 10 seconds
            if ((System.currentTimeMillis() - startTime) % 10000 < 1000) {
                int remaining = getRemainingTime();
                player.sendMessage(Text.literal("Progress: " + cleanedSources + "/" + totalSources + 
                    " | Time: " + remaining + "s").formatted(Formatting.AQUA), true);
            }
            
            if (checkWinCondition()) {
                endGame(player, true);
            } else if (isTimeUp()) {
                endGame(player, false);
            }
        }
        
        public boolean cleanPollutionSource(BlockPos clickedPos) {
            for (BlockPos source : pollutionSources) {
                if (source.isWithinDistance(clickedPos, 10)) {
                    pollutionSources.remove(source);
                    cleanedSources++;
                    score += 20;
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public void endGame(ServerPlayerEntity player, boolean success) {
            isActive = false;
            isCompleted = success;
            
            if (success) {
                player.sendMessage(Text.literal("🎉 SUCCESS! 🎉").formatted(Formatting.GREEN, Formatting.BOLD), true);
                player.sendMessage(Text.literal("All ship emission filters installed!").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("Score: " + score + " points").formatted(Formatting.GOLD), false);
            } else {
                player.sendMessage(Text.literal("⏰ TIME UP!").formatted(Formatting.RED, Formatting.BOLD), true);
                player.sendMessage(Text.literal("Cleaned " + cleanedSources + "/" + totalSources + " sources").formatted(Formatting.YELLOW), false);
            }
        }
        
        @Override
        public boolean checkWinCondition() {
            return cleanedSources >= totalSources;
        }
        
        private void spawnPollutionCloud(World world, BlockPos pos) {
            // This would spawn dark smoke particles
            // Implementation depends on Minecraft version and particle system
        }
    }
    
    // Green Transport Network Puzzle
    public static class GreenTransportPuzzle extends MiniGame {
        private int[][] grid = new int[10][10]; // 0 = empty, 1 = bus stop, 2 = bike lane, 3 = charging station
        private List<BlockPos> requiredConnections = new ArrayList<>();
        private boolean isNetworkComplete = false;
        
        public GreenTransportPuzzle() {
            super("green_transport_puzzle", "Green Transport Network", 
                  "Create an efficient network connecting bus stops, bike lanes, and charging stations", 
                  120, Mission.MissionType.GREEN_TRANSPORT);
        }
        
        @Override
        public void startGame(ServerPlayerEntity player, World world, BlockPos location) {
            isActive = true;
            startTime = System.currentTimeMillis();
            score = 0;
            
            // Initialize grid with some pre-placed elements
            initializeGrid();
            
            player.sendMessage(Text.literal("=== GREEN TRANSPORT NETWORK ===").formatted(Formatting.GREEN, Formatting.BOLD), false);
            player.sendMessage(Text.literal("Connect all transport nodes efficiently!").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("Right-click to place bus stops, bike lanes, and charging stations").formatted(Formatting.GRAY), false);
            
            displayGrid(player);
        }
        
        private void initializeGrid() {
            Random random = new Random();
            // Place some initial bus stops
            for (int i = 0; i < 3; i++) {
                int x = random.nextInt(10);
                int z = random.nextInt(10);
                grid[x][z] = 1; // Bus stop
                requiredConnections.add(new BlockPos(x, 0, z));
            }
        }
        
        private void displayGrid(ServerPlayerEntity player) {
            player.sendMessage(Text.literal("Current Grid:").formatted(Formatting.AQUA), false);
            for (int z = 0; z < 10; z++) {
                StringBuilder row = new StringBuilder();
                for (int x = 0; x < 10; x++) {
                    switch (grid[x][z]) {
                        case 0: row.append("▢ "); break; // Empty
                        case 1: row.append("🚌 "); break; // Bus stop
                        case 2: row.append("🚴 "); break; // Bike lane
                        case 3: row.append("⚡ "); break; // Charging station
                    }
                }
                player.sendMessage(Text.literal(row.toString()).formatted(Formatting.WHITE), false);
            }
        }
        
        @Override
        public void updateGame(ServerPlayerEntity player, World world) {
            if (!isActive || isCompleted) return;
            
            if (isTimeUp()) {
                endGame(player, false);
            } else if (checkWinCondition()) {
                endGame(player, true);
            }
        }
        
        @Override
        public void endGame(ServerPlayerEntity player, boolean success) {
            isActive = false;
            isCompleted = success;
            
            if (success) {
                player.sendMessage(Text.literal("🎉 NETWORK COMPLETE! 🎉").formatted(Formatting.GREEN, Formatting.BOLD), true);
                player.sendMessage(Text.literal("Efficient green transport network created!").formatted(Formatting.GREEN), false);
                score = 100;
            } else {
                player.sendMessage(Text.literal("⏰ TIME UP!").formatted(Formatting.RED, Formatting.BOLD), true);
                player.sendMessage(Text.literal("Network incomplete - keep working on connections!").formatted(Formatting.YELLOW), false);
            }
        }
        
        @Override
        public boolean checkWinCondition() {
            // Check if all required connections are made
            return isNetworkEfficient() && hasRequiredElements();
        }
        
        private boolean isNetworkEfficient() {
            // Simple check: ensure there's a path between major nodes
            return requiredConnections.size() >= 3;
        }
        
        private boolean hasRequiredElements() {
            int busStops = 0, bikeLines = 0, chargingStations = 0;
            
            for (int x = 0; x < 10; x++) {
                for (int z = 0; z < 10; z++) {
                    switch (grid[x][z]) {
                        case 1: busStops++; break;
                        case 2: bikeLines++; break;
                        case 3: chargingStations++; break;
                    }
                }
            }
            
            return busStops >= 3 && bikeLines >= 5 && chargingStations >= 2;
        }
        
        public boolean placeElement(int x, int z, int elementType) {
            if (x >= 0 && x < 10 && z >= 0 && z < 10 && grid[x][z] == 0) {
                grid[x][z] = elementType;
                return true;
            }
            return false;
        }
    }
    
    // Air Quality Monitor Game
    public static class AirQualityMonitorGame extends MiniGame {
        private List<BlockPos> monitoringPoints = new ArrayList<>();
        private List<Double> pollutionLevels = new ArrayList<>();
        private int correctReadings = 0;
        private int totalReadings = 8;
        
        public AirQualityMonitorGame() {
            super("air_quality_monitor", "Air Quality Monitoring", 
                  "Take accurate air quality readings at different locations", 
                  90, Mission.MissionType.AIR_QUALITY_MONITORING);
        }
        
        @Override
        public void startGame(ServerPlayerEntity player, World world, BlockPos location) {
            isActive = true;
            startTime = System.currentTimeMillis();
            score = 0;
            correctReadings = 0;
            
            // Generate monitoring points with different pollution levels
            Random random = new Random();
            monitoringPoints.clear();
            pollutionLevels.clear();
            
            for (int i = 0; i < totalReadings; i++) {
                int x = location.getX() + random.nextInt(300) - 150;
                int z = location.getZ() + random.nextInt(300) - 150;
                monitoringPoints.add(new BlockPos(x, location.getY(), z));
                
                // Generate pollution level based on distance from pollution sources
                double pollutionLevel = 50 + random.nextDouble() * 200; // AQI 50-250
                pollutionLevels.add(pollutionLevel);
            }
            
            player.sendMessage(Text.literal("=== AIR QUALITY MONITORING ===").formatted(Formatting.BLUE, Formatting.BOLD), false);
            player.sendMessage(Text.literal("Visit " + totalReadings + " monitoring points and record AQI levels!").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("Use your air quality meter (right-click) at each location").formatted(Formatting.GRAY), false);
        }
        
        @Override
        public void updateGame(ServerPlayerEntity player, World world) {
            if (!isActive || isCompleted) return;
            
            int remaining = getRemainingTime();
            if (remaining % 15 == 0) { // Update every 15 seconds
                player.sendMessage(Text.literal("Readings: " + correctReadings + "/" + totalReadings + 
                    " | Time: " + remaining + "s").formatted(Formatting.BLUE), true);
            }
            
            if (checkWinCondition()) {
                endGame(player, true);
            } else if (isTimeUp()) {
                endGame(player, false);
            }
        }
        
        public boolean takeMeasurement(BlockPos playerPos, double measuredValue) {
            for (int i = 0; i < monitoringPoints.size(); i++) {
                BlockPos point = monitoringPoints.get(i);
                if (point.isWithinDistance(playerPos, 10)) {
                    double actualValue = pollutionLevels.get(i);
                    double accuracy = 1.0 - Math.abs(measuredValue - actualValue) / actualValue;
                    
                    if (accuracy >= 0.8) { // 80% accuracy required
                        correctReadings++;
                        score += 15;
                        return true;
                    }
                    break;
                }
            }
            return false;
        }
        
        @Override
        public void endGame(ServerPlayerEntity player, boolean success) {
            isActive = false;
            isCompleted = success;
            
            if (success) {
                player.sendMessage(Text.literal("📊 MONITORING COMPLETE! 📊").formatted(Formatting.GREEN, Formatting.BOLD), true);
                player.sendMessage(Text.literal("All air quality readings recorded accurately!").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("Data Score: " + score + " points").formatted(Formatting.GOLD), false);
            } else {
                player.sendMessage(Text.literal("⏰ MONITORING INCOMPLETE!").formatted(Formatting.RED, Formatting.BOLD), true);
                player.sendMessage(Text.literal("Recorded " + correctReadings + "/" + totalReadings + " accurate readings").formatted(Formatting.YELLOW), false);
            }
        }
        
        @Override
        public boolean checkWinCondition() {
            return correctReadings >= totalReadings;
        }
    }
    
    // Factory emission puzzle - match pollution sources with appropriate filters
    public static class FactoryEmissionPuzzle extends MiniGame {
        private List<String> pollutionTypes = List.of("PM2.5", "SO2", "NOx", "CO", "VOCs");
        private List<String> filterTypes = List.of("Baghouse", "Scrubber", "Catalytic", "Absorber", "Thermal");
        private List<Integer> correctMatches = List.of(0, 1, 2, 3, 4); // Correct filter for each pollution type
        private List<Integer> playerMatches = new ArrayList<>();
        
        public FactoryEmissionPuzzle() {
            super("factory_emission_puzzle", "Industrial Filter Installation", 
                  "Match the correct filtration system to each type of industrial emission", 
                  100, Mission.MissionType.INDUSTRIAL_FILTERS);
        }
        
        @Override
        public void startGame(ServerPlayerEntity player, World world, BlockPos location) {
            isActive = true;
            startTime = System.currentTimeMillis();
            score = 0;
            playerMatches.clear();
            
            // Fill with -1 (no selection)
            for (int i = 0; i < pollutionTypes.size(); i++) {
                playerMatches.add(-1);
            }
            
            player.sendMessage(Text.literal("=== INDUSTRIAL FILTER INSTALLATION ===").formatted(Formatting.RED, Formatting.BOLD), false);
            player.sendMessage(Text.literal("Match each pollution type with its optimal filter:").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal(""), false);
            
            displayPuzzle(player);
        }
        
        private void displayPuzzle(ServerPlayerEntity player) {
            player.sendMessage(Text.literal("Pollution Types:").formatted(Formatting.RED), false);
            for (int i = 0; i < pollutionTypes.size(); i++) {
                String match = playerMatches.get(i) >= 0 ? filterTypes.get(playerMatches.get(i)) : "Not Selected";
                player.sendMessage(Text.literal((i+1) + ". " + pollutionTypes.get(i) + " → " + match)
                    .formatted(Formatting.WHITE), false);
            }
            
            player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("Available Filters:").formatted(Formatting.GREEN), false);
            for (int i = 0; i < filterTypes.size(); i++) {
                player.sendMessage(Text.literal((char)('A' + i) + ". " + filterTypes.get(i))
                    .formatted(Formatting.GREEN), false);
            }
        }
        
        @Override
        public void updateGame(ServerPlayerEntity player, World world) {
            if (!isActive || isCompleted) return;
            
            if (checkWinCondition()) {
                endGame(player, true);
            } else if (isTimeUp()) {
                endGame(player, false);
            }
        }
        
        public boolean makeMatch(int pollutionIndex, int filterIndex) {
            if (pollutionIndex >= 0 && pollutionIndex < pollutionTypes.size() && 
                filterIndex >= 0 && filterIndex < filterTypes.size()) {
                playerMatches.set(pollutionIndex, filterIndex);
                
                if (filterIndex == correctMatches.get(pollutionIndex)) {
                    score += 20;
                }
                
                return true;
            }
            return false;
        }
        
        @Override
        public void endGame(ServerPlayerEntity player, boolean success) {
            isActive = false;
            isCompleted = success;
            
            int correctCount = 0;
            for (int i = 0; i < pollutionTypes.size(); i++) {
                if (playerMatches.get(i).equals(correctMatches.get(i))) {
                    correctCount++;
                }
            }
            
            if (success) {
                player.sendMessage(Text.literal("🏭 PERFECT FILTRATION! 🏭").formatted(Formatting.GREEN, Formatting.BOLD), true);
                player.sendMessage(Text.literal("All pollution types correctly matched with filters!").formatted(Formatting.GREEN), false);
            } else {
                player.sendMessage(Text.literal("⏰ INSTALLATION INCOMPLETE!").formatted(Formatting.RED, Formatting.BOLD), true);
                player.sendMessage(Text.literal("Correct matches: " + correctCount + "/" + pollutionTypes.size()).formatted(Formatting.YELLOW), false);
            }
        }
        
        @Override
        public boolean checkWinCondition() {
            for (int i = 0; i < pollutionTypes.size(); i++) {
                if (!playerMatches.get(i).equals(correctMatches.get(i))) {
                    return false;
                }
            }
            return true;
        }
    }
    
    // Getters
    public String getGameId() { return gameId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isActive() { return isActive; }
    public boolean isCompleted() { return isCompleted; }
    public int getScore() { return score; }
    public Mission.MissionType getRelatedMission() { return relatedMission; }
}