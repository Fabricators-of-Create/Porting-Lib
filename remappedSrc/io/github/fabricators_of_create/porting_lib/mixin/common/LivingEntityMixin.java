package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.block.CustomScaffoldingBlock;
import io.github.fabricators_of_create.porting_lib.util.ContinueUsingItem;
import io.github.fabricators_of_create.porting_lib.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import io.github.fabricators_of_create.porting_lib.util.UsingTickItem;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.attributes.PortingLibAttributes;
import io.github.fabricators_of_create.porting_lib.block.CustomFrictionBlock;
import io.github.fabricators_of_create.porting_lib.block.CustomLandingEffectsBlock;
import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents;
import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityEvents.Fall.FallEvent;
import io.github.fabricators_of_create.porting_lib.event.common.PotionEvents;
import io.github.fabricators_of_create.porting_lib.event.common.LivingEntityUseItemEvents;
import io.github.fabricators_of_create.porting_lib.extensions.EntityExtensions;
import io.github.fabricators_of_create.porting_lib.item.EntitySwingListenerItem;
import io.github.fabricators_of_create.porting_lib.item.EquipmentItem;

@Mixin(value = LivingEntity.class, priority = 500)
public abstract class LivingEntityMixin extends Entity implements EntityExtensions {
	@Shadow
	protected PlayerEntity lastHurtByPlayer;

	@Shadow
	public abstract ItemStack getItemInHand(Hand interactionHand);

	@Shadow
	public abstract boolean hasEffect(StatusEffect potion);

	@Shadow
	@Nullable
	public abstract EntityAttributeInstance getAttribute(EntityAttribute attribute);

	@Shadow
	public abstract ItemStack getUseItem();

	@Shadow
	public abstract int getUseItemRemainingTicks();

	@Shadow
	protected ItemStack useItem;

	@Shadow
	protected int useItemRemaining;

	@Shadow
	public abstract Hand getUsedItemHand();

