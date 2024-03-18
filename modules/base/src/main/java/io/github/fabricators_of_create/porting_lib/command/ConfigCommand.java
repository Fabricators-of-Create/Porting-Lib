package io.github.fabricators_of_create.porting_lib.command;

import java.io.File;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import io.github.fabricators_of_create.porting_lib.config.ConfigTracker;
import io.github.fabricators_of_create.porting_lib.config.ConfigType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

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
							then(Commands.argument("type", EnumArgument.enumArgument(ConfigType.class)).
									executes(ShowFile::showFile)
							)
					);
		}

		private static int showFile(final CommandContext<CommandSourceStack> context) {
			final String modId = context.getArgument("mod", String.class);
			final ConfigType type = context.getArgument("type", ConfigType.class);
			final String configFileName = ConfigTracker.INSTANCE.getConfigFileName(modId, type);
			if (configFileName != null) {
				File f = new File(configFileName);
				context.getSource().sendSuccess(() -> Component.translatable("commands.config.getwithtype",
						modId, type,
						Component.literal(f.getName()).withStyle(ChatFormatting.UNDERLINE).
								withStyle((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, f.getAbsolutePath())))
				), true);
			} else {
				context.getSource().sendSuccess(() -> Component.translatable("commands.config.noconfig", modId, type),
						true);
			}
			return 0;
		}
	}
}
