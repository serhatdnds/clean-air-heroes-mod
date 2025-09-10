package com.cleanairheroes.story;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;

import com.cleanairheroes.data.PlayerData;
import com.cleanairheroes.data.PlayerProgress;
import com.cleanairheroes.rewards.RewardSystem;

import java.util.*;

public class RegionalTransitionStory {
    
    public static class TransitionData {
        public final String fromRegion;
        public final String toRegion;
        public final String storyTitle;
        public final List<String> storyLines;
        public final String transportMethod;
        public final List<String> journeyActivities;
        public final List<String> newEquipment;
        public final String contactPerson;
        
        public TransitionData(String fromRegion, String toRegion, String storyTitle, 
                            List<String> storyLines, String transportMethod, 
                            List<String> journeyActivities, List<String> newEquipment, String contactPerson) {
            this.fromRegion = fromRegion;
            this.toRegion = toRegion;
            this.storyTitle = storyTitle;
            this.storyLines = storyLines;
            this.transportMethod = transportMethod;
            this.journeyActivities = journeyActivities;
            this.newEquipment = newEquipment;
            this.contactPerson = contactPerson;
        }
    }
    
    private static final Map<String, TransitionData> TRANSITIONS = new HashMap<>();
    
    static {
        initializeTransitions();
    }
    
    private static void initializeTransitions() {
        // Varna → Zonguldak Transition
        TRANSITIONS.put("varna_to_zonguldak", new TransitionData(
            "varna", "zonguldak",
            "The Coal Captain's Call",
            Arrays.asList(
                "After successfully cleaning Varna's port, you meet Captain Mehmet",
                "from a Turkish cargo vessel. His weathered face shows concern:",
                "",
                "\"Friend, I've sailed these Black Sea waters for 20 years.\"",
                "\"The coal dust in Zonguldak is getting worse each season.\"",
                "\"Ships arrive covered in black powder, and the miners...\"",
                "\"They need someone with your expertise.\"",
                "",
                "Captain Mehmet offers you passage on his cargo ship.",
                "\"We're carrying environmental equipment to Zonguldak.\"",
                "\"Perhaps you could help us install it?\""
            ),
            "Cargo Ship across the Black Sea",
            Arrays.asList(
                "Monitor ship emissions during the journey",
                "Learn about coal mining environmental impact",
                "Study Black Sea marine ecosystem effects",
                "Prepare dust suppression equipment for installation"
            ),
            Arrays.asList(
                "Industrial-grade dust monitors",
                "Mining safety equipment",
                "Coal dust suppression systems",
                "Turkish environmental regulations handbook"
            ),
            "Captain Mehmet - Environmental Transport Specialist"
        ));
        
        // Zonguldak → Odesa Transition
        TRANSITIONS.put("zonguldak_to_odesa", new TransitionData(
            "zonguldak", "odesa",
            "The Research Partnership",
            Arrays.asList(
                "Your success with Çatalağzı's mine dust control catches the attention",
                "of Dr. Ayşe Demir, a local environmental scientist:",
                "",
                "\"Your innovative approaches have impressed the international community.\"",
                "\"My colleague, Professor Kovalenko at Odesa University,\"",
                "\"is leading a groundbreaking marine pollution research project.\"",
                "",
                "\"They desperately need an environmental engineer\"",
                "\"with your practical experience for their Black Sea study.\"",
                "",
                "Dr. Demir arranges your passage on a research vessel:",
                "\"This ship is equipped with the latest monitoring technology.\""
            ),
            "Research Vessel 'Black Sea Explorer'",
            Arrays.asList(
                "Conduct water quality research during transit",
                "Study marine pollution source identification",
                "Test advanced emission monitoring equipment",
                "Collaborate with international research team"
            ),
            Arrays.asList(
                "Marine pollution detection sensors",
                "Water quality analysis kit",
                "International research credentials",
                "University partnership documentation"
            ),
            "Dr. Ayşe Demir - Marine Research Coordinator"
        ));
        
        // Odesa → Trabzon Transition
        TRANSITIONS.put("odesa_to_trabzon", new TransitionData(
            "odesa", "trabzon",
            "The DOKA Partnership",
            Arrays.asList(
                "While analyzing air quality data at Odesa University,",
                "you notice alarming pollution levels from Trabzon:",
                "",
                "Professor Kovalenko approaches with a concerned expression:",
                "\"These readings from Trabzon are deeply troubling.\"",
                "\"The mountainous terrain is trapping pollutants in the city.\"",
                "",
                "\"I've contacted my colleague at DOKA Environmental Agency.\"",
                "\"They specifically requested an engineer with your expertise\"",
                "\"for an urgent air quality intervention project.\"",
                "",
                "A DOKA agency vessel arrives at Odesa port:",
                "\"We're part of a Black Sea environmental protection initiative.\""
            ),
            "DOKA Environmental Protection Vessel",
            Arrays.asList(
                "Study coastal air quality measurement techniques",
                "Learn about mountain terrain pollution challenges",
                "Review DOKA environmental protocols",
                "Prepare smart city air quality solutions"
            ),
            Arrays.asList(
                "DOKA official partnership credentials",
                "Smart city monitoring systems",
                "Mountain terrain analysis tools",
                "Traffic pollution assessment equipment"
            ),
            "DOKA Agent Mehmet Çelik - Regional Coordinator"
        ));
        
        // Trabzon → Romania Transition
        TRANSITIONS.put("trabzon_to_romania", new TransitionData(
            "trabzon", "romania",
            "The European Green Corridor",
            Arrays.asList(
                "Your successful smart traffic implementation in Trabzon",
                "earns you an invitation to the International Environmental Conference:",
                "",
                "DOKA Agent Çelik presents you with an official document:",
                "\"Your work has been recognized at the EU level.\"",
                "\"Romanian environmental representatives specifically\"",
                "\"requested your expertise for their industrial region.\"",
                "",
                "\"Constanta, Galați, and Brașov are facing severe challenges\"",
                "\"with PM10 and NO2 levels exceeding EU standards.\"",
                "",
                "\"This is part of the 'European Green Corridor' initiative.\"",
                "\"A special transport has been arranged for you.\""
            ),
            "European Green Corridor Transport",
            Arrays.asList(
                "Study EU environmental regulations during travel",
                "Review multi-city pollution coordination strategies",
                "Prepare for large-scale industrial transformation",
                "Learn about cross-border environmental cooperation"
            ),
            Arrays.asList(
                "EU environmental policy documentation",
                "Multi-city coordination systems",
                "Industrial transformation toolkit",
                "Cross-border partnership credentials"
            ),
            "EU Environmental Coordinator Maria Ionescu"
        ));
    }
    
