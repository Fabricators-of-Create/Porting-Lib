package io.github.fabricators_of_create.porting_lib.mixin.client.accessor;

import com.mojang.brigadier.suggestion.Suggestion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import net.minecraft.client.gui.screen.CommandSuggestor;

@Mixin(CommandSuggestor.SuggestionWindow.class)
public interface CommandSuggestions$SuggestionsListAccessor {
	@Invoker("<init>")
	static CommandSuggestor.SuggestionWindow port_lib$create(CommandSuggestor suggestions, int i, int j, int k, List<Suggestion> list, boolean bl) {
		throw new RuntimeException("mixin failed");
	}
}
