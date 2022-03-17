package io.github.fabricators_of_create.porting_lib.util;

import java.util.Optional;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

public class TransferUtil {
	public static Transaction getTransaction() {
		TransactionContext open = Transaction.getCurrentUnsafe();
		if (open != null) {
			return open.openNested();
		}
		return Transaction.openOuter();
	}

	public static Optional<FluidStack> getFluidContained(ItemStack container) {
		if (container != null && !container.isEmpty()) {
			try (Transaction t = getTransaction()) {
				Storage<FluidVariant> storage = FluidStorage.ITEM.find(container, ContainerItemContext.withInitial(container));
				if (storage != null) {
					for (StorageView<FluidVariant> view : storage.iterable(t)) {
						return Optional.of(new FluidStack(view.getResource(), view.getAmount()));
					}
				}
			}
		}
		return Optional.empty();
	}
}
