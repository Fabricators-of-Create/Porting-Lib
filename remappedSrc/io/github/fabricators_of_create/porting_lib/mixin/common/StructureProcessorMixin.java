package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.extensions.StructureProcessorExtensions;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

@Mixin(StructureProcessor.class)
public abstract class StructureProcessorMixin implements StructureProcessorExtensions {
	@Override
	public Structure.StructureEntityInfo processEntity(WorldView world, BlockPos seedPos, Structure.StructureEntityInfo rawEntityInfo,
															   Structure.StructureEntityInfo entityInfo, StructurePlacementData placementSettings,
															   Structure template) {
		return entityInfo;
	}
}
