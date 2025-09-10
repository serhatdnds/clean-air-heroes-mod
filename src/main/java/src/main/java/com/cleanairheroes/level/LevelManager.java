package com.cleanairheroes.level;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentState;

import java.util.*;

public class LevelManager extends PersistentState {
    private static final String DATA_NAME = "cleanairheroes_levels";
    
    private Map<UUID, LevelProgress> playerProgress = new HashMap<>();
    private List<Level> levels = new ArrayList<>();
    
    public LevelManager() {
        initializeLevels();
    }
    
    private void initializeLevels() {
        Level varnaLevel = new Level(
            "bulgaria_varna",
            "Bulgaria - Varna (BATTI)",
            "Coastal city fighting maritime and industrial pollution",
            Level.LevelType.REGIONAL,
            "varna",
            500
        );
        varnaLevel.addMission(new Mission("port_cleanup", "Port Emission Control", "Install ship filters in the harbor", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 100));
        varnaLevel.addMission(new Mission("green_transport", "Green Transport Network", "Build electric bus stops and bike lanes", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 150));
        varnaLevel.addMission(new Mission("smart_heating", "Smart Heating Systems", "Convert solid fuel systems to renewable energy", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 200));
        varnaLevel.addMission(new Mission("industrial_filters", "Industrial Filtration", "Install factory filtering systems", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 250));
        levels.add(varnaLevel);
        
        Level zonguldakLevel = new Level(
            "turkey_zonguldak",
            "Turkey - Zonguldak",
            "Coal mining region with severe air quality issues",
            Level.LevelType.REGIONAL,
            "zonguldak",
            700
        );
        zonguldakLevel.addMission(new Mission("mine_dust_control", "Mine Dust Control", "Install dust emission reduction systems", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 150));
        zonguldakLevel.addMission(new Mission("power_plant_upgrade", "Power Plant Modernization", "Upgrade thermal plants with filters", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 300));
        zonguldakLevel.addMission(new Mission("green_corridor", "Green Corridor", "Plant trees for natural filtering", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 200));
        zonguldakLevel.addMission(new Mission("electric_fleet", "Electric Transport Fleet", "Convert city transport to electric", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 250));
        levels.add(zonguldakLevel);
        
        Level odesaLevel = new Level(
            "ukraine_odesa",
            "Ukraine - Odesa",
            "Port city with industrial and maritime pollution",
            Level.LevelType.REGIONAL,
            "odesa",
            600
        );
        odesaLevel.addMission(new Mission("maritime_optimization", "Maritime Trade Optimization", "Make port activities eco-friendly", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 200));
        odesaLevel.addMission(new Mission("industrial_modernization", "Industrial Modernization", "Reduce factory emissions", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 250));
        odesaLevel.addMission(new Mission("urban_greening", "Urban Green Planning", "Create parks and green spaces", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 150));
        odesaLevel.addMission(new Mission("monitoring_network", "Air Quality Monitoring", "Install monitoring stations", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 100));
        levels.add(odesaLevel);
        
        Level trabzonLevel = new Level(
            "turkey_trabzon",
            "Turkey - Trabzon (DOKA)",
            "City struggling with traffic and industrial pollution",
            Level.LevelType.REGIONAL,
            "trabzon",
            800
        );
        trabzonLevel.addMission(new Mission("smart_traffic", "Smart Traffic Systems", "Optimize traffic flow", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 200));
        trabzonLevel.addMission(new Mission("industrial_cleaning", "Industrial Cleaning", "Install modern filters in factories", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 250));
        trabzonLevel.addMission(new Mission("home_heating", "Home Heating Conversion", "Switch to clean energy sources", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 300));
        trabzonLevel.addMission(new Mission("green_belt", "Green Belt Project", "Create natural air filters", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 200));
        trabzonLevel.addMission(new Mission("recycling_center", "Recycling Center", "Build waste management system", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 150));
        levels.add(trabzonLevel);
        
        Level romaniaLevel = new Level(
            "romania_southeast",
            "Southeast Romania (ATG)",
            "Industrial region with critical pollution levels",
            Level.LevelType.REGIONAL,
            "romania",
            1200
        );
        romaniaLevel.addMission(new Mission("clean_industry", "Clean Industry Transformation", "Modernize industrial facilities", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 400));
        romaniaLevel.addMission(new Mission("sustainable_transport", "Sustainable Transport Network", "Develop public transport", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 300));
        romaniaLevel.addMission(new Mission("agricultural_improvement", "Agricultural Improvement", "Support sustainable farming", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 200));
        romaniaLevel.addMission(new Mission("energy_reform", "Energy Production Reform", "Convert coal plants to renewable", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 500));
        romaniaLevel.addMission(new Mission("monitoring_center", "Regional Monitoring Center", "Build comprehensive monitoring", com.cleanairheroes.mission.MissionType.MAIN_OBJECTIVE, 250));
        levels.add(romaniaLevel);
    }
    
