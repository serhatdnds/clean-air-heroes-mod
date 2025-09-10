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

public class PortStructure extends Structure {
    
    public static final Codec<PortStructure> CODEC = RecordCodecBuilder.<PortStructure>mapCodec(instance ->
        instance.group(PortStructure.configCodecBuilder(instance))
            .apply(instance, PortStructure::new)).codec();
    
    public PortStructure(Config config) {
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
        
        holder.addPiece(new PortStructure.Piece(blockPos));
    }
    
    @Override
    public StructureType<?> getType() {
        return ModStructures.PORT_STRUCTURE;
    }
    
    public static class Piece extends StructurePiece {
        
        public Piece(BlockPos pos) {
            super(ModStructures.PORT_PIECE, 0, createBoundingBox(pos));
        }
        
        public Piece(StructureContext context, NbtCompound nbt) {
            super(ModStructures.PORT_PIECE, nbt);
        }
        
        private static net.minecraft.util.math.Box createBoundingBox(BlockPos pos) {
            return net.minecraft.util.math.Box.of(pos.toCenterPos(), 60, 20, 40);
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
            
            // Create port docks
            for (int x = 0; x < 30; x++) {
                for (int z = 0; z < 20; z++) {
                    BlockPos pos = startPos.add(x, 0, z);
                    if (chunkBox.contains(pos.toCenterPos())) {
                        world.setBlockState(pos, net.minecraft.block.Blocks.STONE.getDefaultState(), 3);
                    }
                }
            }
            
            // Add shipping containers
            for (int i = 0; i < 5; i++) {
                BlockPos containerPos = startPos.add(5 + i * 4, 1, 5);
                if (chunkBox.contains(containerPos.toCenterPos())) {
                    // Create container structure
                    for (int y = 0; y < 3; y++) {
                        for (int x = 0; x < 3; x++) {
                            for (int z = 0; z < 8; z++) {
                                BlockPos blockPos = containerPos.add(x, y, z);
                                if (chunkBox.contains(blockPos.toCenterPos())) {
                                    world.setBlockState(blockPos, net.minecraft.block.Blocks.IRON_BLOCK.getDefaultState(), 3);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}