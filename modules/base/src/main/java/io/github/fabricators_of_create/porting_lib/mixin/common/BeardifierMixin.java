package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.block.CullingBlockEntityIterator;
import io.github.fabricators_of_create.porting_lib.world.PieceBeardifierIterator;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.Beardifier;

import net.minecraft.world.level.levelgen.structure.StructurePiece;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.Iterator;

@Mixin(Beardifier.class)
public class BeardifierMixin {
	@ModifyVariable(
			method = "method_42694",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/world/level/levelgen/structure/Structure;terrainAdaptation()Lnet/minecraft/world/level/levelgen/structure/TerrainAdjustment;"
					),
					to = @At(
							value = "INVOKE",
							target = "Ljava/util/Iterator;hasNext()Z"
					)
			),
			at = @At("STORE")
	)
	private static Iterator<StructurePiece> port_lib$wrapStructureIterator(Iterator<StructurePiece> iterator, ChunkPos pos, ObjectList<Beardifier.Rigid> rigids) {
		return new PieceBeardifierIterator(iterator, rigids);
	}
}
