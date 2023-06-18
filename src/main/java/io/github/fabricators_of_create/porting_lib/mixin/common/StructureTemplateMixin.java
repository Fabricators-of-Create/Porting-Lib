package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.llamalad7.mixinextras.injector.ModifyReceiver;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import io.github.fabricators_of_create.porting_lib.extensions.StructureTemplateExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureEntityInfo;
import net.minecraft.world.phys.Vec3;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin implements StructureTemplateExtensions {
	@Shadow
	@Final
	private List<StructureEntityInfo> entityInfoList;

	@Shadow
	protected abstract void placeEntities(ServerLevelAccessor serverLevel, BlockPos pos, Mirror mirror, Rotation rotation, BlockPos pivot, @Nullable BoundingBox boundingBox, boolean withEntities);

	@Unique
	private StructurePlaceSettings settings;

	@WrapWithCondition(
			method = "placeInWorld",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;placeEntities(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Mirror;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Z)V"
			)
	)
	private boolean grabSettings(StructureTemplate self,
								 ServerLevelAccessor serverLevel, BlockPos pos, Mirror mirror, Rotation rotation,
								 BlockPos pivot, @Nullable BoundingBox boundingBox, boolean withEntities,
								 ServerLevelAccessor serverLevel2, BlockPos pos2, BlockPos pivot2,
								 StructurePlaceSettings settings, Random random, int flags) {
		this.settings = settings;
		return true;
	}

	@ModifyReceiver(
			method = "placeEntities",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;iterator()Ljava/util/Iterator;"
			)
	)
	private List<StructureEntityInfo> processEntities(List<StructureEntityInfo> original,
													  ServerLevelAccessor level, BlockPos pos, Mirror mirror, Rotation rotation,
													  BlockPos pivot, @Nullable BoundingBox boundingBox, boolean withEntities) {
		return processEntityInfos((StructureTemplate) (Object) this, level, pos, settings, original);
	}

	@Override
	public List<StructureEntityInfo> getEntities() {
		return entityInfoList;
	}

	@Override
	public Vec3 transformedVec3d(StructurePlaceSettings placementIn, Vec3 pos) {
		return StructureTemplate.transform(pos, placementIn.getMirror(), placementIn.getRotation(), placementIn.getRotationPivot());
	}

	@SuppressWarnings("removal")
	@Override
	public List<StructureEntityInfo> processEntityInfos(@Nullable StructureTemplate template, LevelAccessor level, BlockPos pos, StructurePlaceSettings settings, List<StructureEntityInfo> unprocessedEntities) {
		// this does not transform positions, unlike forge
		List<StructureEntityInfo> processedEntities = new ArrayList<>();
		entities: for (StructureEntityInfo entity : unprocessedEntities) {
			Vec3 vec = transformedVec3d(settings, entity.pos).add(Vec3.atLowerCornerOf(pos));
			BlockPos blockPos = StructureTemplate.calculateRelativePosition(settings, entity.blockPos).offset(pos);
			StructureEntityInfo info = new StructureEntityInfo(vec, blockPos, entity.nbt);

			for (StructureProcessor processor : settings.getProcessors()) {
				StructureEntityInfo newInfo = processor.processEntity(level, pos, entity, info, settings, template);
				if (newInfo == null) {
					continue entities;
				} else if (newInfo == info) {
					processedEntities.add(entity);
				} else {
					// processors transformed the positions. Apply the transformation to the original positions instead.
					Vec3 vecOffset = newInfo.pos.subtract(info.pos);
					BlockPos posOffset = newInfo.blockPos.subtract(info.blockPos);
					Vec3 newVec = entity.pos.add(vecOffset);
					BlockPos newPos = entity.blockPos.offset(posOffset);
					processedEntities.add(new StructureEntityInfo(newVec, newPos, entity.nbt));
				}
			}

			processedEntities.add(info);
		}
		return processedEntities;
	}

	@SuppressWarnings("removal")
	@Override
	public void addEntitiesToWorld(ServerLevelAccessor world, BlockPos blockPos, StructurePlaceSettings settings) {
		placeEntities(world, blockPos, settings.getMirror(), settings.getRotation(), settings.getRotationPivot(), settings.getBoundingBox(), settings.shouldFinalizeEntities());
	}
}
