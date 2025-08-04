package io.github.fabricators_of_create.porting_lib.milk.client;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.milk.PortingLibMilk;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.resources.ResourceLocation;

public class PortingLibMilkClient {
	public static void init() {
		PortingLibMilk.MILK_TYPE.asOptional().ifPresent(milkType -> {
			ResourceLocation MILK_STILL = PortingLib.id("block/milk_still");
			ResourceLocation MILK_FLOW = PortingLib.id("block/milk_flowing");
			FluidRenderHandlerRegistry.INSTANCE.register(PortingLibMilk.MILK.get(), PortingLibMilk.FLOWING_MILK.get(), new SimpleFluidRenderHandler(MILK_STILL, MILK_FLOW));
		});
	}
}
