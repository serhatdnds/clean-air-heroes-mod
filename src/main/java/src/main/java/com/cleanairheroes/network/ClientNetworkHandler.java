package com.cleanairheroes.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import com.cleanairheroes.client.gui.*;
import com.cleanairheroes.data.ClientPlayerData;

@Environment(EnvType.CLIENT)
public class ClientNetworkHandler {
    
    public static void registerClientPackets() {
        // Sync player data from server
        ClientPlayNetworking.registerGlobalReceiver(NetworkHandler.SYNC_PLAYER_DATA, (client, handler, buf, responseSender) -> {
            // Read player progress data
            int totalScore = buf.readInt();
            double pollutionReduced = buf.readDouble();
            int equipmentInstalled = buf.readInt();
            int completedMissions = buf.readInt();
            int playTime = buf.readInt();
            
            // Read unlocked regions
            int regionCount = buf.readInt();
            java.util.Set<String> unlockedRegions = new java.util.HashSet<>();
            for (int i = 0; i < regionCount; i++) {
                unlockedRegions.add(buf.readString());
            }
            
            // Read completed missions
            int missionCount = buf.readInt();
            java.util.Set<String> completedMissionSet = new java.util.HashSet<>();
            for (int i = 0; i < missionCount; i++) {
                completedMissionSet.add(buf.readString());
            }
            
            client.execute(() -> {
                // Update client-side data
                ClientPlayerData.updateData(totalScore, pollutionReduced, equipmentInstalled, 
                                          completedMissions, playTime, unlockedRegions, completedMissionSet);
            });
        });
        
        // Handle pollution updates
        ClientPlayNetworking.registerGlobalReceiver(NetworkHandler.UPDATE_POLLUTION, (client, handler, buf, responseSender) -> {
            float pm25 = buf.readFloat();
            float pm10 = buf.readFloat();
            float no2 = buf.readFloat();
            float so2 = buf.readFloat();
            float co = buf.readFloat();
            float o3 = buf.readFloat();
            int aqi = buf.readInt();
            String category = buf.readString();
            
            client.execute(() -> {
                // Update client-side pollution data
                ClientPlayerData.updatePollutionData(pm25, pm10, no2, so2, co, o3, aqi, category);
                
                // Show pollution notification if needed
                if (client.player != null && aqi > 100) {
                    client.player.sendMessage(
                        Text.literal("⚠️ Air Quality Alert: ").formatted(Formatting.RED)
                            .append(Text.literal("AQI " + aqi + " - " + category).formatted(Formatting.YELLOW)), 
                        true
                    );
                }
            });
        });
        
        // Handle GUI opening
        ClientPlayNetworking.registerGlobalReceiver(NetworkHandler.OPEN_GUI_PACKET, (client, handler, buf, responseSender) -> {
            String guiType = buf.readString();
            
            client.execute(() -> {
                openGUI(client, guiType);
            });
        });
        
        // Handle mission updates
        ClientPlayNetworking.registerGlobalReceiver(NetworkHandler.MISSION_UPDATE, (client, handler, buf, responseSender) -> {
            String missionId = buf.readString();
            String status = buf.readString();
            
            client.execute(() -> {
                if (client.player != null) {
                    switch (status) {
                        case "completed" -> {
                            client.player.sendMessage(
                                Text.literal("✅ Mission Completed: ").formatted(Formatting.GREEN)
                                    .append(Text.literal(missionId).formatted(Formatting.GOLD)), 
                                false
                            );
                        }
                        case "started" -> {
                            client.player.sendMessage(
                                Text.literal("📋 Mission Started: ").formatted(Formatting.BLUE)
                                    .append(Text.literal(missionId).formatted(Formatting.WHITE)), 
                                false
                            );
                        }
                        case "failed" -> {
                            client.player.sendMessage(
                                Text.literal("❌ Mission Failed: ").formatted(Formatting.RED)
                                    .append(Text.literal(missionId).formatted(Formatting.GRAY)), 
                                false
                            );
                        }
                    }
                }
            });
        });
        
        // Handle pollution particles
        ClientPlayNetworking.registerGlobalReceiver(NetworkHandler.POLLUTION_PARTICLE, (client, handler, buf, responseSender) -> {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            String pollutantType = buf.readString();
            float intensity = buf.readFloat();
            
            client.execute(() -> {
                // Spawn pollution particles at the specified location
                if (client.world != null) {
                    spawnPollutionParticles(client, x, y, z, pollutantType, intensity);
                }
            });
        });
    }
    
    private static void openGUI(MinecraftClient client, String guiType) {
        switch (guiType) {
            case "mission_control" -> client.setScreen(new MissionControlScreen());
            case "air_quality" -> client.setScreen(new AirQualityDashboard());
            case "achievement" -> client.setScreen(new AchievementGalleryScreen());
            case "travel_hub" -> client.setScreen(new TravelHubScreen());
            case "protection_center" -> client.setScreen(new ProtectionCenterScreen());
            case "global_report" -> client.setScreen(new GlobalReportScreen());
            case "research_data" -> client.setScreen(new ResearchDataScreen());
            case "leaderboard" -> client.setScreen(new LeaderboardScreen());
            case "minigame_selection" -> client.setScreen(new MiniGameSelectionScreen());
        }
    }
    
    private static void spawnPollutionParticles(MinecraftClient client, double x, double y, double z, String pollutantType, float intensity) {
        if (client.world == null) return;
        
        // Determine particle type based on pollutant
        net.minecraft.particle.ParticleEffect particleType = switch (pollutantType) {
            case "PM2.5" -> net.minecraft.particle.ParticleTypes.SMOKE;
            case "PM10" -> net.minecraft.particle.ParticleTypes.LARGE_SMOKE;
            case "NO2" -> net.minecraft.particle.ParticleTypes.CAMPFIRE_COSY_SMOKE;
            case "SO2" -> net.minecraft.particle.ParticleTypes.WHITE_SMOKE;
            case "CO" -> net.minecraft.particle.ParticleTypes.SMOKE;
            case "O3" -> net.minecraft.particle.ParticleTypes.END_ROD;
            default -> net.minecraft.particle.ParticleTypes.SMOKE;
        };
        
        // Spawn particles based on intensity
        int particleCount = Math.round(intensity * 10);
        for (int i = 0; i < particleCount; i++) {
            double offsetX = (Math.random() - 0.5) * 2.0;
            double offsetY = Math.random() * 1.0;
            double offsetZ = (Math.random() - 0.5) * 2.0;
            
            client.world.addParticle(particleType, 
                x + offsetX, y + offsetY, z + offsetZ,
                0.0, 0.01, 0.0);
        }
    }
    
    // Client-side packet sending methods
    public static void sendGUIInteraction(String guiType, String action) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(guiType);
        buf.writeString(action);
        
        ClientPlayNetworking.send(NetworkHandler.GUI_INTERACTION, buf);
    }
    
    public static void sendGUIInteraction(String guiType, String action, String parameter) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(guiType);
        buf.writeString(action);
        buf.writeString(parameter);
        
        ClientPlayNetworking.send(NetworkHandler.GUI_INTERACTION, buf);
    }
    
    public static void sendMissionUpdate(String missionId, String action) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(missionId);
        buf.writeString(action);
        
        ClientPlayNetworking.send(NetworkHandler.MISSION_UPDATE, buf);
    }
    
    public static void requestDataSync() {
        PacketByteBuf buf = PacketByteBufs.create();
        ClientPlayNetworking.send(NetworkHandler.SYNC_PLAYER_DATA, buf);
    }
}