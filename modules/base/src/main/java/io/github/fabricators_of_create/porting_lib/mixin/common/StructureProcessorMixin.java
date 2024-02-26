package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.StructureProcessorExtensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

@Mixin(StructureProcessor.class)
public abstract class StructureProcessorMixin implements StructureProcessorExtensions {
	@Override
	public StructureTemplate.StructureEntityInfo processEntity(LevelReader world, BlockPos seedPos, StructureTemplate.StructureEntityInfo rawEntityInfo,
															   StructureTemplate.StructureEntityInfo entityInfo, StructurePlaceSettings placementSettings,
															   StructureTemplate template) {
		return entityInfo;
	}
}
