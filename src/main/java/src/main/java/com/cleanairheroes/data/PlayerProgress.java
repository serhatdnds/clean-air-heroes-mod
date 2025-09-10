package com.cleanairheroes.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.*;

public class PlayerProgress {
    
    private String currentRegion = "varna"; // Starting region
    private Map<String, Integer> regionScores = new HashMap<>();
    private Set<String> unlockedRegions = new HashSet<>();
    private Set<String> completedMissions = new HashSet<>();
    private Map<String, Integer> achievements = new HashMap<>();
    private long lastPlayTime = System.currentTimeMillis();
    private int totalPlayTimeMinutes = 0;
    
    // Environmental stats
    private double totalPollutionReduced = 0.0;
    private int treesPlanted = 0;
    private int equipmentInstalled = 0;
    private int educationSessionsGiven = 0;
    
    public PlayerProgress() {
        // Initialize with starting region unlocked
        unlockedRegions.add("varna");
        regionScores.put("varna", 0);
        regionScores.put("zonguldak", 0);
        regionScores.put("odesa", 0);
        regionScores.put("trabzon", 0);
        regionScores.put("romania", 0);
    }
    
    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        
        // Basic progress
        nbt.putString("currentRegion", currentRegion);
        nbt.putLong("lastPlayTime", lastPlayTime);
        nbt.putInt("totalPlayTimeMinutes", totalPlayTimeMinutes);
        
        // Region scores
        NbtCompound scoresNbt = new NbtCompound();
        for (Map.Entry<String, Integer> entry : regionScores.entrySet()) {
            scoresNbt.putInt(entry.getKey(), entry.getValue());
        }
        nbt.put("regionScores", scoresNbt);
        
        // Unlocked regions
        NbtList unlockedList = new NbtList();
        for (String region : unlockedRegions) {
            unlockedList.add(NbtString.of(region));
        }
        nbt.put("unlockedRegions", unlockedList);
        
        // Completed missions
        NbtList missionsLi̇st = new NbtList();
        for (String mission : completedMissions) {
            missionsLi̇st.add(NbtString.of(mission));
        }
        nbt.put("completedMissions", missionsLi̇st);
        
        // Achievements
        NbtCompound achievementsNbt = new NbtCompound();
        for (Map.Entry<String, Integer> entry : achievements.entrySet()) {
            achievementsNbt.putInt(entry.getKey(), entry.getValue());
        }
        nbt.put("achievements", achievementsNbt);
        
        // Environmental stats
        nbt.putDouble("totalPollutionReduced", totalPollutionReduced);
        nbt.putInt("treesPlanted", treesPlanted);
        nbt.putInt("equipmentInstalled", equipmentInstalled);
        nbt.putInt("educationSessionsGiven", educationSessionsGiven);
        
