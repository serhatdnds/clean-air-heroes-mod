package com.cleanairheroes.world.structure;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructureType;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

import com.cleanairheroes.CleanAirHeroes;

public class ModStructures {
    
    // Structure Types
    public static final StructureType<ThermalPlantStructure> THERMAL_PLANT_STRUCTURE = 
        () -> ThermalPlantStructure.CODEC;
    public static final StructureType<PortStructure> PORT_STRUCTURE = 
        () -> PortStructure.CODEC;
    public static final StructureType<ResearchCenterStructure> RESEARCH_CENTER_STRUCTURE = 
        () -> ResearchCenterStructure.CODEC;
    public static final StructureType<EnvironmentalStationStructure> ENVIRONMENTAL_STATION_STRUCTURE = 
        () -> EnvironmentalStationStructure.CODEC;
    public static final StructureType<MiningComplexStructure> MINING_COMPLEX_STRUCTURE = 
        () -> MiningComplexStructure.CODEC;
    
    // Structure Piece Types
    public static final StructurePieceType THERMAL_PLANT_PIECE = ThermalPlantStructure.Piece::new;
    public static final StructurePieceType PORT_PIECE = PortStructure.Piece::new;
    public static final StructurePieceType RESEARCH_CENTER_PIECE = ResearchCenterStructure.Piece::new;
    public static final StructurePieceType ENVIRONMENTAL_STATION_PIECE = EnvironmentalStationStructure.Piece::new;
    public static final StructurePieceType MINING_COMPLEX_PIECE = MiningComplexStructure.Piece::new;
    
    public static void registerStructures() {
        CleanAirHeroes.LOGGER.info("Registering Clean Air Heroes structures...");
        
        // Register Structure Types
        Registry.register(Registries.STRUCTURE_TYPE, 
            new Identifier(CleanAirHeroes.MOD_ID, "thermal_plant"), 
            THERMAL_PLANT_STRUCTURE);
        
        Registry.register(Registries.STRUCTURE_TYPE, 
            new Identifier(CleanAirHeroes.MOD_ID, "port"), 
            PORT_STRUCTURE);
        
        Registry.register(Registries.STRUCTURE_TYPE, 
            new Identifier(CleanAirHeroes.MOD_ID, "research_center"), 
            RESEARCH_CENTER_STRUCTURE);
        
        Registry.register(Registries.STRUCTURE_TYPE, 
            new Identifier(CleanAirHeroes.MOD_ID, "environmental_station"), 
            ENVIRONMENTAL_STATION_STRUCTURE);
        
        Registry.register(Registries.STRUCTURE_TYPE, 
            new Identifier(CleanAirHeroes.MOD_ID, "mining_complex"), 
            MINING_COMPLEX_STRUCTURE);
        
        // Register Structure Piece Types
        Registry.register(Registries.STRUCTURE_PIECE_TYPE, 
            new Identifier(CleanAirHeroes.MOD_ID, "thermal_plant_piece"), 
            THERMAL_PLANT_PIECE);
        
        Registry.register(Registries.STRUCTURE_PIECE_TYPE, 
            new Identifier(CleanAirHeroes.MOD_ID, "port_piece"), 
            PORT_PIECE);
        
        Registry.register(Registries.STRUCTURE_PIECE_TYPE, 
            new Identifier(CleanAirHeroes.MOD_ID, "research_center_piece"), 
            RESEARCH_CENTER_PIECE);
        
        Registry.register(Registries.STRUCTURE_PIECE_TYPE, 
            new Identifier(CleanAirHeroes.MOD_ID, "environmental_station_piece"), 
            ENVIRONMENTAL_STATION_PIECE);
        
        Registry.register(Registries.STRUCTURE_PIECE_TYPE, 
            new Identifier(CleanAirHeroes.MOD_ID, "mining_complex_piece"), 
            MINING_COMPLEX_PIECE);
        
        CleanAirHeroes.LOGGER.info("Clean Air Heroes structures registered successfully!");
    }
}