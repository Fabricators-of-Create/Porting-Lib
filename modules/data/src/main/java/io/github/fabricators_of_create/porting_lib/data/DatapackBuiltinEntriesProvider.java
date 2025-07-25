package io.github.fabricators_of_create.porting_lib.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;

import io.github.fabricators_of_create.porting_lib.resources.conditions.ConditionalOps;
import io.github.fabricators_of_create.porting_lib.resources.conditions.ICondition;
import io.github.fabricators_of_create.porting_lib.resources.conditions.WithConditions;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;

/**
 * An extension of the {@link RegistriesDatapackGenerator} which properly handles
 * referencing existing dynamic registry objects within another dynamic registry
 * object.
 */
public class DatapackBuiltinEntriesProvider extends RegistriesDatapackGenerator {
	private final CompletableFuture<HolderLookup.Provider> fullRegistries;
	private final Predicate<String> namespacePredicate;
	private final Map<ResourceKey<?>, List<ICondition>> conditions;

	/**
	 * Constructs a new datapack provider which generates all registry objects
	 * from the provided mods using the holder.
	 *
	 * @param output     the target directory of the data generator
	 * @param registries a future of a lookup for registries and their objects
	 * @param modIds     a set of mod ids to generate the dynamic registry objects of
	 */
	public DatapackBuiltinEntriesProvider(FabricDataOutput output, CompletableFuture<RegistrySetBuilder.PatchedRegistries> registries, Set<String> modIds) {
		this(output, registries, (b) -> {
		}, modIds);
	}

	/**
	 * Constructs a new datapack provider which generates all registry objects
	 * from the provided mods using the holder.
	 *
	 * @param output            the target directory of the data generator
	 * @param registries        a future of a lookup for registries and their objects
	 * @param modIds            a set of mod ids to generate the dynamic registry objects of
	 * @param conditionsBuilder a builder for conditions to append to registry objects
	 */
	public DatapackBuiltinEntriesProvider(FabricDataOutput output, CompletableFuture<RegistrySetBuilder.PatchedRegistries> registries, Consumer<BiConsumer<ResourceKey<?>, ICondition>> conditionsBuilder, Set<String> modIds) {
		this(output, registries, buildConditionsMap(conditionsBuilder), modIds);
	}

	/**
	 * Constructs a new datapack provider which generates all registry objects
	 * from the provided mods using the holder.
	 *
	 * @param output     the target directory of the data generator
	 * @param registries a future of a lookup for registries and their objects
	 * @param modIds     a set of mod ids to generate the dynamic registry objects of
	 * @param conditions a map containing conditions to append to registry objects
	 */
	public DatapackBuiltinEntriesProvider(FabricDataOutput output, CompletableFuture<RegistrySetBuilder.PatchedRegistries> registries, Map<ResourceKey<?>, List<ICondition>> conditions, Set<String> modIds) {
		super(output, registries.thenApply(RegistrySetBuilder.PatchedRegistries::patches));
		this.namespacePredicate = modIds == null ? namespace -> true : modIds::contains;
		this.conditions = conditions;
		this.fullRegistries = registries.thenApply(RegistrySetBuilder.PatchedRegistries::full);
	}

