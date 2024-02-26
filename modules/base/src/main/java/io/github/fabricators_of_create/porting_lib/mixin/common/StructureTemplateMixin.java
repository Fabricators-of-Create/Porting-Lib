package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Lists;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.StructureTemplateExtensions;
import io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor.StructureTemplateAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin implements StructureTemplateExtensions {
	@Shadow
	@Final
	private List<StructureTemplate.StructureEntityInfo> entityInfoList;

	@Unique
	@Override
	public List<StructureTemplate.StructureEntityInfo> getEntities() {
		return entityInfoList;
	}

	private final ThreadLocal<BlockPos> PIVOT_SAVED = new ThreadLocal<>();

	@Inject(
			method = "placeInWorld",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;placeEntities(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Mirror;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Z)V",
					shift = At.Shift.BEFORE
			),
			cancellable = true
	)
	public void port_lib$place(ServerLevelAccessor iServerWorld, BlockPos blockPos, BlockPos pivot, StructurePlaceSettings placementSettings, RandomSource random, int flags, CallbackInfoReturnable<Boolean> cir) {
		PIVOT_SAVED.set(pivot);
		addEntitiesToWorld(iServerWorld, blockPos, placementSettings);
		PIVOT_SAVED.remove();
		cir.setReturnValue(true);
	}

	@Override
	public Vec3 transformedVec3d(StructurePlaceSettings placementIn, Vec3 pos) {
		return StructureTemplate.transform(pos, placementIn.getMirror(), placementIn.getRotation(), placementIn.getRotationPivot());
	}

	@Override
	public List<StructureTemplate.StructureEntityInfo> processEntityInfos(@Nullable StructureTemplate template, LevelAccessor world, BlockPos blockPos, StructurePlaceSettings settings, List<StructureTemplate.StructureEntityInfo> infos) {
		List<StructureTemplate.StructureEntityInfo> list = Lists.newArrayList();
		BlockPos pivot = PIVOT_SAVED.get();
		for(StructureTemplate.StructureEntityInfo entityInfo : infos) {
			Vec3 pos = transformedVec3d(settings, entityInfo.pos).add(Vec3.atLowerCornerOf(blockPos));
			BlockPos blockpos = StructureTemplate.calculateRelativePosition(settings, entityInfo.blockPos).offset(blockPos);
			StructureTemplate.StructureEntityInfo info = new StructureTemplate.StructureEntityInfo(pos, blockpos, entityInfo.nbt);
			for (StructureProcessor proc : settings.getProcessors()) {
				info = proc.processEntity(world, blockPos, entityInfo, info, settings, template);
				if (info == null)
					break;
			}
			if (info != null)
				list.add(info);
		}
		return list;
	}

	@Override
	public void addEntitiesToWorld(ServerLevelAccessor world, BlockPos blockPos, StructurePlaceSettings settings) {
		for(StructureTemplate.StructureEntityInfo template$entityinfo : processEntityInfos((StructureTemplate) (Object) this, world, blockPos, settings, this.getEntities())) {
			BlockPos blockpos = StructureTemplate.transform(template$entityinfo.blockPos, settings.getMirror(), settings.getRotation(), settings.getRotationPivot()).offset(blockPos);
			blockpos = template$entityinfo.blockPos;
			if (settings.getBoundingBox() == null || settings.getBoundingBox().isInside(blockpos)) {
				CompoundTag compoundnbt = template$entityinfo.nbt.copy();
				Vec3 vector3d1 = template$entityinfo.pos;
				ListTag listnbt = new ListTag();
				listnbt.add(DoubleTag.valueOf(vector3d1.x));
				listnbt.add(DoubleTag.valueOf(vector3d1.y));
				listnbt.add(DoubleTag.valueOf(vector3d1.z));
				compoundnbt.put("Pos", listnbt);
				compoundnbt.remove("UUID");
				StructureTemplateAccessor.port_lib$createEntityIgnoreException(world, compoundnbt).ifPresent((entity) -> {
					float f = entity.mirror(settings.getMirror());
					f = f + (entity.getYRot() - entity.rotate(settings.getRotation()));
					entity.moveTo(vector3d1.x, vector3d1.y, vector3d1.z, f, entity.getXRot());
					if (settings.shouldFinalizeEntities() && entity instanceof Mob) {
						((Mob) entity).finalizeSpawn(world, world.getCurrentDifficultyAt(BlockPos.containing(vector3d1)), MobSpawnType.STRUCTURE, (SpawnGroupData)null, compoundnbt);
					}

					world.addFreshEntityWithPassengers(entity);
				});
			}
		}
	}
}
