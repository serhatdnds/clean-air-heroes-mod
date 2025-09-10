package com.cleanairheroes.rewards;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;

import com.cleanairheroes.CleanAirHeroes;
import com.cleanairheroes.data.PlayerData;
import com.cleanairheroes.data.PlayerProgress;
import com.cleanairheroes.item.ModItems;

import java.util.*;

public class RewardSystem {
    
    private static final Map<String, RegionRewards> REGION_REWARDS = new HashMap<>();
    private static final Map<Integer, LevelReward> LEVEL_REWARDS = new HashMap<>();
    
    static {
        initializeRegionRewards();
        initializeLevelRewards();
    }
    
    public static class RegionRewards {
        public final String regionName;
        public final List<ItemStack> completionItems;
        public final List<String> unlockables;
        public final int bonusPoints;
        public final String title;
        public final String certificate;
        
        public RegionRewards(String regionName, List<ItemStack> items, List<String> unlockables, 
                           int bonusPoints, String title, String certificate) {
            this.regionName = regionName;
            this.completionItems = items;
            this.unlockables = unlockables;
            this.bonusPoints = bonusPoints;
            this.title = title;
            this.certificate = certificate;
        }
    }
    
    public static class LevelReward {
        public final int level;
        public final String title;
        public final List<ItemStack> items;
        public final List<String> perks;
        public final String badge;
        
        public LevelReward(int level, String title, List<ItemStack> items, List<String> perks, String badge) {
            this.level = level;
            this.title = title;
            this.items = items;
            this.perks = perks;
            this.badge = badge;
        }
    }
    
    private static void initializeRegionRewards() {
        // Varna, Bulgaria - Port Pollution Specialist
        REGION_REWARDS.put("varna", new RegionRewards(
            "Varna Port Authority",
            Arrays.asList(
                new ItemStack(ModItems.AIR_QUALITY_MONITOR),
                new ItemStack(ModItems.EMISSION_SCANNER),
                new ItemStack(Items.SPYGLASS), // Marine observation
                new ItemStack(Items.MAP) // Port navigation
            ),
            Arrays.asList(
                "Advanced ship emission monitoring",
                "Port authority communications",
                "Marine pollution database access",
                "Bulgarian Environmental Research Institute partnership"
            ),
            50,
            "Black Sea Port Specialist",
            "Port Environmental Compliance Certificate"
        ));
        
        // Zonguldak, Turkey - Mining Pollution Expert
        REGION_REWARDS.put("zonguldak", new RegionRewards(
            "Turkish Mining Environmental Agency",
            Arrays.asList(
                new ItemStack(ModItems.POLLUTION_FILTER, 3),
                new ItemStack(Items.IRON_PICKAXE), // Mining equipment
                new ItemStack(Items.GOLDEN_HELMET), // Safety equipment
                new ItemStack(Items.REDSTONE, 16) // Control systems
            ),
            Arrays.asList(
                "Coal dust suppression technology",
                "Mining safety equipment access",
                "Industrial pollution control systems",
                "Çatalağzı Power Plant collaboration"
            ),
            75,
            "Industrial Pollution Control Expert",
            "Turkish Environmental Engineering License"
        ));
        
        // Odesa, Ukraine - Research Coordinator
        REGION_REWARDS.put("odesa", new RegionRewards(
            "Odesa National University Marine Lab",
            Arrays.asList(
                new ItemStack(Items.BOOK, 5), // Research materials
                new ItemStack(ModItems.EMISSION_SCANNER, 2),
                new ItemStack(Items.COMPASS), // Navigation
                new ItemStack(Items.FILLED_MAP) // Research mapping
            ),
            Arrays.asList(
                "University research database access",
                "International scientific collaboration",
                "Marine ecosystem monitoring tools",
                "Black Sea research station network"
            ),
            100,
            "Marine Environmental Research Coordinator",
            "International Research Collaboration Certificate"
        ));
        
        // Trabzon, Turkey - Urban Air Quality Manager
        REGION_REWARDS.put("trabzon", new RegionRewards(
            "DOKA Environmental Management",
            Arrays.asList(
                new ItemStack(ModItems.AIR_QUALITY_MONITOR, 2),
                new ItemStack(Items.MINECART), // Urban transport
                new ItemStack(Items.POWERED_RAIL, 8), // Infrastructure
                new ItemStack(Items.REDSTONE_TORCH, 4) // Monitoring systems
            ),
            Arrays.asList(
                "DOKA official partnership status",
                "Urban planning consultation access",
                "Traffic management system control",
                "Mountain terrain expertise recognition"
            ),
            125,
            "DOKA Urban Air Quality Manager",
            "Turkish Government Environmental Partnership"
        ));
        
        // Southeast Romania - Regional Environmental Director
        REGION_REWARDS.put("romania", new RegionRewards(
            "Romanian Environmental Protection Agency",
            Arrays.asList(
                new ItemStack(Items.GOLDEN_APPLE, 2), // Achievement recognition
                new ItemStack(ModItems.POLLUTION_FILTER, 5),
                new ItemStack(Items.ELYTRA), // Regional mobility
                new ItemStack(Items.FIREWORK_ROCKET, 16) // Celebration
            ),
            Arrays.asList(
                "EU environmental policy influence",
                "Cross-border pollution management",
                "Industrial transformation leadership",
                "Regional environmental director authority"
            ),
            150,
            "Regional Environmental Director",
            "EU Environmental Leadership Recognition"
        ));
    }
    
