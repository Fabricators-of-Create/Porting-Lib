package io.github.fabricators_of_create.porting_lib.command;

import java.io.File;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import io.github.fabricators_of_create.porting_lib.config.ConfigTracker;
import io.github.fabricators_of_create.porting_lib.config.ModConfig;
import io.github.fabricators_of_create.porting_lib.config.ModConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ConfigCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
		dispatcher.register(
				Commands.literal("porting_lib")
						.then(Commands.literal("config").
							then(ShowFile.register())
		));
	}

	public static class ShowFile {
		static ArgumentBuilder<CommandSourceStack, ?> register() {
			return Commands.literal("showfile").
					requires(cs->cs.hasPermission(0)).
					then(Commands.argument("mod", ModIdArgument.modIdArgument()).
							then(Commands.argument("type", EnumArgument.enumArgument(ModConfig.Type.class)).
									executes(ShowFile::showFile)
							)
					);
		}

		private static int showFile(final CommandContext<CommandSourceStack> context) {
			final String modId = context.getArgument("mod", String.class);
			final ModConfig.Type type = ModConfig.Type.CLIENT;
			var configFileNames = ModConfigs.getConfigFileNames(modId, type);
			for (var configFileName : configFileNames) {
				File f = new File(configFileName);
				MutableComponent fileComponent = Component.literal(f.getName()).withStyle(ChatFormatting.UNDERLINE)
						.withStyle((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, f.getAbsolutePath())));

				context.getSource().sendSuccess(() -> Component.translatable("commands.config.getwithtype",
						modId, type.toString(), fileComponent), true);
			}
			if (configFileNames.isEmpty()) {
				context.getSource().sendSuccess(() -> Component.translatable("commands.config.noconfig", modId, type.toString()),
						true);
			}
			return 0;
		}
	}
}
