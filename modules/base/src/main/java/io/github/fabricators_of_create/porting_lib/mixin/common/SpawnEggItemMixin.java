package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.util.DeferredSpawnEggItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(SpawnEggItem.class)
public class SpawnEggItemMixin {
	@Inject(method = "byId", at = @At("HEAD"), cancellable = true)
	private static void lazyMap(EntityType<?> entityType, CallbackInfoReturnable<SpawnEggItem> cir) {
		var ret = DeferredSpawnEggItem.deferredOnlyById(entityType);
		if (ret != null)
			cir.setReturnValue(ret);
	}

	@WrapWithCondition(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
	private boolean cancelIfNull(Map<?, ?> instance, Object key, Object v) {
		return key != null;
	}
}
