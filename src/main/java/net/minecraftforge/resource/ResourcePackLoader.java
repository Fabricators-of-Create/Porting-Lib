package net.minecraftforge.resource;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Pair;

import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.PackRepositoryAccessor;
import io.github.fabricators_of_create.porting_lib.util.PathResourcePack;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class ResourcePackLoader {
	private static Map<ModContainer, PathResourcePack> modResourcePacks;

	public static Optional<PathResourcePack> getPackFor(String modId) {
		return Optional.ofNullable(FabricLoader.getInstance().getModContainer(modId).orElse(null)).
				map(mf->modResourcePacks.get(mf));
	}

	@Deprecated
	public static void loadResourcePacks(PackRepository resourcePacks, BiFunction<Map<ModContainer, ? extends PathResourcePack>, BiConsumer<? super PathResourcePack, Pack>, ? extends RepositorySource> packFinder) {
		loadResourcePacks(resourcePacks, (map) -> packFinder.apply(map, (rp,p) -> {}));
	}

	public static void loadResourcePacks(PackRepository resourcePacks, Function<Map<ModContainer, ? extends PathResourcePack>, ? extends RepositorySource> packFinder) {
		modResourcePacks = FabricLoader.getInstance().getAllMods().stream()
				.map(mf -> Pair.of(mf, createPackForMod(mf)))
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond, (u, v) -> { throw new IllegalStateException(String.format(Locale.ENGLISH, "Duplicate key %s", u)); },  LinkedHashMap::new));
		((PackRepositoryAccessor)resourcePacks).getSources().add(packFinder.apply(modResourcePacks));
	}

	@SuppressWarnings("deprecation")
	@NotNull
	public static PathResourcePack createPackForMod(ModContainer mf)
	{
		return new PathResourcePack(mf.getMetadata().getName(), mf.getRootPath()){
			@Nonnull
			@Override
			protected Path resolve(@Nonnull String... paths)
			{
				if (paths.length < 1) {
					throw new IllegalArgumentException("Missing path");
				}
				String path = String.join("/", paths);
				for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
					if (modContainer.findPath(path).isPresent())
						return modContainer.findPath(path).get();
				}
				return mf.findPath(path).orElseThrow();
			}
		};
	}

	public static List<String> getPackNames() {
		return FabricLoader.getInstance().getAllMods().stream().map(mf->"mod:"+mf.getMetadata().getId()).filter(n->!n.equals("mod:minecraft")).collect(Collectors.toList());
	}

	public static <V> Comparator<Map.Entry<String,V>> getSorter() {
		List<String> order = new ArrayList<>();
		order.add("vanilla");
		order.add("mod_resources");

		FabricLoader.getInstance().getAllMods().stream()
				.map(e -> e.getMetadata().getId())
				.map(e -> "mod:" + e)
				.forEach(order::add);

		final Object2IntMap<String> order_f = new Object2IntOpenHashMap<>(order.size());
		for (int x = 0; x < order.size(); x++)
			order_f.put(order.get(x), x);

		return (e1, e2) -> {
			final String s1 = e1.getKey();
			final String s2 = e2.getKey();
			final int i1 = order_f.getOrDefault(s1, -1);
			final int i2 = order_f.getOrDefault(s2, -1);

			if (i1 == i2 && i1 == -1)
				return s1.compareTo(s2);
			if (i1 == -1) return 1;
			if (i2 == -1) return -1;
			return i2 - i1;
		};
	}

}
