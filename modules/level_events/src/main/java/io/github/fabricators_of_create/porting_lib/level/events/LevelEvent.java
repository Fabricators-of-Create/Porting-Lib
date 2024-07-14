package io.github.fabricators_of_create.porting_lib.level.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.ServerLevelData;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired whenever an event involving a {@link LevelAccessor} occurs.
 */
public abstract class LevelEvent extends BaseEvent {
	private final LevelAccessor level;

	public LevelEvent(LevelAccessor level) {
		this.level = level;
	}

	/**
	 * {@return the level this event is affecting}
	 */
	public LevelAccessor getLevel() {
		return level;
	}

	/**
	 * This event is fired whenever a level loads.
	 * This event is fired whenever a level loads in ClientLevel's constructor and
	 * {@literal MinecraftServer#createLevels(ChunkProgressListener)}.
	 * <p>
	 * This event is not {@linkplain CancellableEvent cancellable}.
	 * <p>
	 * This event is fired on both logical sides.
	 **/
	public static class Load extends LevelEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks)
				callback.onLoad(event);
		});

		public Load(LevelAccessor level) {
			super(level);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onLoad(this);
		}

		public interface Callback {
			void onLoad(Load event);
		}
	}

	/**
	 * This event is fired whenever a level unloads.
	 * This event is fired whenever a level unloads in
	 * {@link Minecraft#setLevel(ClientLevel, ReceivingLevelScreen.Reason)},
	 * {@link MinecraftServer#stopServer()},
	 * {@link Minecraft#disconnect(Screen, boolean)}.
	 * <p>
	 * This event is not {@linkplain CancellableEvent cancellable}.
	 * <p>
	 * This event is fired on both logical sides.
	 **/
	public static class Unload extends LevelEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks)
				callback.onUnload(event);
		});

		public Unload(LevelAccessor level) {
			super(level);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onUnload(this);
		}

		public interface Callback {
			void onUnload(Unload event);
		}
	}

	/**
	 * This event fires whenever a level is saved.
	 * This event is fired when a level is saved in
	 * {@link ServerLevel#save(ProgressListener, boolean, boolean)}.
	 * <p>
	 * This event is not {@linkplain CancellableEvent cancellable}.
	 * <p>
	 * This event is fired only on the {@linkplain EnvType#SERVER logical server}.
	 **/
	public static class Save extends LevelEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks)
				callback.onSave(event);
		});

		public Save(LevelAccessor level) {
			super(level);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onSave(this);
		}

		public interface Callback {
			void onSave(Save event);
		}
	}

	/**
	 * This event fires whenever a {@link ServerLevel} is initialized for the first time
	 * and a spawn position needs to be chosen.
	 * <p>
	 * This event is {@linkplain CancellableEvent cancellable}.
	 * If the event is canceled, the vanilla logic to choose a spawn position will be skipped.
	 * <p>
	 * This event is fired only on the {@linkplain EnvType#SERVER logical server}.
	 *
	 * @see ServerLevelData#isInitialized()
	 */
	public static class CreateSpawnPosition extends LevelEvent implements CancellableEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks)
				callback.onCreateSpawnPosition(event);
		});

		private final ServerLevelData settings;

		public CreateSpawnPosition(LevelAccessor level, ServerLevelData settings) {
			super(level);
			this.settings = settings;
		}

		public ServerLevelData getSettings() {
			return settings;
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onCreateSpawnPosition(this);
		}

		public interface Callback {
			void onCreateSpawnPosition(CreateSpawnPosition event);
		}
	}

	/**
	 * Fired when building a list of all possible entities that can spawn at the specified location.
	 *
	 * <p>If an entry is added to the list, it needs to be a globally unique instance.</p>
	 *
	 * The event is called in {@link net.minecraft.world.level.NaturalSpawner#mobsAt(ServerLevel,
	 * StructureManager, ChunkGenerator, MobCategory, BlockPos, Holder)}.</p>
	 *
	 * <p>This event is {@linkplain CancellableEvent cancellable},.
	 * Canceling the event will result in an empty list, meaning no entity will be spawned.</p>
	 */
	public static class PotentialSpawns extends LevelEvent implements CancellableEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks)
				callback.onPotentialSpawn(event);
		});

		private final MobCategory mobcategory;
		private final BlockPos pos;
		@Nullable
		private List<MobSpawnSettings.SpawnerData> list;
		private List<MobSpawnSettings.SpawnerData> view;

		public PotentialSpawns(LevelAccessor level, MobCategory category, BlockPos pos, WeightedRandomList<MobSpawnSettings.SpawnerData> oldList) {
			super(level);
			this.pos = pos;
			this.mobcategory = category;
			this.list = null;
			this.view = oldList.unwrap();
		}

		/**
		 * {@return the category of the mobs in the spawn list.}
		 */
		public MobCategory getMobCategory() {
			return mobcategory;
		}

		/**
		 * {@return the block position where the chosen mob will be spawned.}
		 */
		public BlockPos getPos() {
			return pos;
		}

		/**
		 * {@return the list of mobs that can potentially be spawned.}
		 */
		public List<MobSpawnSettings.SpawnerData> getSpawnerDataList() {
			return view;
		}

		private void makeList() {
			if (list == null) {
				list = new ArrayList<>(view);
				view = Collections.unmodifiableList(list);
			}
		}

		/**
		 * Appends a SpawnerData entry to the spawn list.
		 *
		 * @param data SpawnerData entry to be appended to the spawn list.
		 */
		public void addSpawnerData(MobSpawnSettings.SpawnerData data) {
			makeList();
			list.add(data);
		}

		/**
		 * Removes a SpawnerData entry from the spawn list.
		 *
		 * @param data SpawnerData entry to be removed from the spawn list.
		 *
		 *             {@return {@code true} if the spawn list contained the specified element.}
		 */
		public boolean removeSpawnerData(MobSpawnSettings.SpawnerData data) {
			makeList();
			return list.remove(data);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onPotentialSpawn(this);
		}

		public interface Callback {
			void onPotentialSpawn(PotentialSpawns event);
		}
	}
}
