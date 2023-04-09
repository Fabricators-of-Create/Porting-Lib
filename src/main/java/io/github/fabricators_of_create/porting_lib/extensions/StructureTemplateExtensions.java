package io.github.fabricators_of_create.porting_lib.extensions;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

public interface StructureTemplateExtensions {
	default List<StructureTemplate.StructureEntityInfo> getEntities() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Vec3 transformedVec3d(StructurePlaceSettings placementIn, Vec3 pos) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default List<StructureTemplate.StructureEntityInfo> processEntityInfos(@Nullable StructureTemplate template, LevelAccessor world, BlockPos blockPos, StructurePlaceSettings settings, List<StructureTemplate.StructureEntityInfo> infos) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void addEntitiesToWorld(ServerLevelAccessor world, BlockPos blockPos, StructurePlaceSettings settings) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
