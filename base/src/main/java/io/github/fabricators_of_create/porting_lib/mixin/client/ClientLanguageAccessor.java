package io.github.fabricators_of_create.porting_lib.mixin.client;

import net.minecraft.client.resources.language.ClientLanguage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ClientLanguage.class)
public interface ClientLanguageAccessor {
	@Accessor("storage")
	Map<String, String> port_lib$getStorage();
}
