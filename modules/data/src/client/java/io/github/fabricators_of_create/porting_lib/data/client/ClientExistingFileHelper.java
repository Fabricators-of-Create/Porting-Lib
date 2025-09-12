package io.github.fabricators_of_create.porting_lib.data.client;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;

import io.github.fabricators_of_create.porting_lib.data.client.injects.MinecraftInjection;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;

import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.client.resources.IndexedAssetSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ClientExistingFileHelper extends ExistingFileHelper {
	private final MultiPackResourceManager clientResources;

	/**
	 * Create a helper with existing resources provided from a JVM argument.
	 * To use, a JVM argument mapping {@link ExistingFileHelper#EXISTING_RESOURCES the key}
	 * to the desired resource directory is required.
	 */
	public static ExistingFileHelper withResourcesFromArg() {
		String property = System.getProperty(EXISTING_RESOURCES);
		if (property == null)
			throw new IllegalArgumentException("Existing resources not specified with '" + EXISTING_RESOURCES + "' argument");
		Path path = Paths.get(property);
		if (!Files.isDirectory(path))
			throw new IllegalStateException("Path " + property + " is not a directory or does not exist");
		String mods = System.getProperty(EXISTING_MODS);
		if (mods == null)
			mods = "";
		return withResources(new HashSet<>(List.of(mods.split(","))), path);
	}

	/**
	 * Create a helper with the provided paths being used for resources.
	 */
	public static ExistingFileHelper withResources(Path... paths) {
		GameConfig gameConfig = ((MinecraftInjection) Minecraft.getInstance()).port_lib$getGameConfig();
		List<Path> resources = List.of(paths);
		return new ExistingFileHelper(resources, Set.of(), true, gameConfig.location.assetIndex, gameConfig.location.assetDirectory);
	}

	/**
	 * Create a helper with the provided paths being used for resources.
	 */
	public static ExistingFileHelper withResources(Set<String> mods, Path... paths) {
		GameConfig gameConfig = ((MinecraftInjection) Minecraft.getInstance()).port_lib$getGameConfig();
		List<Path> resources = List.of(paths);
		return new ExistingFileHelper(resources, mods, true, gameConfig.location.assetIndex, gameConfig.location.assetDirectory);
	}

	/**
	 * Create a new helper. This should probably <em>NOT</em> be used by mods, as
	 * the instance provided by forge is designed to be a central instance that
	 * tracks existence of generated data.
	 * <p>
	 * Only create a new helper if you intentionally want to ignore the existence of
	 * other generated files.
	 *
	 * @param existingPacks a collection of paths to existing packs
	 * @param existingMods  a set of mod IDs for existing mods
	 * @param enable        {@code true} if validation is enabled
	 * @param assetIndex    the identifier for the asset index, generally Minecraft's current major version
	 * @param assetsDir     the directory in which to find vanilla assets and indexes
	 */
	public ClientExistingFileHelper(Collection<Path> existingPacks, Set<String> existingMods, boolean enable, @Nullable String assetIndex, @Nullable File assetsDir) {
		super(existingPacks, existingMods, enable, assetIndex, assetsDir);
		List<PackResources> candidateClientResources = new ArrayList<>();

		if (assetIndex != null && assetsDir != null && assetsDir.exists()) {
			candidateClientResources.add(ClientPackSource.createVanillaPackSource(IndexedAssetSource.createIndexFs(assetsDir.toPath(), assetIndex)));
		}
		for (Path existing : existingPacks) {
			File file = existing.toFile();
			if (!file.exists())
				continue;
			PackResources pack = file.isDirectory() ? new PathPackResources(new PackLocationInfo(file.getName(), Component.empty(), PackSource.BUILT_IN, Optional.empty()), file.toPath()) : new FilePackResources(new PackLocationInfo(file.getName(), Component.empty(), PackSource.BUILT_IN, Optional.empty()), new FilePackResources.SharedZipFileAccess(file), "");
			candidateClientResources.add(pack);
		}
		for (String existingMod : existingMods) {
			Optional<ModContainer> modFileInfo = FabricLoader.getInstance().getModContainer(existingMod);
			modFileInfo.ifPresent(modContainer -> {
				// Only opens primary packs - overlays are not currently considered for datagen
				final String name = "mod/" + existingMod;
				candidateClientResources.add(createPackForMod(modContainer).openPrimary(new PackLocationInfo(name, Component.empty(), PackSource.BUILT_IN, Optional.empty())));
			});
		}

		this.clientResources = new MultiPackResourceManager(PackType.CLIENT_RESOURCES, candidateClientResources);
	}

	@Override
	protected ResourceManager getManager(PackType packType) {
		return packType == PackType.CLIENT_RESOURCES ? clientResources : super.getManager(packType);
	}
}
