package io.github.fabricators_of_create.porting_lib.registries.mixin;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import io.github.fabricators_of_create.porting_lib.registries.DynamicRegistryHandler;
import net.minecraft.resources.RegistryDataLoader;

@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {
	@ModifyArg(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/util/List;of([Ljava/lang/Object;)Ljava/util/List;"))
	private static <E> E[] port_lib$unhardcodeRegistries(E[] elements) {
		DynamicRegistryHandler.loadDynamicRegistries();
		var registryData = DynamicRegistryHandler.getRegistryData();
		List<E> resultList = new ArrayList<>();
		Collections.addAll(resultList, elements);
		Collections.addAll((List<RegistryDataLoader.RegistryData<?>>)resultList, registryData.toArray(RegistryDataLoader.RegistryData<?>[]::new));

		@SuppressWarnings("unchecked")
		//the type cast is safe as the array1 has the type T[]
		E[] resultArray = (E[]) Array.newInstance(elements.getClass().getComponentType());
		return resultList.toArray(resultArray);
	}
}
