package com.cleanairheroes.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.AbstractBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.cleanairheroes.CleanAirHeroes;

public class ModBlocks {
    
    // Air Quality Monitoring Equipment
    public static final Block AIR_QUALITY_MONITOR = registerBlock("air_quality_monitor",
        new AirQualityMonitorBlock(AbstractBlock.Settings.of(Material.METAL).strength(3.0f).nonOpaque()));
    
    // Ship Emission Filter
    public static final Block SHIP_EMISSION_FILTER = registerBlock("ship_emission_filter",
        new EmissionFilterBlock(AbstractBlock.Settings.of(Material.METAL).strength(4.0f)));
    
    // Industrial Smokestack Filter
    public static final Block INDUSTRIAL_FILTER = registerBlock("industrial_filter",
        new IndustrialFilterBlock(AbstractBlock.Settings.of(Material.STONE).strength(5.0f)));
    
    // Green Transport Infrastructure
    public static final Block ELECTRIC_BUS_STOP = registerBlock("electric_bus_stop",
        new ElectricBusStopBlock(AbstractBlock.Settings.of(Material.METAL).strength(3.0f).nonOpaque()));
    
    public static final Block EV_CHARGING_STATION = registerBlock("ev_charging_station",
        new ChargingStationBlock(AbstractBlock.Settings.of(Material.METAL).strength(4.0f).nonOpaque()));
    
    public static final Block BIKE_LANE_MARKER = registerBlock("bike_lane_marker",
        new BikeLaneBlock(AbstractBlock.Settings.of(Material.STONE).strength(2.0f)));
    
    // Renewable Energy Systems
    public static final Block SOLAR_PANEL_HEATING = registerBlock("solar_panel_heating",
        new SolarHeatingBlock(AbstractBlock.Settings.of(Material.METAL).strength(3.0f).nonOpaque()));
    
    public static final Block WIND_TURBINE_BASE = registerBlock("wind_turbine_base",
        new WindTurbineBlock(AbstractBlock.Settings.of(Material.STONE).strength(6.0f)));
    
    // Mining Equipment
    public static final Block DUST_SUPPRESSION_SYSTEM = registerBlock("dust_suppression_system",
        new DustSuppressionBlock(AbstractBlock.Settings.of(Material.METAL).strength(4.0f).nonOpaque()));
    
    public static final Block MINE_VENTILATION_SHAFT = registerBlock("mine_ventilation_shaft",
        new VentilationShaftBlock(AbstractBlock.Settings.of(Material.STONE).strength(5.0f).nonOpaque()));
    
    // Power Plant Equipment
    public static final Block FLUE_GAS_SCRUBBER = registerBlock("flue_gas_scrubber",
        new FlueGasScrubberBlock(AbstractBlock.Settings.of(Material.METAL).strength(6.0f)));
    
    public static final Block PARTICULATE_FILTER = registerBlock("particulate_filter",
        new ParticulateFilterBlock(AbstractBlock.Settings.of(Material.METAL).strength(4.0f)));
    
    // Traffic Management
    public static final Block SMART_TRAFFIC_LIGHT = registerBlock("smart_traffic_light",
        new SmartTrafficLightBlock(AbstractBlock.Settings.of(Material.METAL).strength(3.0f).nonOpaque()));
    
    public static final Block TRAFFIC_MONITOR = registerBlock("traffic_monitor",
        new TrafficMonitorBlock(AbstractBlock.Settings.of(Material.METAL).strength(3.0f).nonOpaque()));
    
    // Recycling and Waste Management
    public static final Block RECYCLING_BIN = registerBlock("recycling_bin",
        new RecyclingBinBlock(AbstractBlock.Settings.of(Material.METAL).strength(3.0f).nonOpaque()));
    
    public static final Block WASTE_SORTING_STATION = registerBlock("waste_sorting_station",
        new WasteSortingBlock(AbstractBlock.Settings.of(Material.METAL).strength(4.0f).nonOpaque()));
    
    // Regional Portal Blocks
    public static final Block CLEAN_AIR_PORTAL = registerBlock("clean_air_portal",
        new CleanAirPortalBlock(AbstractBlock.Settings.of(Material.PORTAL).strength(-1.0f).nonOpaque()));
    
    public static final Block ENVIRONMENTAL_PROTECTION_CENTER = registerBlock("environmental_protection_center",
        new ProtectionCenterBlock(AbstractBlock.Settings.of(Material.METAL).strength(8.0f)));
    
    // Custom Block Classes
    public static class AirQualityMonitorBlock extends Block {
        public AirQualityMonitorBlock(Settings settings) {
            super(settings);
        }
        
        @Override
        public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
            if (!world.isClient) {
                // Start monitoring air quality at this location
                CleanAirHeroes.getPollutionManager().addPollutionSource(pos, "MONITORING_STATION", 0);
            }
        }
        
        @Override
        public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
            if (!world.isClient && !state.isOf(newState.getBlock())) {
                // Remove monitoring station
                CleanAirHeroes.getPollutionManager().removePollutionSource(pos);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
    
    public static class EmissionFilterBlock extends Block {
        public EmissionFilterBlock(Settings settings) {
            super(settings);
        }
        
        @Override
        public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
            if (!world.isClient) {
                // Reduce ship emissions in nearby area
                CleanAirHeroes.getPollutionManager().reducePollutionInRadius(pos, 50, 0.3);
            }
        }
    }
    
