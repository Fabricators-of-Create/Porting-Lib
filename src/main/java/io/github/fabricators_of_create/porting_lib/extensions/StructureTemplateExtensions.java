package io.github.fabricators_of_create.porting_lib.extensions;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

public interface StructureTemplateExtensions {
	List<StructureTemplate.StructureEntityInfo> port_lib$getEntities();

	Vec3 port_lib$transformedVec3d(StructurePlaceSettings placementIn, Vec3 pos);

	List<StructureTemplate.StructureEntityInfo> port_lib$processEntityInfos(@Nullable StructureTemplate template, LevelAccessor world, BlockPos blockPos, StructurePlaceSettings settings, List<StructureTemplate.StructureEntityInfo> infos);

	void port_lib$addEntitiesToWorld(ServerLevelAccessor world, BlockPos blockPos, StructurePlaceSettings settings);
}