    private static void initializeLevelRewards() {
        // Level-based rewards for overall progress
        LEVEL_REWARDS.put(50, new LevelReward(50, "Environmental Volunteer",
            Arrays.asList(new ItemStack(ModItems.AIR_QUALITY_MONITOR)),
            Arrays.asList("Basic monitoring access", "Community education materials"),
            "🌱 Volunteer"
        ));
        
        LEVEL_REWARDS.put(100, new LevelReward(100, "Clean Air Advocate",
            Arrays.asList(new ItemStack(ModItems.EMISSION_SCANNER), new ItemStack(Items.IRON_INGOT, 5)),
            Arrays.asList("Equipment upgrade access", "Local partnership opportunities"),
            "🌿 Advocate"
        ));
        
        LEVEL_REWARDS.put(200, new LevelReward(200, "Pollution Control Specialist",
            Arrays.asList(new ItemStack(ModItems.POLLUTION_FILTER, 2), new ItemStack(Items.DIAMOND, 2)),
            Arrays.asList("Advanced equipment authorization", "Technical consultation access"),
            "⚙️ Specialist"
        ));
        
        LEVEL_REWARDS.put(350, new LevelReward(350, "Senior Environmental Engineer",
            Arrays.asList(new ItemStack(Items.NETHERITE_INGOT), new ItemStack(Items.BEACON)),
            Arrays.asList("Industrial collaboration access", "Research partnership eligibility"),
            "🔬 Engineer"
        ));
        
        LEVEL_REWARDS.put(500, new LevelReward(500, "Master Clean Air Hero",
            Arrays.asList(new ItemStack(Items.NETHER_STAR), new ItemStack(Items.END_CRYSTAL)),
            Arrays.asList("Full system administrative access", "International recognition"),
            "🏆 Master Hero"
        ));
    }
    
