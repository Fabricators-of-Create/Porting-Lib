package io.github.fabricators_of_create.porting_lib.milk.testmod;

import io.github.fabricators_of_create.porting_lib.milk.PortingLibMilk;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class PortingLibMilkTest implements ModInitializer {
	@Override
	public void onInitialize() {
		PortingLibMilk.enableMilkFluid();
		UseItemCallback.EVENT.register((player, world, hand) -> {
			ItemStack stack = player.getItemInHand(hand);
			if (stack.is(Items.MILK_BUCKET)) {
				Storage<FluidVariant> storge = FluidStorage.ITEM.find(stack, ContainerItemContext.ofPlayerHand(player, hand));
				if (storge == null)
					return InteractionResultHolder.pass(stack);
				for (StorageView<FluidVariant> view : storge.nonEmptyViews()) {
					player.displayClientMessage(Component.literal("Contains ").append(view.getResource().getFluid().getFluidType().getDescription()), true);
					break;
				}
			}
			return InteractionResultHolder.pass(stack);
		});
	}
}
