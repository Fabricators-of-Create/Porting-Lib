package io.github.fabricators_of_create.porting_lib.entity.events;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

/**
 * Events for when entities save and load their data to/from NBT.
 */
public class EntityDataEvents {
	/**
	 * Fired when an entity saves its data.
	 */
	public static final Event<Save> SAVE = EventFactory.createArrayBacked(Save.class, callbacks -> (entity, nbt) -> {
		for (Save callback : callbacks)
			callback.onSave(entity, nbt);
	});

	public interface Save {
		/**
		 * @param nbt the NBT data that the entity has been saved to
		 */
		void onSave(Entity entity, @Nullable CompoundTag nbt);
	}

	public static final Event<Load> LOAD = EventFactory.createArrayBacked(Load.class, callbacks -> (entity, nbt) -> {
		for (Load callback : callbacks)
			callback.onLoad(entity, nbt);
	});

	public interface Load {
		/**
		 * @param nbt the NBT data that the entity has been loaded from
		 */
		void onLoad(Entity entity, @Nullable CompoundTag nbt);
	}
}
