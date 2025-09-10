package com.cleanairheroes.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.cleanairheroes.CleanAirHeroes;

public class ModItems {
    
    // Environmental Engineering Tools
    public static final Item AIR_QUALITY_METER = registerItem("air_quality_meter",
        new AirQualityMeterItem(new Item.Settings().maxCount(1)));
    
    public static final Item EMISSION_ANALYZER = registerItem("emission_analyzer",
        new EmissionAnalyzerItem(new Item.Settings().maxCount(1)));
    
    public static final Item POLLUTION_DETECTOR = registerItem("pollution_detector",
        new PollutionDetectorItem(new Item.Settings().maxCount(1)));
    
    // Mission-Specific Equipment
    public static final Item SHIP_FILTER_KIT = registerItem("ship_filter_kit",
        new ShipFilterKitItem(new Item.Settings().maxCount(64)));
    
    public static final Item INDUSTRIAL_FILTER_KIT = registerItem("industrial_filter_kit",
        new IndustrialFilterKitItem(new Item.Settings().maxCount(32)));
    
    public static final Item DUST_SUPPRESSION_KIT = registerItem("dust_suppression_kit",
        new DustSuppressionKitItem(new Item.Settings().maxCount(32)));
    
    public static final Item SOLAR_HEATING_KIT = registerItem("solar_heating_kit",
        new SolarHeatingKitItem(new Item.Settings().maxCount(16)));
    
    public static final Item EV_CHARGING_KIT = registerItem("ev_charging_kit",
        new EVChargingKitItem(new Item.Settings().maxCount(8)));
    
    // Region-Specific Tools
    public static final Item MINING_DUST_ANALYZER = registerItem("mining_dust_analyzer",
        new MiningAnalyzerItem(new Item.Settings().maxCount(1)));
    
    public static final Item MARITIME_EMISSION_DETECTOR = registerItem("maritime_emission_detector",
        new MaritimeDetectorItem(new Item.Settings().maxCount(1)));
    
    public static final Item TRAFFIC_FLOW_ANALYZER = registerItem("traffic_flow_analyzer",
        new TrafficAnalyzerItem(new Item.Settings().maxCount(1)));
    
    public static final Item MULTI_CITY_MONITOR = registerItem("multi_city_monitor",
        new MultiCityMonitorItem(new Item.Settings().maxCount(1)));
    
    // Certificates and Documentation
    public static final Item CLEAN_AIR_CERTIFICATE = registerItem("clean_air_certificate",
        new CertificateItem(new Item.Settings().maxCount(1)));
    
    public static final Item ENVIRONMENTAL_HANDBOOK = registerItem("environmental_handbook",
        new HandbookItem(new Item.Settings().maxCount(1)));
    
    // Travel Items
    public static final Item REGION_PORTAL_KEY = registerItem("region_portal_key",
        new PortalKeyItem(new Item.Settings().maxCount(1)));
    
    // Custom Item Classes
    public static class AirQualityMeterItem extends Item {
        public AirQualityMeterItem(Settings settings) {
            super(settings);
        }
        
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            World world = context.getWorld();
            BlockPos pos = context.getBlockPos();
            
            if (!world.isClient && context.getPlayer() instanceof ServerPlayerEntity player) {
                double aqi = CleanAirHeroes.getPollutionManager().getAQIAt(pos);
                
                player.sendMessage(Text.literal("📊 Air Quality Reading").formatted(Formatting.BLUE, Formatting.BOLD), false);
                player.sendMessage(Text.literal("Location: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()).formatted(Formatting.GRAY), false);
                player.sendMessage(Text.literal("AQI: " + String.format("%.1f", aqi)).formatted(Formatting.WHITE), false);
                
                String category = getAQICategory(aqi);
                Formatting color = getAQIColor(aqi);
                player.sendMessage(Text.literal("Status: " + category).formatted(color), false);
                
                // Add to player statistics
                // CleanAirHeroes.getAchievementSystem().updatePlayerProgress(player, "measurements_taken", 1);
                
                return ActionResult.SUCCESS;
            }
            
            return ActionResult.PASS;
        }
        
        private String getAQICategory(double aqi) {
            if (aqi <= 50) return "Good";
            if (aqi <= 100) return "Moderate";
            if (aqi <= 150) return "Unhealthy for Sensitive Groups";
            if (aqi <= 200) return "Unhealthy";
            if (aqi <= 300) return "Very Unhealthy";
            return "Hazardous";
        }
        
        private Formatting getAQIColor(double aqi) {
            if (aqi <= 50) return Formatting.GREEN;
            if (aqi <= 100) return Formatting.YELLOW;
            if (aqi <= 150) return Formatting.GOLD;
            if (aqi <= 200) return Formatting.RED;
            return Formatting.DARK_RED;
        }
    }
    
