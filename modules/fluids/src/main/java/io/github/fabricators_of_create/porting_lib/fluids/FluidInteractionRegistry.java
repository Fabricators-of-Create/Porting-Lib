package io.github.fabricators_of_create.porting_lib.fluids;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/**
 * A registry which defines the interactions a source fluid can have with its
 * surroundings. Each possible flow direction is checked for all interactions with
 * the source.
 *
 * <p>Fluid interactions mimic the behavior of {@code LiquidBlock#shouldSpreadLiquid}.
 * As such, all directions, besides {@link Direction#DOWN} is tested and then replaced.
 * Any fluids which cause a change in the down interaction must be handled in
 * {@code FlowingFluid#spreadTo} and not by this interaction manager.
 */
public final class FluidInteractionRegistry {
	private static final Map<FluidType, List<InteractionInformation>> INTERACTIONS = new HashMap<>();

	/**
	 * Adds an interaction between a source and its surroundings.
	 *
	 * @param source      the source of the interaction, this will be replaced if the interaction occurs
	 * @param interaction the interaction data to check and perform
	 */
	public static synchronized void addInteraction(FluidType source, InteractionInformation interaction) {
		INTERACTIONS.computeIfAbsent(source, s -> new ArrayList<>()).add(interaction);
	}

	/**
	 * Performs all potential fluid interactions at a given position.
	 *
	 * <p>Note: Only the first interaction check that succeeds will occur.
	 *
	 * @param level the level the interactions take place in
	 * @param pos   the position of the source fluid
	 * @return {@code true} if an interaction took place, {@code false} otherwise
	 */
	public static boolean canInteract(Level level, BlockPos pos) {
		return canInteract(level, pos, true);
	}

	/**
	 * Performs all potential fluid interactions at a given position.
	 *
	 * <p>Note: Only the first interaction check that succeeds will occur.
	 *
	 * @param level the level the interactions take place in
	 * @param pos   the position of the source fluid
	 * @return {@code true} if an interaction took place, {@code false} otherwise
	 */
	public static boolean canInteract(Level level, BlockPos pos, boolean overrideVanilla) {
		FluidState state = level.getFluidState(pos);
		for (Direction direction : LiquidBlock.POSSIBLE_FLOW_DIRECTIONS) {
			BlockPos relativePos = pos.relative(direction.getOpposite());
			List<InteractionInformation> interactions = INTERACTIONS.getOrDefault(state.getFluidType(), Collections.emptyList());
			for (InteractionInformation interaction : interactions) {
				if (interaction.isVanilla() && !overrideVanilla)
					continue;
				if (interaction.predicate().test(level, pos, relativePos, state)) {
					interaction.interaction().interact(level, pos, relativePos, state);
					return true;
				}
			}
		}

		return false;
	}

	static {
		// Lava + Water = Obsidian (Source Lava) / Cobblestone (Flowing Lava)
		addInteraction(PortingLibFluids.LAVA_TYPE, new InteractionInformation(
				PortingLibFluids.WATER_TYPE,
				fluidState -> fluidState.isSource() ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.COBBLESTONE.defaultBlockState(),
				true
		));

		// Lava + Soul Soil (Below) + Blue Ice = Basalt
		addInteraction(PortingLibFluids.LAVA_TYPE, new InteractionInformation(
				(level, currentPos, relativePos, currentState) -> level.getBlockState(currentPos.below()).is(Blocks.SOUL_SOIL) && level.getBlockState(relativePos).is(Blocks.BLUE_ICE),
				Blocks.BASALT.defaultBlockState(),
				true
		));
	}

