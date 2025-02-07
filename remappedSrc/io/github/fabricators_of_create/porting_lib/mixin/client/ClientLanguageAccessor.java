package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import net.minecraft.client.resource.language.TranslationStorage;

@Mixin(TranslationStorage.class)
public interface ClientLanguageAccessor {
	@Accessor("storage")
	Map<String, String> port_lib$getStorage();
}
