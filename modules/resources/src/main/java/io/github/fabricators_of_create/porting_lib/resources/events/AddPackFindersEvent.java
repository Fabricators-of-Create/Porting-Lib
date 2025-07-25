package io.github.fabricators_of_create.porting_lib.resources.events;

import java.util.function.Consumer;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;

/**
 * Fired on {@link PackRepository} creation to allow mods to add new pack finders.
 */
public class AddPackFindersEvent extends BaseEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (Callback c : callbacks)
			c.findPacks(event);
	});
	private final PackType packType;
	private final Consumer<RepositorySource> sources;
	private final boolean trusted;

	public AddPackFindersEvent(PackType packType, Consumer<RepositorySource> sources, boolean trusted) {
		this.packType = packType;
		this.sources = sources;
		this.trusted = trusted;
	}

	/**
	 * Adds a new source to the list of pack finders.
	 *
	 * <p>Sources are processed in the order that they are added to the event.
	 * Use {@link Pack.Position#TOP} to add high priority packs,
	 * and {@link Pack.Position#BOTTOM} to add low priority packs.
	 *
	 * @param source the pack finder
	 */
	public void addRepositorySource(RepositorySource source) {
		sources.accept(source);
	}

	/**
	 * @return the {@link PackType} of the pack repository being constructed.
	 */
	public PackType getPackType() {
		return packType;
	}

	/**
	 * {@return whether or not the pack repository being assembled is the one used to provide known packs to the client to avoid syncing from the server}
	 */
	public boolean isTrusted() {
		return trusted;
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().findPacks(this);
	}

	public interface Callback {
		void findPacks(AddPackFindersEvent event);
	}
}