    public static void initiateTransition(ServerPlayerEntity player, String targetRegion) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        String currentRegion = progress.getCurrentRegion();
        String transitionKey = currentRegion + "_to_" + targetRegion;
        
        TransitionData transition = TRANSITIONS.get(transitionKey);
        if (transition == null) {
            player.sendMessage(Text.literal("⚠️ No story available for this transition!").formatted(Formatting.RED), false);
            return;
        }
        
        // Check if player meets requirements
        if (progress.getRegionScore(currentRegion) < 70) {
            player.sendMessage(Text.literal("🔒 Complete more missions in " + currentRegion.toUpperCase() + " before traveling!").formatted(Formatting.RED), false);
            return;
        }
        
        showTransitionStory(player, transition);
    }
    
    private static void showTransitionStory(ServerPlayerEntity player, TransitionData transition) {
        player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.MASTER, 1.0f, 1.0f);
        
        // Story introduction
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("═══════════════════════════════").formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("📖 " + transition.storyTitle).formatted(Formatting.AQUA, Formatting.BOLD), false);
        player.sendMessage(Text.literal("═══════════════════════════════").formatted(Formatting.GOLD), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Tell the story
        for (String line : transition.storyLines) {
            if (line.isEmpty()) {
                player.sendMessage(Text.literal(""), false);
            } else if (line.startsWith("\"")) {
                player.sendMessage(Text.literal(line).formatted(Formatting.YELLOW, Formatting.ITALIC), false);
            } else {
                player.sendMessage(Text.literal(line).formatted(Formatting.WHITE), false);
            }
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Journey details
        player.sendMessage(Text.literal("🚢 Transport Method:").formatted(Formatting.BLUE, Formatting.BOLD), false);
        player.sendMessage(Text.literal(transition.transportMethod).formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        player.sendMessage(Text.literal("⚓ Journey Activities:").formatted(Formatting.GREEN, Formatting.BOLD), false);
        for (String activity : transition.journeyActivities) {
            player.sendMessage(Text.literal("• " + activity).formatted(Formatting.GREEN), false);
        }
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // New equipment preview
        player.sendMessage(Text.literal("🎒 New Equipment Acquired:").formatted(Formatting.YELLOW, Formatting.BOLD), false);
        for (String equipment : transition.newEquipment) {
            player.sendMessage(Text.literal("• " + equipment).formatted(Formatting.YELLOW), false);
        }
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Contact information
        player.sendMessage(Text.literal("👤 Regional Contact:").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), false);
        player.sendMessage(Text.literal(transition.contactPerson).formatted(Formatting.LIGHT_PURPLE), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Complete the transition
        completeTransition(player, transition);
    }
    
    private static void completeTransition(ServerPlayerEntity player, TransitionData transition) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        
        // Play journey sound
        player.playSound(SoundEvents.ITEM_TRIDENT_RIPTIDE_3, SoundCategory.MASTER, 0.7f, 1.2f);
        
        // Journey simulation
        player.sendMessage(Text.literal("🌊 Beginning journey to " + transition.toRegion.toUpperCase() + "...").formatted(Formatting.AQUA, Formatting.BOLD), false);
        
        // Simulate journey activities and provide mini-rewards
        for (int i = 0; i < transition.journeyActivities.size(); i++) {
            String activity = transition.journeyActivities.get(i);
            player.sendMessage(Text.literal("📍 " + activity).formatted(Formatting.GRAY), false);
            
            // Small learning bonus for each activity
            progress.addScore(transition.fromRegion, 2);
            player.sendMessage(Text.literal("  +2 experience points").formatted(Formatting.GREEN), false);
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Equipment upgrades
        player.sendMessage(Text.literal("🔧 Equipment Upgrade Complete!").formatted(Formatting.GOLD, Formatting.BOLD), false);
        for (String equipment : transition.newEquipment) {
            player.sendMessage(Text.literal("✓ " + equipment + " added to inventory").formatted(Formatting.GREEN), false);
            progress.addEquipmentInstalled(1);
        }
        
        // Update player region
        progress.setCurrentRegion(transition.toRegion);
        progress.unlockRegion(transition.toRegion);
        PlayerData.updatePlayer(player, progress);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Arrival announcement
        player.playSound(SoundEvents.BLOCK_BELL_USE, SoundCategory.MASTER, 1.0f, 1.0f);
        player.sendMessage(Text.literal("🎯 ARRIVAL: " + transition.toRegion.toUpperCase()).formatted(Formatting.GREEN, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Your reputation as a Clean Air Hero precedes you!").formatted(Formatting.AQUA, Formatting.ITALIC), false);
        player.sendMessage(Text.literal("Local environmental teams are eager to work with you.").formatted(Formatting.YELLOW), false);
        
        // Show region welcome message
        showRegionWelcome(player, transition.toRegion);
        
        // Check for transition completion rewards
        RewardSystem.checkAndAwardLevelRewards(player);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Use '/cleanair profile' to see your updated status!").formatted(Formatting.GRAY), false);
    }
    
    private static void showRegionWelcome(ServerPlayerEntity player, String region) {
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        switch (region) {
            case "zonguldak" -> {
                player.sendMessage(Text.literal("🏭 Welcome to ZONGULDAK, TURKEY").formatted(Formatting.RED, Formatting.BOLD), false);
                player.sendMessage(Text.literal("Coal mining capital facing severe dust pollution").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("Priority: Mine dust control and thermal plant modernization").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("Expected impact: 2,400 tons/day coal extraction improvement").formatted(Formatting.GREEN), false);
            }
            case "odesa" -> {
                player.sendMessage(Text.literal("🌊 Welcome to ODESA, UKRAINE").formatted(Formatting.BLUE, Formatting.BOLD), false);
                player.sendMessage(Text.literal("Major Black Sea port with marine pollution challenges").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("Priority: Port emission control and research collaboration").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("Expected impact: 2000+ vessels/month emission reduction").formatted(Formatting.GREEN), false);
            }
            case "trabzon" -> {
                player.sendMessage(Text.literal("🏔️ Welcome to TRABZON, TURKEY").formatted(Formatting.DARK_GREEN, Formatting.BOLD), false);
                player.sendMessage(Text.literal("Mountain city with severe traffic pollution").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("Priority: Smart traffic systems and urban air quality").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("Expected impact: 85,000 vehicles/day emission control").formatted(Formatting.GREEN), false);
            }
            case "romania" -> {
                player.sendMessage(Text.literal("🏛️ Welcome to SOUTHEAST ROMANIA").formatted(Formatting.GOLD, Formatting.BOLD), false);
                player.sendMessage(Text.literal("Multi-city region: Constanta, Galați, Brașov").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("Priority: Industrial transformation and regional coordination").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("Expected impact: EU air quality standards compliance").formatted(Formatting.GREEN), false);
            }
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🎯 New missions await! Use '/cleanair mission progress' to begin.").formatted(Formatting.AQUA), false);
    }
    
    public static void showAvailableTransitions(ServerPlayerEntity player) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        String currentRegion = progress.getCurrentRegion();
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🗺️ AVAILABLE TRANSITIONS").formatted(Formatting.AQUA, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Current Region: " + currentRegion.toUpperCase()).formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        String nextRegion = progress.getNextRegion();
        if (nextRegion != null) {
            int currentScore = progress.getRegionScore(currentRegion);
            if (currentScore >= 70) {
                player.sendMessage(Text.literal("✅ Ready for transition to: " + nextRegion.toUpperCase()).formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("Use '/cleanair travel' to begin your journey!").formatted(Formatting.AQUA), false);
            } else {
                int needed = 70 - currentScore;
                player.sendMessage(Text.literal("🔒 Next region: " + nextRegion.toUpperCase()).formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("Need " + needed + " more points to unlock").formatted(Formatting.RED), false);
            }
        } else {
            player.sendMessage(Text.literal("🏆 All regions completed! You are a Master Clean Air Hero!").formatted(Formatting.GOLD), false);
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
    }
}