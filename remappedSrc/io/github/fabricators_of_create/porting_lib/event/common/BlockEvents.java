package io.github.fabricators_of_create.porting_lib.event.common;

import io.github.fabricators_of_create.porting_lib.block.CustomExpBlock;
import io.github.fabricators_of_create.porting_lib.event.BaseEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public abstract class BlockEvents extends BaseEvent {

	public static final Event<BlockBreak> BLOCK_BREAK = EventFactory.createArrayBacked(BlockBreak.class, callbacks -> event -> {
		for(BlockBreak e : callbacks)
			e.onBlockBreak(event);
	});

	public interface BlockBreak {
		void onBlockBreak(BreakEvent event);
	}

	public static final Event<LeftClickBlock> LEFT_CLICK_BLOCK = EventFactory.createArrayBacked(LeftClickBlock.class, callbacks -> (player, pos, face) -> {
		for(LeftClickBlock e : callbacks)
			e.onLeftClickBlock(player, pos, face);
	});

	public interface LeftClickBlock {
		void onLeftClickBlock(PlayerEntity player, BlockPos pos, Direction face);
	}

	/**
	 * Invoked before a block is placed from the head of {@link BlockItem#useOnBlock(ItemUsageContext)}. Called on both client and server.
	 * Return null to fall back to further processing. Any non-null value will result in placement being cancelled.
	 */
	public static final Event<BeforePlace> BEFORE_PLACE = EventFactory.createArrayBacked(BeforePlace.class, callbacks -> context -> {
		for (BeforePlace callback : callbacks) {
			ActionResult result = callback.beforePlace(context);
			if (result != null)
				return result;
		}
//		return InteractionResult.PASS;
		//noinspection removal
		ActionResult legacyResult = BlockPlaceCallback.EVENT.invoker().onBlockPlace(context);
		return legacyResult == ActionResult.PASS ? null : legacyResult;
	});

	public interface BeforePlace {
		@Nullable
		ActionResult beforePlace(ItemPlacementContext ctx);
	}

	/**
	 * Invoked after a block is placed, from {@link BlockItem#useOnBlock(ItemUsageContext)}. Called on both client and server.
	 * @deprecated Use {@link BlockEvents#POST_PROCESS_PLACE} instead.
	 */
	@Deprecated
	public static final Event<AfterPlace> AFTER_PLACE = EventFactory.createArrayBacked(AfterPlace.class, callbacks -> context -> {
		for (AfterPlace callback : callbacks)
			callback.afterPlace(context);
	});

	public interface AfterPlace {
		void afterPlace(ItemPlacementContext ctx);
	}

	/**
	 * Invoked after a block is placed, from the TAIL of {@link BlockItem#place(ItemPlacementContext)}.
	 * Called on both client and server.
	 * Provides the block's Position and BlockState as well.
	 */
	public static final Event<PostProcessPlace> POST_PROCESS_PLACE = EventFactory.createArrayBacked(PostProcessPlace.class, callbacks -> (context, blockPos, blockState) -> {
		for (PostProcessPlace callback : callbacks)
			callback.postProcessPlace(context, blockPos, blockState);
	});

	public interface PostProcessPlace {
		void postProcessPlace(ItemPlacementContext ctx, BlockPos blockPos, BlockState blockState);
	}

	private final WorldAccess world;
	private final BlockPos pos;
	private final BlockState state;
	public BlockEvents(WorldAccess world, BlockPos pos, BlockState state) {
		this.pos = pos;
		this.world = world;
		this.state = state;
	}

	public WorldAccess getWorld()
	{
		return world;
	}

	public BlockPos getPos()
	{
		return pos;
	}

	public BlockState getState()
	{
		return state;
	}

	public static class BreakEvent extends BlockEvents {
		/** Reference to the Player who broke the block. If no player is available, use a EntityFakePlayer */
		private final PlayerEntity player;
		private int exp;

		public BreakEvent(World world, BlockPos pos, BlockState state, PlayerEntity player) {
			super(world, pos, state);
			this.player = player;

			if (state == null || !player.canHarvest(state)) {// Handle empty block or player unable to break block scenario{
				this.exp = 0;
			} else{
				int bonusLevel = EnchantmentHelper.getLevel(Enchantments.FORTUNE, player.getMainHandStack());
				int silklevel = EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, player.getMainHandStack());
				this.exp = state.getBlock() instanceof CustomExpBlock exp ? exp.getExpDrop(state, world, pos, bonusLevel, silklevel) : 0;
			}
		}

		public PlayerEntity getPlayer()
		{
			return player;
		}

		/**
		 * Get the experience dropped by the block after the event has processed
		 *
		 * @return The experience to drop or 0 if the event was canceled
		 */
		public int getExpToDrop()
		{
			return this.isCanceled() ? 0 : exp;
		}

		/**
		 * Set the amount of experience dropped by the block after the event has processed
		 *
		 * @param exp 1 or higher to drop experience, else nothing will drop
		 */
		public void setExpToDrop(int exp)
		{
			this.exp = exp;
		}

		@Override
		public void sendEvent() {
			BLOCK_BREAK.invoker().onBlockBreak(this);
		}
	}
}
