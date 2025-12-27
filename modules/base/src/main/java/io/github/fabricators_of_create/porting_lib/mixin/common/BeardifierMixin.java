package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.fabricators_of_create.porting_lib.world.PieceBeardifierIterator;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Beardifier;

import net.minecraft.world.level.levelgen.structure.StructurePiece;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.Iterator;
import java.util.List;

@Mixin(Beardifier.class)
public class BeardifierMixin {
	@ModifyExpressionValue(
			method = "forStructuresInChunk",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/world/level/levelgen/structure/Structure;terrainAdaptation()Lnet/minecraft/world/level/levelgen/structure/TerrainAdjustment;"
					),
					to = @At(
							value = "INVOKE",
							target = "Ljava/util/Iterator;hasNext()Z",
							ordinal = 1
					)
			),
			at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;")
	)
	private static Iterator<StructurePiece> port_lib$wrapStructureIterator(Iterator<StructurePiece> iterator, @Local(ordinal = 1) List<Beardifier.Rigid> rigids) {
		return new PieceBeardifierIterator(iterator, rigids);
	}
}
