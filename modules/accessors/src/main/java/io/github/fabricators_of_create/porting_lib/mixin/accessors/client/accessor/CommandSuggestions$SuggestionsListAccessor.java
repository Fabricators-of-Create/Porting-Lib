package io.github.fabricators_of_create.porting_lib.mixin.accessors.client.accessor;

import com.mojang.brigadier.suggestion.Suggestion;

import net.minecraft.client.gui.components.CommandSuggestions;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(CommandSuggestions.SuggestionsList.class)
public interface CommandSuggestions$SuggestionsListAccessor {
	@Invoker("<init>")
	static CommandSuggestions.SuggestionsList port_lib$create(CommandSuggestions suggestions, int i, int j, int k, List<Suggestion> list, boolean bl) {
		throw new RuntimeException("mixin failed");
	}
}
