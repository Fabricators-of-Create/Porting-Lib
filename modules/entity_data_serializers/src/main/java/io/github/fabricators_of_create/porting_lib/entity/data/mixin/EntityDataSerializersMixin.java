package io.github.fabricators_of_create.porting_lib.entity.data.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.entity.data.PortingLibEntityDataSerializers;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityDataSerializers.class)
public class EntityDataSerializersMixin {
	@WrapOperation(method = "getSerializer", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/CrudeIncrementalIntIdentityHashBiMap;byId(I)Ljava/lang/Object;"))
	private static Object getSerializer(CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> instance, int id, Operation<Object> original) {
		return PortingLibEntityDataSerializers.getSerializer(id, instance, original);
	}

	@WrapOperation(method = "getSerializedId", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/CrudeIncrementalIntIdentityHashBiMap;getId(Ljava/lang/Object;)I"))
	private static int getSerializedId(CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> instance, Object value, Operation<Integer> original) {
		return PortingLibEntityDataSerializers.getSerializerId((EntityDataSerializer<?>) value, instance, original);
	}
}