	public LivingEntityMixin(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "createLivingAttributes", at = @At("RETURN"))
	private static void port_lib$addModdedAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
		cir.getReturnValue().add(PortingLibAttributes.ENTITY_GRAVITY).add(PortingLibAttributes.SWIM_SPEED);
	}

	@ModifyArgs(
			method = "dropAllDeathLoot",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/entity/LivingEntity;dropCustomDeathLoot(Lnet/minecraft/world/damagesource/DamageSource;IZ)V"
			)
	)
	private void port_lib$modifyLootingLevel(Args args) {
		DamageSource source = args.get(0);
		int originalLevel = args.get(1);
		boolean recentlyHit = args.get(2);
		int modifiedLevel = LivingEntityEvents.LOOTING_LEVEL.invoker().modifyLootingLevel(source, (LivingEntity) (Object) this, originalLevel, recentlyHit);
		args.set(1, modifiedLevel);
	}

	@Inject(method = "dropAllDeathLoot", at = @At(value = "JUMP", opcode = Opcodes.IFLE, ordinal = 0))
	public void port_lib$fixNullDrops(DamageSource damageSource, CallbackInfo ci) {
		captureDrops(new ArrayList<>());
	}

	@Inject(method = "dropAllDeathLoot", at = @At("TAIL"))
	private void port_lib$spawnDropsTAIL(DamageSource source, CallbackInfo ci) {
		Collection<ItemEntity> drops = this.captureDrops(null);
		if (!LivingEntityEvents.DROPS.invoker().onLivingEntityDrops((LivingEntity) (Object) this, source, drops))
			drops.forEach(e -> world.spawnEntity(e));
	}

	@Unique
	private FallEvent port_lib$currentFallEvent = null;

	@Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
	public void port_lib$cancelFall(float fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
		port_lib$currentFallEvent = new FallEvent((LivingEntity) (Object) this, source, fallDistance, multiplier);
		port_lib$currentFallEvent.sendEvent();
		if (port_lib$currentFallEvent.isCanceled()) {
			cir.setReturnValue(true);
		}
	}

	@ModifyVariable(method = "causeFallDamage", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	public float port_lib$modifyDistance(float fallDistance) {
		if (port_lib$currentFallEvent != null) {
			return port_lib$currentFallEvent.getDistance();
		}
		return fallDistance;
	}

	@ModifyVariable(method = "causeFallDamage", at = @At("HEAD"), argsOnly = true, ordinal = 1)
	public float port_lib$modifyMultiplier(float multiplier) {
		if (port_lib$currentFallEvent != null) {
			return port_lib$currentFallEvent.getDamageMultiplier();
		}
		return multiplier;
	}

	@ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private float port_lib$modifyDamage(float damageAmount, DamageSource source, float damageAmountButAgainBecauseYes) {
		return LivingEntityEvents.ACTUALLY_HURT.invoker().onHurt(source, (LivingEntity) (Object) this, damageAmount);
	}

	@Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("HEAD"), cancellable = true)
	private void port_lib$swingHand(Hand hand, boolean bl, CallbackInfo ci) {
		ItemStack stack = getItemInHand(hand);
		if (!stack.isEmpty() && stack.getItem() instanceof EntitySwingListenerItem listener && listener.onEntitySwing(stack, (LivingEntity) (Object) this))
			ci.cancel();
	}

	@ModifyArgs(method = "dropExperience", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"))
	private void create$dropExperience(Args args) {
		int amount = args.get(2);
		int newAmount = LivingEntityEvents.EXPERIENCE_DROP.invoker().onLivingEntityExperienceDrop(amount, lastHurtByPlayer);
		newAmount = LivingEntityEvents.EXPERIENCE_DROP_WITH_ENTITY.invoker().onLivingEntityExperienceDrop(newAmount, lastHurtByPlayer, (LivingEntity) (Object) this);
		if (amount != newAmount) args.set(2, newAmount);
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"))
	private void port_lib$tick(CallbackInfo ci) {
		LivingEntityEvents.TICK.invoker().onLivingEntityTick((LivingEntity) (Object) this);
	}

	@ModifyVariable(method = "knockback", at = @At("STORE"), ordinal = 0, argsOnly = true)
	private double port_lib$takeKnockback(double f) {
		if (lastHurtByPlayer != null)
			return LivingEntityEvents.KNOCKBACK_STRENGTH.invoker().onLivingEntityTakeKnockback(f, lastHurtByPlayer);

		return f;
	}

	@ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
	private float port_lib$onHurt(float amount, DamageSource source, float amount2) {
		return LivingEntityEvents.HURT.invoker().onHurt(source, amount);
	}

	@Inject(method = "jumpFromGround", at = @At("TAIL"))
	public void port_lib$onJump(CallbackInfo ci) {
		LivingEntityEvents.JUMP.invoker().onLivingEntityJump((LivingEntity) (Object) this);
	}

	@SuppressWarnings("InvalidInjectorMethodSignature")
	@Inject(
			method = "checkFallDamage",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I",
					shift = At.Shift.BEFORE
			),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	protected void port_lib$updateFallState(double y, boolean onGround, BlockState state, BlockPos pos,
										  CallbackInfo ci, float f, double d, int i) {
		if (state.getBlock() instanceof CustomLandingEffectsBlock custom &&
				custom.addLandingEffects(state, (ServerWorld) world, pos, state, (LivingEntity) (Object) this, i)) {
			super.fall(y, onGround, state, pos);
			ci.cancel();
		}
	}

	@SuppressWarnings("InvalidInjectorMethodSignature")
	@ModifyVariable(
			method = "travel",
			slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getBlockPosBelowThatAffectsMyMovement()Lnet/minecraft/core/BlockPos;")),
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/block/Block;getFriction()F")
	)
	public float port_lib$setSlipperiness(float p) {
		BlockPos pos = getVelocityAffectingPos();
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof CustomFrictionBlock custom) {
			return custom.getFriction(state, world, pos, (LivingEntity) (Object) this);
		}
		return p;
	}

	@ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z", ordinal = 0))
	private boolean port_lib$disableOldLogicFallingLogic(boolean original) {
		return false;
	}

	private static final EntityAttributeModifier SLOW_FALLING = new EntityAttributeModifier(UUID.fromString("A5B6CF2A-2F7C-31EF-9022-7C3E7D5E6ABA"), "Slow falling acceleration reduction", -0.07, EntityAttributeModifier.Operation.ADDITION); // Add -0.07 to 0.08 so we get the vanilla default of 0.01

	@Inject(
			method = "travel",
			at = @At(
					value = "CONSTANT",args = {
						"doubleValue=0.08D"
				}
			)
	)
	public void port_lib$entityGravity(Vec3d travelVector, CallbackInfo ci) {
		EntityAttributeInstance gravity = this.getAttribute(PortingLibAttributes.ENTITY_GRAVITY);
		if (gravity != null) {
			boolean falling = this.getVelocity().y <= 0.0D;
			if (falling && this.hasEffect(StatusEffects.SLOW_FALLING)) {
				if (!gravity.hasModifier(SLOW_FALLING)) gravity.addTemporaryModifier(SLOW_FALLING);
				this.resetFallDistance();
			} else if (gravity.hasModifier(SLOW_FALLING)) {
				gravity.removeModifier(SLOW_FALLING);
			}
		}
	}

	@ModifyVariable(method = "travel", at = @At(value = "STORE", ordinal = 0)) // double d = 0.08;
	private double port_lib$modifyGravity(double original) {
		if (original == 0.08) { // only apply gravity if other mods haven't changed it
			EntityAttributeInstance attribute = this.getAttribute(PortingLibAttributes.ENTITY_GRAVITY);
			if (attribute != null)
				return attribute.getValue();
		}
		return original;
	}

	@Inject(method = "completeUsingItem", at = @At(value = "INVOKE", shift = Shift.BY, by = 2, target = "Lnet/minecraft/world/item/ItemStack;finishUsingItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;"),
			locals = LocalCapture.CAPTURE_FAILHARD)
	public void port_lib$onFinishUsing(CallbackInfo ci, Hand hand, ItemStack result) {
		LivingEntityUseItemEvents.LIVING_USE_ITEM_FINISH.invoker().onUseItem((LivingEntity) (Object) this, this.getUseItem().copy(), getUseItemRemainingTicks(), result);
	}

	@Inject(method = "getEquipmentSlotForItem", at = @At("HEAD"), cancellable = true)
	private static void port_lib$getSlotForItemStack(ItemStack itemStack, CallbackInfoReturnable<EquipmentSlot> cir) {
		if (itemStack.getItem() instanceof EquipmentItem equipment) {
			cir.setReturnValue(equipment.getEquipmentSlot(itemStack));
		}
	}

	@Inject(
			method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;",
					shift = Shift.BY,
					by = 3,
					remap = false
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void port_lib$addEffect(StatusEffectInstance newEffect, @Nullable Entity source, CallbackInfoReturnable<Boolean> cir,
								   StatusEffectInstance oldEffect) {
		PotionEvents.POTION_ADDED.invoker().onPotionAdded((LivingEntity) (Object) this, newEffect, oldEffect, source);
	}

	@Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
	public void port_lib$canBeAffected(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
		ActionResult result = PotionEvents.POTION_APPLICABLE.invoker().onPotionApplicable((LivingEntity) (Object) this, effect);
		if (result != ActionResult.PASS)
			cir.setReturnValue(result == ActionResult.SUCCESS);
	}

	@Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
	public void port_lib$attackEvent(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if(LivingEntityEvents.ATTACK.invoker().onAttack((LivingEntity) (Object) this, source, amount)) cir.setReturnValue(false);
	}

	@Inject(method = "collectEquipmentChanges", at = @At(value = "JUMP", opcode = Opcodes.IFNONNULL), locals = LocalCapture.CAPTURE_FAILHARD)
	public void port_lib$equipmentChange(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir, Map<EquipmentSlot, ItemStack> map, EquipmentSlot[] equipmentslots, int i, int j, EquipmentSlot equipmentslot, ItemStack itemstack, ItemStack itemstack1) {
		LivingEntityEvents.EQUIPMENT_CHANGE.invoker().onEquipmentChange((LivingEntity) (Object) this, equipmentslot, itemstack, itemstack1);
	}

	@Inject(method = "updatingUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", shift = Shift.AFTER, ordinal = 1))
	public void port_lib$onUsingTick(CallbackInfo ci) {
		if (useItem.getItem() instanceof UsingTickItem usingTickItem) {
			if (!this.useItem.isEmpty()) {
				if (useItemRemaining > 0)
					usingTickItem.onUsingTick(useItem, (LivingEntity) (Object) this, useItemRemaining);
			}
		}
	}

	@ModifyExpressionValue(method = "updatingUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameIgnoreDurability(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
	public boolean port_lib$canContinueUsing(boolean original) {
		if (useItem.getItem() instanceof ContinueUsingItem continueUsingItem) {
			ItemStack to = this.getItemInHand(this.getUsedItemHand());
			if (!useItem.isEmpty() && !to.isEmpty())
			{
				return continueUsingItem.canContinueUsing(useItem, to);
			}
			return false;
		}
		return original;
	}

	@Inject(method = "getVisibilityPercent", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void port_lib$getVisibility(Entity lookingEntity, CallbackInfoReturnable<Double> cir, double d) {
		double newVis = LivingEntityEvents.VISIBILITY.invoker().getEntityVisibilityMultiplier(MixinHelper.cast(this), lookingEntity, d);
		if (newVis != d)
			cir.setReturnValue(Math.max(0, newVis));
	}

	@WrapOperation(
			method = "dropFromLootTable",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V"
			)
	)
	private void wrapConsumer(LootTable lootTable, LootContext ctx, Consumer<ItemStack> consumer, Operation<Void> original) {
		List<ItemStack> stacks = new ArrayList<>();
		Consumer<ItemStack> collector = stacks::add;
		original.call(lootTable, ctx, collector);
		List<ItemStack> modified = PortingHooks.modifyLoot(lootTable.getLootTableId(), stacks, ctx);
		modified.forEach(consumer);
	}

	@ModifyExpressionValue(
			method = "handleOnClimbable",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"
			)
	)
	private boolean customScaffoldingMovement(boolean original) {
		BlockState state = getBlockStateAtPos();
		if (state.getBlock() instanceof CustomScaffoldingBlock custom)
			return custom.isScaffolding(state, world, getBlockPos(), (LivingEntity) (Object) this);
		return original;
	}

	@ModifyArgs(
			method = "checkFallDamage",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ServerLevel;sendParticles(Lnet/minecraft/core/particles/ParticleOptions;DDDIDDDD)I"
			)
	)
	private void addSourcePos(Args args, double y, boolean onGround, BlockState state, BlockPos pos) {
		ParticleEffect options = args.get(0);
		if (options instanceof BlockStateParticleEffect block)
			block.setSourcePos(pos);
	}
}
