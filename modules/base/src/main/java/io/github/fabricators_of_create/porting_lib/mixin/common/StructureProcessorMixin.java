package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.extensions.common.StructureProcessorExtension;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

@Mixin(StructureProcessor.class)
@Implements(@Interface(iface = StructureProcessorExtension.class, prefix = "port_lib$"))
public abstract class StructureProcessorMixin {
	@Intrinsic
	public StructureTemplate.StructureEntityInfo port_lib$processEntity(LevelReader world, BlockPos seedPos, StructureTemplate.StructureEntityInfo rawEntityInfo,
																		StructureTemplate.StructureEntityInfo entityInfo, StructurePlaceSettings placementSettings,
																		StructureTemplate template) {
		return entityInfo;
	}
}
