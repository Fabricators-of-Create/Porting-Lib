package io.github.fabricators_of_create.porting_lib.entity.events.player;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Optional;

/**
 * PlayerEvent is fired whenever an event involving a {@link Player} occurs. <br>
 * If a method utilizes this {@link BaseEvent} as its parameter, the method will
 * receive every child event of this class.<br>
 **/
public abstract class PlayerEvent extends LivingEvent {
	private final Player player;

	public PlayerEvent(Player player) {
		super(player);
		this.player = player;
	}

	@Override
	public Player getEntity() {
		return player;
	}

//	/** TODO: Implement
//	 * HarvestCheck is fired when a player attempts to harvest a block.<br>
//	 * This event is fired whenever a player attempts to harvest a block in
//	 * {@link Player#hasCorrectToolForDrops(BlockState)}.<br>
//	 * <br>
//	 * This event is fired via the {@link EntityHooks#doPlayerHarvestCheck(Player, BlockState, BlockGetter, BlockPos)}.<br>
//	 * <br>
//	 * {@link #state} contains the {@link BlockState} that is being checked for harvesting. <br>
//	 * {@link #success} contains the boolean value for whether the Block will be successfully harvested. <br>
//	 * <br>
//	 * This event is not {@link CancellableEvent}.<br>
//	 **/
//	public static class HarvestCheck extends PlayerEvent {
//		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
//			for (final Callback callback : callbacks)
//				callback.onHarvestCheck(event);
//		});
//
//		private final BlockState state;
//		private final BlockGetter level;
//		private final BlockPos pos;
//		private boolean success;
//
//		public HarvestCheck(Player player, BlockState state, BlockGetter level, BlockPos pos, boolean success) {
//			super(player);
//			this.state = state;
//			this.level = level;
//			this.pos = pos;
//			this.success = success;
//		}
//
//		public BlockState getTargetBlock() {
//			return this.state;
//		}
//
//		public BlockGetter getLevel() {
//			return level;
//		}
//
//		public BlockPos getPos() {
//			return pos;
//		}
//
//		public boolean canHarvest() {
//			return this.success;
//		}
//
//		public void setCanHarvest(boolean success) {
//			this.success = success;
//		}
//
//		@Override
//		public void sendEvent() {
//			EVENT.invoker().onHarvestCheck(this);
//		}
//
//		public interface Callback {
//			void onHarvestCheck(HarvestCheck event);
//		}
//	}

