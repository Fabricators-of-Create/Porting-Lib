package io.github.fabricators_of_create.porting_lib.registries.mixin;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.resources.RegistryDataLoader;

@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {
	@Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Ljava/util/List;of([Ljava/lang/Object;)Ljava/util/List;"))
	private static <E> List<E> port_lib$unhardcodeRegistries(E[] elements) {
		return Arrays.asList(elements);
	}
}
