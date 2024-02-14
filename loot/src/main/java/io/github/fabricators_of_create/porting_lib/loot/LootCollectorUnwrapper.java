package io.github.fabricators_of_create.porting_lib.loot;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.world.item.ItemStack;

public class LootCollectorUnwrapper {
	private static final Map<Class<?>, UnaryOperator<Consumer<ItemStack>>> unwrappers = new IdentityHashMap<>();

	/**
	 * Unwrap a consumer until a LootCollector is found, throwing if not found.
	 */
	public static LootCollector unwrap(Consumer<ItemStack> consumer) {
		while (!(consumer instanceof LootCollector collector)) {
			Consumer<ItemStack> finalConsumer = consumer;
			consumer = unwrappers.computeIfAbsent(consumer.getClass(), $ -> makeUnwrapper(finalConsumer)).apply(consumer);
		}

		return collector;
	}

	private static UnaryOperator<Consumer<ItemStack>> makeUnwrapper(Consumer<ItemStack> consumer) {
		Class<?> clazz = consumer.getClass();
		// search for a field holding a Consumer.
		// It's not possible to filter for Consumer<ItemStack> due to erasure, hopefully that doesn't cause problems later.
		List<Field> potentialHolders = new ArrayList<>();
		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			Object value = rethrow(() -> field.get(consumer));
			if (value instanceof Consumer<?>) {
				potentialHolders.add(field);
			}
		}
		return switch (potentialHolders.size()) {
			case 0 -> throw unwrapFail(consumer); // none found, this will cause problems if we continue.
			case 1 -> getFromField(potentialHolders.get(0)); // one found, all is well
			default -> findDirectHolder(consumer, potentialHolders); // multiple found, uh oh. Check each one again for directly holding a LootCollector
		};
	}

	@SuppressWarnings("unchecked")
	private static UnaryOperator<Consumer<ItemStack>> findDirectHolder(Consumer<ItemStack> consumer, List<Field> consumerHolders) {
		List<Field> holdingCollectors = consumerHolders.stream()
				.filter(field -> {
					Consumer<ItemStack> heldConsumer = rethrow(() -> (Consumer<ItemStack>) field.get(consumer));
					return heldConsumer instanceof LootCollector;
				}).toList();

		return switch (holdingCollectors.size()) {
			case 0 -> throw unwrapFail(consumer); // none found. Problem!
			case 1 -> getFromField(holdingCollectors.get(0)); // found one, everything is fine
			default -> throw new IllegalArgumentException("Consumer contains multiple LootCollectors, cannot unwrap");
		};
	}

	@SuppressWarnings("unchecked")
	private static UnaryOperator<Consumer<ItemStack>> getFromField(Field field) {
		return rethrow(() -> {
			MethodHandle handle = MethodHandles.lookup().unreflectGetter(field);
			return consumer -> rethrow(() -> (Consumer<ItemStack>) handle.invoke(consumer));
		});
	}

	private static <T> T rethrow(UnsafeSupplier<T> supplier) {
		try {
			return supplier.get();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	private static RuntimeException unwrapFail(Consumer<ItemStack> consumer) {
		return new IllegalArgumentException("Could not unwrap consumer " + consumer);
	}

	/**
	 * Register a manual unwrapper. This can be used to handle weird cases if any arise.
	 */
	public static void registerUnwrapper(Class<?> clazz, UnaryOperator<Consumer<ItemStack>> unwrapper) {
		if (unwrappers.put(clazz, unwrapper) != null) {
			throw new IllegalStateException("Tried to register a second unwrapper for " + clazz.getName());
		}
	}

	@Internal
	@SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
	public static boolean test() {
		LootCollector collector = new LootCollector(stack -> {});
		Consumer<ItemStack> wrapper1 = stack -> collector.accept(stack);
		Consumer<ItemStack> wrapper2 = stack -> wrapper1.accept(stack);
		Consumer<ItemStack> dual = stack -> {
			collector.accept(stack);
			wrapper2.accept(stack);
		};
		Consumer<ItemStack> dualWrapper = stack -> dual.accept(stack);
		Consumer<ItemStack> notWrapper = stack -> {};

		unwrap(wrapper1);
		unwrap(wrapper2);
		unwrap(dual);
		unwrap(dualWrapper);
		try {
			unwrap(notWrapper);
			return false;
		} catch (RuntimeException ignored) {
		}
		return true;
	}

	private interface UnsafeSupplier<T> {
		T get() throws Throwable;
	}
}
