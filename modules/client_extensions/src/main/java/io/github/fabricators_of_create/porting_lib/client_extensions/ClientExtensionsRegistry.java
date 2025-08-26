package io.github.fabricators_of_create.porting_lib.client_extensions;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ClientExtensionsRegistry {
	static final Map<Block, IClientBlockExtensions> BLOCK_EXTENSIONS = new Reference2ObjectOpenHashMap<>();
	static final Map<Item, IClientItemExtensions> ITEM_EXTENSIONS = new Reference2ObjectOpenHashMap<>();
	static final Map<MobEffect, IClientMobEffectExtensions> MOB_EFFECT_EXTENSIONS = new Reference2ObjectOpenHashMap<>();

	@SafeVarargs
	static <T, E> void register(E extensions, Map<T, E> target, T... objects) {
		if (objects.length == 0) {
			throw new IllegalArgumentException("At least one target must be provided");
		}
		Objects.requireNonNull(extensions, "Extensions must not be null");

		for (T object : objects) {
			Objects.requireNonNull(objects, "Target must not be null");
			E oldExtensions = target.put(object, extensions);
			if (oldExtensions != null) {
				throw new IllegalStateException(String.format(
						Locale.ROOT,
						"Duplicate client extensions registration for %s (old: %s, new: %s)",
						object,
						oldExtensions,
						extensions));
			}
		}
	}

	/**
	 * Register the given {@link IClientBlockExtensions} for the given {@link Block}s
	 */
	public static void registerBlock(IClientBlockExtensions extensions, Block... blocks) {
		register(extensions, BLOCK_EXTENSIONS, blocks);
	}

	/**
	 * Register the given {@link IClientBlockExtensions} for the given {@link Block}s
	 */
	@SafeVarargs
	public static void registerBlock(IClientBlockExtensions extensions, Holder<Block>... blocks) {
		registerBlock(extensions, Arrays.stream(blocks).map(Holder::value).toArray(Block[]::new));
	}

	/**
	 * {@return whether a {@link IClientBlockExtensions} has been registered for the given {@link Block}}
	 */
	public static boolean isBlockRegistered(Block block) {
		return BLOCK_EXTENSIONS.containsKey(block);
	}

	/**
	 * Register the given {@link IClientItemExtensions} for the given {@link Item}s
	 */
	public static void registerItem(IClientItemExtensions extensions, Item... items) {
		register(extensions, ITEM_EXTENSIONS, items);

		// Register the item renderers directly to Fabric's item renderer registry.
		for (Item item : items) {
			BuiltinItemRendererRegistry.INSTANCE.register(item, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
				extensions.getCustomRenderer().renderByItem(stack, mode, matrices, vertexConsumers, light, overlay);
			});
		}
	}

	/**
	 * Register the given {@link IClientItemExtensions} for the given {@link Item}s
	 */
	@SafeVarargs
	public static void registerItem(IClientItemExtensions extensions, Holder<Item>... items) {
		registerItem(extensions, Arrays.stream(items).map(Holder::value).toArray(Item[]::new));
	}

	/**
	 * {@return whether a {@link IClientItemExtensions} has been registered for the given {@link Item}}
	 */
	public static boolean isItemRegistered(Item item) {
		return ITEM_EXTENSIONS.containsKey(item);
	}

	/**
	 * Register the given {@link IClientMobEffectExtensions} for the given {@link MobEffect}s
	 */
	public static void registerMobEffect(IClientMobEffectExtensions extensions, MobEffect... mobEffects) {
		register(extensions, MOB_EFFECT_EXTENSIONS, mobEffects);
	}

	/**
	 * Register the given {@link IClientMobEffectExtensions} for the given {@link MobEffect}s
	 */
	@SafeVarargs
	public static void registerMobEffect(IClientMobEffectExtensions extensions, Holder<MobEffect>... mobEffects) {
		registerMobEffect(extensions, Arrays.stream(mobEffects).map(Holder::value).toArray(MobEffect[]::new));
	}

	/**
	 * {@return whether a {@link IClientMobEffectExtensions} has been registered for the given {@link MobEffect}}
	 */
	public static boolean isMobEffectRegistered(MobEffect mobEffect) {
		return MOB_EFFECT_EXTENSIONS.containsKey(mobEffect);
	}
}
