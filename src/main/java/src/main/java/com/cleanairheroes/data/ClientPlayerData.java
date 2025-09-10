package com.cleanairheroes.data;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Set;
import java.util.HashSet;

@Environment(EnvType.CLIENT)
public class ClientPlayerData {
    
    private static int totalScore = 0;
    private static double pollutionReduced = 0.0;
    private static int equipmentInstalled = 0;
    private static int completedMissions = 0;
    private static int playTime = 0;
    private static Set<String> unlockedRegions = new HashSet<>();
    private static Set<String> completedMissionSet = new HashSet<>();
    
    // Pollution data
    private static float pm25 = 0.0f;
    private static float pm10 = 0.0f;
    private static float no2 = 0.0f;
    private static float so2 = 0.0f;
    private static float co = 0.0f;
    private static float o3 = 0.0f;
    private static int aqi = 0;
    private static String aqiCategory = "Good";
    
    public static void updateData(int score, double pollution, int equipment, int missions, int time, 
                                Set<String> regions, Set<String> missionSet) {
        totalScore = score;
        pollutionReduced = pollution;
        equipmentInstalled = equipment;
        completedMissions = missions;
        playTime = time;
        unlockedRegions = new HashSet<>(regions);
        completedMissionSet = new HashSet<>(missionSet);
    }
    
    public static void updatePollutionData(float newPm25, float newPm10, float newNo2, float newSo2, 
                                         float newCo, float newO3, int newAqi, String newCategory) {
        pm25 = newPm25;
        pm10 = newPm10;
        no2 = newNo2;
        so2 = newSo2;
        co = newCo;
        o3 = newO3;
        aqi = newAqi;
        aqiCategory = newCategory;
    }
    
    // Getters
    public static int getTotalScore() { return totalScore; }
    public static double getPollutionReduced() { return pollutionReduced; }
    public static int getEquipmentInstalled() { return equipmentInstalled; }
    public static int getCompletedMissions() { return completedMissions; }
    public static int getPlayTime() { return playTime; }
    public static Set<String> getUnlockedRegions() { return new HashSet<>(unlockedRegions); }
    public static Set<String> getCompletedMissionSet() { return new HashSet<>(completedMissionSet); }
    
    public static float getPM25() { return pm25; }
    public static float getPM10() { return pm10; }
    public static float getNO2() { return no2; }
    public static float getSO2() { return so2; }
    public static float getCO() { return co; }
    public static float getO3() { return o3; }
    public static int getAQI() { return aqi; }
    public static String getAQICategory() { return aqiCategory; }
    
    // Utility methods
    public static boolean isRegionUnlocked(String regionId) {
        return unlockedRegions.contains(regionId);
    }
    
    public static boolean isMissionCompleted(String missionId) {
        return completedMissionSet.contains(missionId);
    }
    
    public static int getUnlockedRegionCount() {
        return unlockedRegions.size();
    }
    
    public static float getOverallProgress() {
        // Calculate overall progress percentage based on various factors
        float regionProgress = (float) unlockedRegions.size() / 5.0f * 20.0f; // 20% for regions
        float missionProgress = Math.min(completedMissions / 50.0f, 1.0f) * 30.0f; // 30% for missions
        float scoreProgress = Math.min(totalScore / 5000.0f, 1.0f) * 25.0f; // 25% for score
        float equipmentProgress = Math.min(equipmentInstalled / 100.0f, 1.0f) * 15.0f; // 15% for equipment
        float pollutionProgress = Math.min((float) pollutionReduced / 1000.0f, 1.0f) * 10.0f; // 10% for pollution reduction
        
        return regionProgress + missionProgress + scoreProgress + equipmentProgress + pollutionProgress;
    }
    
    public static String getPlayerRank() {
        if (totalScore >= 5000) return "Clean Air Master";
        if (totalScore >= 3000) return "Environmental Engineer";
        if (totalScore >= 1500) return "Pollution Fighter";
        if (totalScore >= 500) return "Air Quality Advocate";
        return "Environmental Rookie";
    }
    
    public static int getEstimatedGlobalRank() {
        // Simulate global ranking based on score
        return Math.max(1, 1000 - totalScore / 5);
    }
}