    public void initialize(MinecraftServer server) {
    }
    
    public void tick(MinecraftServer server) {
    }
    
    public boolean startLevel(ServerPlayerEntity player, int levelIndex) {
        if (levelIndex < 0 || levelIndex >= levels.size()) {
            return false;
        }
        
        UUID playerId = player.getUuid();
        LevelProgress progress = playerProgress.computeIfAbsent(playerId, k -> new LevelProgress());
        
        if (levelIndex > 0 && !progress.isLevelCompleted(levelIndex - 1)) {
            player.sendMessage(Text.literal("You must complete the previous level first!").formatted(Formatting.RED), false);
            return false;
        }
        
        progress.setCurrentLevel(levelIndex);
        Level level = levels.get(levelIndex);
        
        player.sendMessage(Text.literal("=== " + level.getDisplayName() + " ===").formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal(level.getDescription()).formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal("Missions:").formatted(Formatting.YELLOW), false);
        
        for (Mission mission : level.getMissions()) {
            player.sendMessage(Text.literal("- " + mission.getName() + " (" + mission.getPoints() + " points)")
                .formatted(Formatting.AQUA), false);
        }
        
        markDirty();
        return true;
    }
    
    public void completeMission(ServerPlayerEntity player, String missionId) {
        UUID playerId = player.getUuid();
        LevelProgress progress = playerProgress.get(playerId);
        
        if (progress == null) return;
        
        int currentLevel = progress.getCurrentLevel();
        if (currentLevel < 0 || currentLevel >= levels.size()) return;
        
        Level level = levels.get(currentLevel);
        Mission mission = level.getMissionById(missionId);
        
        if (mission == null) return;
        
        if (progress.completeMission(currentLevel, missionId)) {
            progress.addPoints(mission.getPoints());
            player.sendMessage(Text.literal("Mission Complete: " + mission.getName() + " (+" + mission.getPoints() + " points)")
                .formatted(Formatting.GREEN), false);
            
            if (progress.isLevelCompleted(currentLevel, level.getMissions().size())) {
                player.sendMessage(Text.literal("LEVEL COMPLETE! " + level.getDisplayName())
                    .formatted(Formatting.GOLD, Formatting.BOLD), true);
                
                if (currentLevel == levels.size() - 1) {
                    player.sendMessage(Text.literal("Congratulations! You are now a CLEAN AIR HERO!")
                        .formatted(Formatting.GOLD, Formatting.BOLD), false);
                }
            }
            
            markDirty();
        }
    }
    
    public Level getCurrentLevel(ServerPlayerEntity player) {
        LevelProgress progress = playerProgress.get(player.getUuid());
        if (progress == null || progress.getCurrentLevel() < 0) return null;
        return levels.get(progress.getCurrentLevel());
    }
    
    public LevelProgress getPlayerProgress(ServerPlayerEntity player) {
        return playerProgress.computeIfAbsent(player.getUuid(), k -> new LevelProgress());
    }
    
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound playersNbt = new NbtCompound();
        playerProgress.forEach((uuid, progress) -> {
            playersNbt.put(uuid.toString(), progress.toNbt());
        });
        nbt.put("PlayerProgress", playersNbt);
        return nbt;
    }
    
    public static LevelManager fromNbt(NbtCompound nbt) {
        LevelManager manager = new LevelManager();
        NbtCompound playersNbt = nbt.getCompound("PlayerProgress");
        
        for (String key : playersNbt.getKeys()) {
            UUID uuid = UUID.fromString(key);
            manager.playerProgress.put(uuid, LevelProgress.fromNbt(playersNbt.getCompound(key)));
        }
        
        return manager;
    }
}