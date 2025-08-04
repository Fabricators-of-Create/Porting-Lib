package io.github.fabricators_of_create.porting_lib.entity.data;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;

import org.jetbrains.annotations.Nullable;

public class PortingLibEntityDataSerializers {
	public static final ResourceKey<Registry<EntityDataSerializer<?>>> ENTITY_DATA_SERIALIZERS_KEY = PortingLib.key("entity_data_serializers");
	public static final Registry<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = FabricRegistryBuilder.createSimple(ENTITY_DATA_SERIALIZERS_KEY).attribute(RegistryAttribute.SYNCED).buildAndRegister();

	public static void init() {}

	public static final int VANILLA_SERIALIZER_LIMIT = 256;

	@Nullable
	public static EntityDataSerializer<?> getSerializer(int id, CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> vanilla) {
		EntityDataSerializer<?> serializer = vanilla.byId(id);
		if (serializer == null) {
			return ENTITY_DATA_SERIALIZERS.byId(id - VANILLA_SERIALIZER_LIMIT);
		}
		return serializer;
	}

	public static int getSerializerId(EntityDataSerializer<?> serializer, CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> vanilla) {
		int id = vanilla.getId(serializer);
		if (id < 0) {
			id = ENTITY_DATA_SERIALIZERS.getId(serializer);
			if (id >= 0) {
				return id + VANILLA_SERIALIZER_LIMIT;
			}
		}
		return id;
	}

	@Nullable
	public static EntityDataSerializer<?> getSerializer(int id, CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> vanilla, Operation<Object> original) {
		EntityDataSerializer<?> serializer = (EntityDataSerializer<?>) original.call(vanilla, id);
		if (serializer == null) {
			return ENTITY_DATA_SERIALIZERS.byId(id - VANILLA_SERIALIZER_LIMIT);
		}
		return serializer;
	}

	public static int getSerializerId(EntityDataSerializer<?> serializer, CrudeIncrementalIntIdentityHashBiMap<EntityDataSerializer<?>> vanilla, Operation<Integer> original) {
		int id = original.call(vanilla, serializer);
		if (id < 0) {
			id = ENTITY_DATA_SERIALIZERS.getId(serializer);
			if (id >= 0) {
				return id + VANILLA_SERIALIZER_LIMIT;
			}
		}
		return id;
	}
}
