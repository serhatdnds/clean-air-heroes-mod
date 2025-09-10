package com.cleanairheroes.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import com.cleanairheroes.CleanAirHeroes;
import com.cleanairheroes.entity.npc.*;

public class ModEntities {
    
    // NPC Entities for mission interactions
    public static final EntityType<ShipCaptainEntity> SHIP_CAPTAIN = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(CleanAirHeroes.MOD_ID, "ship_captain"),
        FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, ShipCaptainEntity::new)
            .dimensions(EntityDimensions.fixed(0.6f, 1.8f))
            .build()
    );
    
    public static final EntityType<EnvironmentalScientistEntity> ENVIRONMENTAL_SCIENTIST = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(CleanAirHeroes.MOD_ID, "environmental_scientist"),
        FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, EnvironmentalScientistEntity::new)
            .dimensions(EntityDimensions.fixed(0.6f, 1.8f))
            .build()
    );
    
    public static final EntityType<DOKAAgentEntity> DOKA_AGENT = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(CleanAirHeroes.MOD_ID, "doka_agent"),
        FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, DOKAAgentEntity::new)
            .dimensions(EntityDimensions.fixed(0.6f, 1.8f))
            .build()
    );
    
    public static final EntityType<MinerFormanEntity> MINER_FORMAN = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(CleanAirHeroes.MOD_ID, "miner_forman"),
        FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, MinerFormanEntity::new)
            .dimensions(EntityDimensions.fixed(0.6f, 1.8f))
            .build()
    );
    
    public static final EntityType<FactoryManagerEntity> FACTORY_MANAGER = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(CleanAirHeroes.MOD_ID, "factory_manager"),
        FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, FactoryManagerEntity::new)
            .dimensions(EntityDimensions.fixed(0.6f, 1.8f))
            .build()
    );
    
    public static final EntityType<UniversityProfessorEntity> UNIVERSITY_PROFESSOR = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(CleanAirHeroes.MOD_ID, "university_professor"),
        FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, UniversityProfessorEntity::new)
            .dimensions(EntityDimensions.fixed(0.6f, 1.8f))
            .build()
    );
    
    // Interactive vehicle entities
    public static final EntityType<PollutionMonitoringShipEntity> MONITORING_SHIP = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(CleanAirHeroes.MOD_ID, "monitoring_ship"),
        FabricEntityTypeBuilder.create(SpawnGroup.MISC, PollutionMonitoringShipEntity::new)
            .dimensions(EntityDimensions.fixed(3.0f, 1.5f))
            .build()
    );
    
    public static final EntityType<CargoShipEntity> CARGO_SHIP = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(CleanAirHeroes.MOD_ID, "cargo_ship"),
        FabricEntityTypeBuilder.create(SpawnGroup.MISC, CargoShipEntity::new)
            .dimensions(EntityDimensions.fixed(8.0f, 4.0f))
            .build()
    );
    
    public static final EntityType<ElectricBusEntity> ELECTRIC_BUS = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(CleanAirHeroes.MOD_ID, "electric_bus"),
        FabricEntityTypeBuilder.create(SpawnGroup.MISC, ElectricBusEntity::new)
            .dimensions(EntityDimensions.fixed(2.0f, 1.5f))
            .build()
    );
    
    public static void registerEntities() {
        CleanAirHeroes.LOGGER.info("Registering entities for " + CleanAirHeroes.MOD_ID);
        
        // Register entity attributes
        FabricDefaultAttributeRegistry.register(SHIP_CAPTAIN, VillagerEntity.createVillagerAttributes());
        FabricDefaultAttributeRegistry.register(ENVIRONMENTAL_SCIENTIST, VillagerEntity.createVillagerAttributes());
        FabricDefaultAttributeRegistry.register(DOKA_AGENT, VillagerEntity.createVillagerAttributes());
        FabricDefaultAttributeRegistry.register(MINER_FORMAN, VillagerEntity.createVillagerAttributes());
        FabricDefaultAttributeRegistry.register(FACTORY_MANAGER, VillagerEntity.createVillagerAttributes());
        FabricDefaultAttributeRegistry.register(UNIVERSITY_PROFESSOR, VillagerEntity.createVillagerAttributes());
        
        // Vehicle entities use mob attributes for basic movement
        FabricDefaultAttributeRegistry.register(MONITORING_SHIP, MobEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(CARGO_SHIP, MobEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(ELECTRIC_BUS, MobEntity.createMobAttributes());
    }
}