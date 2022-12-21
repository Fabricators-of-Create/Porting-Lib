package net.minecraftforge.resource;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public class ResourcePackLoader {
	private static Map<ModContainer, PathPackResources> modResourcePacks;

	public static Optional<PathPackResources> getPackFor(String modId) {
		return Optional.ofNullable(FabricLoader.getInstance().getModContainer(modId).orElse(null)).
				map(mf->modResourcePacks.get(mf));
	}

	@SuppressWarnings("deprecation")
	@NotNull
	public static PathPackResources createPackForMod(ModContainer mf) {
		return new PathPackResources(mf.getMetadata().getName(), false, mf.getRootPath()){
			@Nonnull
			@Override
			protected Path resolve(@Nonnull String... paths) {
				if (paths.length < 1) {
					throw new IllegalArgumentException("Missing path");
				}
				String path = String.join("/", paths);
				for (ModContainer modContainer : FabricLoader.getInstance().getAllMods()) {
					if (modContainer.findPath(path).isPresent())
						return modContainer.findPath(path).get();
				}

				return mf.findPath(path).orElse(new RefPath(mf.getRootPath().getFileSystem(), makeKey(mf.getRootPath().getRoot()), path));
			}
		};
	}

	private static int index = 0;

	private static synchronized String makeKey(Path path) {
		var key= path.toAbsolutePath().normalize().toUri().getPath();
		return key.replace('!', '_') + "#" + index++;
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
