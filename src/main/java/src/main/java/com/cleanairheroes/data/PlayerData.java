package com.cleanairheroes.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.util.Identifier;

import com.cleanairheroes.CleanAirHeroes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData extends PersistentState {
    
    private Map<UUID, PlayerProgress> playerData = new HashMap<>();
    
    public PlayerData() {
        super();
    }
    
    public PlayerData(NbtCompound nbt) {
        this();
        
        NbtCompound playersNbt = nbt.getCompound("players");
        for (String key : playersNbt.getKeys()) {
            UUID playerUUID = UUID.fromString(key);
            NbtCompound playerNbt = playersNbt.getCompound(key);
            PlayerProgress progress = PlayerProgress.fromNbt(playerNbt);
            playerData.put(playerUUID, progress);
        }
    }
    
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound playersNbt = new NbtCompound();
        
        for (Map.Entry<UUID, PlayerProgress> entry : playerData.entrySet()) {
            String playerKey = entry.getKey().toString();
            NbtCompound playerNbt = entry.getValue().toNbt();
            playersNbt.put(playerKey, playerNbt);
        }
        
        nbt.put("players", playersNbt);
        return nbt;
    }
    
    public static PlayerData getServerState(ServerWorld world) {
        PersistentStateManager persistentStateManager = world.getPersistentStateManager();
        
        PlayerData state = persistentStateManager.getOrCreate(
            PlayerData::new,
            PlayerData::new,
            CleanAirHeroes.MOD_ID + "_player_data"
        );
        
        state.markDirty();
        return state;
    }
    
    public PlayerProgress getPlayerProgress(UUID playerUUID) {
        return playerData.computeIfAbsent(playerUUID, uuid -> new PlayerProgress());
    }
    
    public void setPlayerProgress(UUID playerUUID, PlayerProgress progress) {
        playerData.put(playerUUID, progress);
        markDirty();
    }
    
    public void updatePlayerScore(UUID playerUUID, String region, int scoreIncrease) {
        PlayerProgress progress = getPlayerProgress(playerUUID);
        progress.addScore(region, scoreIncrease);
        setPlayerProgress(playerUUID, progress);
    }
    
    public void setPlayerCurrentRegion(UUID playerUUID, String region) {
        PlayerProgress progress = getPlayerProgress(playerUUID);
        progress.setCurrentRegion(region);
        setPlayerProgress(playerUUID, progress);
    }
    
    public void addCompletedMission(UUID playerUUID, String missionId) {
        PlayerProgress progress = getPlayerProgress(playerUUID);
        progress.addCompletedMission(missionId);
        setPlayerProgress(playerUUID, progress);
    }
    
    public void unlockRegion(UUID playerUUID, String region) {
        PlayerProgress progress = getPlayerProgress(playerUUID);
        progress.unlockRegion(region);
        setPlayerProgress(playerUUID, progress);
    }
    
    // Convenience method for getting player data from player entity
    public static PlayerProgress getPlayerProgress(ServerPlayerEntity player) {
        PlayerData worldState = getServerState(player.getServerWorld());
        return worldState.getPlayerProgress(player.getUuid());
    }
    
    // Convenience method for updating player data
    public static void updatePlayer(ServerPlayerEntity player, PlayerProgress progress) {
        PlayerData worldState = getServerState(player.getServerWorld());
        worldState.setPlayerProgress(player.getUuid(), progress);
    }
}