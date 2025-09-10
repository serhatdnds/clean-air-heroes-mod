package com.cleanairheroes.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import com.cleanairheroes.client.gui.MissionProgressHud;
import com.cleanairheroes.client.gui.AirQualityHud;
import com.cleanairheroes.client.gui.MissionScreen;
import com.cleanairheroes.client.render.PollutionRenderer;

import org.lwjgl.glfw.GLFW;

public class CleanAirHeroesClient implements ClientModInitializer {
    
    // Key Bindings
    public static KeyBinding OPEN_MISSION_GUI;
    public static KeyBinding QUICK_AIR_CHECK;
    public static KeyBinding OPEN_TRAVEL_HUB;
    public static KeyBinding TOGGLE_POLLUTION_OVERLAY;
    
    // GUI Components
    private static MissionProgressHud missionProgressHud;
    private static AirQualityHud airQualityHud;
    private static PollutionRenderer pollutionRenderer;
    
    // Client state
    private static boolean showPollutionOverlay = false;
    private static boolean showMissionProgress = true;
    private static boolean showAirQuality = true;
    
    @Override
    public void onInitializeClient() {
        // Initialize key bindings
        initializeKeyBindings();
        
        // Initialize HUD components
        initializeHudComponents();
        
        // Register event handlers
        registerEventHandlers();
        
        // Initialize renderers
        initializeRenderers();
        
        // Register client network packets
        com.cleanairheroes.network.ClientNetworkHandler.registerClientPackets();
    }
    
    private void initializeKeyBindings() {
        OPEN_MISSION_GUI = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.cleanairheroes.open_mission_gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            "category.cleanairheroes.general"
        ));
        
        QUICK_AIR_CHECK = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.cleanairheroes.quick_air_check",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_Q,
            "category.cleanairheroes.general"
        ));
        
        OPEN_TRAVEL_HUB = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.cleanairheroes.open_travel_hub",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_T,
            "category.cleanairheroes.general"
        ));
        
        TOGGLE_POLLUTION_OVERLAY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.cleanairheroes.toggle_pollution_overlay",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            "category.cleanairheroes.general"
        ));
    }
    
    private void initializeHudComponents() {
        missionProgressHud = new MissionProgressHud();
        airQualityHud = new AirQualityHud();
    }
    
    private void registerEventHandlers() {
        // Client tick event for key handling
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            handleKeyPresses(client);
        });
        
        // HUD rendering
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            renderHud(drawContext, tickDelta);
        });
        
        // World rendering for pollution overlay
        WorldRenderEvents.AFTER_TRANSLUCENT.register((context) -> {
            if (showPollutionOverlay && pollutionRenderer != null) {
                pollutionRenderer.render(context);
            }
        });
    }
    
    private void initializeRenderers() {
        pollutionRenderer = new PollutionRenderer();
    }
    
    private void handleKeyPresses(MinecraftClient client) {
        if (client.player == null) return;
        
        // Open Mission GUI
        if (OPEN_MISSION_GUI.wasPressed()) {
            client.setScreen(new MissionScreen());
        }
        
        // Quick Air Quality Check
        if (QUICK_AIR_CHECK.wasPressed()) {
            performQuickAirCheck(client);
        }
        
        // Open Travel Hub
        if (OPEN_TRAVEL_HUB.wasPressed()) {
            openTravelHub(client);
        }
        
        // Toggle Pollution Overlay
        if (TOGGLE_POLLUTION_OVERLAY.wasPressed()) {
            showPollutionOverlay = !showPollutionOverlay;
            String status = showPollutionOverlay ? "ON" : "OFF";
            client.player.sendMessage(Text.literal("Pollution Overlay: " + status)
                .formatted(showPollutionOverlay ? Formatting.GREEN : Formatting.GRAY), true);
        }
    }
    
    private void performQuickAirCheck(MinecraftClient client) {
        if (client.player != null) {
            // This would integrate with the pollution system
            client.player.sendMessage(Text.literal("🌪️ Quick Air Check")
                .formatted(Formatting.BLUE, Formatting.BOLD), true);
            client.player.sendMessage(Text.literal("AQI: 85 (Moderate)")
                .formatted(Formatting.YELLOW), false);
        }
    }
    
    private void openTravelHub(MinecraftClient client) {
        if (client.player != null) {
            // This would open travel hub GUI or send command
            client.player.networkHandler.sendCommand("cleanair travel");
        }
    }
    
    private void renderHud(DrawContext drawContext, RenderTickCounter tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.debugEnabled) return;
        
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        
        // Render Mission Progress HUD
        if (showMissionProgress) {
            missionProgressHud.render(drawContext, screenWidth, screenHeight);
        }
        
        // Render Air Quality HUD
        if (showAirQuality) {
            airQualityHud.render(drawContext, screenWidth, screenHeight);
        }
        
        // Render key hints
        renderKeyHints(drawContext, screenWidth, screenHeight);
    }
    
    private void renderKeyHints(DrawContext drawContext, int screenWidth, int screenHeight) {
        // Render key binding hints in bottom right
        int x = screenWidth - 150;
        int y = screenHeight - 60;
        
        drawContext.drawText(MinecraftClient.getInstance().textRenderer,
            Text.literal("M - Missions").formatted(Formatting.WHITE),
            x, y, 0xFFFFFF, true);
        
        drawContext.drawText(MinecraftClient.getInstance().textRenderer,
            Text.literal("Q - Air Check").formatted(Formatting.WHITE),
            x, y + 10, 0xFFFFFF, true);
        
        drawContext.drawText(MinecraftClient.getInstance().textRenderer,
            Text.literal("T - Travel Hub").formatted(Formatting.WHITE),
            x, y + 20, 0xFFFFFF, true);
        
        drawContext.drawText(MinecraftClient.getInstance().textRenderer,
            Text.literal("P - Pollution Overlay").formatted(Formatting.WHITE),
            x, y + 30, 0xFFFFFF, true);
    }
    
    // Getters for static access
    public static boolean isShowingPollutionOverlay() {
        return showPollutionOverlay;
    }
    
    public static void setShowMissionProgress(boolean show) {
        showMissionProgress = show;
    }
    
    public static void setShowAirQuality(boolean show) {
        showAirQuality = show;
    }
}