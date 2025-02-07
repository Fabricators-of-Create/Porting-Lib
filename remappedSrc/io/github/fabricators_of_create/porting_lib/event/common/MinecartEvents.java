package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public class MinecartEvents {
	public static final Event<Spawn> SPAWN = EventFactory.createArrayBacked(Spawn.class, callbacks -> (cart, level) -> {
		for (Spawn callback : callbacks) {
			callback.minecartSpawn(cart, level);
		}
	});

	public static final Event<Read> READ = EventFactory.createArrayBacked(Read.class, callbacks -> (cart, data) -> {
		for (Read callback : callbacks) {
			callback.minecartRead(cart, data);
		}
	});

	public static final Event<Write> WRITE = EventFactory.createArrayBacked(Write.class, callbacks -> (cart, data) -> {
		for (Write callback : callbacks) {
			callback.minecartWrite(cart, data);
		}
	});

	public static final Event<Remove> REMOVE = EventFactory.createArrayBacked(Remove.class, callbacks -> (cart, level) -> {
		for (Remove callback : callbacks) {
			callback.minecartRemove(cart, level);
		}
	});

	public interface Spawn {
		void minecartSpawn(AbstractMinecartEntity cart, World level);
	}

	public interface Read {
		void minecartRead(AbstractMinecartEntity cart, NbtCompound data);
	}

	public interface Write {
		void minecartWrite(AbstractMinecartEntity cart, NbtCompound data);
	}

	public interface Remove {
		void minecartRemove(AbstractMinecartEntity cart, World level);
	}
}
