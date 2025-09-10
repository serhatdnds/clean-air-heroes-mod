package com.cleanairheroes.world.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.structure.StructureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.nbt.NbtCompound;

import java.util.Optional;

public class EnvironmentalStationStructure extends Structure {
    
    public static final Codec<EnvironmentalStationStructure> CODEC = RecordCodecBuilder.<EnvironmentalStationStructure>mapCodec(instance ->
        instance.group(EnvironmentalStationStructure.configCodecBuilder(instance))
            .apply(instance, EnvironmentalStationStructure::new)).codec();
    
    public EnvironmentalStationStructure(Config config) {
        super(config);
    }
    
    @Override
    public Optional<StructurePosition> getStructurePosition(Context context) {
        return getStructurePosition(context, (structurePiecesHolder) -> {
            addPieces(structurePiecesHolder, context);
        });
    }
    
    private static void addPieces(StructurePiecesHolder holder, Context context) {
        BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 
                                       context.chunkGenerator().getSeaLevel(), 
                                       context.chunkPos().getStartZ());
        
        holder.addPiece(new EnvironmentalStationStructure.Piece(blockPos));
    }
    
    @Override
    public StructureType<?> getType() {
        return ModStructures.ENVIRONMENTAL_STATION_STRUCTURE;
    }
    
    public static class Piece extends StructurePiece {
        
        public Piece(BlockPos pos) {
            super(ModStructures.ENVIRONMENTAL_STATION_PIECE, 0, createBoundingBox(pos));
        }
        
        public Piece(StructureContext context, NbtCompound nbt) {
            super(ModStructures.ENVIRONMENTAL_STATION_PIECE, nbt);
        }
        
        private static net.minecraft.util.math.Box createBoundingBox(BlockPos pos) {
            return net.minecraft.util.math.Box.of(pos.toCenterPos(), 20, 15, 20);
        }
        
        @Override
        protected void writeNbt(StructureContext context, NbtCompound nbt) {
            // Write structure data to NBT
        }
        
        @Override
        public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, 
                           ChunkGenerator chunkGenerator, Random random, 
                           net.minecraft.util.math.Box chunkBox, ChunkPos chunkPos, BlockPos pivot) {
            
            BlockPos startPos = this.boundingBox.getMinPos();
            
            // Create environmental monitoring station
            for (int x = 0; x < 15; x++) {
                for (int z = 0; z < 15; z++) {
                    for (int y = 0; y < 8; y++) {
                        BlockPos pos = startPos.add(x, y, z);
                        if (chunkBox.contains(pos.toCenterPos())) {
                            // Walls
                            if (x == 0 || x == 14 || z == 0 || z == 14) {
                                world.setBlockState(pos, net.minecraft.block.Blocks.WHITE_CONCRETE.getDefaultState(), 3);
                            }
                            // Floor
                            else if (y == 0) {
                                world.setBlockState(pos, net.minecraft.block.Blocks.LIGHT_GRAY_CONCRETE.getDefaultState(), 3);
                            }
                        }
                    }
                }
            }
            
            // Add monitoring tower
            for (int y = 8; y < 20; y++) {
                BlockPos towerPos = startPos.add(7, y, 7);
                if (chunkBox.contains(towerPos.toCenterPos())) {
                    world.setBlockState(towerPos, net.minecraft.block.Blocks.OBSERVER.getDefaultState(), 3);
                }
            }
        }
    }
}