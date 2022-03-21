package io.github.fabricators_of_create.porting_lib.transfer;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;

public interface Transferable<T> {
	Storage<T> getStorage(@Nullable Direction face);
}
