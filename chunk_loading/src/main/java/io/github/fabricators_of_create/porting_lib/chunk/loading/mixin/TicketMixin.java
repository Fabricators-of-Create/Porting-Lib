package io.github.fabricators_of_create.porting_lib.chunk.loading.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.chunk.loading.extensions.TicketExtension;
import net.minecraft.server.level.Ticket;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(Ticket.class)
public class TicketMixin<T> implements TicketExtension {
	private boolean port_lib$forceTicks;

	@Override
	public void setForceTicks(boolean forceTicks) {
		this.port_lib$forceTicks = forceTicks;
	}

	@Override
	public boolean isForceTicks() {
		return this.port_lib$forceTicks;
	}

	@ModifyReturnValue(method = "toString", at = @At("RETURN"))
	private String addForceTicks(String old) {
		return old + " force ticks " + port_lib$forceTicks;
	}

	@ModifyReturnValue(method = "equals", at = @At(value = "RETURN", ordinal = 2))
	private boolean extendedEquals(boolean oldEqual, Object other) {
		Ticket<T> ticket = (Ticket<T>) other;
		return oldEqual && this.port_lib$forceTicks == ticket.isForceTicks();
	}

	@ModifyArg(method = "hashCode", at = @At(value = "INVOKE", target = "Ljava/util/Objects;hash([Ljava/lang/Object;)I"))
	private Object[] addForcedHash(Object[] values) {
		return List.of(values, port_lib$forceTicks).toArray();
	}
}
