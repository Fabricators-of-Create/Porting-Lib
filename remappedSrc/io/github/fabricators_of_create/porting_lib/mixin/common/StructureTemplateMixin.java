package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Lists;

import io.github.fabricators_of_create.porting_lib.extensions.StructureTemplateExtensions;
import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.StructureTemplateAccessor;

@Mixin(Structure.class)
public abstract class StructureTemplateMixin implements StructureTemplateExtensions {
	@Shadow
	@Final
	private List<Structure.StructureEntityInfo> entityInfoList;

	@Unique
	@Override
	public List<Structure.StructureEntityInfo> getEntities() {
		return entityInfoList;
	}

	@Inject(
			method = "placeInWorld",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;placeEntities(Lnet/minecraft/world/level/ServerLevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Mirror;Lnet/minecraft/world/level/block/Rotation;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Z)V",
					shift = At.Shift.BEFORE
			),
			cancellable = true
	)
	public void port_lib$place(ServerWorldAccess iServerWorld, BlockPos blockPos, BlockPos blockPos2, StructurePlacementData placementSettings, Random random, int i, CallbackInfoReturnable<Boolean> cir) {
		addEntitiesToWorld(iServerWorld, blockPos, placementSettings);
		cir.setReturnValue(true);
	}

	@Override
	public Vec3d transformedVec3d(StructurePlacementData placementIn, Vec3d pos) {
		return Structure.transformAround(pos, placementIn.getMirror(), placementIn.getRotation(), placementIn.getPosition());
	}

	@Override
	public List<Structure.StructureEntityInfo> processEntityInfos(@Nullable Structure template, WorldAccess world, BlockPos blockPos, StructurePlacementData settings, List<Structure.StructureEntityInfo> infos) {
		List<Structure.StructureEntityInfo> list = Lists.newArrayList();
		for(Structure.StructureEntityInfo entityInfo : infos) {
			Vec3d pos = transformedVec3d(settings, entityInfo.pos).add(Vec3d.of(blockPos));
			BlockPos blockpos = Structure.transform(settings, entityInfo.blockPos).add(blockPos);
			Structure.StructureEntityInfo info = new Structure.StructureEntityInfo(pos, blockpos, entityInfo.nbt);
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
	public void addEntitiesToWorld(ServerWorldAccess world, BlockPos blockPos, StructurePlacementData settings) {
		for(Structure.StructureEntityInfo template$entityinfo : processEntityInfos((Structure) (Object) this, world, blockPos, settings, this.getEntities())) {
			BlockPos blockpos = Structure.transformAround(template$entityinfo.blockPos, settings.getMirror(), settings.getRotation(), settings.getPosition()).add(blockPos);
			blockpos = template$entityinfo.blockPos;
			if (settings.getBoundingBox() == null || settings.getBoundingBox().contains(blockpos)) {
				NbtCompound compoundnbt = template$entityinfo.nbt.copy();
				Vec3d vector3d1 = template$entityinfo.pos;
				NbtList listnbt = new NbtList();
				listnbt.add(NbtDouble.of(vector3d1.x));
				listnbt.add(NbtDouble.of(vector3d1.y));
				listnbt.add(NbtDouble.of(vector3d1.z));
				compoundnbt.put("Pos", listnbt);
				compoundnbt.remove("UUID");
				StructureTemplateAccessor.port_lib$createEntityIgnoreException(world, compoundnbt).ifPresent((entity) -> {
					float f = entity.applyMirror(settings.getMirror());
					f = f + (entity.getYaw() - entity.applyRotation(settings.getRotation()));
					entity.refreshPositionAndAngles(vector3d1.x, vector3d1.y, vector3d1.z, f, entity.getPitch());
					if (settings.shouldFinalizeEntities() && entity instanceof MobEntity) {
						((MobEntity) entity).initialize(world, world.getLocalDifficulty(new BlockPos(vector3d1)), SpawnReason.STRUCTURE, (EntityData)null, compoundnbt);
					}

					world.spawnEntityAndPassengers(entity);
				});
			}
		}
	}
}
