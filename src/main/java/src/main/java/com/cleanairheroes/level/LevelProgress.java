package com.cleanairheroes.level;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.*;

public class LevelProgress {
    private int currentLevel = -1;
    private int totalPoints = 0;
    private Map<Integer, Set<String>> completedMissions = new HashMap<>();
    private Map<Integer, Boolean> levelCompletionStatus = new HashMap<>();
    private long startTime = System.currentTimeMillis();
    private long lastPlayTime = System.currentTimeMillis();
    
    public LevelProgress() {
        // Initialize with default values
    }
    
    // Getters
    public int getCurrentLevel() { return currentLevel; }
    public int getTotalPoints() { return totalPoints; }
    public long getStartTime() { return startTime; }
    public long getLastPlayTime() { return lastPlayTime; }
    
    // Setters
    public void setCurrentLevel(int level) { 
        this.currentLevel = level; 
        updateLastPlayTime();
    }
    
    public void addPoints(int points) { 
        this.totalPoints += points; 
        updateLastPlayTime();
    }
    
    public void updateLastPlayTime() {
        this.lastPlayTime = System.currentTimeMillis();
    }
    
    // Mission management
    public boolean completeMission(int levelIndex, String missionId) {
        Set<String> missions = completedMissions.computeIfAbsent(levelIndex, k -> new HashSet<>());
        boolean wasNew = missions.add(missionId);
        
        if (wasNew) {
            updateLastPlayTime();
        }
        
        return wasNew;
    }
    
    public boolean isMissionCompleted(int levelIndex, String missionId) {
        Set<String> missions = completedMissions.get(levelIndex);
        return missions != null && missions.contains(missionId);
    }
    
    public Set<String> getCompletedMissions(int levelIndex) {
        return completedMissions.getOrDefault(levelIndex, new HashSet<>());
    }
    
    public int getCompletedMissionCount(int levelIndex) {
        return getCompletedMissions(levelIndex).size();
    }
    
    // Level completion
    public boolean isLevelCompleted(int levelIndex) {
        return levelCompletionStatus.getOrDefault(levelIndex, false);
    }
    
    public boolean isLevelCompleted(int levelIndex, int totalMissions) {
        if (levelCompletionStatus.containsKey(levelIndex)) {
            return levelCompletionStatus.get(levelIndex);
        }
        
        // Check if all missions are completed
        Set<String> missions = completedMissions.get(levelIndex);
        boolean completed = missions != null && missions.size() >= totalMissions;
        
        if (completed) {
            setLevelCompleted(levelIndex, true);
        }
        
        return completed;
    }
    
    public void setLevelCompleted(int levelIndex, boolean completed) {
        levelCompletionStatus.put(levelIndex, completed);
        updateLastPlayTime();
    }
    
    // Progress calculation
    public float getLevelProgress(int levelIndex, int totalMissions) {
        if (totalMissions == 0) return 100.0f;
        
        int completed = getCompletedMissionCount(levelIndex);
        return (float) completed / totalMissions * 100.0f;
    }
    
    public float getOverallProgress(int totalLevels) {
        if (totalLevels == 0) return 0.0f;
        
        int completedLevels = 0;
        for (int i = 0; i < totalLevels; i++) {
            if (isLevelCompleted(i)) {
                completedLevels++;
            }
        }
        
        return (float) completedLevels / totalLevels * 100.0f;
    }
    
    // Statistics
    public long getPlayTimeHours() {
        return (lastPlayTime - startTime) / (1000 * 60 * 60);
    }
    
    public long getPlayTimeMinutes() {
        return (lastPlayTime - startTime) / (1000 * 60);
    }
    
    public int getHighestCompletedLevel() {
        return levelCompletionStatus.entrySet().stream()
            .filter(Map.Entry::getValue)
            .mapToInt(Map.Entry::getKey)
            .max()
            .orElse(-1);
    }
    
    public int getTotalCompletedMissions() {
        return completedMissions.values().stream()
            .mapToInt(Set::size)
            .sum();
    }
    
    // NBT Serialization
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        
        nbt.putInt("currentLevel", currentLevel);
        nbt.putInt("totalPoints", totalPoints);
        nbt.putLong("startTime", startTime);
        nbt.putLong("lastPlayTime", lastPlayTime);
        
        // Serialize completed missions
        NbtCompound missionsNbt = new NbtCompound();
        for (Map.Entry<Integer, Set<String>> entry : completedMissions.entrySet()) {
            NbtList missionList = new NbtList();
            for (String missionId : entry.getValue()) {
                NbtCompound missionNbt = new NbtCompound();
                missionNbt.putString("id", missionId);
                missionList.add(missionNbt);
            }
            missionsNbt.put(String.valueOf(entry.getKey()), missionList);
        }
        nbt.put("completedMissions", missionsNbt);
        
        // Serialize level completion status
        NbtCompound levelsNbt = new NbtCompound();
        for (Map.Entry<Integer, Boolean> entry : levelCompletionStatus.entrySet()) {
            levelsNbt.putBoolean(String.valueOf(entry.getKey()), entry.getValue());
        }
        nbt.put("levelCompletionStatus", levelsNbt);
        
        return nbt;
    }
    
    public static LevelProgress fromNbt(NbtCompound nbt) {
        LevelProgress progress = new LevelProgress();
        
        progress.currentLevel = nbt.getInt("currentLevel");
        progress.totalPoints = nbt.getInt("totalPoints");
        progress.startTime = nbt.getLong("startTime");
        progress.lastPlayTime = nbt.getLong("lastPlayTime");
        
        // Deserialize completed missions
        if (nbt.contains("completedMissions")) {
            NbtCompound missionsNbt = nbt.getCompound("completedMissions");
            for (String levelKey : missionsNbt.getKeys()) {
                int levelIndex = Integer.parseInt(levelKey);
                Set<String> missions = new HashSet<>();
                
                NbtList missionList = missionsNbt.getList(levelKey, 10); // 10 = Compound tag type
                for (int i = 0; i < missionList.size(); i++) {
                    NbtCompound missionNbt = missionList.getCompound(i);
                    missions.add(missionNbt.getString("id"));
                }
                
                progress.completedMissions.put(levelIndex, missions);
            }
        }
        
        // Deserialize level completion status
        if (nbt.contains("levelCompletionStatus")) {
            NbtCompound levelsNbt = nbt.getCompound("levelCompletionStatus");
            for (String levelKey : levelsNbt.getKeys()) {
                int levelIndex = Integer.parseInt(levelKey);
                boolean isCompleted = levelsNbt.getBoolean(levelKey);
                progress.levelCompletionStatus.put(levelIndex, isCompleted);
            }
        }
        
        return progress;
    }
    
    // Utility methods
    public void reset() {
        currentLevel = -1;
        totalPoints = 0;
        completedMissions.clear();
        levelCompletionStatus.clear();
        startTime = System.currentTimeMillis();
        lastPlayTime = System.currentTimeMillis();
    }
    
    public void resetLevel(int levelIndex) {
        completedMissions.remove(levelIndex);
        levelCompletionStatus.remove(levelIndex);
        updateLastPlayTime();
    }
    
    @Override
    public String toString() {
        return String.format("LevelProgress{currentLevel=%d, points=%d, completedLevels=%d}", 
                           currentLevel, totalPoints, levelCompletionStatus.size());
    }
}