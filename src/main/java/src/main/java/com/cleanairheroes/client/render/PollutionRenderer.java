package com.cleanairheroes.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import com.mojang.blaze3d.systems.RenderSystem;

import java.util.Map;
import java.util.HashMap;

public class PollutionRenderer {
    
    // Cache pollution levels for rendering
    private Map<BlockPos, PollutionRenderData> pollutionCache = new HashMap<>();
    private long lastCacheUpdate = 0;
    private static final long CACHE_UPDATE_INTERVAL = 2000; // 2 seconds
    
    public void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        
        updatePollutionCache(client);
        
        MatrixStack matrixStack = context.matrixStack();
        Vec3d cameraPos = context.camera().getPos();
        
        matrixStack.push();
        matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        
        // Setup rendering state
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.disableDepthTest();
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        // Render pollution areas
        for (Map.Entry<BlockPos, PollutionRenderData> entry : pollutionCache.entrySet()) {
            BlockPos pos = entry.getKey();
            PollutionRenderData data = entry.getValue();
            
            // Only render if within reasonable distance
            if (pos.isWithinDistance(client.player.getPos(), 200)) {
                renderPollutionArea(buffer, pos, data);
            }
        }
        
        // Finish rendering
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        
        // Restore rendering state
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        
        matrixStack.pop();
    }
    
    private void updatePollutionCache(MinecraftClient client) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCacheUpdate < CACHE_UPDATE_INTERVAL) return;
        
        pollutionCache.clear();
        BlockPos playerPos = client.player.getBlockPos();
        
        // Sample pollution data in area around player
        for (int x = -100; x <= 100; x += 20) {
            for (int z = -100; z <= 100; z += 20) {
                BlockPos samplePos = playerPos.add(x, 0, z);
                
                // This would get real pollution data from the pollution manager
                double pollutionLevel = samplePollutionLevel(samplePos);
                
                if (pollutionLevel > 50) { // Only show significant pollution
                    PollutionRenderData data = new PollutionRenderData(pollutionLevel, getPollutionType(pollutionLevel));
                    pollutionCache.put(samplePos, data);
                }
            }
        }
        
        lastCacheUpdate = currentTime;
    }
    
    private double samplePollutionLevel(BlockPos pos) {
        // This would integrate with the actual pollution manager
        // For now, simulate pollution based on position and some noise
        
        // Higher pollution near industrial areas (simulated)
        double distanceFromIndustry = Math.sqrt((pos.getX() % 200) * (pos.getX() % 200) + (pos.getZ() % 200) * (pos.getZ() % 200));
        double industrialPollution = Math.max(0, 200 - distanceFromIndustry);
        
        // Add some noise for variation
        double noise = Math.sin(pos.getX() * 0.1) * Math.cos(pos.getZ() * 0.1) * 50 + 50;
        
        return industrialPollution + noise;
    }
    
    private PollutionType getPollutionType(double level) {
        if (level > 200) return PollutionType.INDUSTRIAL;
        if (level > 150) return PollutionType.TRAFFIC;
        if (level > 100) return PollutionType.RESIDENTIAL;
        return PollutionType.AMBIENT;
    }
    
    private void renderPollutionArea(BufferBuilder buffer, BlockPos pos, PollutionRenderData data) {
        float alpha = (float) Math.min(0.6, data.level / 300.0); // Max 60% opacity
        float[] color = getPollutionColor(data.type, data.level);
        
        float red = color[0];
        float green = color[1];
        float blue = color[2];
        
        // Render as a hovering translucent area
        float x = pos.getX();
        float y = pos.getY() + 64; // Hover above ground
        float z = pos.getZ();
        float size = (float) (10 + data.level / 20); // Larger areas for higher pollution
        
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        
        // Top face of pollution cloud
        buffer.vertex(x - size, y, z - size).color(red, green, blue, alpha).next();
        buffer.vertex(x + size, y, z - size).color(red, green, blue, alpha).next();
        buffer.vertex(x + size, y, z + size).color(red, green, blue, alpha).next();
        buffer.vertex(x - size, y, z + size).color(red, green, blue, alpha).next();
        
        // Add some vertical layers for 3D effect
        for (int layer = 1; layer <= 3; layer++) {
            float layerY = y + layer * 2;
            float layerAlpha = alpha * (1.0f - layer * 0.3f);
            float layerSize = size * (1.0f - layer * 0.1f);
            
            buffer.vertex(x - layerSize, layerY, z - layerSize).color(red, green, blue, layerAlpha).next();
            buffer.vertex(x + layerSize, layerY, z - layerSize).color(red, green, blue, layerAlpha).next();
            buffer.vertex(x + layerSize, layerY, z + layerSize).color(red, green, blue, layerAlpha).next();
            buffer.vertex(x - layerSize, layerY, z + layerSize).color(red, green, blue, layerAlpha).next();
        }
    }
    
    private float[] getPollutionColor(PollutionType type, double level) {
        float intensity = (float) Math.min(1.0, level / 200.0);
        
        switch (type) {
            case INDUSTRIAL:
                return new float[]{0.8f * intensity, 0.2f * intensity, 0.1f * intensity}; // Red-orange
            case TRAFFIC:
                return new float[]{0.6f * intensity, 0.6f * intensity, 0.2f * intensity}; // Yellow-brown
            case RESIDENTIAL:
                return new float[]{0.7f * intensity, 0.5f * intensity, 0.3f * intensity}; // Brown
            case AMBIENT:
                return new float[]{0.5f * intensity, 0.5f * intensity, 0.5f * intensity}; // Gray
            default:
                return new float[]{0.5f, 0.5f, 0.5f};
        }
    }
    
    // Data classes
    private static class PollutionRenderData {
        public final double level;
        public final PollutionType type;
        
        public PollutionRenderData(double level, PollutionType type) {
            this.level = level;
            this.type = type;
        }
    }
    
    private enum PollutionType {
        INDUSTRIAL,
        TRAFFIC,
        RESIDENTIAL,
        AMBIENT
    }
}