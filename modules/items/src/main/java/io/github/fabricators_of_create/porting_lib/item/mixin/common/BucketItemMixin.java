package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.item.extensions.CustomPickupBucketSoundItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.BucketItem;

import net.minecraft.world.level.block.BucketPickup;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {
	@WrapOperation(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BucketPickup;getPickupSound()Ljava/util/Optional;"))
	private Optional<SoundEvent> port_lib$tryUseCustomPickupSound(BucketPickup instance, Operation<Optional<SoundEvent>> original, @Local BlockState state) {
		if (instance instanceof CustomPickupBucketSoundItem pickupBucketSoundItem) {
			return pickupBucketSoundItem.getPickupSound(state);
		}

		return original.call(instance);
	}
}
