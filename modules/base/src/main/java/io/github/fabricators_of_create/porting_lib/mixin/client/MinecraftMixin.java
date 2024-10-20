package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.block.CustomHitEffectsBlock;
import io.github.fabricators_of_create.porting_lib.event.common.AddPackFindersEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.event.client.InteractEvents;
import io.github.fabricators_of_create.porting_lib.event.client.MinecraftTailCallback;
import io.github.fabricators_of_create.porting_lib.event.client.ParticleManagerRegistrationCallback;
import io.github.fabricators_of_create.porting_lib.event.client.RenderFrameEvent;
import io.github.fabricators_of_create.porting_lib.event.common.ModsLoadedCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.HitResult;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Shadow
	public LocalPlayer player;

	@Shadow
	public @Nullable ClientLevel level;

	@Shadow
	@Nullable
	public HitResult hitResult;

	@Shadow
	@Final
	private PackRepository resourcePackRepository;

	@Shadow
	@Final
	private DeltaTracker.Timer timer;

	@Inject(
			method = "<init>",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/Minecraft;particleEngine:Lnet/minecraft/client/particle/ParticleEngine;",
					shift = Shift.AFTER,
					ordinal = 0
			)
	)
	public void port_lib$registerParticleManagers(GameConfig gameConfiguration, CallbackInfo ci) {
		ParticleManagerRegistrationCallback.EVENT.invoker().onParticleManagerRegistration();
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void port_lib$mcTail(GameConfig gameConfiguration, CallbackInfo ci) {
		MinecraftTailCallback.EVENT.invoker().onMinecraftTail((Minecraft) (Object) this);
	}

	// Inject right after the fabric entrypoint
	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;"))
	public void port_lib$modsLoaded(GameConfig gameConfig, CallbackInfo ci) {
		ModsLoadedCallback.EVENT.invoker().onAllModsLoaded(EnvType.CLIENT);
	}

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;reload()V"))
	private void addClientResources(GameConfig gameConfig, CallbackInfo ci) {
		new AddPackFindersEvent(PackType.CLIENT_RESOURCES, this.resourcePackRepository::pl$addPackFinder).sendEvent();
	}

	@Inject(
			method = "startAttack",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/phys/HitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;"
			),
			cancellable = true
	)
	private void port_lib$onAttack(CallbackInfoReturnable<Boolean> cir, @Local ItemStack stack, @Local boolean bl) {
		InteractionResult result = InteractEvents.ATTACK.invoker().onAttack((Minecraft) (Object) this, hitResult);
		if (result != InteractionResult.PASS) {
			if (result == InteractionResult.SUCCESS) {
				player.swing(InteractionHand.MAIN_HAND);
			}
			cir.setReturnValue(bl); // bl is returned anyway, but return early to skip the switch
		}
	}

	@WrapOperation(
			method = "continueAttack",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;continueDestroyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"
			)
	)
	private boolean port_lib$onContinueAttack(MultiPlayerGameMode gameMode, BlockPos posBlock, Direction directionFacing, Operation<Boolean> original) {
		InteractionResult result = InteractEvents.ATTACK.invoker().onAttack((Minecraft) (Object) this, hitResult);
		if (result != InteractionResult.PASS) {
			// true -> skip continueDestroyBlock, do swing and crack
			// false -> skip continueDestroyBlock, do NOT swing or crack
			return result == InteractionResult.SUCCESS;
		}
		return original.call(gameMode, posBlock, directionFacing); // continue to continueDestroyBlock
	}

	@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;render(Lnet/minecraft/client/DeltaTracker;Z)V"))
	private void renderTickStart(CallbackInfo ci) {
		RenderFrameEvent.PRE.invoker().onRenderFrame(this.timer);
	}

	@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;render(Lnet/minecraft/client/DeltaTracker;Z)V", shift = Shift.AFTER))
	private void renderTickEnd(CallbackInfo ci) {
		RenderFrameEvent.POST.invoker().onRenderFrame(this.timer);
	}

	@Inject(
			method = "startUseItem",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"
			),
			cancellable = true
	)
	private void port_lib$onStartUseItem(CallbackInfo ci, @Local InteractionHand hand) {
		InteractionResult result = InteractEvents.USE.invoker().onUse((Minecraft) (Object) this, hitResult, hand);
		if (result != InteractionResult.PASS) {
			if (result == InteractionResult.SUCCESS) {
				player.swing(hand);
			}
			ci.cancel();
		}
	}

	@Inject(method = "pickBlock", cancellable = true, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getAbilities()Lnet/minecraft/world/entity/player/Abilities;"))
	private void port_lib$onPickBlock(CallbackInfo ci) {
		if (InteractEvents.PICK.invoker().onPick((Minecraft) (Object) this, hitResult)) {
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
	private boolean customHitEffects(ParticleEngine engine, BlockPos pos, Direction side) {
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() instanceof CustomHitEffectsBlock custom)
			return !custom.addHitEffects(state, level, hitResult, engine);
		return true;
	}
}