    public static class EmissionAnalyzerItem extends Item {
        public EmissionAnalyzerItem(Settings settings) {
            super(settings);
        }
        
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            if (!context.getWorld().isClient && context.getPlayer() instanceof ServerPlayerEntity player) {
                BlockPos pos = context.getBlockPos();
                
                player.sendMessage(Text.literal("🔬 Emission Analysis").formatted(Formatting.GREEN, Formatting.BOLD), false);
                player.sendMessage(Text.literal("Analyzing emission sources...").formatted(Formatting.YELLOW), false);
                
                // Simulate emission analysis
                player.sendMessage(Text.literal("PM2.5: 25.3 μg/m³").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("NO2: 18.7 μg/m³").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("SO2: 12.1 μg/m³").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("CO: 8.4 mg/m³").formatted(Formatting.WHITE), false);
                
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
    }
    
    public static class PollutionDetectorItem extends Item {
        public PollutionDetectorItem(Settings settings) {
            super(settings);
        }
        
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            if (!context.getWorld().isClient && context.getPlayer() instanceof ServerPlayerEntity player) {
                BlockPos pos = context.getBlockPos();
                
                player.sendMessage(Text.literal("🚨 Pollution Detection Scan").formatted(Formatting.RED, Formatting.BOLD), false);
                player.sendMessage(Text.literal("Scanning 100m radius...").formatted(Formatting.YELLOW), false);
                
                // Find nearby pollution sources
                player.sendMessage(Text.literal("Sources detected:").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("• Industrial smokestack (150m NE)").formatted(Formatting.RED), false);
                player.sendMessage(Text.literal("• Vehicle emissions (80m S)").formatted(Formatting.YELLOW), false);
                player.sendMessage(Text.literal("• Residential heating (120m W)").formatted(Formatting.GOLD), false);
                
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
    }
    
    public static class ShipFilterKitItem extends Item {
        public ShipFilterKitItem(Settings settings) {
            super(settings);
        }
        
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            if (!context.getWorld().isClient && context.getPlayer() instanceof ServerPlayerEntity player) {
                player.sendMessage(Text.literal("🚢 Installing Ship Emission Filter...").formatted(Formatting.BLUE), false);
                
                // Reduce ship emissions in area
                CleanAirHeroes.getPollutionManager().reducePollutionInRadius(context.getBlockPos(), 50, 0.2);
                
                player.sendMessage(Text.literal("✅ Ship filter installed successfully!").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("Emission reduction: 20% in 50m radius").formatted(Formatting.YELLOW), false);
                
                // Update mission progress
                // CleanAirHeroes.getAchievementSystem().updatePlayerProgress(player, "ships_filtered", 1);
                
                // Consume item
                context.getStack().decrement(1);
                
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
    }
    
    public static class IndustrialFilterKitItem extends Item {
        public IndustrialFilterKitItem(Settings settings) {
            super(settings);
        }
        
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            if (!context.getWorld().isClient && context.getPlayer() instanceof ServerPlayerEntity player) {
                player.sendMessage(Text.literal("🏭 Installing Industrial Filter System...").formatted(Formatting.RED), false);
                
                // Significantly reduce industrial emissions
                CleanAirHeroes.getPollutionManager().reducePollutionInRadius(context.getBlockPos(), 100, 0.4);
                
                player.sendMessage(Text.literal("✅ Industrial filter installed!").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("Major emission reduction: 40% in 100m radius").formatted(Formatting.YELLOW), false);
                
                // Update statistics
                // CleanAirHeroes.getAchievementSystem().updatePlayerProgress(player, "factories_upgraded", 1);
                
                context.getStack().decrement(1);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
    }
    
    public static class DustSuppressionKitItem extends Item {
        public DustSuppressionKitItem(Settings settings) {
            super(settings);
        }
        
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            if (!context.getWorld().isClient && context.getPlayer() instanceof ServerPlayerEntity player) {
                player.sendMessage(Text.literal("⛏️ Installing Dust Suppression System...").formatted(Formatting.GRAY), false);
                
                // Reduce mining dust
                CleanAirHeroes.getPollutionManager().reducePollutionInRadius(context.getBlockPos(), 80, 0.35);
                
                player.sendMessage(Text.literal("✅ Dust suppression system active!").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("Particulate reduction: 35% in mining area").formatted(Formatting.YELLOW), false);
                
                context.getStack().decrement(1);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
    }
    
    public static class SolarHeatingKitItem extends Item {
        public SolarHeatingKitItem(Settings settings) {
            super(settings);
        }
        
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            if (!context.getWorld().isClient && context.getPlayer() instanceof ServerPlayerEntity player) {
                player.sendMessage(Text.literal("☀️ Installing Solar Heating System...").formatted(Formatting.GOLD), false);
                
                // Reduce residential heating emissions
                CleanAirHeroes.getPollutionManager().reducePollutionInRadius(context.getBlockPos(), 30, 0.25);
                
                player.sendMessage(Text.literal("✅ Solar heating system installed!").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("Clean energy conversion complete").formatted(Formatting.YELLOW), false);
                
                // Update home conversion statistics
                // CleanAirHeroes.getAchievementSystem().updatePlayerProgress(player, "homes_converted", 1);
                
                context.getStack().decrement(1);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
    }
    
