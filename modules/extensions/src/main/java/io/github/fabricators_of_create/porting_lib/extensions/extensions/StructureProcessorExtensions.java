package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public interface StructureProcessorExtensions {
	default StructureTemplate.StructureEntityInfo processEntity(LevelReader world, BlockPos seedPos, StructureTemplate.StructureEntityInfo rawEntityInfo, StructureTemplate.StructureEntityInfo entityInfo, StructurePlaceSettings placementSettings, StructureTemplate template) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
