package com.cleanairheroes.mission;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;

import java.util.List;
import java.util.ArrayList;

public class Mission {
    private final String missionId;
    private final String name;
    private final String description;
    private final int requiredScore;
    private final MissionType type;
    private final List<MissionObjective> objectives;
    private final List<String> rewards;
    
    private boolean isCompleted = false;
    private boolean isActive = false;
    private int currentProgress = 0;
    
    public enum MissionType {
        PORT_CLEANUP("Port Emissions Control"),
        GREEN_TRANSPORT("Green Transport Network"), 
        SMART_HEATING("Smart Heating Systems"),
        INDUSTRIAL_FILTERS("Industrial Filtration"),
        MINE_DUST_CONTROL("Mine Dust Control"),
        POWER_PLANT_UPGRADE("Power Plant Modernization"),
        GREEN_CORRIDOR("Green Corridor"),
        ELECTRIC_FLEET("Electric Transport Fleet"),
        MARITIME_OPTIMIZATION("Maritime Trade Optimization"),
        INDUSTRIAL_MODERNIZATION("Industrial Area Modernization"),
        URBAN_GREENING("Urban Green Planning"),
        AIR_QUALITY_MONITORING("Air Quality Monitoring Network"),
        SMART_TRAFFIC("Smart Traffic Systems"),
        INDUSTRIAL_CLEANING("Industrial Cleaning"),
        HOME_HEATING("Home Heating Conversion"),
        GREEN_BELT("Green Belt Project"),
        RECYCLING_CENTER("Recycling Center"),
        CLEAN_INDUSTRY("Clean Industry Transformation"),
        SUSTAINABLE_TRANSPORT("Sustainable Transport Network"),
        AGRICULTURAL_IMPROVEMENT("Agricultural Improvement"),
        ENERGY_REFORM("Energy Production Reform"),
        REGIONAL_MONITORING("Regional Monitoring Center");
        
        private final String displayName;
        
        MissionType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public static class MissionObjective {
        public final String id;
        public final String description;
        public final int requiredAmount;
        public final String targetType;
        public final BlockPos targetLocation;
        public boolean completed = false;
        public int progress = 0;
        
        public MissionObjective(String id, String description, int requiredAmount, String targetType, BlockPos targetLocation) {
            this.id = id;
            this.description = description;
            this.requiredAmount = requiredAmount;
            this.targetType = targetType;
            this.targetLocation = targetLocation;
        }
        
        public boolean isCompleted() {
            return progress >= requiredAmount;
        }
        
        public void addProgress(int amount) {
            progress = Math.min(progress + amount, requiredAmount);
            if (progress >= requiredAmount && !completed) {
                completed = true;
            }
        }
        
        public double getProgressPercentage() {
            return (double) progress / requiredAmount;
        }
    }
    
    public Mission(String missionId, String name, String description, MissionType type, int requiredScore) {
        this.missionId = missionId;
        this.name = name;
        this.description = description;
        this.type = type;
        this.requiredScore = requiredScore;
        this.objectives = new ArrayList<>();
        this.rewards = new ArrayList<>();
        
        initializeMissionObjectives();
    }
    
