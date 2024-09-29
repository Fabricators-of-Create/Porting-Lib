package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.List;
import java.util.Objects;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.util.StructureTemplateUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureEntityInfo;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin {
	@Unique private static final ThreadLocal<StructurePlaceSettings> currentSettings = new ThreadLocal<>();

	@Inject(
			method = "placeInWorld",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;placeEntities(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Mirror;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Z)V"
			)
	)
	private void grabSettings(ServerLevelAccessor level, BlockPos pos, BlockPos pivot, StructurePlaceSettings settings,
							  RandomSource random, int i, CallbackInfoReturnable<Boolean> cir) {
		currentSettings.set(settings);
	}

	@ModifyExpressionValue(
			method = "placeEntities",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;entityInfoList:Ljava/util/List;"
			)
	)
	private List<StructureEntityInfo> processEntityInfos(List<StructureEntityInfo> original,
														 ServerLevelAccessor level, BlockPos pos, Mirror mirror, Rotation rotation,
														 BlockPos pivot, @Nullable BoundingBox bounds, boolean finalizeMobs) {
		StructurePlaceSettings settings = currentSettings.get();

		if (PortingLib.DEBUG)
			Objects.requireNonNull(settings);

		if (settings == null)
			return original;

		return StructureTemplateUtils.processEntityInfos(
				(StructureTemplate) (Object) this, level, pos, settings, original
		);
	}

	@ModifyExpressionValue(
			method = "placeEntities",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/core/BlockPos;offset(Lnet/minecraft/core/Vec3i;)Lnet/minecraft/core/BlockPos;"
			)
	)
	private BlockPos dontProcessBlockPosTwice(BlockPos original, @Local StructureEntityInfo info) {
		StructurePlaceSettings settings = currentSettings.get();
		if (settings != null) { // pos was already processed.
			return info.blockPos;
		}
		return original;
	}

	@ModifyExpressionValue(
			method = "placeEntities",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"
			)
	)
	private Vec3 dontProcessVecTwice(Vec3 original, @Local StructureEntityInfo info) {
		StructurePlaceSettings settings = currentSettings.get();
		if (settings != null) { // pos was already processed.
			return info.pos;
		}
		return original;
	}

	@Inject(method = "placeEntities", at = @At("RETURN"))
	private void clearGrabbedSettings(CallbackInfo ci) {
		currentSettings.remove(); // clear the settings when done to avoid leaking it
	}
}
