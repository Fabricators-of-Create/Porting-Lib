package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.core.Holder;
import net.minecraft.world.level.chunk.ChunkGenerator;

import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ChunkGenerator.class)
public interface ChunkGeneratorAccessor {
	@Invoker("getPlacementsForStructure")
	List<StructurePlacement> port_lib$getPlacementsForStructure(Holder<Structure> holder, RandomState randomState);
}
