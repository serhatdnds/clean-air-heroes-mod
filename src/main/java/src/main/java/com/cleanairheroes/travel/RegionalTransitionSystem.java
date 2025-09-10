package com.cleanairheroes.travel;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.nbt.NbtCompound;

import com.cleanairheroes.level.LevelManager;

import java.util.*;

public class RegionalTransitionSystem {
    
    private final Map<String, RegionPortal> portals = new HashMap<>();
    private final Map<UUID, TravelData> playerTravelData = new HashMap<>();
    private final EnvironmentalProtectionCenter protectionCenter;
    
    public RegionalTransitionSystem() {
        this.protectionCenter = new EnvironmentalProtectionCenter();
        initializeRegionalPortals();
    }
    
    private void initializeRegionalPortals() {
        // Portal from Varna to Zonguldak - by cargo ship
        portals.put("varna_to_zonguldak", new RegionPortal(
            "varna_to_zonguldak",
            "Cargo Ship to Zonguldak",
            "Meet with the cargo ship captain who needs help with coal mining pollution in Zonguldak",
            new BlockPos(100, 67, 50), // Varna port location
            "zonguldak_turkey",
            new BlockPos(0, 67, -50), // Zonguldak arrival point
            TravelMethod.CARGO_SHIP,
            70 // Minimum score required
        ));
        
        // Portal from Zonguldak to Odesa - by research vessel
        portals.put("zonguldak_to_odesa", new RegionPortal(
            "zonguldak_to_odesa",
            "Research Vessel to Odesa",
            "Join scientists from Odesa University who are conducting air pollution research",
            new BlockPos(-50, 67, -30), // Zonguldak port
            "odesa_ukraine",
            new BlockPos(-100, 87, 0), // Odesa port arrival
            TravelMethod.RESEARCH_VESSEL,
            70
        ));
        
        // Portal from Odesa to Trabzon - by environmental patrol ship
        portals.put("odesa_to_trabzon", new RegionPortal(
            "odesa_to_trabzon",
            "Environmental Patrol Ship to Trabzon",
            "DOKA Agency partnership project - help tackle Trabzon's air quality crisis",
            new BlockPos(50, 87, 30), // Odesa port
            "trabzon_turkey",
            new BlockPos(0, 64, -20), // Trabzon harbor
            TravelMethod.ENVIRONMENTAL_VESSEL,
            70
        ));
        
        // Portal from Trabzon to Southeast Romania - by Green Corridor project
        portals.put("trabzon_to_romania", new RegionPortal(
            "trabzon_to_romania",
            "European Green Corridor to Romania",
            "Join the European Green Corridor project to tackle Romania's industrial pollution",
            new BlockPos(20, 85, 100), // Trabzon conference center
            "southeast_romania",
            new BlockPos(-800, 65, 50), // Constanta arrival
            TravelMethod.GREEN_CORRIDOR_TRANSPORT,
            70
        ));
    }
    
    public static class RegionPortal {
        public final String portalId;
        public final String displayName;
        public final String description;
        public final BlockPos location;
        public final String targetRegion;
        public final BlockPos targetLocation;
        public final TravelMethod method;
        public final int requiredScore;
        public boolean isActive = false;
        
        public RegionPortal(String portalId, String displayName, String description, 
                           BlockPos location, String targetRegion, BlockPos targetLocation, 
                           TravelMethod method, int requiredScore) {
            this.portalId = portalId;
            this.displayName = displayName;
            this.description = description;
            this.location = location;
            this.targetRegion = targetRegion;
            this.targetLocation = targetLocation;
            this.method = method;
            this.requiredScore = requiredScore;
        }
    }
    
    public enum TravelMethod {
        CARGO_SHIP("Cargo Ship", "Travel across the Black Sea aboard a cargo vessel", 30),
        RESEARCH_VESSEL("Research Vessel", "Join marine scientists on their research expedition", 45),
        ENVIRONMENTAL_VESSEL("Environmental Patrol", "Board an environmental protection vessel", 40),
        GREEN_CORRIDOR_TRANSPORT("Green Corridor", "Use sustainable transport via European Green Corridor", 60);
        
