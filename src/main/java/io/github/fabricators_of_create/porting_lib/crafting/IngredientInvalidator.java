package io.github.fabricators_of_create.porting_lib.crafting;

public class IngredientInvalidator {
	//Because Mojang caches things... we need to invalidate them.. so... here we go..
	public static final java.util.concurrent.atomic.AtomicInteger INVALIDATION_COUNTER = new java.util.concurrent.atomic.AtomicInteger();
	public static void invalidateAll() {
		INVALIDATION_COUNTER.incrementAndGet();
	}
}