	/**
	 * Constructs a new datapack provider which generates all registry objects
	 * from the provided mods using the holder. All entries that need to be
	 * bootstrapped are provided within the {@link RegistrySetBuilder}.
	 *
	 * @param output                 the target directory of the data generator
	 * @param registries             a future of a lookup for registries and their objects
	 * @param datapackEntriesBuilder a builder containing the dynamic registry objects added by this provider
	 * @param modIds                 a set of mod ids to generate the dynamic registry objects of
	 */
	public DatapackBuiltinEntriesProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries, RegistrySetBuilder datapackEntriesBuilder, Set<String> modIds) {
		this(output, RegistryPatchGenerator.createLookup(registries, datapackEntriesBuilder), modIds);
	}

	/**
	 * Constructs a new datapack provider which generates all registry objects
	 * from the provided mods using the holder. All entries that need to be
	 * bootstrapped are provided within the {@link RegistrySetBuilder}.
	 *
	 * @param output                 the target directory of the data generator
	 * @param registries             a future of a lookup for registries and their objects
	 * @param datapackEntriesBuilder a builder containing the dynamic registry objects added by this provider
	 * @param conditions             a map containing conditions to append to registry objects
	 * @param modIds                 a set of mod ids to generate the dynamic registry objects of
	 */
	public DatapackBuiltinEntriesProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries, RegistrySetBuilder datapackEntriesBuilder, Map<ResourceKey<?>, List<ICondition>> conditions, Set<String> modIds) {
		this(output, RegistryPatchGenerator.createLookup(registries, datapackEntriesBuilder), conditions, modIds);
	}

	/**
	 * Constructs a new datapack provider which generates all registry objects
	 * from the provided mods using the holder. All entries that need to be
	 * bootstrapped are provided within the {@link RegistrySetBuilder}.
	 *
	 * @param output                 the target directory of the data generator
	 * @param registries             a future of a lookup for registries and their objects
	 * @param datapackEntriesBuilder a builder containing the dynamic registry objects added by this provider
	 * @param conditionsBuilder      a builder for conditions to append to registry objects
	 * @param modIds                 a set of mod ids to generate the dynamic registry objects of
	 */
	public DatapackBuiltinEntriesProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries, RegistrySetBuilder datapackEntriesBuilder, Consumer<BiConsumer<ResourceKey<?>, ICondition>> conditionsBuilder, Set<String> modIds) {
		this(output, RegistryPatchGenerator.createLookup(registries, datapackEntriesBuilder), conditionsBuilder, modIds);
	}

	/**
	 * Get the registry holder lookup provider that includes elements added from the {@link RegistrySetBuilder}
	 */
	public CompletableFuture<HolderLookup.Provider> getRegistryProvider() {
		return fullRegistries;
	}

	private static Map<ResourceKey<?>, List<ICondition>> buildConditionsMap(Consumer<BiConsumer<ResourceKey<?>, ICondition>> conditionBuilder) {
		Map<ResourceKey<?>, List<ICondition>> conditions = new IdentityHashMap<>();
		conditionBuilder.accept((key, condition) -> conditions.computeIfAbsent(key, k -> new ArrayList<>()).add(condition));
		return conditions;
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		return this.registries
				.thenCompose(
						provider -> {
							DynamicOps<JsonElement> dynamicops = provider.createSerializationContext(JsonOps.INSTANCE);
							return CompletableFuture.allOf(
									getDataPackRegistriesWithDimensions()
											.flatMap(
													data -> dumpRegistryCap(output, provider, dynamicops, (RegistryDataLoader.RegistryData<?>) data).stream()
											)
											.toArray(CompletableFuture[]::new)
							);
						}
				);
	}

	public PackOutput.PathProvider createRegistryElementsPathProvider(ResourceKey<? extends Registry<?>> registryKey) {
		return this.output.createPathProvider(PackOutput.Target.DATA_PACK, PortingLibDataHack.prefixNamespace(registryKey.location()));
	}

	private <T> Optional<CompletableFuture<?>> dumpRegistryCap(
			CachedOutput output, HolderLookup.Provider provider, DynamicOps<JsonElement> dynamicOps, RegistryDataLoader.RegistryData<T> data
	) {
		ResourceKey<? extends Registry<T>> resourcekey = data.key();
		var conditionalCodec = ConditionalOps.createConditionalCodecWithConditions(data.elementCodec());
		return provider.lookup(resourcekey)
				.map(
						registryLookup -> {
							PackOutput.PathProvider packoutput$pathprovider = createRegistryElementsPathProvider(resourcekey);
							return CompletableFuture.allOf(
									registryLookup.listElements()
											.filter(holder -> this.namespacePredicate.test(holder.key().location().getNamespace()))
											.map(
													ref -> dumpValueF(
															packoutput$pathprovider.json(ref.key().location()),
															output,
															dynamicOps,
															conditionalCodec,
															Optional.of(new WithConditions<>(conditions.getOrDefault(ref.key(), List.of()), ref.value()))
													)
											)
											.toArray(CompletableFuture[]::new)
							);
						}
				);
	}

	private static <E> CompletableFuture<?> dumpValueF(
			Path path, CachedOutput output, DynamicOps<JsonElement> dynamicOps, Encoder<Optional<WithConditions<E>>> encoder, Optional<WithConditions<E>> conditions
	) {
		return encoder.encodeStart(dynamicOps, conditions)
				.mapOrElse(
						json -> DataProvider.saveStable(output, json, path),
						p_351701_ -> CompletableFuture.failedFuture(new IllegalStateException("Couldn't generate file '" + path + "': " + p_351701_.message()))
				);
	}

	public static Stream<RegistryDataLoader.RegistryData<?>> getDataPackRegistriesWithDimensions() {
		return Stream.concat(DynamicRegistries.getDynamicRegistries().stream(), RegistryDataLoader.DIMENSION_REGISTRIES.stream());
	}
}
