package com.cleanairheroes.level;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import com.cleanairheroes.mission.Mission;
import com.cleanairheroes.mission.MissionType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Level {
    
    private String id;
    private String displayName;
    private String description;
    private List<Mission> missions;
    private LevelType levelType;
    private String region;
    private int requiredScore;
    private boolean isUnlocked;
    private boolean isCompleted;
    private long completionTime;
    
    public Level(String id, String displayName, String description, LevelType levelType, String region, int requiredScore) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.levelType = levelType;
        this.region = region;
        this.requiredScore = requiredScore;
        this.missions = new ArrayList<>();
        this.isUnlocked = false;
        this.isCompleted = false;
        this.completionTime = 0;
    }
    
    // Getters
    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public List<Mission> getMissions() { return missions; }
    public LevelType getLevelType() { return levelType; }
    public String getRegion() { return region; }
    public int getRequiredScore() { return requiredScore; }
    public boolean isUnlocked() { return isUnlocked; }
    public boolean isCompleted() { return isCompleted; }
    public long getCompletionTime() { return completionTime; }
    
    // Setters
    public void setUnlocked(boolean unlocked) { this.isUnlocked = unlocked; }
    public void setCompleted(boolean completed) { 
        this.isCompleted = completed; 
        if (completed && completionTime == 0) {
            this.completionTime = System.currentTimeMillis();
        }
    }
    
    // Mission management
    public void addMission(Mission mission) {
        this.missions.add(mission);
    }
    
    public void removeMission(Mission mission) {
        this.missions.remove(mission);
    }
    
    public Mission getMissionById(String missionId) {
        return missions.stream()
            .filter(mission -> mission.getId().equals(missionId))
            .findFirst()
            .orElse(null);
    }
    
    public List<Mission> getActiveMissions() {
        return missions.stream()
            .filter(mission -> mission.isActive() && !mission.isCompleted())
            .collect(Collectors.toList());
    }
    
    public List<Mission> getCompletedMissions() {
        return missions.stream()
            .filter(Mission::isCompleted)
            .collect(Collectors.toList());
    }
    
    public List<Mission> getAvailableMissions() {
        return missions.stream()
            .filter(mission -> !mission.isActive() && !mission.isCompleted() && mission.canStart())
            .collect(Collectors.toList());
    }
    
    // Progress calculation
    public float getCompletionPercentage() {
        if (missions.isEmpty()) return 0.0f;
        
        int completedCount = getCompletedMissions().size();
        return (float) completedCount / missions.size() * 100.0f;
    }
    
    public int getTotalScore() {
        return missions.stream()
            .filter(Mission::isCompleted)
            .mapToInt(Mission::getRewardScore)
            .sum();
    }
    
    public boolean canStart() {
        return isUnlocked && !isCompleted;
    }
    
    public boolean canComplete() {
        if (missions.isEmpty()) return true;
        
        // Check if all required missions are completed
        long requiredMissions = missions.stream()
            .filter(mission -> mission.getMissionType() == MissionType.MAIN_OBJECTIVE)
            .count();
        
        long completedRequired = missions.stream()
            .filter(mission -> mission.getMissionType() == MissionType.MAIN_OBJECTIVE && mission.isCompleted())
            .count();
        
        return completedRequired >= requiredMissions;
    }
    
    // Player interaction
    public void startLevel(ServerPlayerEntity player) {
        if (!canStart()) return;
        
        // Activate initial missions
        missions.stream()
            .filter(mission -> mission.getMissionType() == MissionType.MAIN_OBJECTIVE)
            .forEach(mission -> mission.start(player));
        
        // Send level start notification
        player.sendMessage(
            Text.literal("Level Started: ").formatted(Formatting.GREEN)
                .append(Text.literal(displayName).formatted(Formatting.YELLOW))
                .append(Text.literal(" - " + description).formatted(Formatting.GRAY)),
            false
        );
    }
    
    public void completeLevel(ServerPlayerEntity player) {
        if (!canComplete()) return;
        
        setCompleted(true);
        
        // Complete any remaining active missions
        getActiveMissions().forEach(mission -> {
            if (mission.canComplete()) {
                mission.complete(player);
            }
        });
        
        // Send completion notification
        player.sendMessage(
            Text.literal("Level Completed: ").formatted(Formatting.GOLD)
                .append(Text.literal(displayName).formatted(Formatting.YELLOW))
                .append(Text.literal(" | Score: " + getTotalScore()).formatted(Formatting.GREEN)),
            false
        );
    }
    
    public void updateProgress(ServerPlayerEntity player) {
        // Update all active missions
        getActiveMissions().forEach(mission -> mission.updateProgress(player));
        
        // Check if level can be completed
        if (!isCompleted && canComplete()) {
            completeLevel(player);
        }
    }
    
    // NBT Serialization
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        
        nbt.putString("id", id);
        nbt.putString("displayName", displayName);
        nbt.putString("description", description);
        nbt.putString("levelType", levelType.name());
        nbt.putString("region", region);
        nbt.putInt("requiredScore", requiredScore);
        nbt.putBoolean("isUnlocked", isUnlocked);
        nbt.putBoolean("isCompleted", isCompleted);
        nbt.putLong("completionTime", completionTime);
        
        // Serialize missions
        NbtList missionsList = new NbtList();
        for (Mission mission : missions) {
            missionsList.add(mission.toNbt());
        }
        nbt.put("missions", missionsList);
        
        return nbt;
    }
    
    public static Level fromNbt(NbtCompound nbt) {
        String id = nbt.getString("id");
        String displayName = nbt.getString("displayName");
        String description = nbt.getString("description");
        LevelType levelType = LevelType.valueOf(nbt.getString("levelType"));
        String region = nbt.getString("region");
        int requiredScore = nbt.getInt("requiredScore");
        
        Level level = new Level(id, displayName, description, levelType, region, requiredScore);
        
        level.setUnlocked(nbt.getBoolean("isUnlocked"));
        level.setCompleted(nbt.getBoolean("isCompleted"));
        level.completionTime = nbt.getLong("completionTime");
        
        // Deserialize missions
        if (nbt.contains("missions")) {
            NbtList missionsList = nbt.getList("missions", 10); // 10 = Compound tag type
            for (int i = 0; i < missionsList.size(); i++) {
                NbtCompound missionNbt = missionsList.getCompound(i);
                Mission mission = Mission.fromNbt(missionNbt);
                level.addMission(mission);
            }
        }
        
        return level;
    }
    
    // Factory methods for creating standard levels
    public static Level createTutorialLevel() {
        Level level = new Level(
            "tutorial",
            "Environmental Basics",
            "Learn the fundamentals of air quality monitoring and pollution control",
            LevelType.TUTORIAL,
            "global",
            0
        );
        
        level.setUnlocked(true); // Tutorial is always unlocked
        
        // Add tutorial missions
        Mission introMission = new Mission(
            "intro_pollution",
            "Understanding Air Pollution",
            "Learn about different types of air pollutants and their sources",
            MissionType.MAIN_OBJECTIVE,
            100
        );
        introMission.setActive(true);
        level.addMission(introMission);
        
        return level;
    }
    
    public static Level createRegionalLevel(String regionId, String regionName) {
        Level level = new Level(
            regionId + "_main",
            regionName + " Environmental Challenge",
            "Address the environmental challenges specific to " + regionName,
            LevelType.REGIONAL,
            regionId,
            500
        );
        
        return level;
    }
    
    public static Level createAdvancedLevel(String specialization) {
        Level level = new Level(
            "advanced_" + specialization,
            "Advanced " + specialization,
            "Master advanced techniques in " + specialization,
            LevelType.ADVANCED,
            "global",
            1000
        );
        
        return level;
    }
    
    // Utility methods
    public String getStatusText() {
        if (isCompleted) {
            return "Completed";
        } else if (isUnlocked) {
            return String.format("In Progress (%.1f%%)", getCompletionPercentage());
        } else {
            return "Locked";
        }
    }
    
    public Formatting getStatusColor() {
        if (isCompleted) {
            return Formatting.GREEN;
        } else if (isUnlocked) {
            return Formatting.YELLOW;
        } else {
            return Formatting.RED;
        }
    }
    
    @Override
    public String toString() {
        return String.format("Level{id='%s', name='%s', region='%s', completed=%s}", 
                           id, displayName, region, isCompleted);
    }
    
    // Level type enumeration
    public enum LevelType {
        TUTORIAL("Tutorial", "Basic introduction to environmental concepts"),
        REGIONAL("Regional", "Location-specific environmental challenges"),
        ADVANCED("Advanced", "Specialized environmental engineering topics"),
        CHALLENGE("Challenge", "Special challenge scenarios"),
        CERTIFICATION("Certification", "Professional certification requirements");
        
        private final String displayName;
        private final String description;
        
        LevelType(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
}