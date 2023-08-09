package io.github.fabricators_of_create.porting_lib.mixin.capabilities;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.capabilities.Capability;
import io.github.fabricators_of_create.porting_lib.capabilities.CapabilityDispatcher;
import io.github.fabricators_of_create.porting_lib.capabilities.CapabilityProvider;
import io.github.fabricators_of_create.porting_lib.capabilities.CapabilityProviderWorkaround;
import io.github.fabricators_of_create.porting_lib.capabilities.ICapabilityProvider;
import io.github.fabricators_of_create.porting_lib.extensions.LevelExtensions;
import io.github.fabricators_of_create.porting_lib.extensions.capabilities.CapabilityProviderExtension;
import io.github.fabricators_of_create.porting_lib.extensions.capabilities.LevelCapabilityProviderImpl;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

@Mixin(Level.class)
public abstract class LevelMixin implements CapabilityProviderExtension, LevelCapabilityProviderImpl, LevelExtensions {
	private final CapabilityProviderWorkaround<Level> workaround = new CapabilityProviderWorkaround<>(Level.class, (Level) (Object) this);

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return workaround.getCapability(cap, side);
	}

	@Override
	public CapabilityProviderWorkaround<Level> port_lib$getWorkaround() {
		return workaround;
	}

	@Override
	public boolean areCapsCompatible(CapabilityProvider<Level> other) {
		return workaround.areCapsCompatible(other);
	}

	@Override
	public boolean areCapsCompatible(@Nullable CapabilityDispatcher other) {
		return workaround.areCapsCompatible(other);
	}

	@Override
	public void invalidateCaps() {
		workaround.invalidateCaps();
	}

	@Override
	public void reviveCaps() {
		workaround.reviveCaps();
	}

	@Override
	public void gatherCapabilities(@Nullable Supplier<ICapabilityProvider> parent) {
		workaround.invokeGatherCapabilities(parent);
	}
}
