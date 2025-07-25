package io.github.fabricators_of_create.porting_lib.item.extensions;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public interface CustomPickupBucketSoundItem {
	/**
	 * State sensitive variant of {@link BucketPickup#getPickupSound()}.
	 *
	 * Override to change the pickup sound based on the {@link BlockState} of the object being picked up.
	 *
	 * @param state State
	 *
	 * @return Sound event for pickup sound or empty if there isn't a pickup sound.
	 */
	Optional<SoundEvent> getPickupSound(BlockState state);
}
