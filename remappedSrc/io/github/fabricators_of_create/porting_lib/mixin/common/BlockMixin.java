package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.extensions.BlockExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import io.github.fabricators_of_create.porting_lib.extensions.RegistryNameProvider;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Block.class)
public abstract class BlockMixin extends AbstractBlock implements RegistryNameProvider, BlockExtensions {

	private BlockMixin(AbstractBlock.Settings properties) {
		super(properties);
	}

	@Unique
	private Identifier port_lib$registryName = null;

	@Override
	public Identifier getRegistryName() {
		if (port_lib$registryName == null) {
			port_lib$registryName = Registry.BLOCK.getId((Block) (Object) this);
		}
		return port_lib$registryName;
	}

	@ModifyExpressionValue(method = "shouldRenderFace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;skipRendering(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z"))
	private static boolean shouldRenderFace(boolean orignial, BlockState pState, BlockView pLevel, BlockPos pOffset, Direction pFace, BlockPos pPos) {
		return orignial || (pState.supportsExternalFaceHiding() && pLevel.getBlockState(pPos).hidesNeighborFace(pLevel, pPos, pState, pFace.getOpposite()));
	}
}
