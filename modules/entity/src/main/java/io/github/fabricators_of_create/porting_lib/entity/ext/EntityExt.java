package io.github.fabricators_of_create.porting_lib.entity.ext;

import io.github.fabricators_of_create.porting_lib.core.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.entity.IEntityWithComplexSpawn;
import io.github.fabricators_of_create.porting_lib.entity.network.AdvancedAddEntityPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.Collection;
import java.util.function.Consumer;

public interface EntityExt {
	default CompoundTag getCustomData() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Collection<ItemEntity> captureDrops() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Collection<ItemEntity> captureDrops(Collection<ItemEntity> value) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	/**
	 * If a rider of this entity can interact with this entity. Should return true on the
	 * ridden entity if so.
	 *
	 * @return if the entity can be interacted with from a rider
	 */
	default boolean canRiderInteract() {
		return false;
	}

	/**
	 * Sends the pairing data to the client.
	 *
	 * @param serverPlayer  The player to send the data to.
	 * @param bundleBuilder Callback to add a custom payload to the packet.
	 */
	default void sendPairingData(ServerPlayer serverPlayer, Consumer<CustomPacketPayload> bundleBuilder) {
		if (this instanceof IEntityWithComplexSpawn) {
			bundleBuilder.accept(new AdvancedAddEntityPayload(MixinHelper.cast(this)));
		}
	}
}