    private void initializeMissionObjectives() {
        switch (type) {
            case PORT_CLEANUP:
                objectives.add(new MissionObjective("install_ship_filters", "Install ship emission filters", 10, "SHIP_FILTER", new BlockPos(0, 65, 0)));
                objectives.add(new MissionObjective("reduce_port_emissions", "Reduce port emissions by 30%", 30, "EMISSION_REDUCTION", new BlockPos(0, 65, 0)));
                objectives.add(new MissionObjective("clean_harbor_water", "Clean harbor water pollution", 5, "WATER_TREATMENT", new BlockPos(0, 62, 0)));
                break;
                
            case GREEN_TRANSPORT:
                objectives.add(new MissionObjective("build_electric_bus_stops", "Build electric bus stops", 8, "BUS_STOP", new BlockPos(0, 67, 100)));
                objectives.add(new MissionObjective("create_bike_lanes", "Create bike lane network", 15, "BIKE_LANE", new BlockPos(0, 67, 100)));
                objectives.add(new MissionObjective("install_charging_stations", "Install EV charging stations", 6, "CHARGING_STATION", new BlockPos(0, 67, 100)));
                break;
                
            case SMART_HEATING:
                objectives.add(new MissionObjective("convert_solid_fuel", "Convert solid fuel heating systems", 20, "HEATING_CONVERSION", new BlockPos(50, 67, 150)));
                objectives.add(new MissionObjective("install_solar_panels", "Install solar heating systems", 15, "SOLAR_PANEL", new BlockPos(50, 67, 150)));
                objectives.add(new MissionObjective("improve_insulation", "Improve building insulation", 25, "INSULATION", new BlockPos(50, 67, 150)));
                break;
                
            case INDUSTRIAL_FILTERS:
                objectives.add(new MissionObjective("install_factory_filters", "Install factory emission filters", 12, "FACTORY_FILTER", new BlockPos(0, 70, 450)));
                objectives.add(new MissionObjective("upgrade_smokestacks", "Upgrade industrial smokestacks", 8, "SMOKESTACK_UPGRADE", new BlockPos(0, 70, 450)));
                objectives.add(new MissionObjective("monitor_emissions", "Set up emission monitoring", 5, "EMISSION_MONITOR", new BlockPos(0, 70, 450)));
                break;
                
            case MINE_DUST_CONTROL:
                objectives.add(new MissionObjective("install_dust_suppression", "Install dust suppression systems", 15, "DUST_SUPPRESSION", new BlockPos(0, 150, 900)));
                objectives.add(new MissionObjective("upgrade_ventilation", "Upgrade mine ventilation", 10, "MINE_VENTILATION", new BlockPos(0, 150, 900)));
                objectives.add(new MissionObjective("seal_mine_shafts", "Seal abandoned mine shafts", 8, "SHAFT_SEALING", new BlockPos(0, 150, 900)));
                break;
                
            case POWER_PLANT_UPGRADE:
                objectives.add(new MissionObjective("install_scrubbers", "Install flue gas scrubbers", 4, "SCRUBBER", new BlockPos(-128, 130, 800)));
                objectives.add(new MissionObjective("upgrade_filters", "Upgrade particulate filters", 6, "PARTICULATE_FILTER", new BlockPos(-128, 130, 800)));
                objectives.add(new MissionObjective("optimize_combustion", "Optimize combustion efficiency", 3, "COMBUSTION_OPTIMIZATION", new BlockPos(-128, 130, 800)));
                break;
                
            case GREEN_CORRIDOR:
                objectives.add(new MissionObjective("plant_trees", "Plant trees for natural filtering", 100, "TREE_PLANTING", new BlockPos(0, 150, 600)));
                objectives.add(new MissionObjective("create_green_spaces", "Create urban green spaces", 20, "GREEN_SPACE", new BlockPos(0, 150, 600)));
                objectives.add(new MissionObjective("install_air_purifiers", "Install natural air purification systems", 8, "AIR_PURIFIER", new BlockPos(0, 150, 600)));
                break;
                
            case ELECTRIC_FLEET:
                objectives.add(new MissionObjective("convert_buses", "Convert city buses to electric", 12, "ELECTRIC_BUS", new BlockPos(0, 82, 50)));
                objectives.add(new MissionObjective("convert_trucks", "Convert delivery trucks to electric", 8, "ELECTRIC_TRUCK", new BlockPos(0, 82, 50)));
                objectives.add(new MissionObjective("build_depot", "Build electric vehicle depot", 1, "ELECTRIC_DEPOT", new BlockPos(0, 82, 50)));
                break;
        }
        
        // Add rewards based on mission type
        rewards.add("Clean Air Points: " + requiredScore);
        rewards.add("Environmental Engineering Experience");
        if (type == MissionType.PORT_CLEANUP || type == MissionType.MARITIME_OPTIMIZATION) {
            rewards.add("Marine Pollution Control Certificate");
        } else if (type == MissionType.MINE_DUST_CONTROL || type == MissionType.POWER_PLANT_UPGRADE) {
            rewards.add("Industrial Air Quality Specialist Badge");
        }
    }
    
