package io.github.fabricators_of_create.porting_lib.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModIdArgument implements ArgumentType<String> {
	private static final List<String> EXAMPLES = Arrays.asList(PortingLib.ID, "inventorysorter");

	public static ModIdArgument modIdArgument() {
		return new ModIdArgument();
	}

	@Override
	public String parse(final StringReader reader) throws CommandSyntaxException {
		return reader.readUnquotedString();
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggest(FabricLoader.getInstance().getAllMods().stream().map(modContainer -> modContainer.getMetadata().getId()), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
