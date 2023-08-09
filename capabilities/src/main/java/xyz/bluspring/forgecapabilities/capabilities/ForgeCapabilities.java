package xyz.bluspring.forgecapabilities.capabilities;

import io.github.fabricators_of_create.porting_lib.transfer.item.SlotExposedStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;

/*
 * References to  Forge's built in capabilities.
 * Modders are recommended to use their own CapabilityTokens for 3rd party caps to maintain soft dependencies.
 * However, since nobody has a soft dependency on Forge, we expose this as API.
 */
public class ForgeCapabilities
{
	// TODO: Correctly match these to existing storages
	//		 Right now, they are only matching to what their transfer API uses.
    public static final Capability<Storage<Object>> ENERGY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<Storage<FluidVariant>> FLUID_HANDLER = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<SingleSlotStorage<FluidVariant>> FLUID_HANDLER_ITEM = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<SlotExposedStorage> ITEM_HANDLER = CapabilityManager.get(new CapabilityToken<>(){});
}