    public void startMission(ServerPlayerEntity player) {
        if (isActive || isCompleted) return;
        
        isActive = true;
        player.sendMessage(Text.literal("=== MISSION STARTED ===").formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.sendMessage(Text.literal(name).formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal(description).formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Objectives:").formatted(Formatting.AQUA), false);
        
        for (MissionObjective objective : objectives) {
            player.sendMessage(Text.literal("• " + objective.description + " (0/" + objective.requiredAmount + ")")
                .formatted(Formatting.WHITE), false);
        }
    }
    
    public boolean updateObjectiveProgress(String objectiveId, int amount) {
        for (MissionObjective objective : objectives) {
            if (objective.id.equals(objectiveId)) {
                int oldProgress = objective.progress;
                objective.addProgress(amount);
                
                if (objective.progress > oldProgress) {
                    currentProgress = calculateOverallProgress();
                    return true;
                }
                break;
            }
        }
        return false;
    }
    
    public void checkCompletion(ServerPlayerEntity player) {
        if (isCompleted || !isActive) return;
        
        boolean allCompleted = objectives.stream().allMatch(MissionObjective::isCompleted);
        
        if (allCompleted) {
            completeMission(player);
        } else {
            // Send progress update
            sendProgressUpdate(player);
        }
    }
    
    private void completeMission(ServerPlayerEntity player) {
        isCompleted = true;
        isActive = false;
        
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("🎉 MISSION COMPLETE! 🎉").formatted(Formatting.GREEN, Formatting.BOLD), true);
        player.sendMessage(Text.literal(name).formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal(""), false);
        player.sendMessage(Text.literal("Rewards:").formatted(Formatting.YELLOW), false);
        
        for (String reward : rewards) {
            player.sendMessage(Text.literal("+ " + reward).formatted(Formatting.GREEN), false);
        }
        
        // Award experience and points
        // This would integrate with your existing progression system
    }
    
    private void sendProgressUpdate(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("Mission Progress: " + name).formatted(Formatting.YELLOW), true);
        
        for (MissionObjective objective : objectives) {
            String status = objective.isCompleted() ? "✓" : "○";
            Formatting color = objective.isCompleted() ? Formatting.GREEN : Formatting.GRAY;
            
            player.sendMessage(Text.literal(status + " " + objective.description + 
                " (" + objective.progress + "/" + objective.requiredAmount + ")")
                .formatted(color), false);
        }
    }
    
    private int calculateOverallProgress() {
        int totalObjectives = objectives.size();
        int completedObjectives = (int) objectives.stream().mapToLong(obj -> obj.isCompleted() ? 1 : 0).sum();
        return (int) ((double) completedObjectives / totalObjectives * 100);
    }
    
    public boolean canStart() {
        return !isActive && !isCompleted;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public boolean isCompleted() {
        return isCompleted;
    }
    
    public String getMissionId() {
        return missionId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public MissionType getType() {
        return type;
    }
    
    public int getRequiredScore() {
        return requiredScore;
    }
    
    public int getCurrentProgress() {
        return currentProgress;
    }
    
    public List<MissionObjective> getObjectives() {
        return objectives;
    }
    
    public List<String> getRewards() {
        return rewards;
    }
    
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("missionId", missionId);
        nbt.putString("name", name);
        nbt.putString("description", description);
        nbt.putString("type", type.name());
        nbt.putInt("requiredScore", requiredScore);
        nbt.putBoolean("isCompleted", isCompleted);
        nbt.putBoolean("isActive", isActive);
        nbt.putInt("currentProgress", currentProgress);
        
        // Save objectives progress
        NbtCompound objectivesNbt = new NbtCompound();
        for (int i = 0; i < objectives.size(); i++) {
            MissionObjective obj = objectives.get(i);
            NbtCompound objNbt = new NbtCompound();
            objNbt.putString("id", obj.id);
            objNbt.putInt("progress", obj.progress);
            objNbt.putBoolean("completed", obj.completed);
            objectivesNbt.put("objective_" + i, objNbt);
        }
        nbt.put("objectives", objectivesNbt);
        
        return nbt;
    }
    
    public void fromNbt(NbtCompound nbt) {
        this.isCompleted = nbt.getBoolean("isCompleted");
        this.isActive = nbt.getBoolean("isActive");
        this.currentProgress = nbt.getInt("currentProgress");
        
        // Load objectives progress
        if (nbt.contains("objectives")) {
            NbtCompound objectivesNbt = nbt.getCompound("objectives");
            for (int i = 0; i < objectives.size(); i++) {
                if (objectivesNbt.contains("objective_" + i)) {
                    NbtCompound objNbt = objectivesNbt.getCompound("objective_" + i);
                    MissionObjective obj = objectives.get(i);
                    obj.progress = objNbt.getInt("progress");
                    obj.completed = objNbt.getBoolean("completed");
                }
            }
        }
    }
}