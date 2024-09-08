package io.github.fabricators_of_create.porting_lib.chunk.loading;

import net.minecraft.server.level.ServerLevel;

@FunctionalInterface
public interface LoadingValidationCallback {
	/**
	 * Called back when tickets are about to be loaded and reinstated to allow mods to invalidate and remove specific tickets that may no longer be valid.
	 *
	 * @param level        The level
	 * @param ticketHelper Ticket helper to remove any invalid tickets.
	 */
	void validateTickets(ServerLevel level, TicketHelper ticketHelper);
}
