package io.github.fabricators_of_create.porting_lib.entity;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;

public abstract class PartEntity<T extends Entity> extends Entity {
	private final T parent;

	public PartEntity(T parent) {
		super(parent.getType(), parent.world);
		this.parent = parent;
	}

	public T getParent() {
		return parent;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		throw new UnsupportedOperationException();
	}
}
