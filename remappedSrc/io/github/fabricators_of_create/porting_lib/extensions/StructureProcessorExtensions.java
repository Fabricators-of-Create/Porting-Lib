package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public interface StructureProcessorExtensions {
	default Structure.StructureEntityInfo processEntity(WorldView world, BlockPos seedPos, Structure.StructureEntityInfo rawEntityInfo, Structure.StructureEntityInfo entityInfo, StructurePlacementData placementSettings, Structure template) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
