package xyz.bluspring.forgecapabilities.capabilities;

import io.github.fabricators_of_create.porting_lib.transfer.item.SlotExposedStorage;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;

public class PortingLibCapabilities implements ModInitializer {
	// TODO: Correctly match these to existing storages
	//		 Right now, they are only matching to what their equivalent transfer API uses.
	public static final Capability<Storage<FluidVariant>> FLUID_HANDLER = CapabilityManager.get(new CapabilityToken<>(){});
	public static final Capability<SingleSlotStorage<FluidVariant>> FLUID_HANDLER_ITEM = CapabilityManager.get(new CapabilityToken<>(){});
	public static final Capability<SlotExposedStorage> ITEM_HANDLER = CapabilityManager.get(new CapabilityToken<>(){});

	@Override
	public void onInitialize() {
		CapabilityManager.INSTANCE.injectCapabilities();
	}
}
