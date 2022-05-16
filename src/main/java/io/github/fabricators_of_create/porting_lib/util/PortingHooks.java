package io.github.fabricators_of_create.porting_lib.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.github.fabricators_of_create.porting_lib.entity.MultiPartEntity;
import io.github.fabricators_of_create.porting_lib.entity.PartEntity;
import io.github.fabricators_of_create.porting_lib.event.common.BlockEvents;
import io.github.fabricators_of_create.porting_lib.extensions.BlockItemExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.LevelExtensions;
import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.MinecraftServerAccessor;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.RegistryEntryRemovedCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class PortingHooks {

	private static final Logger LOGGER = LogManager.getLogger();

	public static int onBlockBreakEvent(Level world, GameType gameType, ServerPlayer entityPlayer, BlockPos pos) {
		// Logic from tryHarvestBlock for pre-canceling the event
		boolean preCancelEvent = false;
		ItemStack itemstack = entityPlayer.getMainHandItem();
		if (!itemstack.isEmpty() && !itemstack.getItem().canAttackBlock(world.getBlockState(pos), world, pos, entityPlayer)) {
			preCancelEvent = true;
		}

		if (gameType.isBlockPlacingRestricted()) {
			if (gameType == GameType.SPECTATOR)
				preCancelEvent = true;

			if (!entityPlayer.mayBuild()) {
				if (itemstack.isEmpty() || !itemstack.hasAdventureModeBreakTagForBlock(world.registryAccess().registryOrThrow(Registry.BLOCK_REGISTRY), new BlockInWorld(world, pos, false)))
					preCancelEvent = true;
			}
		}

		// Tell client the block is gone immediately then process events
		if (world.getBlockEntity(pos) == null) {
			entityPlayer.connection.send(new ClientboundBlockUpdatePacket(pos, world.getFluidState(pos).createLegacyBlock()));
		}

		// Post the block break event
		BlockState state = world.getBlockState(pos);
		BlockEvents.BreakEvent event = new BlockEvents.BreakEvent(world, pos, state, entityPlayer);
		event.setCanceled(preCancelEvent);
		event.sendEvent();

		// Handle if the event is canceled
		if (event.isCanceled()) {
			// Let the client know the block still exists
			entityPlayer.connection.send(new ClientboundBlockUpdatePacket(world, pos));

			// Update any tile entity data for this block
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity != null) {
				Packet<?> pkt = blockEntity.getUpdatePacket();
				if (pkt != null) {
					entityPlayer.connection.send(pkt);
				}
			}
		}
		return event.isCanceled() ? -1 : event.getExpToDrop();
	}

	public static void init() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if(entity instanceof MultiPartEntity partEntity && partEntity.isMultipartEntity()) {
				PartEntity<?>[] parts = partEntity.getParts();
				if (parts != null) {
					for (PartEntity<?> part : parts) {
						((LevelExtensions)world).getPartEntityMap().put(part.getId(), part);
					}
				}
			}
		});
		ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
			if(entity instanceof MultiPartEntity partEntity && partEntity.isMultipartEntity()) {
				PartEntity<?>[] parts = partEntity.getParts();
				if (parts != null) {
					for (PartEntity<?> part : parts) {
						((LevelExtensions)world).getPartEntityMap().remove(part.getId());
					}
				}
			}
		});
		RegistryEntryRemovedCallback.event(Registry.ITEM).register((rawId, id, item) -> {
			if (item instanceof BlockItemExtensions blockItem) {
				blockItem.removeFromBlockToItemMap(Item.BY_BLOCK, item);
			}
		});
		ServerLifecycleEvents.SERVER_STARTED.register(PortingHooks::loadLanguagesOnServer);
	}

	private static List<Map<String, String>> capturedTables = new ArrayList<>(2);
	private static Map<String, String> modTable;

	static void loadLanguagesOnServer(MinecraftServer server) {
		modTable = new HashMap<>(5000);
		// Possible multi-language server support?
		for (String lang : Arrays.asList("en_us")) {
			loadLanguage(lang, server);
		}
		capturedTables.forEach(t->t.putAll(modTable));
		ForgeI18n.loadLanguageData(modTable);
	}

	public static void captureLanguageMap(Map<String, String> table)
	{
		capturedTables.add(table);
		if (modTable != null) {
			capturedTables.forEach(t->t.putAll(modTable));
		}
	}

	private static final Gson GSON = new Gson();
	private static final Pattern PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");

	// The below is based on client side net.minecraft.client.resources.Locale code
	private static void loadLocaleData(final List<Resource> allResources) {
		allResources.stream().map(Resource::getInputStream).forEach(PortingHooks::loadLocaleData);
	}

	private static void loadLocaleData(final InputStream inputstream) {
		try {
			JsonElement jsonelement = GSON.fromJson(new InputStreamReader(inputstream, StandardCharsets.UTF_8), JsonElement.class);
			JsonObject jsonobject = GsonHelper.convertToJsonObject(jsonelement, "strings");

			jsonobject.entrySet().forEach(entry -> {
				String s = PATTERN.matcher(GsonHelper.convertToString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
				modTable.put(entry.getKey(), s);
			});
		}
		finally
		{
			IOUtils.closeQuietly(inputstream);
		}
	}

	private static void loadLanguage(String langName, MinecraftServer server) {
		String langFile = String.format(Locale.ROOT, "lang/%s.json", langName);
		ResourceManager resourceManager = ((MinecraftServerAccessor)server).port_lib$getServerResources().resourceManager();
		resourceManager.getNamespaces().forEach(namespace -> {
			try {
				ResourceLocation langResource = new ResourceLocation(namespace, langFile);
				loadLocaleData(resourceManager.getResources(langResource));
			} catch (FileNotFoundException fnfe) {
			} catch (Exception exception) {
				LOGGER.warn("Skipped language file: {}:{}", namespace, langFile, exception);
			}
		});

	}
}
