package io.github.fabricators_of_create.porting_lib.chunk.loading;

import it.unimi.dsi.fastutil.longs.LongSet;

/**
 * Represents a pair of chunk-loaded ticket sets.
 *
 * @param nonTicking the non-fully ticking tickets
 * @param ticking    the fully ticking tickets
 */
public record TicketSet(LongSet nonTicking, LongSet ticking) {}