        public final String displayName;
        public final String description;
        public final int travelDuration; // in seconds
        
        TravelMethod(String displayName, String description, int travelDuration) {
            this.displayName = displayName;
            this.description = description;
            this.travelDuration = travelDuration;
        }
    }
    
    public static class TravelData {
        public String currentRegion = "varna_bulgaria";
        public Set<String> unlockedRegions = new HashSet<>();
        public Map<String, Integer> regionScores = new HashMap<>();
        public int totalCleanAirPoints = 0;
        public List<String> completedJourneys = new ArrayList<>();
        public Map<String, Long> equipmentUpgrades = new HashMap<>();
        
        public TravelData() {
            unlockedRegions.add("varna_bulgaria"); // Starting region
            regionScores.put("varna_bulgaria", 0);
        }
    }
    
    public static class EnvironmentalProtectionCenter {
        private final BlockPos centerLocation = new BlockPos(0, 100, 0);
        private final Map<String, RegionStatus> regionStatuses = new HashMap<>();
        
        public EnvironmentalProtectionCenter() {
            initializeRegionStatuses();
        }
        
        private void initializeRegionStatuses() {
            regionStatuses.put("varna_bulgaria", new RegionStatus("Varna, Bulgaria", 0, "Starting Point"));
            regionStatuses.put("zonguldak_turkey", new RegionStatus("Zonguldak, Turkey", 0, "Locked"));
            regionStatuses.put("odesa_ukraine", new RegionStatus("Odesa, Ukraine", 0, "Locked"));
            regionStatuses.put("trabzon_turkey", new RegionStatus("Trabzon, Turkey", 0, "Locked"));
            regionStatuses.put("southeast_romania", new RegionStatus("Southeast Romania", 0, "Locked"));
        }
        
        public void displayCenterInterface(ServerPlayerEntity player) {
            TravelData playerData = getPlayerTravelData(player.getUuid());
            
            player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("═══════════════════════════════").formatted(Formatting.GOLD), false);
            player.sendMessage(Text.literal("  🌍 ENVIRONMENTAL PROTECTION CENTER 🌍").formatted(Formatting.GREEN, Formatting.BOLD), false);
            player.sendMessage(Text.literal("═══════════════════════════════").formatted(Formatting.GOLD), false);
            player.sendMessage(Text.literal(""), false);
            
            player.sendMessage(Text.literal("📊 REGIONAL AIR QUALITY STATUS:").formatted(Formatting.AQUA, Formatting.BOLD), false);
            player.sendMessage(Text.literal(""), false);
            
            for (Map.Entry<String, RegionStatus> entry : regionStatuses.entrySet()) {
                String regionId = entry.getKey();
                RegionStatus status = entry.getValue();
                
                boolean isUnlocked = playerData.unlockedRegions.contains(regionId);
                boolean isCurrent = playerData.currentRegion.equals(regionId);
                
                String statusIcon = isCurrent ? "📍" : (isUnlocked ? "✅" : "🔒");
                Formatting color = isCurrent ? Formatting.YELLOW : (isUnlocked ? Formatting.GREEN : Formatting.GRAY);
                
                int score = playerData.regionScores.getOrDefault(regionId, 0);
                String scoreText = isUnlocked ? " (" + score + " pts)" : "";
                
                player.sendMessage(Text.literal(statusIcon + " " + status.displayName + scoreText)
                    .formatted(color), false);
            }
            
            player.sendMessage(Text.literal(""), false);
            player.sendMessage(Text.literal("🏆 Total Clean Air Points: " + playerData.totalCleanAirPoints).formatted(Formatting.GOLD), false);
            
            if (playerData.totalCleanAirPoints >= 350) {
                player.sendMessage(Text.literal("🌟 CLEAN AIR HERO STATUS ACHIEVED! 🌟").formatted(Formatting.GREEN, Formatting.BOLD), false);
            }
        }
        
