package io.github.fabricators_of_create.porting_lib.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("deprecation")
public abstract class LanguageProvider implements DataProvider {
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().setLenient().create();
	private final Map<String, String> data = new TreeMap<>();
	private final PackOutput output;
	private final String modid;
	private final String locale;

	public LanguageProvider(PackOutput output, String modid, String locale) {
		this.output = output;
		this.modid = modid;
		this.locale = locale;
	}

	protected abstract void addTranslations();

	@Override
	public void run(CachedOutput cache) throws IOException {
		addTranslations();
		if (!data.isEmpty())
			save(cache, data, this.output.getOutputFolder().resolve("assets/" + modid + "/lang/" + locale + ".json"));
	}

	@Override
	public String getName() {
		return "Languages: " + locale;
	}

	private void save(CachedOutput cache, Object object, Path target) throws IOException {
		// TODO: DataProvider.saveStable handles the caching and hashing already, but creating the JSON Object this way seems unreliable. -C
		JsonObject json = new JsonObject();
		for (Map.Entry<String, String> pair : data.entrySet()) {
			json.addProperty(pair.getKey(), pair.getValue());
		}

		DataProvider.saveStable(cache, json, target);
	}

	public void addBlock(Supplier<? extends Block> key, String name) {
		add(key.get(), name);
	}

	public void add(Block key, String name) {
		add(key.getDescriptionId(), name);
	}

	public void addItem(Supplier<? extends Item> key, String name) {
		add(key.get(), name);
	}

	public void add(Item key, String name) {
		add(key.getDescriptionId(), name);
	}

	public void addItemStack(Supplier<ItemStack> key, String name) {
		add(key.get(), name);
	}

	public void add(ItemStack key, String name) {
		add(key.getDescriptionId(), name);
	}

	public void addEnchantment(Supplier<? extends Enchantment> key, String name) {
		add(key.get(), name);
	}

	public void add(Enchantment key, String name) {
		add(key.getDescriptionId(), name);
	}

    /*
    public void addBiome(Supplier<? extends Biome> key, String name) {
        add(key.get(), name);
    }

    public void add(Biome key, String name) {
        add(key.getTranslationKey(), name);
    }
    */

	public void addEffect(Supplier<? extends MobEffect> key, String name) {
		add(key.get(), name);
	}

	public void add(MobEffect key, String name) {
		add(key.getDescriptionId(), name);
	}

	public void addEntityType(Supplier<? extends EntityType<?>> key, String name) {
		add(key.get(), name);
	}

	public void add(EntityType<?> key, String name) {
		add(key.getDescriptionId(), name);
	}

	public void add(String key, String value) {
		if (data.put(key, value) != null)
			throw new IllegalStateException("Duplicate translation key " + key);
	}
}
