package io.github.fabricators_of_create.porting_lib.extensions;

import java.util.List;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public interface StructureTemplateExtensions {
	default List<Structure.StructureEntityInfo> getEntities() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Vec3d transformedVec3d(StructurePlacementData placementIn, Vec3d pos) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default List<Structure.StructureEntityInfo> processEntityInfos(@Nullable Structure template, WorldAccess world, BlockPos blockPos, StructurePlacementData settings, List<Structure.StructureEntityInfo> infos) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void addEntitiesToWorld(ServerWorldAccess world, BlockPos blockPos, StructurePlacementData settings) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
