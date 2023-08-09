package xyz.bluspring.forgecapabilities.mixin.common.capabilities;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import io.github.fabricators_of_create.porting_lib.extensions.LevelExtensions;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import xyz.bluspring.forgecapabilities.capabilities.Capability;
import xyz.bluspring.forgecapabilities.capabilities.CapabilityDispatcher;
import xyz.bluspring.forgecapabilities.capabilities.CapabilityProvider;
import xyz.bluspring.forgecapabilities.capabilities.CapabilityProviderWorkaround;
import xyz.bluspring.forgecapabilities.capabilities.ICapabilityProvider;
import xyz.bluspring.forgecapabilities.extensions.capabilities.CapabilityProviderExtension;
import xyz.bluspring.forgecapabilities.extensions.capabilities.LevelCapabilityProviderImpl;

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
