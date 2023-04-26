package io.github.fabricators_of_create.porting_lib.event.common;

import com.google.common.base.Preconditions;

import io.github.fabricators_of_create.porting_lib.item.UseFirstBehaviorItem;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.fabricators_of_create.porting_lib.event.BaseEvent.Result.DEFAULT;
import static io.github.fabricators_of_create.porting_lib.event.BaseEvent.Result.DENY;

public abstract class PlayerInteractionEvents extends PlayerEvents {
	/**
	 * This event is fired when a player left clicks while targeting a block.
	 * This event controls which of {@link Block#attack(BlockState, Level, BlockPos, Player)} and/or the item harvesting methods will be called
	 * Canceling the event will cause none of the above noted methods to be called.
	 * There are various results to this event, see the getters below.
	 * <p>
	 * Note that if the event is canceled and the player holds down left mouse, the event will continue to fire.
	 * This is due to how vanilla calls the left click handler methods.
	 * <p>
	 * Also note that creative mode directly breaks the block without running any other logic.
	 * Therefore, in creative mode, {@link LeftClickBlock#setUseBlock} and {@link LeftClickBlock#setUseItem} have no effect.
	 * <p>
	 * This event is fired after {@link AttackBlockCallback}.
	 * This event will not fire if {@link AttackBlockCallback} is canceled.
	 * Developers should use the fabric version of this event, and only use this event when needed.
	 */
	public static final Event<PlayerLeftClickBlock> LEFT_CLICK_BLOCK = EventFactory.createArrayBacked(PlayerLeftClickBlock.class, callbacks -> (event -> {
		for(PlayerLeftClickBlock e : callbacks)
			e.onLeftClickBlock(event);
	}));

	/**
	 * This event is fired on both sides whenever a player right clicks an entity.
	 *
	 * "Interact at" is an interact where the local vector (which part of the entity you clicked) is known.
	 * The state of this event affects whether {@link Entity#interactAt(Player, Vec3, InteractionHand)} is called.
	 *
	 * Let result be the return value of {@link Entity#interactAt(Player, Vec3, InteractionHand)}, or {@link #cancellationResult} if the event is cancelled.
	 * If we are on the client and result is not {@link InteractionResult#SUCCESS}, the client will then try {@link EntityInteract}.
	 */
	public static final Event<EntityInteractSpecificCallback> ENTITY_INTERACT_SPECIFIC = EventFactory.createArrayBacked(EntityInteractSpecificCallback.class, callbacks -> (event -> {
		for(EntityInteractSpecificCallback e : callbacks)
			e.onEntityInteract(event);
	}));

	/**
	 * This event is fired on both sides when the player right clicks an entity.
	 * It is responsible for all general entity interactions.
	 *
	 * This event is fired only if the result of the above {@link EntityInteractSpecific} is not {@link InteractionResult#SUCCESS}.
	 * This event's state affects whether {@link Entity#interact(Player, InteractionHand)} and
	 * {@link Item#interactLivingEntity(ItemStack, Player, LivingEntity, InteractionHand)} are called.
	 *
	 * Let result be {@link InteractionResult#SUCCESS} if {@link Entity#interact(Player, InteractionHand)} or
	 * {@link Item#interactLivingEntity(ItemStack, Player, LivingEntity, InteractionHand)} return true,
	 * or {@link #cancellationResult} if the event is cancelled.
	 * If we are on the client and result is not {@link InteractionResult#SUCCESS}, the client will then try {@link RightClickItem}.
	 */
	public static final Event<EntityInteractCallback> ENTITY_INTERACT = EventFactory.createArrayBacked(EntityInteractCallback.class, callbacks -> (event -> {
		for(EntityInteractCallback e : callbacks)
			e.onEntityInteract(event);
	}));

	/**
	 * This event is fired on both sides whenever the player right clicks while targeting a block. <br>
	 * This event controls which of {@link UseFirstBehaviorItem#onItemUseFirst}, {@link Block#use(BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult)},
	 * and {@link Item#useOn(UseOnContext)} will be called. <br>
	 * Canceling the event will cause none of the above three to be called. <br>
	 * <br>
	 * Let result be the first non-pass return value of the above three methods, or pass, if they all pass. <br>
	 * Or {@link #cancellationResult} if the event is cancelled. <br>
	 * If result equals {@link InteractionResult#PASS}, we proceed to {@link RightClickItem}.  <br>
	 * <br>
	 * There are various results to this event, see the getters below.  <br>
	 * Note that handling things differently on the client vs server may cause desynchronizations!
	 */
	public static final Event<RightClickBlockCallback> RIGHT_CLICK_BLOCK = EventFactory.createArrayBacked(RightClickBlockCallback.class, callbacks -> (event -> {
		for(RightClickBlockCallback e : callbacks)
			e.onRightClickBlock(event);
	}));