        private static class RegionStatus {
            public final String displayName;
            public int airQualityIndex;
            public String status;
            
            public RegionStatus(String displayName, int airQualityIndex, String status) {
                this.displayName = displayName;
                this.airQualityIndex = airQualityIndex;
                this.status = status;
            }
        }
    }
    
    public boolean canTravelTo(ServerPlayerEntity player, String targetRegion) {
        TravelData playerData = getPlayerTravelData(player.getUuid());
        
        // Check if region is unlocked
        if (!playerData.unlockedRegions.contains(targetRegion)) {
            return false;
        }
        
        // Check current region score requirement
        int currentScore = playerData.regionScores.getOrDefault(playerData.currentRegion, 0);
        return currentScore >= 70; // Minimum 70 points to travel
    }
    
    public void initiateTravel(ServerPlayerEntity player, String portalId) {
        RegionPortal portal = portals.get(portalId);
        if (portal == null) return;
        
        TravelData playerData = getPlayerTravelData(player.getUuid());
        
        // Check requirements
        int currentScore = playerData.regionScores.getOrDefault(playerData.currentRegion, 0);
        if (currentScore < portal.requiredScore) {
            player.sendMessage(Text.literal("❌ Insufficient Clean Air Points!")
                .formatted(Formatting.RED), false);
            player.sendMessage(Text.literal("Required: " + portal.requiredScore + " | Your score: " + currentScore)
                .formatted(Formatting.YELLOW), false);
            return;
        }
        
        // Start journey
        startJourney(player, portal);
    }
    
    private void startJourney(ServerPlayerEntity player, RegionPortal portal) {
        TravelData playerData = getPlayerTravelData(player.getUuid());
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🚢 " + portal.displayName.toUpperCase() + " 🚢").formatted(Formatting.BLUE, Formatting.BOLD), true);
        player.sendMessage(Text.literal(portal.description).formatted(Formatting.GRAY), false);
        player.sendMessage(Text.literal(""), false);
        
        // Play journey mini-game based on travel method
        playJourneyMiniGame(player, portal.method);
        
        // Show information about destination
        showDestinationInfo(player, portal.targetRegion);
        
        // Upgrade equipment for new region
        upgradeEquipmentForRegion(player, portal.targetRegion);
        
        // Show achievements from previous region
        showAchievementGallery(player, playerData.currentRegion);
        
        // Update travel data
        playerData.currentRegion = portal.targetRegion;
        playerData.unlockedRegions.add(portal.targetRegion);
        playerData.regionScores.putIfAbsent(portal.targetRegion, 0);
        playerData.completedJourneys.add(portal.portalId);
        
        // Teleport player
        // This would need actual world/dimension management in a real implementation
        player.sendMessage(Text.literal("🌍 Welcome to " + getRegionDisplayName(portal.targetRegion) + "!")
            .formatted(Formatting.GREEN, Formatting.BOLD), true);
    }
    
    private void playJourneyMiniGame(ServerPlayerEntity player, TravelMethod method) {
        player.sendMessage(Text.literal("═══ JOURNEY MINI-GAME ═══").formatted(Formatting.BLUE, Formatting.BOLD), false);
        
        switch (method) {
            case CARGO_SHIP:
                player.sendMessage(Text.literal("🚢 Aboard the cargo ship...").formatted(Formatting.BLUE), false);
                player.sendMessage(Text.literal("Mini-game: Detect marine pollution sources").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("You spot 3 oil spills and report their coordinates!").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("+ 15 Environmental Awareness Points").formatted(Formatting.GOLD), false);
                break;
                
            case RESEARCH_VESSEL:
                player.sendMessage(Text.literal("🔬 On the research vessel...").formatted(Formatting.BLUE), false);
                player.sendMessage(Text.literal("Mini-game: Calculate carbon footprint of the journey").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("Journey carbon footprint: 2.3 kg CO2 - within acceptable limits!").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("+ 20 Scientific Research Points").formatted(Formatting.GOLD), false);
                break;
                
            case ENVIRONMENTAL_VESSEL:
                player.sendMessage(Text.literal("🛡️ Environmental patrol vessel...").formatted(Formatting.BLUE), false);
                player.sendMessage(Text.literal("Mini-game: Monitor air quality during coastal journey").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("AQI readings recorded: Port areas show 150+ AQI levels").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("+ 18 Monitoring Experience Points").formatted(Formatting.GOLD), false);
                break;
                
            case GREEN_CORRIDOR_TRANSPORT:
                player.sendMessage(Text.literal("🌱 Green Corridor transport...").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("Mini-game: Optimize route for minimal emissions").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("Route optimized: 40% reduction in travel emissions achieved!").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("+ 25 Sustainability Points").formatted(Formatting.GOLD), false);
                break;
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
    }
    
    private void showDestinationInfo(ServerPlayerEntity player, String regionId) {
        player.sendMessage(Text.literal("📋 DESTINATION BRIEFING").formatted(Formatting.AQUA, Formatting.BOLD), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        Map<String, String[]> regionInfo = getRegionInformation();
        String[] info = regionInfo.get(regionId);
        
        if (info != null) {
            player.sendMessage(Text.literal("🏭 Main Pollution Sources:").formatted(Formatting.RED), false);
            player.sendMessage(Text.literal("• " + info[0]).formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• " + info[1]).formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("🎯 Your Mission:").formatted(Formatting.YELLOW), false);
            player.sendMessage(Text.literal("• " + info[2]).formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("• " + info[3]).formatted(Formatting.WHITE), false);
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
    }
    
    private Map<String, String[]> getRegionInformation() {
        Map<String, String[]> info = new HashMap<>();
        
        info.put("zonguldak_turkey", new String[]{
            "Coal mining dust and particulates",
            "Thermal power plant emissions (SO2, NOx)",
            "Install mine dust suppression systems",
            "Upgrade power plant with modern filters"
        });
        
        info.put("odesa_ukraine", new String[]{
            "Major port shipping emissions", 
            "Industrial area pollution (PM10, PM2.5)",
            "Optimize maritime trade operations",
            "Modernize industrial facilities"
        });
        
        info.put("trabzon_turkey", new String[]{
            "Heavy traffic in narrow mountain streets",
            "Solid fuel heating systems in homes",
            "Implement smart traffic management",
            "Convert residential heating to clean energy"
        });
        
        info.put("southeast_romania", new String[]{
            "Steel industry emissions (Galați)",
            "Multiple city pollution (PM10, NO2 exceedances)",
            "Transform industrial facilities",
            "Create regional monitoring network"
        });
        
        return info;
    }
    
    private void upgradeEquipmentForRegion(ServerPlayerEntity player, String regionId) {
        TravelData playerData = getPlayerTravelData(player.getUuid());
        
        player.sendMessage(Text.literal("🔧 EQUIPMENT UPGRADE").formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Your environmental engineering equipment has been upgraded!").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        switch (regionId) {
            case "zonguldak_turkey":
                player.sendMessage(Text.literal("+ Mining Dust Analyzer").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("+ Thermal Plant Monitoring Kit").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("+ Coal Emission Calculator").formatted(Formatting.GREEN), false);
                break;
                
            case "odesa_ukraine":
                player.sendMessage(Text.literal("+ Maritime Emission Detector").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("+ Port Activity Monitor").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("+ Industrial Air Quality Meter").formatted(Formatting.GREEN), false);
                break;
                
            case "trabzon_turkey":
                player.sendMessage(Text.literal("+ Traffic Flow Analyzer").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("+ Residential Heating Scanner").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("+ Mountain Terrain AQI Adapter").formatted(Formatting.GREEN), false);
                break;
                
            case "southeast_romania":
                player.sendMessage(Text.literal("+ Multi-City Monitoring System").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("+ Steel Industry Analyzer").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("+ Regional Data Coordinator").formatted(Formatting.GREEN), false);
                break;
        }
        
        playerData.equipmentUpgrades.put(regionId, System.currentTimeMillis());
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
    }
    
    private void showAchievementGallery(ServerPlayerEntity player, String completedRegion) {
        TravelData playerData = getPlayerTravelData(player.getUuid());
        int regionScore = playerData.regionScores.getOrDefault(completedRegion, 0);
        
        if (regionScore < 70) return; // Only show for completed regions
        
        player.sendMessage(Text.literal("🏆 ACHIEVEMENT GALLERY").formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Results from " + getRegionDisplayName(completedRegion) + ":").formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Show before and after statistics
        player.sendMessage(Text.literal("📊 BEFORE & AFTER COMPARISON:").formatted(Formatting.AQUA), false);
        
        switch (completedRegion) {
            case "varna_bulgaria":
                player.sendMessage(Text.literal("• Port emissions: 200 AQI → 85 AQI (-57%)").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("• Ships with filters: 0% → 80%").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("• Green transport usage: 15% → 65%").formatted(Formatting.GREEN), false);
                break;
                
            case "zonguldak_turkey":
                player.sendMessage(Text.literal("• Mine dust levels: 300 AQI → 120 AQI (-60%)").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("• Power plant efficiency: +25%").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("• Forest coverage: +40%").formatted(Formatting.GREEN), false);
                break;
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🌟 Region Score: " + regionScore + "/100").formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
    }
    
    public void teleportToProtectionCenter(ServerPlayerEntity player) {
        TravelData playerData = getPlayerTravelData(player.getUuid());
        
        player.sendMessage(Text.literal("🌍 Teleporting to Environmental Protection Center...").formatted(Formatting.GREEN), true);
        
        // In a real implementation, this would teleport the player
        // player.teleport(serverWorld, protectionCenter.centerLocation.getX(), 
        //                protectionCenter.centerLocation.getY(), protectionCenter.centerLocation.getZ(), 0, 0);
        
        protectionCenter.displayCenterInterface(player);
    }
    
    public boolean unlockNextRegion(ServerPlayerEntity player, String currentRegion, int score) {
        if (score < 70) return false;
        
        TravelData playerData = getPlayerTravelData(player.getUuid());
        playerData.regionScores.put(currentRegion, score);
        playerData.totalCleanAirPoints += score;
        
        // Find and activate next portal
        String nextPortalId = getNextPortalId(currentRegion);
        if (nextPortalId != null) {
            RegionPortal portal = portals.get(nextPortalId);
            if (portal != null) {
                portal.isActive = true;
                
                player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("🚪 NEW REGION UNLOCKED!").formatted(Formatting.GREEN, Formatting.BOLD), true);
                player.sendMessage(Text.literal("Portal: " + portal.displayName).formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal(portal.description).formatted(Formatting.GRAY), false);
                
                return true;
            }
        }
        
        return false;
    }
    
    private String getNextPortalId(String currentRegion) {
        switch (currentRegion) {
            case "varna_bulgaria": return "varna_to_zonguldak";
            case "zonguldak_turkey": return "zonguldak_to_odesa";
            case "odesa_ukraine": return "odesa_to_trabzon";
            case "trabzon_turkey": return "trabzon_to_romania";
            default: return null;
        }
    }
    
    private String getRegionDisplayName(String regionId) {
        switch (regionId) {
            case "varna_bulgaria": return "Varna, Bulgaria";
            case "zonguldak_turkey": return "Zonguldak, Turkey";
            case "odesa_ukraine": return "Odesa, Ukraine";
            case "trabzon_turkey": return "Trabzon, Turkey";
            case "southeast_romania": return "Southeast Romania";
            default: return regionId;
        }
    }
    
    private TravelData getPlayerTravelData(UUID playerId) {
        return playerTravelData.computeIfAbsent(playerId, k -> new TravelData());
    }
    
    public TravelData getPlayerData(ServerPlayerEntity player) {
        return getPlayerTravelData(player.getUuid());
    }
    
    public Collection<RegionPortal> getActivePortals() {
        return portals.values();
    }
    
    public RegionPortal getPortal(String portalId) {
        return portals.get(portalId);
    }
}