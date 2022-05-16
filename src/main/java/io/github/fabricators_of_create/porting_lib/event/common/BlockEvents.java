package io.github.fabricators_of_create.porting_lib.event.common;

import io.github.fabricators_of_create.porting_lib.block.CustomExpBlock;
import io.github.fabricators_of_create.porting_lib.event.BaseEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockEvents extends BaseEvent {

	public static final Event<BlockBreak> BLOCK_BREAK = EventFactory.createArrayBacked(BlockBreak.class, callbacks -> event -> {
		for(BlockBreak e : callbacks)
			e.onBlockBreak(event);
	});

	public static final Event<LeftClickBlock> LEFT_CLICK_BLOCK = EventFactory.createArrayBacked(LeftClickBlock.class, callbacks -> ((player, pos, face) -> {
		for(LeftClickBlock e : callbacks)
			e.onLeftClickBlock(player, pos, face);
	}));

	public interface BlockBreak {
		void onBlockBreak(BreakEvent event);
	}

	public interface LeftClickBlock {
		void onLeftClickBlock(Player player, BlockPos pos, Direction face);
	}

	private final LevelAccessor world;
	private final BlockPos pos;
	private final BlockState state;
	public BlockEvents(LevelAccessor world, BlockPos pos, BlockState state) {
		this.pos = pos;
		this.world = world;
		this.state = state;
	}

	public LevelAccessor getWorld()
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
		private final Player player;
		private int exp;

		public BreakEvent(Level world, BlockPos pos, BlockState state, Player player) {
			super(world, pos, state);
			this.player = player;

			if (state == null || !player.hasCorrectToolForDrops(state)) {// Handle empty block or player unable to break block scenario{
				this.exp = 0;
			} else{
				int bonusLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, player.getMainHandItem());
				int silklevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, player.getMainHandItem());
				this.exp = state.getBlock() instanceof CustomExpBlock exp ? exp.getExpDrop(state, world, pos, bonusLevel, silklevel) : 0;
			}
		}

		public Player getPlayer()
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