	/**
	 * BreakSpeed is fired when a player attempts to harvest a block.<br>
	 * This event is fired whenever a player attempts to harvest a block in
	 * {@link Player#getDigSpeed(BlockState, BlockPos)}.<br>
	 * <br>
	 * This event is fired via the {@link EntityHooks#getBreakSpeed(Player, BlockState, float, BlockPos)}.<br>
	 * <br>
	 * {@link #state} contains the block being broken. <br>
	 * {@link #originalSpeed} contains the original speed at which the player broke the block. <br>
	 * {@link #newSpeed} contains the newSpeed at which the player will break the block. <br>
	 * {@link #pos} contains the coordinates at which this event is occurring. Optional value.<br>
	 * <br>
	 * This event is {@link CancellableEvent}.<br>
	 * If it is canceled, the player is unable to break the block.<br>
	 **/
	public static class BreakSpeed extends PlayerEvent implements CancellableEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onBreakSpeed(event);
		});

		private static final BlockPos LEGACY_UNKNOWN = new BlockPos(0, -1, 0);
		private final BlockState state;
		private final float originalSpeed;
		private float newSpeed = 0.0f;
		private final Optional<BlockPos> pos; // Y position of -1 notes unknown location

		public BreakSpeed(Player player, BlockState state, float original, @Nullable BlockPos pos) {
			super(player);
			this.state = state;
			this.originalSpeed = original;
			this.setNewSpeed(original);
			this.pos = Optional.ofNullable(pos);
		}

		public BlockState getState() {
			return state;
		}

		public float getOriginalSpeed() {
			return originalSpeed;
		}

		public float getNewSpeed() {
			return newSpeed;
		}

		public void setNewSpeed(float newSpeed) {
			this.newSpeed = newSpeed;
		}

		public Optional<BlockPos> getPosition() {
			return this.pos;
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onBreakSpeed(this);
		}

		public interface Callback {
			void onBreakSpeed(BreakSpeed event);
		}
	}

	/**
	 * NameFormat is fired when a player's display name is retrieved.<br>
	 * This event is fired whenever a player's name is retrieved in
	 * {@link Player#getDisplayName()} or {@link Player#refreshDisplayName()}.<br>
	 * <br>
	 * This event is fired via the {@link EntityHooks#getPlayerDisplayName(Player, Component)}.<br>
	 * <br>
	 * {@link #username} contains the username of the player.
	 * {@link #displayname} contains the display name of the player.
	 * <br>
	 * This event is not {@link CancellableEvent}.
	 **/
	public static class NameFormat extends PlayerEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onNameFormat(event);
		});

		private final Component username;
		private Component displayname;

		public NameFormat(Player player, Component username) {
			super(player);
			this.username = username;
			this.setDisplayname(username);
		}

		public Component getUsername() {
			return username;
		}

		public Component getDisplayname() {
			return displayname;
		}

		public void setDisplayname(Component displayname) {
			this.displayname = displayname;
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onNameFormat(this);
		}

		public interface Callback {
			void onNameFormat(NameFormat event);
		}
	}

	/**
	 * TabListNameFormat is fired when a player's display name for the tablist is retrieved.<br>
	 * This event is fired whenever a player's display name for the tablist is retrieved in
	 * {@link ServerPlayer#getTabListDisplayName()} or {@link ServerPlayer#refreshTabListName()}.<br>
	 * <br>
	 * This event is fired via the {@link EntityHooks#getPlayerTabListDisplayName(Player)}.<br>
	 * <br>
	 * {@link #getDisplayName()} contains the display name of the player or null if the client should determine the display name itself.
	 * <br>
	 * This event is not {@link CancellableEvent}.
	 **/
	public static class TabListNameFormat extends PlayerEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onTabListNameFormat(event);
		});

		@Nullable
		private Component displayName;

		public TabListNameFormat(Player player) {
			super(player);
		}

		@Nullable
		public Component getDisplayName() {
			return displayName;
		}

		public void setDisplayName(@Nullable Component displayName) {
			this.displayName = displayName;
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onTabListNameFormat(this);
		}

		public interface Callback {
			void onTabListNameFormat(TabListNameFormat event);
		}
	}

	/**
	 * Fired when the EntityPlayer is cloned, typically caused by the impl sending a RESPAWN_PLAYER event.
	 * Either caused by death, or by traveling from the End to the overworld.
	 */
	public static class Clone extends PlayerEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onClone(event);
		});

		private final Player original;
		private final boolean wasDeath;

		public Clone(Player _new, Player oldPlayer, boolean wasDeath) {
			super(_new);
			this.original = oldPlayer;
			this.wasDeath = wasDeath;
		}

		/**
		 * The old EntityPlayer that this new entity is a clone of.
		 */
		public Player getOriginal() {
			return original;
		}

		/**
		 * True if this event was fired because the player died.
		 * False if it was fired because the entity switched dimensions.
		 */
		public boolean isWasDeath() {
			return wasDeath;
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onClone(this);
		}

		public interface Callback {
			void onClone(Clone event);
		}
	}

	/**
	 * Fired when an Entity is started to be "tracked" by this player (the player receives updates about this entity, e.g. motion).
	 *
	 */
	public static class StartTracking extends PlayerEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onStartTracking(event);
		});

		private final Entity target;

		public StartTracking(Player player, Entity target) {
			super(player);
			this.target = target;
		}

		/**
		 * The Entity now being tracked.
		 */
		public Entity getTarget() {
			return target;
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onStartTracking(this);
		}

		public interface Callback {
			void onStartTracking(StartTracking event);
		}
	}

	/**
	 * Fired when an Entity is stopped to be "tracked" by this player (the player no longer receives updates about this entity, e.g. motion).
	 *
	 */
	public static class StopTracking extends PlayerEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onStopTracking(event);
		});

		private final Entity target;

		public StopTracking(Player player, Entity target) {
			super(player);
			this.target = target;
		}

		/**
		 * The Entity no longer being tracked.
		 */
		public Entity getTarget() {
			return target;
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onStopTracking(this);
		}

		public interface Callback {
			void onStopTracking(StopTracking event);
		}
	}

	/**
	 * The player is being loaded from the world save. Note that the
	 * player won't have been added to the world yet. Intended to
	 * allow mods to load an additional file from the players directory
	 * containing additional mod related player data.
	 */
	public static class LoadFromFile extends PlayerEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onLoadFromFile(event);
		});

		private final File playerDirectory;
		private final String playerUUID;

		public LoadFromFile(Player player, File originDirectory, String playerUUID) {
			super(player);
			this.playerDirectory = originDirectory;
			this.playerUUID = playerUUID;
		}

		/**
		 * Construct and return a recommended file for the supplied suffix
		 *
		 * @param suffix The suffix to use.
		 */
		public File getPlayerFile(String suffix) {
			if ("dat".equals(suffix)) throw new IllegalArgumentException("The suffix 'dat' is reserved");
			return new File(this.getPlayerDirectory(), this.getPlayerUUID() + "." + suffix);
		}

		/**
		 * The directory where player data is being stored. Use this
		 * to locate your mod additional file.
		 */
		public File getPlayerDirectory() {
			return playerDirectory;
		}

		/**
		 * The UUID is the standard for player related file storage.
		 * It is broken out here for convenience for quick file generation.
		 */
		public String getPlayerUUID() {
			return playerUUID;
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onLoadFromFile(this);
		}

		public interface Callback {
			void onLoadFromFile(LoadFromFile event);
		}
	}

	/**
	 * The player is being saved to the world store. Note that the
	 * player may be in the process of logging out or otherwise departing
	 * from the world. Don't assume it's association with the world.
	 * This allows mods to load an additional file from the players directory
	 * containing additional mod related player data.
	 * <br>
	 * Use this event to save the additional mod related player data to the world.
	 *
	 * <br>
	 * <em>WARNING</em>: Do not overwrite the player's .dat file here. You will
	 * corrupt the world state.
	 */
	public static class SaveToFile extends PlayerEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onSaveToFile(event);
		});

		private final File playerDirectory;
		private final String playerUUID;

		public SaveToFile(Player player, File originDirectory, String playerUUID) {
			super(player);
			this.playerDirectory = originDirectory;
			this.playerUUID = playerUUID;
		}

		/**
		 * Construct and return a recommended file for the supplied suffix
		 *
		 * @param suffix The suffix to use.
		 */
		public File getPlayerFile(String suffix) {
			if ("dat".equals(suffix)) throw new IllegalArgumentException("The suffix 'dat' is reserved");
			return new File(this.getPlayerDirectory(), this.getPlayerUUID() + "." + suffix);
		}

		/**
		 * The directory where player data is being stored. Use this
		 * to locate your mod additional file.
		 */
		public File getPlayerDirectory() {
			return playerDirectory;
		}

		/**
		 * The UUID is the standard for player related file storage.
		 * It is broken out here for convenience for quick file generation.
		 */
		public String getPlayerUUID() {
			return playerUUID;
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onSaveToFile(this);
		}

		public interface Callback {
			void onSaveToFile(SaveToFile event);
		}
	}

	public static class ItemCraftedEvent extends PlayerEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onItemCrafted(event);
		});

		private final ItemStack crafting;
		private final Container craftMatrix;

		public ItemCraftedEvent(Player player, ItemStack crafting, Container craftMatrix) {
			super(player);
			this.crafting = crafting;
			this.craftMatrix = craftMatrix;
		}

		public ItemStack getCrafting() {
			return this.crafting;
		}

		public Container getInventory() {
			return this.craftMatrix;
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onItemCrafted(this);
		}

		public interface Callback {
			void onItemCrafted(ItemCraftedEvent event);
		}
	}

	public static class ItemSmeltedEvent extends PlayerEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onItemSmelted(event);
		});

		private final ItemStack smelting;

		public ItemSmeltedEvent(Player player, ItemStack crafting) {
			super(player);
			this.smelting = crafting;
		}

		public ItemStack getSmelting() {
			return this.smelting;
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onItemSmelted(this);
		}

		public interface Callback {
			void onItemSmelted(ItemSmeltedEvent event);
		}
	}

	public static class PlayerLoggedInEvent extends PlayerEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onPlayerLoggedIn(event);
		});

		public PlayerLoggedInEvent(Player player) {
			super(player);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onPlayerLoggedIn(this);
		}

		public interface Callback {
			void onPlayerLoggedIn(PlayerLoggedInEvent event);
		}
	}

	public static class PlayerLoggedOutEvent extends PlayerEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onPlayerLoggedOut(event);
		});

		public PlayerLoggedOutEvent(Player player) {
			super(player);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onPlayerLoggedOut(this);
		}

		public interface Callback {
			void onPlayerLoggedOut(PlayerLoggedOutEvent event);
		}
	}

	/**
	 * Fired when the game type of a server player is changed to a different value than what it was previously. Eg Creative to Survival, not Survival to Survival.
	 * If the event is cancelled the game mode of the player is not changed and the value of <code>newGameMode</code> is ignored.
	 */
	public static class PlayerChangeGameModeEvent extends PlayerEvent implements CancellableEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onPlayerChangeGameMode(event);
		});

		private final GameType currentGameMode;
		private GameType newGameMode;

		public PlayerChangeGameModeEvent(Player player, GameType currentGameMode, GameType newGameMode) {
			super(player);
			this.currentGameMode = currentGameMode;
			this.newGameMode = newGameMode;
		}

		public GameType getCurrentGameMode() {
			return currentGameMode;
		}

		public GameType getNewGameMode() {
			return newGameMode;
		}

		/**
		 * Sets the game mode the player will be changed to if this event is not cancelled.
		 */
		public void setNewGameMode(GameType newGameMode) {
			this.newGameMode = newGameMode;
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onPlayerChangeGameMode(this);
		}

		public interface Callback {
			void onPlayerChangeGameMode(PlayerChangeGameModeEvent event);
		}
	}
}