    public static class IndustrialFilterBlock extends Block {
        public IndustrialFilterBlock(Settings settings) {
            super(settings);
        }
        
        @Override
        public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
            if (!world.isClient) {
                // Significantly reduce industrial pollution
                CleanAirHeroes.getPollutionManager().reducePollutionInRadius(pos, 100, 0.5);
            }
        }
    }
    
    public static class ElectricBusStopBlock extends Block {
        public ElectricBusStopBlock(Settings settings) {
            super(settings);
        }
    }
    
    public static class ChargingStationBlock extends Block {
        public ChargingStationBlock(Settings settings) {
            super(settings);
        }
        
        @Override
        public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
            if (!world.isClient) {
                // Reduce traffic emissions slightly
                CleanAirHeroes.getPollutionManager().reducePollutionInRadius(pos, 30, 0.1);
            }
        }
    }
    
    public static class BikeLaneBlock extends Block {
        public BikeLaneBlock(Settings settings) {
            super(settings);
        }
    }
    
    public static class SolarHeatingBlock extends Block {
        public SolarHeatingBlock(Settings settings) {
            super(settings);
        }
        
        @Override
        public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
            if (!world.isClient) {
                // Reduce heating emissions
                CleanAirHeroes.getPollutionManager().reducePollutionInRadius(pos, 20, 0.2);
            }
        }
    }
    
    public static class WindTurbineBlock extends Block {
        public WindTurbineBlock(Settings settings) {
            super(settings);
        }
        
        @Override
        public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
            if (!world.isClient) {
                // Generate clean energy, reduce power plant emissions
                CleanAirHeroes.getPollutionManager().reducePollutionInRadius(pos, 200, 0.15);
            }
        }
    }
    
    public static class DustSuppressionBlock extends Block {
        public DustSuppressionBlock(Settings settings) {
            super(settings);
        }
        
        @Override
        public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
            if (!world.isClient) {
                // Reduce mining dust pollution
                CleanAirHeroes.getPollutionManager().reducePollutionInRadius(pos, 80, 0.4);
            }
        }
    }
    
    public static class VentilationShaftBlock extends Block {
        public VentilationShaftBlock(Settings settings) {
            super(settings);
        }
        
        @Override
        public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
            if (!world.isClient) {
                // Improve mine air quality
                CleanAirHeroes.getPollutionManager().reducePollutionInRadius(pos, 60, 0.25);
            }
        }
    }
    
    public static class FlueGasScrubberBlock extends Block {
        public FlueGasScrubberBlock(Settings settings) {
            super(settings);
        }
        
        @Override
        public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
            if (!world.isClient) {
                // Dramatically reduce power plant emissions
                CleanAirHeroes.getPollutionManager().reducePollutionInRadius(pos, 150, 0.6);
            }
        }
    }
    
    public static class ParticulateFilterBlock extends Block {
        public ParticulateFilterBlock(Settings settings) {
            super(settings);
        }
        
        @Override
        public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
            if (!world.isClient) {
                // Reduce particulate emissions
                CleanAirHeroes.getPollutionManager().reducePollutionInRadius(pos, 100, 0.35);
            }
        }
    }
    
    public static class SmartTrafficLightBlock extends Block {
        public SmartTrafficLightBlock(Settings settings) {
            super(settings);
        }
        
        @Override
        public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
            if (!world.isClient) {
                // Optimize traffic flow, reduce emissions
                CleanAirHeroes.getPollutionManager().reducePollutionInRadius(pos, 40, 0.15);
            }
        }
    }
    
    public static class TrafficMonitorBlock extends Block {
        public TrafficMonitorBlock(Settings settings) {
            super(settings);
        }
    }
    
    public static class RecyclingBinBlock extends Block {
        public RecyclingBinBlock(Settings settings) {
            super(settings);
        }
        
        @Override
        public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
            if (!world.isClient) {
                // Small reduction in waste-related emissions
                CleanAirHeroes.getPollutionManager().reducePollutionInRadius(pos, 15, 0.05);
            }
        }
    }
    
    public static class WasteSortingBlock extends Block {
        public WasteSortingBlock(Settings settings) {
            super(settings);
        }
        
        @Override
        public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
            if (!world.isClient) {
                // Moderate reduction in waste processing emissions
                CleanAirHeroes.getPollutionManager().reducePollutionInRadius(pos, 25, 0.1);
            }
        }
    }
    
    public static class CleanAirPortalBlock extends Block {
        public CleanAirPortalBlock(Settings settings) {
            super(settings);
        }
        
        // Portal functionality would be implemented here
        // Player interaction to travel between regions
    }
    
    public static class ProtectionCenterBlock extends Block {
        public ProtectionCenterBlock(Settings settings) {
            super(settings);
        }
        
        // Central hub functionality
    }
    
    // Registration helper methods
    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(CleanAirHeroes.MOD_ID, name), block);
    }
    
    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(CleanAirHeroes.MOD_ID, name),
            new BlockItem(block, new Item.Settings()));
    }
    
    public static void registerBlocks() {
        CleanAirHeroes.LOGGER.info("Registering blocks for " + CleanAirHeroes.MOD_ID);
    }
}