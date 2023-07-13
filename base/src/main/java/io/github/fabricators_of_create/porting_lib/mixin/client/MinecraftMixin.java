package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.block.CustomHitEffectsBlock;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.event.client.ClientWorldEvents;
import io.github.fabricators_of_create.porting_lib.event.client.InteractEvents;
import io.github.fabricators_of_create.porting_lib.event.client.MinecraftTailCallback;
import io.github.fabricators_of_create.porting_lib.event.client.ParticleManagerRegistrationCallback;
import io.github.fabricators_of_create.porting_lib.event.client.RenderTickStartCallback;
import io.github.fabricators_of_create.porting_lib.event.common.AttackAirCallback;
import io.github.fabricators_of_create.porting_lib.event.common.ModsLoadedCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
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

	@Inject(
			method = "<init>",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/Minecraft;particleEngine:Lnet/minecraft/client/particle/ParticleEngine;",
					shift = Shift.AFTER
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

	@Inject(method = "setLevel", at = @At("HEAD"))
	public void port_lib$onHeadJoinWorld(ClientLevel world, CallbackInfo ci) {
		if (this.level != null) {
			ClientWorldEvents.UNLOAD.invoker().onWorldUnload((Minecraft) (Object) this, this.level);
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
		ClientWorldEvents.UNLOAD.invoker().onWorldUnload((Minecraft) (Object) this, this.level);
	}

	@Inject(
			method = "startAttack",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/phys/HitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;"
			),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	private void port_lib$onAttack(CallbackInfoReturnable<Boolean> cir, ItemStack stack, boolean bl) {
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

	@Inject(
			method = "startAttack",
			at = @At(
					value = "FIELD",
					ordinal = 2,
					target = "Lnet/minecraft/client/Minecraft;player:Lnet/minecraft/client/player/LocalPlayer;"
			)
	)
	private void port_lib$onAttackMiss(CallbackInfoReturnable<Boolean> cir) {
		AttackAirCallback.EVENT.invoker().attackAir(player);
	}

	@Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;render(FJZ)V", shift = Shift.BEFORE))
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
	private void port_lib$onStartUseItem(CallbackInfo ci, InteractionHand[] var1, int var2, int var3, InteractionHand hand) {
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
