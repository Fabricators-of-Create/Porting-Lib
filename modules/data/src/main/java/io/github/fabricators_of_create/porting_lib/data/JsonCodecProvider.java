package io.github.fabricators_of_create.porting_lib.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import io.github.fabricators_of_create.porting_lib.conditions.ConditionalOps;
import io.github.fabricators_of_create.porting_lib.conditions.WithConditions;
import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper.ResourceType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;

/**
 * <p>Dataprovider for using a Codec to generate jsons.
 * Path names for jsons are derived from the given registry folder and each entry's namespaced id, in the format:</p>
 *
 * <pre>
 * {@code <assets/data>/entryid/registryfolder/entrypath.json }
 * </pre>
 *
 * @param <T> the type of thing being generated.
 */
public abstract class JsonCodecProvider<T> implements DataProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	protected final ResourceType resourceType;
	protected final PackOutput.PathProvider pathProvider;
	protected final ExistingFileHelper existingFileHelper;
	protected final CompletableFuture<HolderLookup.Provider> lookupProvider;
	protected final String modid;
	protected final String directory;
	protected final Codec<T> codec;
	protected final Map<ResourceLocation, WithConditions<T>> conditions = Maps.newHashMap();

	/**
	 * @param output    {@linkplain PackOutput} provided by the {@link DataGenerator}.
	 * @param packType  PackType specifying whether to generate entries in assets or data.
	 * @param directory String representing the directory to generate jsons in, e.g. "dimension" or "cheesemod/cheese".
	 * @param codec     Codec to encode values to jsons with using the provided DynamicOps.
	 */
	public JsonCodecProvider(PackOutput output,
							 PackOutput.Target target,
							 String directory,
							 PackType packType,
							 Codec<T> codec,
							 CompletableFuture<HolderLookup.Provider> lookupProvider,
							 String modId,
							 ExistingFileHelper existingFileHelper) {
		// Track generated data so other dataproviders can validate if needed.
		this.resourceType = new ResourceType(packType, ".json", directory);
		this.pathProvider = output.createPathProvider(target, directory);
		this.existingFileHelper = existingFileHelper;
		this.modid = modId;
		this.directory = directory;
		this.codec = codec;
		this.lookupProvider = lookupProvider;
	}

	@Override
	public CompletableFuture<?> run(final CachedOutput cache) {
		ImmutableList.Builder<CompletableFuture<?>> futuresBuilder = new ImmutableList.Builder<>();

		gather();

		return lookupProvider.thenCompose(provider -> {
			final DynamicOps<JsonElement> dynamicOps = RegistryOps.create(JsonOps.INSTANCE, provider);

			this.conditions.forEach((id, withConditions) -> {
				final Path path = this.pathProvider.json(id);

				futuresBuilder.add(CompletableFuture.supplyAsync(() -> {
					final Codec<Optional<WithConditions<T>>> withConditionsCodec = ConditionalOps.createConditionalCodecWithConditions(provider, this.codec);
					return withConditionsCodec.encodeStart(dynamicOps, Optional.of(withConditions)).getOrThrow(msg -> new RuntimeException("Failed to encode %s: %s".formatted(path, msg)));
				}).thenComposeAsync(encoded -> DataProvider.saveStable(cache, encoded, path)));
			});

			return CompletableFuture.allOf(futuresBuilder.build().toArray(CompletableFuture[]::new));
		});
	}

	protected abstract void gather();

	@Override
	public String getName() {
		return String.format("%s generator for %s", this.directory, this.modid);
	}

	public void unconditional(ResourceLocation id, T value) {
		process(id, new WithConditions<>(List.of(), value));
	}

	public void conditionally(ResourceLocation id, Consumer<WithConditions.Builder<T>> configurator) {
		final WithConditions.Builder<T> builder = new WithConditions.Builder<>();
		configurator.accept(builder);

		final WithConditions<T> withConditions = builder.build();
		process(id, withConditions);
	}

	private void process(ResourceLocation id, WithConditions<T> withConditions) {
		this.existingFileHelper.trackGenerated(id, this.resourceType);

		this.conditions.put(id, withConditions);
	}
}
