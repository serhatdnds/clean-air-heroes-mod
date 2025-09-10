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

public class MiningComplexStructure extends Structure {
    
    public static final Codec<MiningComplexStructure> CODEC = RecordCodecBuilder.<MiningComplexStructure>mapCodec(instance ->
        instance.group(MiningComplexStructure.configCodecBuilder(instance))
            .apply(instance, MiningComplexStructure::new)).codec();
    
    public MiningComplexStructure(Config config) {
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
        
        holder.addPiece(new MiningComplexStructure.Piece(blockPos));
    }
    
    @Override
    public StructureType<?> getType() {
        return ModStructures.MINING_COMPLEX_STRUCTURE;
    }
    
    public static class Piece extends StructurePiece {
        
        public Piece(BlockPos pos) {
            super(ModStructures.MINING_COMPLEX_PIECE, 0, createBoundingBox(pos));
        }
        
        public Piece(StructureContext context, NbtCompound nbt) {
            super(ModStructures.MINING_COMPLEX_PIECE, nbt);
        }
        
        private static net.minecraft.util.math.Box createBoundingBox(BlockPos pos) {
            return net.minecraft.util.math.Box.of(pos.toCenterPos(), 80, 40, 60);
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
            
            // Create mining facility buildings
            for (int x = 0; x < 40; x++) {
                for (int z = 0; z < 30; z++) {
                    for (int y = 0; y < 15; y++) {
                        BlockPos pos = startPos.add(x, y, z);
                        if (chunkBox.contains(pos.toCenterPos())) {
                            // Industrial building walls
                            if ((x == 0 || x == 39 || z == 0 || z == 29) && y < 12) {
                                world.setBlockState(pos, net.minecraft.block.Blocks.STONE_BRICKS.getDefaultState(), 3);
                            }
                            // Floor
                            else if (y == 0) {
                                world.setBlockState(pos, net.minecraft.block.Blocks.COBBLESTONE.getDefaultState(), 3);
                            }
                        }
                    }
                }
            }
            
            // Add mine shafts
            for (int i = 0; i < 3; i++) {
                BlockPos shaftPos = startPos.add(10 + i * 10, 0, 15);
                for (int y = 0; y < 30; y++) {
                    BlockPos pos = shaftPos.add(0, -y, 0);
                    if (chunkBox.contains(pos.toCenterPos())) {
                        world.setBlockState(pos, net.minecraft.block.Blocks.AIR.getDefaultState(), 3);
                        
                        // Shaft walls
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                if (dx == 0 && dz == 0) continue;
                                BlockPos wallPos = pos.add(dx, 0, dz);
                                if (chunkBox.contains(wallPos.toCenterPos())) {
                                    world.setBlockState(wallPos, net.minecraft.block.Blocks.STONE.getDefaultState(), 3);
                                }
                            }
                        }
                    }
                }
            }
            
            // Add conveyor belts (simplified as rails)
            for (int x = 5; x < 35; x++) {
                BlockPos railPos = startPos.add(x, 1, 15);
                if (chunkBox.contains(railPos.toCenterPos())) {
                    world.setBlockState(railPos, net.minecraft.block.Blocks.RAIL.getDefaultState(), 3);
                }
            }
        }
    }
}