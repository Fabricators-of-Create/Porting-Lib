package io.github.fabricators_of_create.porting_lib.entity;

import org.jetbrains.annotations.NotNull;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;

/**
 * An entity which is part of a parent entity, like the Ender Dragon.
 * These entities are not saved, synced, or ticked. Their parent is responsible for managing them.
 * @param <T> the type of the parent entity
 */
public abstract class PartEntity<T extends Entity> extends Entity {
	private final T parent;

	public PartEntity(T parent) {
		super(parent.getType(), parent.level);
		this.parent = parent;
	}

	public T getParent() {
		return parent;
	}

	@Override
	@NotNull
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		throw new UnsupportedOperationException();
	}
}
