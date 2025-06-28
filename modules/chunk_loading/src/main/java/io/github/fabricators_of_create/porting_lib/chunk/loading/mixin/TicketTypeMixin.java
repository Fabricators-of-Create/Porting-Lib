package io.github.fabricators_of_create.porting_lib.chunk.loading.mixin;

import io.github.fabricators_of_create.porting_lib.chunk.loading.extensions.TicketTypeExtension;
import net.minecraft.server.level.TicketType;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(TicketType.class)
public class TicketTypeMixin implements TicketTypeExtension {
	private boolean port_lib$forceNaturalSpawning;

	@Override
	public boolean port_lib$forceNaturalSpawning() {
		return this.port_lib$forceNaturalSpawning;
	}

	@Override
	public void port_lib$setForceNaturalSpawning(boolean forceNaturalSpawning) {
		this.port_lib$forceNaturalSpawning = forceNaturalSpawning;
	}
}
