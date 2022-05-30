package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.github.fabricators_of_create.porting_lib.block.LightEmissiveBlock;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@Mixin(ModelBlockRenderer.class)
public abstract class ModelBlockRendererMixin {
	@Shadow
	public abstract boolean tesselateWithAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay);

	@Shadow
	public abstract boolean tesselateWithoutAO(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay);

	@Inject(method = "tesselateBlock", at = @At(value = "HEAD"), cancellable = true)
	private void port_lib$modifyGravity(BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer consumer, boolean checkSides, Random random, long seed, int packedOverlay, CallbackInfoReturnable<Boolean> cir) {
		if (state.getBlock() instanceof LightEmissiveBlock lightEmissiveBlock) {
			boolean bl = Minecraft.useAmbientOcclusion() && lightEmissiveBlock.getLightEmission(state, level, pos) == 0 && model.useAmbientOcclusion();
			Vec3 vec3 = state.getOffset(level, pos);
			poseStack.translate(vec3.x, vec3.y, vec3.z);

			try {
				cir.setReturnValue(bl ? this.tesselateWithAO(level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay) : this.tesselateWithoutAO(level, model, state, pos, poseStack, consumer, checkSides, random, seed, packedOverlay));
			} catch (Throwable var17) {
				CrashReport crashReport = CrashReport.forThrowable(var17, "Tesselating block model");
				CrashReportCategory crashReportCategory = crashReport.addCategory("Block model being tesselated");
				CrashReportCategory.populateBlockDetails(crashReportCategory, level, pos, state);
				crashReportCategory.setDetail("Using AO", bl);
				throw new ReportedException(crashReport);
			}
		}
	}
}