	/**
	 * Holds the interaction data for a given source type on when to succeed
	 * and what to perform.
	 *
	 * @param predicate   a test to see whether an interaction can occur
	 * @param interaction the interaction to perform
	 */
	public record InteractionInformation(HasFluidInteraction predicate, FluidInteraction interaction,
										 boolean isVanilla) {

		public InteractionInformation(HasFluidInteraction predicate, FluidInteraction interaction) {
			this(predicate, interaction, false);
		}

		/**
		 * Constructor which checks the surroundings fluids for a specific type
		 * and then transforms the source state into a block.
		 *
		 * @param type  the type of the fluid that must be surrounding the source
		 * @param state the state of the block replacing the source
		 */
		public InteractionInformation(FluidType type, BlockState state) {
			this(type, fluidState -> state);
		}

		/**
		 * Constructor which transforms the source state into a block.
		 *
		 * @param predicate a test to see whether an interaction can occur
		 * @param state     the state of the block replacing the source
		 */
		public InteractionInformation(HasFluidInteraction predicate, BlockState state) {
			this(predicate, state, false);
		}

		/**
		 * Constructor which transforms the source state into a block.
		 *
		 * @param predicate a test to see whether an interaction can occur
		 * @param state     the state of the block replacing the source
		 */
		public InteractionInformation(HasFluidInteraction predicate, BlockState state, boolean isVanilla) {
			this(predicate, fluidState -> state, isVanilla);
		}

		/**
		 * Constructor which checks the surroundings fluids for a specific type
		 * and then transforms the source state into a block.
		 *
		 * @param type     the type of the fluid that must be surrounding the source
		 * @param getState a function to transform the source fluid into a block state
		 */
		public InteractionInformation(FluidType type, Function<FluidState, BlockState> getState) {
			this((level, currentPos, relativePos, currentState) -> level.getFluidState(relativePos).getFluidType() == type, getState);
		}

		/**
		 * Constructor which checks the surroundings fluids for a specific type
		 * and then transforms the source state into a block.
		 *
		 * @param type     the type of the fluid that must be surrounding the source
		 * @param getState a function to transform the source fluid into a block state
		 */
		public InteractionInformation(FluidType type, Function<FluidState, BlockState> getState, boolean isVanilla) {
			this((level, currentPos, relativePos, currentState) -> level.getFluidState(relativePos).getFluidType() == type, getState, isVanilla);
		}

		/**
		 * Constructor which transforms the source state into a block.
		 *
		 * @param predicate a test to see whether an interaction can occur
		 * @param getState  a function to transform the source fluid into a block state
		 */
		public InteractionInformation(HasFluidInteraction predicate, Function<FluidState, BlockState> getState) {
			this(predicate, getState, false);
		}

		/**
		 * Constructor which transforms the source state into a block.
		 *
		 * @param predicate a test to see whether an interaction can occur
		 * @param getState  a function to transform the source fluid into a block state
		 */
		public InteractionInformation(HasFluidInteraction predicate, Function<FluidState, BlockState> getState, boolean isVanilla) {
			this(predicate, (level, currentPos, relativePos, currentState) ->
			{
				level.setBlockAndUpdate(currentPos, getState.apply(currentState)/*ForgeEventFactory.fireFluidPlaceBlockEvent(level, currentPos, currentPos, getState.apply(currentState))*/);
				level.levelEvent(LevelEvent.LAVA_FIZZ, currentPos, 0);
			}, isVanilla);
		}
	}

	/**
	 * An interface which tests whether a source fluid can interact with its
	 * surroundings.
	 */
	@FunctionalInterface
	public interface HasFluidInteraction {
		/**
		 * Returns whether the interaction can occur.
		 *
		 * @param level        the level the interaction takes place in
		 * @param currentPos   the position of the source
		 * @param relativePos  a position surrounding the source
		 * @param currentState the state of the fluid surrounding the source
		 * @return {@code true} if an interaction can occur, {@code false} otherwise
		 */
		boolean test(Level level, BlockPos currentPos, BlockPos relativePos, FluidState currentState);
	}

	/**
	 * An interface which performs an interaction for a source.
	 */
	@FunctionalInterface
	public interface FluidInteraction {
		/**
		 * Performs the interaction between the source and the surrounding data.
		 *
		 * @param level        the level the interaction takes place in
		 * @param currentPos   the position of the source
		 * @param relativePos  a position surrounding the source
		 * @param currentState the state of the fluid surrounding the source
		 */
		void interact(Level level, BlockPos currentPos, BlockPos relativePos, FluidState currentState);
	}
}
