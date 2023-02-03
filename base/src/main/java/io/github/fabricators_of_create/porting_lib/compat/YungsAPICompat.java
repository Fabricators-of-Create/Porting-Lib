package io.github.fabricators_of_create.porting_lib.compat;

import org.jetbrains.annotations.Nullable;

import com.yungnickyoung.minecraft.yungsapi.world.processor.StructureEntityProcessor;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class YungsAPICompat {
	public static StructureTemplate.StructureEntityInfo processEntity(StructureProcessor processor, ServerLevelAccessor serverLevelAccessor,
																	  BlockPos structurePiecePos,
																	  BlockPos structurePieceBottomCenterPos,
																	  StructureTemplate.StructureEntityInfo localEntityInfo,
																	  StructureTemplate.StructureEntityInfo globalEntityInfo,
																	  StructurePlaceSettings structurePlaceSettings, @Nullable StructureTemplate template) {
		if (processor instanceof StructureEntityProcessor entityProcessor) {
			return entityProcessor.processEntity(serverLevelAccessor, structurePiecePos, structurePieceBottomCenterPos, localEntityInfo, globalEntityInfo, structurePlaceSettings);
		}
		return processor.processEntity(serverLevelAccessor, structurePiecePos, localEntityInfo, globalEntityInfo, structurePlaceSettings, template);
	}
}
