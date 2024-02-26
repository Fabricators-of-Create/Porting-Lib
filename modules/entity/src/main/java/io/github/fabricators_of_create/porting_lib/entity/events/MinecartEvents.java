package io.github.fabricators_of_create.porting_lib.entity.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;

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
		void minecartSpawn(AbstractMinecart cart, Level level);
	}

	public interface Read {
		void minecartRead(AbstractMinecart cart, CompoundTag data);
	}

	public interface Write {
		void minecartWrite(AbstractMinecart cart, CompoundTag data);
	}

	public interface Remove {
		void minecartRemove(AbstractMinecart cart, Level level);
	}
}
