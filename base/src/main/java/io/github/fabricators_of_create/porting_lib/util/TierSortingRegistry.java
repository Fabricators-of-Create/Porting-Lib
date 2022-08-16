package io.github.fabricators_of_create.porting_lib.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.fabricators_of_create.porting_lib.PortingConstants;
import io.github.fabricators_of_create.porting_lib.PortingLib;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;

import net.minecraft.server.packs.PackType;

import net.minecraft.tags.TagKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TierSortingRegistry {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ResourceLocation ITEM_TIER_ORDERING_JSON = new ResourceLocation("port_lib", "item_tier_ordering.json");
	private static final BiMap<ResourceLocation, Tier> tiers = HashBiMap.create();
	private static final Multimap<ResourceLocation, ResourceLocation> edges = HashMultimap.create();
	private static final Multimap<ResourceLocation, ResourceLocation> vanillaEdges = HashMultimap.create();
	private static final List<Tier> sortedTiers = new ArrayList<>();
	private static final List<Tier> sortedTiersUnmodifiable = Collections.unmodifiableList(sortedTiers);
	private static final ResourceLocation CHANNEL_NAME = new ResourceLocation("port_lib:tier_sorting");
	private static final String PROTOCOL_VERSION = "1.0";

	private static boolean hasCustomTiers = false;

	static {
		var wood = new ResourceLocation("wood");
		var stone = new ResourceLocation("stone");
		var iron = new ResourceLocation("iron");
		var diamond = new ResourceLocation("diamond");
		var netherite = new ResourceLocation("netherite");
		var gold = new ResourceLocation("gold");
		processTier(Tiers.WOOD, wood, List.of(), List.of());
		processTier(Tiers.GOLD, gold, List.of(wood), List.of(stone));
		processTier(Tiers.STONE, stone, List.of(wood), List.of(iron));
		processTier(Tiers.IRON, iron, List.of(stone), List.of(diamond));
		processTier(Tiers.DIAMOND, diamond, List.of(iron), List.of(netherite));
		processTier(Tiers.NETHERITE, netherite, List.of(diamond), List.of());
		vanillaEdges.putAll(edges);
	}

	/**
	 * Registers a tier into the tier sorting registry.
	 *
	 * @param tier   The tier to register
	 * @param name   The name to use internally for dependency resolution
	 * @param after  List of tiers to place this tier after (the tiers in the list will be considered lesser tiers)
	 * @param before List of tiers to place this tier before (the tiers in the list will be considered better tiers)
	 */
	public static synchronized Tier registerTier(Tier tier, ResourceLocation name, List<Object> after, List<Object> before) {
		if (tiers.containsKey(name))
			throw new IllegalStateException("Duplicate tier name " + name);

		processTier(tier, name, after, before);

		hasCustomTiers = true;
		return tier;
	}

	/**
	 * Returns the list of tiers in the order defined by the dependencies.
	 * This list will remain valid
	 *
	 * @return An unmodifiable list of tiers ordered lesser to greater
	 */
	public static List<Tier> getSortedTiers() {
		return sortedTiersUnmodifiable;
	}

	/**
	 * Returns the tier associated with a name, if registered into the sorting system.
	 *
	 * @param name The name to look up
	 * @return The tier, or null if not registered
	 */
	@Nullable
	public static Tier byName(ResourceLocation name) {
		return tiers.get(name);
	}

	/**
	 * Returns the name associated with a tier, if the tier is registered into the sorting system.
	 *
	 * @param tier The tier to look up
	 * @return The name for the tier, or null if not registered
	 */
	@Nullable
	public static ResourceLocation getName(Tier tier) {
		return tiers.inverse().get(tier);
	}

	/**
	 * Queries if a tier should be evaluated using the sorting system, by calling isCorrectTierForDrops
	 *
	 * @param tier The tier to query
	 * @return True if isCorrectTierForDrops should be called for the tier
	 */
	public static boolean isTierSorted(Tier tier) {
		return getName(tier) != null;
	}

	/**
	 * Queries if a tier is high enough to be able to get drops for the given blockstate.
	 *
	 * @param tier  The tier to look up
	 * @param state The state to test against
	 * @return True if the tier is good enough
	 */
	public static boolean isCorrectTierForDrops(Tier tier, BlockState state) {
		if (!isTierSorted(tier))
			return isCorrectTierVanilla(tier, state);

		for (int x = sortedTiers.indexOf(tier) + 1; x < sortedTiers.size(); x++) {
			TagKey<Block> tag = TagUtil.getTagFromTier(sortedTiers.get(x));
			if (tag != null && state.is(tag))
				return false;
		}
		return true;
	}

	/**
	 * Helper to query all tiers that are lower than the given tier
	 *
	 * @param tier The tier
	 * @return All the lower tiers
	 */
	public static List<Tier> getTiersLowerThan(Tier tier) {
		if (!isTierSorted(tier)) return List.of();
		return sortedTiers.stream().takeWhile(t -> t != tier).toList();
	}

	/**
	 * Fallback for when a tier isn't in the registry, copy of the logic in {@link DiggerItem#isCorrectToolForDrops}
	 */
	private static boolean isCorrectTierVanilla(Tier tier, BlockState state) {
		int i = tier.getLevel();
		if (i < 3 && state.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
			return false;
		} else if (i < 2 && state.is(BlockTags.NEEDS_IRON_TOOL)) {
			return false;
		} else return i >= 1 || !state.is(BlockTags.NEEDS_STONE_TOOL);
	}

	private static void processTier(Tier tier, ResourceLocation name, List<Object> afters, List<Object> befores) {
		tiers.put(name, tier);
		for (Object after : afters) {
			ResourceLocation other = getTierName(after);
			edges.put(other, name);
		}
		for (Object before : befores) {
			ResourceLocation other = getTierName(before);
			edges.put(name, other);
		}
	}

	private static ResourceLocation getTierName(Object entry) {
		if (entry instanceof String s)
			return new ResourceLocation(s);
		if (entry instanceof ResourceLocation rl)
			return rl;
		if (entry instanceof Tier t)
			return Objects.requireNonNull(getName(t), "Can't have sorting dependencies for tiers not registered in the TierSortingRegistry");
		throw new IllegalStateException("Invalid object type passed into the tier dependencies " + entry.getClass());
	}

	static boolean allowVanilla() {
		return !hasCustomTiers;
	}

	/*package private (not for us >:))*/
	public static void init() {
		ServerPlayConnectionEvents.JOIN.register(TierSortingRegistry::playerLoggedIn);
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(getReloadListener());
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) ClientEvents.init();
	}

	/*package private*/
	static IdentifiableResourceReloadListener getReloadListener() {
		return new IdentifiableSimplePreparableReloadListener<JsonObject>(PortingConstants.id("tier_sorting_registry")) {
			final Gson gson = (new GsonBuilder()).create();

			@Nonnull
			@Override
			protected JsonObject prepare(@Nonnull ResourceManager resourceManager, ProfilerFiller p) {
				if (!resourceManager.hasResource(ITEM_TIER_ORDERING_JSON))
					return new JsonObject();

				try (Resource r = resourceManager.getResource(ITEM_TIER_ORDERING_JSON); InputStream stream = r.getInputStream(); Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
					return gson.fromJson(reader, JsonObject.class);
				} catch (IOException e) {
					LOGGER.error("Could not read Tier sorting file " + ITEM_TIER_ORDERING_JSON, e);
					return new JsonObject();
				}
			}

			@Override
			protected void apply(@Nonnull JsonObject data, @Nonnull ResourceManager resourceManager, ProfilerFiller p) {
				try {
					if (data.size() > 0) {
						JsonArray order = GsonHelper.getAsJsonArray(data, "order");
						List<Tier> customOrder = new ArrayList<>();
						for (JsonElement entry : order) {
							ResourceLocation id = new ResourceLocation(entry.getAsString());
							Tier tier = byName(id);
							if (tier == null) throw new IllegalStateException("Tier not found with name " + id);
							customOrder.add(tier);
						}

						List<Tier> missingTiers = tiers.values().stream().filter(tier -> !customOrder.contains(tier)).toList();
						if (missingTiers.size() > 0)
							throw new IllegalStateException("Tiers missing from the ordered list: " + missingTiers.stream().map(tier -> Objects.toString(TierSortingRegistry.getName(tier))).collect(Collectors.joining(", ")));

						setTierOrder(customOrder);
						return;
					}
				} catch (Exception e) {
					LOGGER.error("Error parsing Tier sorting file " + ITEM_TIER_ORDERING_JSON, e);
				}

				recalculateItemTiers();
			}
		};
	}

	@SuppressWarnings("UnstableApiUsage")
	private static void recalculateItemTiers() {
		final MutableGraph<Tier> graph = GraphBuilder.directed().nodeOrder(ElementOrder.<Tier>insertion()).build();

		for (Tier tier : tiers.values()) {
			graph.addNode(tier);
		}
		edges.forEach((key, value) -> {
			if (tiers.containsKey(key) && tiers.containsKey(value))
				graph.putEdge(tiers.get(key), tiers.get(value));
		});
		List<Tier> tierList = TopologicalSort.topologicalSort(graph, null);

		setTierOrder(tierList);
	}

	private static void setTierOrder(List<Tier> tierList) {
		runInServerThreadIfPossible(hasServer -> {
			sortedTiers.clear();
			sortedTiers.addAll(tierList);
			if (hasServer) syncToAll();
		});
	}

	private static void runInServerThreadIfPossible(BooleanConsumer runnable) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null) server.execute(() -> runnable.accept(true));
		else runnable.accept(false);
	}

	private static void syncToAll() {
		for (ServerPlayer serverPlayer : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
			syncToPlayer(serverPlayer);
		}
	}

	private static void playerLoggedIn(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
		syncToPlayer(handler.getPlayer());
	}

	private static void syncToPlayer(ServerPlayer serverPlayer) {
		if (!serverPlayer.connection.getConnection().isMemoryConnection()) {
			ServerPlayNetworking.send(serverPlayer, CHANNEL_NAME, new SyncPacket(sortedTiers.stream().map(TierSortingRegistry::getName).toList()).encode());
		}
	}

	private static SyncPacket receive(FriendlyByteBuf buffer) {
		int count = buffer.readVarInt();
		List<ResourceLocation> list = new ArrayList<>();
		for (int i = 0; i < count; i++)
			list.add(buffer.readResourceLocation());
		return new SyncPacket(list);
	}

	private static void handle(SyncPacket packet) {
		setTierOrder(packet.tiers.stream().map(TierSortingRegistry::byName).toList());
	}

	private record SyncPacket(List<ResourceLocation> tiers) {
		private FriendlyByteBuf encode() {
			FriendlyByteBuf buffer = PacketByteBufs.create();
			buffer.writeVarInt(tiers.size());
			for (ResourceLocation loc : tiers)
				buffer.writeResourceLocation(loc);
			return buffer;
		}
	}

	private static class ClientEvents {
		public static void init() {
			ClientPlayConnectionEvents.JOIN.register(ClientEvents::clientLogInToServer);
			ClientPlayNetworking.registerGlobalReceiver(CHANNEL_NAME, ((client, handler, buf, responseSender) -> {
				SyncPacket packet = receive(buf);
				handle(packet);
			}));
		}

		private static void clientLogInToServer(ClientPacketListener handler, PacketSender sender, Minecraft client) {
			if (handler.getConnection() == null || !handler.getConnection().isMemoryConnection())
				recalculateItemTiers();
		}
	}
}