	/**
	 * This event is fired on both sides before the player triggers {@link Item#use(Level, Player, InteractionHand)}.
	 * Note that this is NOT fired if the player is targeting a block {@link UseBlockCallback} or entity {@link EntityInteract} {@link EntityInteractSpecific}.
	 *
	 * Let result be the return value of {@link Item#use(Level, Player, InteractionHand)}, or {@link #cancellationResult} if the event is cancelled.
	 * If we are on the client and result is not {@link InteractionResult#SUCCESS}, the client will then continue to other hands.
	 */
	public static final Event<RightClickItemCallback> RIGHT_CLICK_ITEM = EventFactory.createArrayBacked(RightClickItemCallback.class, callbacks -> (event -> {
		for(RightClickItemCallback e : callbacks)
			e.onRightClickItem(event);
	}));

	private final InteractionHand hand;
	private final BlockPos pos;
	@Nullable
	private final Direction face;
	private InteractionResult cancellationResult = InteractionResult.PASS;

	private PlayerInteractionEvents(Player player, InteractionHand hand, BlockPos pos, @Nullable Direction face) {
		super(Preconditions.checkNotNull(player, "Null player in PlayerInteractEvent!"));
		this.hand = Preconditions.checkNotNull(hand, "Null hand in PlayerInteractEvent!");
		this.pos = Preconditions.checkNotNull(pos, "Null position in PlayerInteractEvent!");
		this.face = face;
	}

	public static class LeftClickBlock extends PlayerInteractionEvents {
		private Result useBlock = DEFAULT;
		private Result useItem = DEFAULT;

		public LeftClickBlock(Player player, BlockPos pos, Direction face) {
			super(player, InteractionHand.MAIN_HAND, pos, face);
		}

		/**
		 * @return If {@link Block#attack(BlockState, Level, BlockPos, Player)} should be called. Changing this has no effect in creative mode
		 */
		public Result getUseBlock() {
			return useBlock;
		}

		/**
		 * @return If the block should be attempted to be mined with the current item. Changing this has no effect in creative mode
		 */
		public Result getUseItem() {
			return useItem;
		}

		public void setUseBlock(Result triggerBlock) {
			this.useBlock = triggerBlock;
		}

		public void setUseItem(Result triggerItem) {
			this.useItem = triggerItem;
		}

		@Override
		public void setCanceled(boolean canceled) {
			super.setCanceled(canceled);
			if (canceled) {
				useBlock = Result.DENY;
				useItem = Result.DENY;
			}
		}
		@Override
		public void sendEvent() {
			LEFT_CLICK_BLOCK.invoker().onLeftClickBlock(this);
		}
	}

	public static class EntityInteractSpecific extends PlayerInteractionEvents {
		private final Vec3 localPos;
		private final Entity target;

		public EntityInteractSpecific(Player player, InteractionHand hand, Entity target, Vec3 localPos)
		{
			super(player, hand, target.blockPosition(), null);
			this.localPos = localPos;
			this.target = target;
		}

		/**
		 * Returns the local interaction position. This is a 3D vector, where (0, 0, 0) is centered exactly at the
		 * center of the entity's bounding box at their feet. This means the X and Z values will be in the range
		 * [-width / 2, width / 2] while Y values will be in the range [0, height]
		 * @return The local position
		 */
		public Vec3 getLocalPos()
		{
			return localPos;
		}

		public Entity getTarget()
		{
			return target;
		}

		@Override
		public void sendEvent() {
			ENTITY_INTERACT_SPECIFIC.invoker().onEntityInteract(this);
		}
	}

	public static class EntityInteract extends PlayerInteractionEvents {
		private final Entity target;

		public EntityInteract(Player player, InteractionHand hand, Entity target)
		{
			super(player, hand, target.blockPosition(), null);
			this.target = target;
		}

		public Entity getTarget()
		{
			return target;
		}

		@Override
		public void sendEvent() {
			ENTITY_INTERACT.invoker().onEntityInteract(this);
			setCancellationResult(io.github.fabricators_of_create.porting_lib.event.common.EntityInteractCallback.EVENT.invoker().onEntityInteract(getPlayer(), getHand(), getTarget()));
		}
	}

	public static class RightClickBlock extends PlayerInteractionEvents {
		private Result useBlock = DEFAULT;
		private Result useItem = DEFAULT;
		private BlockHitResult hitVec;

		public RightClickBlock(Player player, InteractionHand hand, BlockPos pos, BlockHitResult hitVec) {
			super(player, hand, pos, hitVec.getDirection());
			this.hitVec = hitVec;
		}

		/**
		 * @return If {@link Block#use(BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult)} should be called
		 */
		public Result getUseBlock() {
			return useBlock;
		}

