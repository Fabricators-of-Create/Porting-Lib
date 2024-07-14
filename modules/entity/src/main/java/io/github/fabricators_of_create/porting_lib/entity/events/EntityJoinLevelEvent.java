package io.github.fabricators_of_create.porting_lib.entity.events;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;

/**
 * This event is fired whenever an {@link Entity} joins a {@link Level}.
 * This event is fired whenever an entity is added to a level in {@link Level#addFreshEntity(Entity)}
 * and {@code PersistentEntitySectionManager#addNewEntity(Entity, boolean)}.
 * <p>
 * <strong>Note:</strong> This event may be called before the underlying {@link LevelChunk} is promoted to {@link ChunkStatus#FULL}.
 * You will cause chunk loading deadlocks if you do not delay your world interactions.
 * <p>
 * This event is {@linkplain CancellableEvent cancellable}.
 * If the event is canceled, the entity will not be added to the level.
 * <p>
 * This event is fired on both logical sides.
 **/
public class EntityJoinLevelEvent extends EntityEvent implements CancellableEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (final Callback callback : callbacks)
			callback.onEntityJoin(event);
	});

	private final Level level;
	private final boolean loadedFromDisk;

	public EntityJoinLevelEvent(Entity entity, Level level) {
		this(entity, level, false);
	}

	public EntityJoinLevelEvent(Entity entity, Level level, boolean loadedFromDisk) {
		super(entity);
		this.level = level;
		this.loadedFromDisk = loadedFromDisk;
	}

	/**
	 * {@return the level that the entity is set to join}
	 */
	public Level getLevel() {
		return level;
	}

	/**
	 * @return {@code true} if the entity was loaded from disk, {@code false} otherwise.
	 *         On the {@linkplain EnvType#CLIENT logical client}, this will always return {@code false}.
	 */
	public boolean loadedFromDisk() {
		return loadedFromDisk;
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().onEntityJoin(this);
	}

	public interface Callback {
		void onEntityJoin(EntityJoinLevelEvent event);
	}
}
