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

public class ResearchCenterStructure extends Structure {
    
    public static final Codec<ResearchCenterStructure> CODEC = RecordCodecBuilder.<ResearchCenterStructure>mapCodec(instance ->
        instance.group(ResearchCenterStructure.configCodecBuilder(instance))
            .apply(instance, ResearchCenterStructure::new)).codec();
    
    public ResearchCenterStructure(Config config) {
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
        
        holder.addPiece(new ResearchCenterStructure.Piece(blockPos));
    }
    
    @Override
    public StructureType<?> getType() {
        return ModStructures.RESEARCH_CENTER_STRUCTURE;
    }
    
    public static class Piece extends StructurePiece {
        
        public Piece(BlockPos pos) {
            super(ModStructures.RESEARCH_CENTER_PIECE, 0, createBoundingBox(pos));
        }
        
        public Piece(StructureContext context, NbtCompound nbt) {
            super(ModStructures.RESEARCH_CENTER_PIECE, nbt);
        }
        
        private static net.minecraft.util.math.Box createBoundingBox(BlockPos pos) {
            return net.minecraft.util.math.Box.of(pos.toCenterPos(), 40, 25, 40);
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
            
            // Create research center building
            for (int x = 0; x < 25; x++) {
                for (int z = 0; z < 25; z++) {
                    for (int y = 0; y < 10; y++) {
                        BlockPos pos = startPos.add(x, y, z);
                        if (chunkBox.contains(pos.toCenterPos())) {
                            // Walls
                            if (x == 0 || x == 24 || z == 0 || z == 24) {
                                world.setBlockState(pos, net.minecraft.block.Blocks.QUARTZ_BLOCK.getDefaultState(), 3);
                            }
                            // Floor
                            else if (y == 0) {
                                world.setBlockState(pos, net.minecraft.block.Blocks.POLISHED_ANDESITE.getDefaultState(), 3);
                            }
                        }
                    }
                }
            }
            
            // Add research equipment
            BlockPos equipmentPos = startPos.add(12, 1, 12);
            if (chunkBox.contains(equipmentPos.toCenterPos())) {
                world.setBlockState(equipmentPos, net.minecraft.block.Blocks.BEACON.getDefaultState(), 3);
            }
        }
    }
}