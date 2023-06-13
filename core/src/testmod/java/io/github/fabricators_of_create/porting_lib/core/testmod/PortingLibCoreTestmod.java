package io.github.fabricators_of_create.porting_lib.core.testmod;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.core.event.CancelBypass;
import io.github.fabricators_of_create.porting_lib.core.testmod.TestCallback.TestEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.chat.Component;

public class PortingLibCoreTestmod implements ModInitializer {
	@Override
	public void onInitialize() {
		PortingLib.LOGGER.info("--- CancelBypass testing ---");
		IntList output = new IntArrayList();
		Int2ObjectMap<TestCallback> callbacks = new Int2ObjectArrayMap<>();
		// set up 5 listeners, 3rd will cancel
		for (int i = 0; i < 5; i++) {
			final int index = i;
			TestCallback listener = event -> {
				output.add(index);
				if (index == 2)
					event.setCanceled(true);
			};
			callbacks.put(index, listener);
			TestCallback.EVENT.register(listener);
		}
		// step 1: no bypassing, 4 and 5 should not do anything
		TestCallback.EVENT.invoker().doTest(new TestEvent());
		PortingLib.LOGGER.info("Step 1 - expected: [0, 1, 2] got: " + output);
		// let all ignore cancellation
		callbacks.values().forEach(listener -> CancelBypass.markIgnoring(TestCallback.EVENT, listener));
		output.clear();
		// step 2: bypassing, all should do stuff
		TestCallback.EVENT.invoker().doTest(new TestEvent());
		PortingLib.LOGGER.info("Step 2 - expected: [0, 1, 2, 3, 4] got: " + output);

		ServerPlayConnectionEvents.JOIN.register(
				(handler, sender, server) -> handler.player.sendSystemMessage(Component.literal("Check the log for CancelBypass results!"))
		);
	}
}
