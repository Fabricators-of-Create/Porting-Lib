package io.github.fabricators_of_create.porting_lib.data;

import com.google.common.collect.Sets;

import com.mojang.logging.LogUtils;

import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.AdvancementProviderAccessor;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

public class ModdedAdvancementProvider extends AdvancementProvider {
	private static final Logger LOGGER = LogUtils.getLogger();

	public ModdedAdvancementProvider(DataGenerator dataGenerator) {
		super(dataGenerator);
	}

	public void run(CachedOutput p_236158_) {
		Set<ResourceLocation> set = Sets.newHashSet();
		Consumer<Advancement> consumer = (p_236162_) -> {
			if (!set.add(p_236162_.getId())) {
				throw new IllegalStateException("Duplicate advancement " + p_236162_.getId());
			} else {
				Path path = ((AdvancementProviderAccessor)this).getPathProvider().json(p_236162_.getId());

				try {
					DataProvider.saveStable(p_236158_, p_236162_.deconstruct().serializeToJson(), path);
				} catch (IOException ioexception) {
					LOGGER.error("Couldn't save advancement {}", path, ioexception);
				}

			}
		};

		registerAdvancements(consumer);
	}

	/**
	 * This method registers all {@link Advancement advancements}.
	 * Mods can override this method to register their own custom advancements.
	 *
	 * @param consumer a {@link Consumer} which saves any advancements provided
	 * @see Advancement.Builder#save(Consumer, String)
	 */
	protected void registerAdvancements(Consumer<Advancement> consumer) {
		for(Consumer<Consumer<Advancement>> consumer1 : ((AdvancementProviderAccessor)this).getTabs()) {
			consumer1.accept(consumer);
		}

	}
}
