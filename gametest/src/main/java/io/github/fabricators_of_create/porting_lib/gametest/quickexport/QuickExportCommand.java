package io.github.fabricators_of_create.porting_lib.gametest.quickexport;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.mojang.brigadier.arguments.StringArgumentType;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;

import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class QuickExportCommand {
	public static final String OUTPUT_PROPERTY = "porting_lib.gametest.quickexport.output";
	private static final String property = System.getProperty(OUTPUT_PROPERTY);
	private static final Path output = property != null ? Paths.get(property)
			: FabricLoader.getInstance().getGameDir().resolve("gametests");

	public static final Component HOLD_SELECTOR = lang("hold_selector");
	public static final Component FAILED = lang("failed");

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, buildCtx, env) -> dispatcher.register(
						literal("porting_lib")
								.then(literal("quickexport")
										.then(argument("path", StringArgumentType.greedyString())
												.requires(source -> source.hasPermission(2))
												.suggests(QuickExportCommand::getSuggestions)
												.executes(
														ctx -> handleExport(ctx.getSource(), StringArgumentType.getString(ctx, "path"))
												)
										)
								)
				)
		);
	}

	// find existing tests and folders for autofill
	private static CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
		String path = builder.getRemaining();
		if (!path.contains("/") || path.contains(".."))
			return findInDir(output, builder);
		int lastSlash = path.lastIndexOf("/");
		Path subDir = output.resolve(path.substring(0, lastSlash));
		return findInDir(subDir, builder);
	}

	private static CompletableFuture<Suggestions> findInDir(Path dir, SuggestionsBuilder builder) {
		if (!Files.exists(dir))
			return builder.buildFuture();
		try (Stream<Path> paths = Files.list(dir)) {
			paths.filter(p -> Files.isDirectory(p) || p.toString().endsWith(".nbt"))
					.forEach(path -> {
						String file = path.toString()
								.replaceAll("\\\\", "/")
								.substring(output.toString().length() + 1);
						if (Files.isDirectory(path))
							file += "/";
						builder.suggest(file);
					});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return builder.buildFuture();
	}

	private static int handleExport(CommandSourceStack source, String path) throws CommandSyntaxException {
		ServerPlayer player = source.getPlayerOrException();
		AreaSelection area = AreaSelectorItem.getArea(player.getMainHandItem());
		if (area == null || area.second == null) {
			source.sendFailure(HOLD_SELECTOR);
			return 0;
		}
		BoundingBox box = area.asBoundingBox();
		BlockPos origin = new BlockPos(box.minX(), box.minY(), box.minZ());
		Vec3i bounds = new Vec3i(box.getXSpan(), box.getYSpan(), box.getZSpan());

		ServerLevel level = source.getLevel();
		StructureTemplate structure = new StructureTemplate();
		structure.fillFromWorld(level, origin, bounds, true, null);
		CompoundTag data = structure.save(new CompoundTag());

		if (!path.endsWith(".nbt"))
			path += ".nbt";

		Path file = output.resolve(path).toAbsolutePath();
		try {
			Files.createDirectories(file.getParent());
			Files.deleteIfExists(file);
			try (OutputStream out = Files.newOutputStream(file, StandardOpenOption.CREATE)) {
				NbtIo.writeCompressed(data, out);
			}
			String cleanPath = file.toString().replace('\\', '/');
			source.sendSuccess(lang("exported", cleanPath), false);
			return 1;
		} catch (IOException e) {
			e.printStackTrace();
			source.sendFailure(FAILED);
			return 0;
		}
	}

	private static Component lang(String type, Object... args) {
		return Component.translatable("quickexport.command.feedback." + type, args);
	}
}