		/**
		 * @return If {@link UseFirstBehaviorItem#onItemUseFirst} and {@link Item#useOn(UseOnContext)} should be called
		 */
		public Result getUseItem() {
			return useItem;
		}

		/**
		 * @return The ray trace result targeting the block.
		 */
		public BlockHitResult getHitVec() {
			return hitVec;
		}

		/**
		 * DENY: {@link Block#use(BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult)} will never be called. <br>
		 * DEFAULT: {@link Block#use(BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult)} will be called if {@link UseFirstBehaviorItem#onItemUseFirst} passes. <br>
		 * Note that default activation can be blocked if the user is sneaking and holding an item that does not return true to {@link Item#doesSneakBypassUse}. <br>
		 * ALLOW: {@link Block#updateOrDestroy(BlockState, BlockState, LevelAccessor, BlockPos, int, int)} will always be called, unless {@link UseFirstBehaviorItem#onItemUseFirst} does not pass. <br>
		 */
		public void setUseBlock(Result triggerBlock) {
			this.useBlock = triggerBlock;
		}

		/**
		 * DENY: Neither {@link Item#useOn(UseOnContext)} or {@link UseFirstBehaviorItem#onItemUseFirst} will be called. <br>
		 * DEFAULT: {@link UseFirstBehaviorItem#onItemUseFirst} will always be called, and {@link Item#useOn(UseOnContext)} will be called if the block passes. <br>
		 * ALLOW: {@link UseFirstBehaviorItem#onItemUseFirst} will always be called, and {@link Item#useOn(UseOnContext)} will be called if the block passes, regardless of cooldowns or emptiness. <br>
		 */
		public void setUseItem(Result triggerItem) {
			this.useItem = triggerItem;
		}

		@Override
		public void setCanceled(boolean canceled) {
			super.setCanceled(canceled);
			if (canceled) {
				useBlock = DENY;
				useItem = DENY;
			}
		}

		@Override
		public void sendEvent() {
			RIGHT_CLICK_BLOCK.invoker().onRightClickBlock(this);
		}
	}

	public static class RightClickItem extends PlayerInteractionEvents {
		public RightClickItem(Player player, InteractionHand hand) {
			super(player, hand, player.blockPosition(), null);
		}

		@Override
		public void sendEvent() {
			RIGHT_CLICK_ITEM.invoker().onRightClickItem(this);
		}
	}

	/**
	 * @return The hand involved in this interaction. Will never be null.
	 */
	@NotNull
	public InteractionHand getHand() {
		return hand;
	}

	/**
	 * @return The itemstack involved in this interaction, {@code ItemStack.EMPTY} if the hand was empty.
	 */
	@NotNull
	public ItemStack getItemStack() {
		return getEntity().getItemInHand(hand);
	}

	/**
	 * If the interaction was on an entity, will be a BlockPos centered on the entity.
	 * If the interaction was on a block, will be the position of that block.
	 * Otherwise, will be a BlockPos centered on the player.
	 * Will never be null.
	 * @return The position involved in this interaction.
	 */
	@NotNull
	public BlockPos getPos() {
		return pos;
	}

	/**
	 * @return The face involved in this interaction. For all non-block interactions, this will return null.
	 */
	@Nullable
	public Direction getFace() {
		return face;
	}

	/**
	 * @return Convenience method to get the level of this interaction.
	 */
	public Level getLevel() {
		return getEntity().level;
	}

	/**
	 * @return The InteractionResult that will be returned to vanilla if the event is cancelled, instead of calling the relevant
	 * method of the event. By default, this is {@link InteractionResult#PASS}, meaning cancelled events will cause
	 * the client to keep trying more interactions until something works.
	 */
	public InteractionResult getCancellationResult() {
		return cancellationResult;
	}

	/**
	 * Set the InteractionResult that will be returned to vanilla if the event is cancelled, instead of calling the relevant
	 * method of the event.
	 * Note that this only has an effect on {@link RightClickBlock}, {@link RightClickItem}, {@link EntityInteract}, and {@link EntityInteractSpecific}.
	 */
	public void setCancellationResult(InteractionResult result) {
		this.cancellationResult = result;
	}

	@FunctionalInterface
	public interface PlayerLeftClickBlock {
		void onLeftClickBlock(LeftClickBlock event);
	}

	@FunctionalInterface
	public interface EntityInteractSpecificCallback {
		void onEntityInteract(EntityInteractSpecific event);
	}

	@FunctionalInterface
	public interface EntityInteractCallback {
		void onEntityInteract(EntityInteract event);
	}

	@FunctionalInterface
	public interface RightClickBlockCallback {
		void onRightClickBlock(RightClickBlock event);
	}

	@FunctionalInterface
	public interface RightClickItemCallback {
		void onRightClickItem(RightClickItem event);
	}
}
