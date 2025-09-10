package com.cleanairheroes.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import com.cleanairheroes.CleanAirHeroes;
import com.cleanairheroes.data.PlayerData;
import com.cleanairheroes.data.PlayerProgress;

public class NetworkHandler {
    
    // Packet identifiers
    public static final Identifier SYNC_PLAYER_DATA = new Identifier(CleanAirHeroes.MOD_ID, "sync_player_data");
    public static final Identifier UPDATE_POLLUTION = new Identifier(CleanAirHeroes.MOD_ID, "update_pollution");
    public static final Identifier OPEN_GUI_PACKET = new Identifier(CleanAirHeroes.MOD_ID, "open_gui");
    public static final Identifier GUI_INTERACTION = new Identifier(CleanAirHeroes.MOD_ID, "gui_interaction");
    public static final Identifier MISSION_UPDATE = new Identifier(CleanAirHeroes.MOD_ID, "mission_update");
    public static final Identifier POLLUTION_PARTICLE = new Identifier(CleanAirHeroes.MOD_ID, "pollution_particle");
    
    public static void registerServerPackets() {
        CleanAirHeroes.LOGGER.info("Registering network packets for Clean Air Heroes");
        
        // GUI interaction handler
        ServerPlayNetworking.registerGlobalReceiver(GUI_INTERACTION, (server, player, handler, buf, responseSender) -> {
            String guiType = buf.readString();
            String action = buf.readString();
            
            server.execute(() -> {
                handleGUIInteraction(player, guiType, action, buf);
            });
        });
        
        // Mission completion handler
        ServerPlayNetworking.registerGlobalReceiver(MISSION_UPDATE, (server, player, handler, buf, responseSender) -> {
            String missionId = buf.readString();
            String action = buf.readString();
            
            server.execute(() -> {
                handleMissionUpdate(player, missionId, action);
            });
        });
        
        // Player join event - sync data
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            syncPlayerData(handler.getPlayer());
        });
    }
    
    private static void handleGUIInteraction(ServerPlayerEntity player, String guiType, String action, PacketByteBuf buf) {
        switch (guiType) {
            case "mission_control" -> handleMissionControlInteraction(player, action, buf);
            case "air_quality" -> handleAirQualityInteraction(player, action, buf);
            case "achievement" -> handleAchievementInteraction(player, action, buf);
            case "travel_hub" -> handleTravelHubInteraction(player, action, buf);
            case "protection_center" -> handleProtectionCenterInteraction(player, action, buf);
        }
    }
    
    private static void handleMissionControlInteraction(ServerPlayerEntity player, String action, PacketByteBuf buf) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        
        switch (action) {
            case "start_mission" -> {
                String missionId = buf.readString();
                // Start mission logic
                syncPlayerData(player);
            }
            case "change_region" -> {
                String regionId = buf.readString();
                if (progress.getUnlockedRegions().contains(regionId)) {
                    // Transport player to region
                    syncPlayerData(player);
                }
            }
            case "check_air_quality" -> {
                // Send air quality data
                sendAirQualityUpdate(player);
            }
        }
    }
    
    private static void handleAirQualityInteraction(ServerPlayerEntity player, String action, PacketByteBuf buf) {
        switch (action) {
            case "refresh_data" -> sendAirQualityUpdate(player);
            case "export_data" -> {
                // Export air quality data
                player.sendMessage(net.minecraft.text.Text.literal("Air quality data exported!"), false);
            }
        }
    }
    
    private static void handleAchievementInteraction(ServerPlayerEntity player, String action, PacketByteBuf buf) {
        switch (action) {
            case "filter_achievements" -> {
                String category = buf.readString();
                // Send filtered achievements
                syncPlayerData(player);
            }
        }
    }
    
    private static void handleTravelHubInteraction(ServerPlayerEntity player, String action, PacketByteBuf buf) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        
        switch (action) {
            case "travel_to_region" -> {
                String regionId = buf.readString();
                if (progress.getUnlockedRegions().contains(regionId)) {
                    progress.incrementTotalTrips();
                    syncPlayerData(player);
                    player.sendMessage(net.minecraft.text.Text.literal("Traveling to " + regionId + "..."), false);
                }
            }
        }
    }
    
    private static void handleProtectionCenterInteraction(ServerPlayerEntity player, String action, PacketByteBuf buf) {
        switch (action) {
            case "access_service" -> {
                String serviceId = buf.readString();
                // Handle service access
                syncPlayerData(player);
            }
        }
    }
    
    private static void handleMissionUpdate(ServerPlayerEntity player, String missionId, String action) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        
        switch (action) {
            case "complete" -> {
                progress.completeMission(missionId);
                progress.addScore(100); // Base mission completion score
                syncPlayerData(player);
                player.sendMessage(net.minecraft.text.Text.literal("Mission completed: " + missionId), false);
            }
            case "start" -> {
                // Start mission logic
                syncPlayerData(player);
                player.sendMessage(net.minecraft.text.Text.literal("Mission started: " + missionId), false);
            }
        }
    }
    
    public static void syncPlayerData(ServerPlayerEntity player) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        
        PacketByteBuf buf = PacketByteBufs.create();
        
        // Write player progress data
        buf.writeInt(progress.getTotalScore());
        buf.writeDouble(progress.getTotalPollutionReduced());
        buf.writeInt(progress.getEquipmentInstalled());
        buf.writeInt(progress.getCompletedMissionsCount());
        buf.writeInt(progress.getTotalPlayTimeMinutes());
        
        // Write unlocked regions
        buf.writeInt(progress.getUnlockedRegions().size());
        for (String region : progress.getUnlockedRegions()) {
            buf.writeString(region);
        }
        
        // Write completed missions
        buf.writeInt(progress.getCompletedMissions().size());
        for (String mission : progress.getCompletedMissions()) {
            buf.writeString(mission);
        }
        
        ServerPlayNetworking.send(player, SYNC_PLAYER_DATA, buf);
    }
    
    public static void sendAirQualityUpdate(ServerPlayerEntity player) {
        PacketByteBuf buf = PacketByteBufs.create();
        
        // Write air quality data
        buf.writeFloat(45.5f); // PM2.5
        buf.writeFloat(78.2f); // PM10
        buf.writeFloat(23.1f); // NO2
        buf.writeFloat(12.8f); // SO2
        buf.writeFloat(0.8f);  // CO
        buf.writeFloat(67.3f); // O3
        buf.writeInt(52);      // AQI
        buf.writeString("Moderate"); // AQI Category
        
        ServerPlayNetworking.send(player, UPDATE_POLLUTION, buf);
    }
    
    public static void sendGUIOpenPacket(ServerPlayerEntity player, String guiType) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(guiType);
        
        ServerPlayNetworking.send(player, OPEN_GUI_PACKET, buf);
    }
    
    public static void sendMissionUpdate(ServerPlayerEntity player, String missionId, String status) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(missionId);
        buf.writeString(status);
        
        ServerPlayNetworking.send(player, MISSION_UPDATE, buf);
    }
    
    public static void sendPollutionParticles(ServerPlayerEntity player, double x, double y, double z, String pollutantType, float intensity) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeString(pollutantType);
        buf.writeFloat(intensity);
        
        ServerPlayNetworking.send(player, POLLUTION_PARTICLE, buf);
    }
}