package io.github.fabricators_of_create.porting_lib.level.events;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkType;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;

/**
 * ChunkDataEvent is fired when an event involving chunk data occurs.<br>
 * If a method utilizes this {@link BaseEvent} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #data} contains the CompoundTag containing the chunk data for this event.<br>
 * <br>
 **/
public abstract class ChunkDataEvent extends ChunkEvent {
	private final CompoundTag data;

	public ChunkDataEvent(ChunkAccess chunk, CompoundTag data) {
		super(chunk);
		this.data = data;
	}

	public ChunkDataEvent(ChunkAccess chunk, LevelAccessor world, CompoundTag data) {
		super(chunk, world);
		this.data = data;
	}

	public CompoundTag getData() {
		return data;
	}

	/**
	 * ChunkDataEvent.Load is fired when vanilla Minecraft attempts to load Chunk data.<br>
	 * This event is fired during chunk loading in
	 * {@link ChunkSerializer#read(ServerLevel, PoiManager, RegionStorageInfo, ChunkPos, CompoundTag)} which means it is async, so be careful.<br>
	 * <br>
	 * This event is not {@link CancellableEvent}.<br>
	 * <br>
	 * This event does not have a result. <br>
	 * <br>
	 **/
	public static class Load extends ChunkDataEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks) {
				callback.onChunkDataLoad(event);
			}
		});

		private ChunkType status;

		public Load(ChunkAccess chunk, CompoundTag data, ChunkType type) {
			super(chunk, data);
			this.status = type;
		}

		public ChunkType getType() {
			return this.status;
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onChunkDataLoad(this);
		}

		public interface Callback {
			void onChunkDataLoad(Load event);
		}
	}

	/**
	 * ChunkDataEvent.Save is fired when vanilla Minecraft attempts to save Chunk data.<br>
	 * This event is fired during chunk saving in
	 * {@link ChunkMap#save(ChunkAccess)}. <br>
	 * <br>
	 * This event is not {@link CancellableEvent}.<br>
	 * <br>
	 * This event does not have a result. <br>
	 * <br>
	 **/
	public static class Save extends ChunkDataEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks) {
				callback.onChunkDataSave(event);
			}
		});

		public Save(ChunkAccess chunk, LevelAccessor world, CompoundTag data) {
			super(chunk, world, data);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onChunkDataSave(this);
		}

		public interface Callback {
			void onChunkDataSave(Save event);
		}
	}
}
