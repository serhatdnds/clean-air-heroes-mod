package com.cleanairheroes;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cleanairheroes.world.ModDimensions;
import com.cleanairheroes.world.structure.ModStructures;
import com.cleanairheroes.block.ModBlocks;
import com.cleanairheroes.item.ModItems;
import com.cleanairheroes.pollution.PollutionManager;
import com.cleanairheroes.command.ModCommands;
import com.cleanairheroes.level.LevelManager;
import com.cleanairheroes.entity.ModEntities;
import com.cleanairheroes.events.PlayerEventHandler;

public class CleanAirHeroes implements ModInitializer {
    public static final String MOD_ID = "cleanairheroes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static PollutionManager pollutionManager;
    private static LevelManager levelManager;
    
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Clean Air Heroes - Fighting pollution across the world!");
        
        ModBlocks.registerBlocks();
        ModItems.registerItems();
        ModEntities.registerEntities();
        ModDimensions.register();
        ModStructures.registerStructures();
        
        pollutionManager = new PollutionManager();
        levelManager = new LevelManager();
        
        CommandRegistrationCallback.EVENT.register(ModCommands::register);
        
        // Register network packets
        com.cleanairheroes.network.NetworkHandler.registerServerPackets();
        
        PlayerEventHandler.registerEvents();
        
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
    }
    
    private void onServerStarted(MinecraftServer server) {
        LOGGER.info("Clean Air Heroes: Server started, initializing pollution systems");
        pollutionManager.initialize(server);
        levelManager.initialize(server);
    }
    
    private void onServerTick(MinecraftServer server) {
        if (server.getTicks() % 20 == 0) {
            pollutionManager.tick(server);
            levelManager.tick(server);
        }
    }
    
    public static PollutionManager getPollutionManager() {
        return pollutionManager;
    }
    
    public static LevelManager getLevelManager() {
        return levelManager;
    }
}