    public static void checkAndAwardRegionCompletion(ServerPlayerEntity player, String region) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        
        if (progress.getRegionScore(region) >= 70 && !hasReceivedRegionReward(progress, region)) {
            awardRegionCompletion(player, region);
            markRegionRewardReceived(progress, region);
        }
    }
    
    public static void checkAndAwardLevelRewards(ServerPlayerEntity player) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        int totalScore = progress.getTotalScore();
        
        for (Map.Entry<Integer, LevelReward> entry : LEVEL_REWARDS.entrySet()) {
            int requiredScore = entry.getKey();
            if (totalScore >= requiredScore && !hasReceivedLevelReward(progress, requiredScore)) {
                awardLevelReward(player, entry.getValue());
                markLevelRewardReceived(progress, requiredScore);
            }
        }
    }
    
    private static void awardRegionCompletion(ServerPlayerEntity player, String region) {
        RegionRewards rewards = REGION_REWARDS.get(region);
        if (rewards == null) return;
        
        // Celebration effects
        player.playSound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 1.0f, 1.0f);
        
        // Award announcement
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🎉 REGION COMPLETED! 🎉").formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.sendMessage(Text.literal(rewards.regionName + " - " + region.toUpperCase()).formatted(Formatting.AQUA, Formatting.BOLD), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Award title
        player.sendMessage(Text.literal("🏅 New Title Earned: " + rewards.title).formatted(Formatting.YELLOW, Formatting.BOLD), false);
        
        // Award certificate
        player.sendMessage(Text.literal("📜 Certificate: " + rewards.certificate).formatted(Formatting.GREEN), false);
        
        // Award items
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🎁 Equipment Rewards:").formatted(Formatting.BLUE, Formatting.BOLD), false);
        for (ItemStack item : rewards.completionItems) {
            player.getInventory().insertStack(item.copy());
            player.sendMessage(Text.literal("+ " + item.getCount() + "x " + 
                item.getItem().getName().getString()).formatted(Formatting.GREEN), false);
        }
        
        // Award bonus points
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        progress.addScore(region, rewards.bonusPoints);
        PlayerData.updatePlayer(player, progress);
        player.sendMessage(Text.literal("+ " + rewards.bonusPoints + " Bonus Points!").formatted(Formatting.GOLD), false);
        
        // Show unlocked capabilities
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🔓 New Capabilities Unlocked:").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), false);
        for (String unlock : rewards.unlockables) {
            player.sendMessage(Text.literal("• " + unlock).formatted(Formatting.LIGHT_PURPLE), false);
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("Your expertise in " + region + " is now recognized internationally!")
            .formatted(Formatting.GREEN, Formatting.ITALIC), false);
    }
    
    private static void awardLevelReward(ServerPlayerEntity player, LevelReward reward) {
        // Celebration effects
        player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1.0f, 1.2f);
        
        // Level up announcement
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("⭐ LEVEL UP! ⭐").formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.sendMessage(Text.literal("New Title: " + reward.title).formatted(Formatting.YELLOW, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Badge: " + reward.badge).formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Award items
        if (!reward.items.isEmpty()) {
            player.sendMessage(Text.literal("🎁 Level Rewards:").formatted(Formatting.BLUE, Formatting.BOLD), false);
            for (ItemStack item : reward.items) {
                player.getInventory().insertStack(item.copy());
                player.sendMessage(Text.literal("+ " + item.getCount() + "x " + 
                    item.getItem().getName().getString()).formatted(Formatting.GREEN), false);
            }
        }
        
        // Show new perks
        if (!reward.perks.isEmpty()) {
            player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
            player.sendMessage(Text.literal("🔓 New Perks Unlocked:").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD), false);
            for (String perk : reward.perks) {
                player.sendMessage(Text.literal("• " + perk).formatted(Formatting.LIGHT_PURPLE), false);
            }
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
    }
    
    // Track which rewards have been received
    private static boolean hasReceivedRegionReward(PlayerProgress progress, String region) {
        return progress.getCompletedMissions().contains("region_reward_" + region);
    }
    
    private static void markRegionRewardReceived(PlayerProgress progress, String region) {
        progress.addCompletedMission("region_reward_" + region);
    }
    
    private static boolean hasReceivedLevelReward(PlayerProgress progress, int level) {
        return progress.getCompletedMissions().contains("level_reward_" + level);
    }
    
    private static void markLevelRewardReceived(PlayerProgress progress, int level) {
        progress.addCompletedMission("level_reward_" + level);
    }
    
    // Public API methods
    public static String getCurrentTitle(PlayerProgress progress) {
        int totalScore = progress.getTotalScore();
        
        for (int level : Arrays.asList(500, 350, 200, 100, 50)) {
            if (totalScore >= level) {
                return LEVEL_REWARDS.get(level).title;
            }
        }
        
        return "Environmental Volunteer";
    }
    
    public static String getCurrentBadge(PlayerProgress progress) {
        int totalScore = progress.getTotalScore();
        
        for (int level : Arrays.asList(500, 350, 200, 100, 50)) {
            if (totalScore >= level) {
                return LEVEL_REWARDS.get(level).badge;
            }
        }
        
        return "🌱 Volunteer";
    }
    
    public static void showRewardProgress(ServerPlayerEntity player) {
        PlayerProgress progress = PlayerData.getPlayerProgress(player);
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("🏆 REWARD PROGRESS").formatted(Formatting.GOLD, Formatting.BOLD), false);
        player.sendMessage(Text.literal("Current Title: " + getCurrentTitle(progress)).formatted(Formatting.YELLOW), false);
        player.sendMessage(Text.literal("Current Badge: " + getCurrentBadge(progress)).formatted(Formatting.AQUA), false);
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        
        // Show next level reward
        int totalScore = progress.getTotalScore();
        for (int level : Arrays.asList(50, 100, 200, 350, 500)) {
            if (totalScore < level) {
                LevelReward nextReward = LEVEL_REWARDS.get(level);
                player.sendMessage(Text.literal("🎯 Next Level: " + nextReward.title).formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("Required: " + level + " points (Current: " + totalScore + ")").formatted(Formatting.YELLOW), false);
                break;
            }
        }
        
        // Show region completion status
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
        player.sendMessage(Text.literal("📍 Region Completion Status:").formatted(Formatting.BLUE, Formatting.BOLD), false);
        
        String[] regions = {"varna", "zonguldak", "odesa", "trabzon", "romania"};
        for (String region : regions) {
            int score = progress.getRegionScore(region);
            boolean completed = score >= 70;
            boolean rewarded = hasReceivedRegionReward(progress, region);
            
            String status = completed ? (rewarded ? "✅ Completed & Rewarded" : "🎁 Reward Available!") : "⏳ In Progress (" + score + "/70)";
            Formatting color = completed ? (rewarded ? Formatting.GREEN : Formatting.GOLD) : Formatting.YELLOW;
            
            player.sendMessage(Text.literal("• " + region.toUpperCase() + ": " + status).formatted(color), false);
        }
        
        player.sendMessage(Text.literal("").formatted(Formatting.WHITE), false);
    }
}