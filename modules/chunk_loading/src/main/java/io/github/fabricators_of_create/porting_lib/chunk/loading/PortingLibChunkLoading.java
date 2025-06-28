package io.github.fabricators_of_create.porting_lib.chunk.loading;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.TicketType;

public class PortingLibChunkLoading {
	public static final Holder<TicketType> GENERATE_FORCED_TICKET = register("generate_forced", 0L, false, TicketType.TicketUse.LOADING);
	//Note: We don't persist the tickets via the TicketType, as we keep handle persisting multiple backing sources of the ticket at once and will reinstate any ones that are still valid
	public static final Holder<TicketType> BLOCK_TICKET = register("block", 0L, false, TicketType.TicketUse.LOADING_AND_SIMULATION);
	public static final Holder<TicketType> BLOCK_WITH_NATURAL_SPAWNING_TICKET = register("block_with_natural_spawning", 0L, false, TicketType.TicketUse.LOADING_AND_SIMULATION, true);
	public static final Holder<TicketType> ENTITY_TICKET = register("entity", 0L, false, TicketType.TicketUse.LOADING_AND_SIMULATION);
	public static final Holder<TicketType> ENTITY_WITH_NATURAL_SPAWNING_TICKET = register("entity_with_natural_spawning", 0L, false, TicketType.TicketUse.LOADING_AND_SIMULATION, true);

	private static Holder<TicketType> register(String id, long timeout, boolean persist, TicketType.TicketUse use) {
		return register(id, timeout, persist, use, false);
	}

	private static Holder<TicketType> register(String id, long timeout, boolean persist, TicketType.TicketUse use, boolean forceNaturalSpawning) {
		TicketType type = new TicketType(timeout, persist, use);
		type.port_lib$setForceNaturalSpawning(forceNaturalSpawning);
		return Registry.registerForHolder(BuiltInRegistries.TICKET_TYPE, PortingLib.id(id), type);
	}

	public static void init() {}
}