    public static class EVChargingKitItem extends Item {
        public EVChargingKitItem(Settings settings) {
            super(settings);
        }
        
        @Override
        public ActionResult useOnBlock(ItemUsageContext context) {
            if (!context.getWorld().isClient && context.getPlayer() instanceof ServerPlayerEntity player) {
                player.sendMessage(Text.literal("⚡ Installing EV Charging Station...").formatted(Formatting.BLUE), false);
                
                // Reduce traffic emissions
                CleanAirHeroes.getPollutionManager().reducePollutionInRadius(context.getBlockPos(), 40, 0.15);
                
                player.sendMessage(Text.literal("✅ EV charging station operational!").formatted(Formatting.GREEN), false);
                player.sendMessage(Text.literal("Supporting electric vehicle adoption").formatted(Formatting.YELLOW), false);
                
                context.getStack().decrement(1);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
    }
    
    // Region-specific tools
    public static class MiningAnalyzerItem extends Item {
        public MiningAnalyzerItem(Settings settings) {
            super(settings);
        }
    }
    
    public static class MaritimeDetectorItem extends Item {
        public MaritimeDetectorItem(Settings settings) {
            super(settings);
        }
    }
    
    public static class TrafficAnalyzerItem extends Item {
        public TrafficAnalyzerItem(Settings settings) {
            super(settings);
        }
    }
    
    public static class MultiCityMonitorItem extends Item {
        public MultiCityMonitorItem(Settings settings) {
            super(settings);
        }
    }
    
    public static class CertificateItem extends Item {
        public CertificateItem(Settings settings) {
            super(settings);
        }
        
        @Override
        public ActionResult use(World world, net.minecraft.entity.player.PlayerEntity user, Hand hand) {
            if (!world.isClient && user instanceof ServerPlayerEntity player) {
                ItemStack stack = user.getStackInHand(hand);
                
                player.sendMessage(Text.literal("📜 CLEAN AIR HERO CERTIFICATE").formatted(Formatting.GOLD, Formatting.BOLD), false);
                player.sendMessage(Text.literal(""), false);
                player.sendMessage(Text.literal("This certifies that").formatted(Formatting.GRAY), false);
                player.sendMessage(Text.literal(player.getName().getString().toUpperCase()).formatted(Formatting.YELLOW, Formatting.BOLD), false);
                player.sendMessage(Text.literal("has successfully demonstrated exceptional").formatted(Formatting.GRAY), false);
                player.sendMessage(Text.literal("environmental leadership and expertise").formatted(Formatting.GRAY), false);
                player.sendMessage(Text.literal("in the fight against air pollution.").formatted(Formatting.GRAY), false);
                player.sendMessage(Text.literal(""), false);
                player.sendMessage(Text.literal("🌟 CLEAN AIR HERO STATUS ACHIEVED 🌟").formatted(Formatting.GREEN, Formatting.BOLD), false);
                
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
    }
    
    public static class HandbookItem extends Item {
        public HandbookItem(Settings settings) {
            super(settings);
        }
        
        @Override
        public ActionResult use(World world, net.minecraft.entity.player.PlayerEntity user, Hand hand) {
            if (!world.isClient && user instanceof ServerPlayerEntity player) {
                player.sendMessage(Text.literal("📖 ENVIRONMENTAL PROTECTION HANDBOOK").formatted(Formatting.GREEN, Formatting.BOLD), false);
                player.sendMessage(Text.literal(""), false);
                player.sendMessage(Text.literal("Real-world air pollution solutions:").formatted(Formatting.AQUA), false);
                player.sendMessage(Text.literal("• Use public transportation or electric vehicles").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("• Support renewable energy initiatives").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("• Advocate for industrial emission controls").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("• Plant trees and support urban green spaces").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal("• Monitor local air quality regularly").formatted(Formatting.WHITE), false);
                player.sendMessage(Text.literal(""), false);
                player.sendMessage(Text.literal("Your knowledge can help create cleaner air!").formatted(Formatting.YELLOW), false);
                
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
    }
    
    public static class PortalKeyItem extends Item {
        public PortalKeyItem(Settings settings) {
            super(settings);
        }
    }
    
    // Registration helper methods
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(CleanAirHeroes.MOD_ID, name), item);
    }
    
    public static void registerItems() {
        CleanAirHeroes.LOGGER.info("Registering items for " + CleanAirHeroes.MOD_ID);
    }
}