        return nbt;
    }
    
    public static PlayerProgress fromNbt(NbtCompound nbt) {
        PlayerProgress progress = new PlayerProgress();
        
        // Basic progress
        if (nbt.contains("currentRegion")) {
            progress.currentRegion = nbt.getString("currentRegion");
        }
        if (nbt.contains("lastPlayTime")) {
            progress.lastPlayTime = nbt.getLong("lastPlayTime");
        }
        if (nbt.contains("totalPlayTimeMinutes")) {
            progress.totalPlayTimeMinutes = nbt.getInt("totalPlayTimeMinutes");
        }
        
        // Region scores
        if (nbt.contains("regionScores")) {
            NbtCompound scoresNbt = nbt.getCompound("regionScores");
            progress.regionScores.clear();
            for (String key : scoresNbt.getKeys()) {
                progress.regionScores.put(key, scoresNbt.getInt(key));
            }
        }
        
        // Unlocked regions
        if (nbt.contains("unlockedRegions")) {
            NbtList unlockedList = nbt.getList("unlockedRegions", NbtElement.STRING_TYPE);
            progress.unlockedRegions.clear();
            for (int i = 0; i < unlockedList.size(); i++) {
                progress.unlockedRegions.add(unlockedList.getString(i));
            }
        }
        
        // Completed missions
        if (nbt.contains("completedMissions")) {
            NbtList missionsList = nbt.getList("completedMissions", NbtElement.STRING_TYPE);
            progress.completedMissions.clear();
            for (int i = 0; i < missionsList.size(); i++) {
                progress.completedMissions.add(missionsList.getString(i));
            }
        }
        
        // Achievements
        if (nbt.contains("achievements")) {
            NbtCompound achievementsNbt = nbt.getCompound("achievements");
            progress.achievements.clear();
            for (String key : achievementsNbt.getKeys()) {
                progress.achievements.put(key, achievementsNbt.getInt(key));
            }
        }
        
        // Environmental stats
        if (nbt.contains("totalPollutionReduced")) {
            progress.totalPollutionReduced = nbt.getDouble("totalPollutionReduced");
        }
        if (nbt.contains("treesPlanted")) {
            progress.treesPlanted = nbt.getInt("treesPlanted");
        }
        if (nbt.contains("equipmentInstalled")) {
            progress.equipmentInstalled = nbt.getInt("equipmentInstalled");
        }
        if (nbt.contains("educationSessionsGiven")) {
            progress.educationSessionsGiven = nbt.getInt("educationSessionsGiven");
        }
        
        return progress;
    }
    
    // Getters
    public String getCurrentRegion() {
        return currentRegion;
    }
    
    public int getRegionScore(String region) {
        return regionScores.getOrDefault(region, 0);
    }
    
    public boolean isRegionUnlocked(String region) {
        return unlockedRegions.contains(region);
    }
    
    public boolean isMissionCompleted(String missionId) {
        return completedMissions.contains(missionId);
    }
    
    public int getTotalScore() {
        return regionScores.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    public int getCompletedMissionsCount() {
        return completedMissions.size();
    }
    
    public Set<String> getUnlockedRegions() {
        return new HashSet<>(unlockedRegions);
    }
    
    public Set<String> getCompletedMissions() {
        return new HashSet<>(completedMissions);
    }
    
    public double getTotalPollutionReduced() {
        return totalPollutionReduced;
    }
    
    public int getTreesPlanted() {
        return treesPlanted;
    }
    
    public int getEquipmentInstalled() {
        return equipmentInstalled;
    }
    
    public int getEducationSessionsGiven() {
        return educationSessionsGiven;
    }
    
    public int getTotalPlayTimeMinutes() {
        return totalPlayTimeMinutes;
    }
    
    public long getLastPlayTime() {
        return lastPlayTime;
    }
    
    // Setters and modifiers
    public void setCurrentRegion(String region) {
        this.currentRegion = region;
    }
    
    public void addScore(String region, int score) {
        int currentScore = regionScores.getOrDefault(region, 0);
        regionScores.put(region, currentScore + score);
    }
    
    public void unlockRegion(String region) {
        unlockedRegions.add(region);
        regionScores.putIfAbsent(region, 0);
    }
    
    public void addCompletedMission(String missionId) {
        completedMissions.add(missionId);
    }
    
    public void addAchievement(String achievementId, int value) {
        achievements.put(achievementId, value);
    }
    
    public void addPollutionReduced(double amount) {
        totalPollutionReduced += amount;
    }
    
    public void addTreesPlanted(int count) {
        treesPlanted += count;
    }
    
    public void addEquipmentInstalled(int count) {
        equipmentInstalled += count;
    }
    
    public void addEducationSession() {
        educationSessionsGiven++;
    }
    
    public void updatePlayTime() {
        long currentTime = System.currentTimeMillis();
        if (lastPlayTime > 0) {
            long sessionTime = currentTime - lastPlayTime;
            totalPlayTimeMinutes += (int) (sessionTime / 60000); // Convert ms to minutes
        }
        lastPlayTime = currentTime;
    }
    
    // Utility methods
    public boolean canTravelToRegion(String region) {
        String currentReg = getCurrentRegion();
        int currentScore = getRegionScore(currentReg);
        
        // Check if the region is the next logical step
        switch (currentReg) {
            case "varna":
                return region.equals("zonguldak") && currentScore >= 70;
            case "zonguldak":
                return region.equals("odesa") && currentScore >= 70;
            case "odesa":
                return region.equals("trabzon") && currentScore >= 70;
            case "trabzon":
                return region.equals("romania") && currentScore >= 70;
            default:
                return false;
        }
    }
    
    public String getNextRegion() {
        switch (getCurrentRegion()) {
            case "varna": return "zonguldak";
            case "zonguldak": return "odesa";
            case "odesa": return "trabzon";
            case "trabzon": return "romania";
            default: return null;
        }
    }
    
    public String getPlayerTitle() {
        int totalScore = getTotalScore();
        int completedMissions = getCompletedMissionsCount();
        
        if (totalScore >= 400 && completedMissions >= 20) {
            return "Master Clean Air Hero";
        } else if (totalScore >= 300 && completedMissions >= 15) {
            return "Senior Environmental Engineer";
        } else if (totalScore >= 200 && completedMissions >= 10) {
            return "Pollution Control Specialist";
        } else if (totalScore >= 100 && completedMissions >= 5) {
            return "Environmental Advocate";
        } else {
            return "Clean Air Volunteer";
        }
    }
}