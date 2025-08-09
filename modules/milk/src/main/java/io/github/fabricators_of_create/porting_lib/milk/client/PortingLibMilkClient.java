package io.github.fabricators_of_create.porting_lib.milk.client;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.fluids.PortingLibFluids;
import io.github.fabricators_of_create.porting_lib.milk.PortingLibMilk;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.event.registry.RegistryIdRemapCallback;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.atomic.AtomicBoolean;

public class PortingLibMilkClient {
	public static void init() {
		ResourceLocation MILK_STILL = PortingLib.id("block/milk_still");
		ResourceLocation MILK_FLOW = PortingLib.id("block/milk_flowing");
		FluidRenderHandlerRegistry.INSTANCE.register(PortingLibMilk.MILK.get(), PortingLibMilk.FLOWING_MILK.get(), new SimpleFluidRenderHandler(MILK_STILL, MILK_FLOW));
	}
}
