package io.github.fabricators_of_create.porting_lib.mixin.client;

import static net.minecraft.util.ActionResult.PASS;
import static net.minecraft.util.ActionResult.SUCCESS;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.block.CustomHitEffectsBlock;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.event.client.ClientWorldEvents;
import io.github.fabricators_of_create.porting_lib.event.client.MinecraftTailCallback;
import io.github.fabricators_of_create.porting_lib.event.client.OnStartUseItemCallback;
import io.github.fabricators_of_create.porting_lib.event.client.ParticleManagerRegistrationCallback;
import io.github.fabricators_of_create.porting_lib.event.client.PickBlockCallback;
import io.github.fabricators_of_create.porting_lib.event.client.RenderTickStartCallback;
import io.github.fabricators_of_create.porting_lib.event.common.AttackAirCallback;
import io.github.fabricators_of_create.porting_lib.event.common.ModsLoadedCallback;
import io.github.fabricators_of_create.porting_lib.model.ModelLoaderRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MinecraftMixin {
	@Shadow
	public ClientPlayerEntity player;

	@Shadow
	@Nullable
	public ClientWorld level;

	@Shadow
	@Nullable
	public HitResult hitResult;

	@Inject(
			method = "<init>",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/Minecraft;particleEngine:Lnet/minecraft/client/particle/ParticleEngine;",
					shift = Shift.AFTER
			)
	)
	public void port_lib$registerParticleManagers(RunArgs gameConfiguration, CallbackInfo ci) {
		ParticleManagerRegistrationCallback.EVENT.invoker().onParticleManagerRegistration();
	}

	@Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;resourceManager:Lnet/minecraft/server/packs/resources/ReloadableResourceManager;", ordinal = 0, shift = Shift.AFTER))
	public void port_lib$initModelRegistry(RunArgs gameConfig, CallbackInfo ci) {
		ModelLoaderRegistry.init();
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void port_lib$mcTail(RunArgs gameConfiguration, CallbackInfo ci) {
		MinecraftTailCallback.EVENT.invoker().onMinecraftTail((MinecraftClient) (Object) this);
	}

	// Inject right after the fabric entrypoint
	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;"))
	public void port_lib$modsLoaded(RunArgs gameConfig, CallbackInfo ci) {
		ModsLoadedCallback.EVENT.invoker().onAllModsLoaded(EnvType.CLIENT);
	}

	@Inject(method = "setLevel", at = @At("HEAD"))
	public void port_lib$onHeadJoinWorld(ClientWorld world, CallbackInfo ci) {
		if (this.level != null) {
			ClientWorldEvents.UNLOAD.invoker().onWorldUnload((MinecraftClient) (Object) this, this.level);
		}
	}

	@Inject(
			method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V",
			at = @At(
					value = "JUMP",
					opcode = Opcodes.IFNULL,
					ordinal = 1,
					shift = Shift.AFTER
			)
	)
	public void port_lib$onDisconnect(Screen screen, CallbackInfo ci) {
		ClientWorldEvents.UNLOAD.invoker().onWorldUnload((MinecraftClient) (Object) this, this.level);
	}

	@Inject(method = "startAttack", at = @At(value = "FIELD", ordinal = 2, target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/player/LocalPlayer;"))
	private void port_lib$onClickMouse(CallbackInfoReturnable<Boolean> cir) {
		AttackAirCallback.EVENT.invoker().attackAir(player);
	}

	@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/toasts/ToastComponent;render(Lcom/mojang/blaze3d/vertex/PoseStack;)V", shift = Shift.BEFORE))
	private void port_lib$renderTickStart(CallbackInfo ci) {
		RenderTickStartCallback.EVENT.invoker().tick();
	}

	@Inject(
			method = "startUseItem",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"
			),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	private void port_lib$onStartUseItem(CallbackInfo ci, Hand[] var1, int var2, int var3, Hand hand) {
		ActionResult result = OnStartUseItemCallback.EVENT.invoker().onStartUse(hand);
		if (result != PASS) {
			if (result == SUCCESS) {
				player.swingHand(hand);
			}
			ci.cancel();
		}
	}

	@Inject(method = "pickBlock", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAbilities()Lnet/minecraft/world/entity/player/Abilities;"))
	private void port_lib$onPickBlock(CallbackInfo ci) {
		if (PickBlockCallback.EVENT.invoker().onPickBlock()) {
			ci.cancel();
		}
	}

	@WrapWithCondition(
			method = "continueAttack",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/particle/ParticleEngine;crack(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)V"
			)
	)
	private boolean customHitEffects(ParticleManager engine, BlockPos pos, Direction side) {
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() instanceof CustomHitEffectsBlock custom)
			return !custom.addHitEffects(state, level, hitResult, engine);
		return true;
	}
}
