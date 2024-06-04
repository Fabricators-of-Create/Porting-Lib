package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureEntityInfo;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Small utilities for StructureTemplates provided by forge.
 */
public class StructureTemplateUtils {
	/**
	 * Transforms the given vector using the provided placement settings (mirror, rotation, pivot)
	 */
	public static Vec3 transformedVec3d(StructurePlaceSettings settings, Vec3 pos) {
		return StructureTemplate.transform(
				pos, settings.getMirror(), settings.getRotation(), settings.getRotationPivot()
		);
	}

	/**
	 * Processes entity infos in the same way blocks are.
	 * StructureProcessors may optionally implement a method to process entities.
	 */
	public static List<StructureEntityInfo> processEntityInfos(@Nullable StructureTemplate template, LevelAccessor level,
															   BlockPos pos, StructurePlaceSettings settings,
															   List<StructureEntityInfo> entityInfos) {
		List<StructureEntityInfo> processed = new ArrayList<>();
		for(StructureEntityInfo entityInfo : entityInfos) {
			Vec3 entityPos = transformedVec3d(settings, entityInfo.pos).add(Vec3.atLowerCornerOf(pos));
			BlockPos blockpos = StructureTemplate.calculateRelativePosition(settings, entityInfo.blockPos).offset(pos);
			StructureEntityInfo info = new StructureEntityInfo(entityPos, blockpos, entityInfo.nbt);
			for (StructureProcessor proc : settings.getProcessors()) {
				info = proc.processEntity(level, pos, entityInfo, info, settings, template);
				if (info == null)
					break;
			}
			if (info != null)
				processed.add(info);
		}
		return processed;
	}
}
