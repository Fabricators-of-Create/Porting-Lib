package me.alphamode.forgetags.data;

import me.alphamode.forgetags.Tags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemTagLangProvider extends FabricLanguageProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemTagLangProvider.class);
	public static final Map<TagKey<Item>, String> SPECIAL_CASES = Map.of();

	protected ItemTagLangProvider(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	public void generateTranslations(TranslationBuilder translationBuilder) {
		for (Field field : Tags.Items.class.getDeclaredFields()) {
			field.setAccessible(true);
			try {
				Object o = field.get(null);
				if (o instanceof TagKey<?> tag) {
					ResourceLocation id = tag.location();
					String path = id.getPath().replaceAll("/", ".");
					String key = "tag.%s.%s".formatted(id.getNamespace(), path);

					String english = SPECIAL_CASES.containsKey(tag)
							? SPECIAL_CASES.get(tag)
							: toEnglish(id.getPath());

					translationBuilder.add(key, english);
					LOGGER.info("Translated tag [{}] as [{}] with key [{}]", id, english, key);
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static String toEnglish(String path) {
		// crops/nether_wart
		String[] sections = path.split("/");
		// "crops", "nether_wart"
		StringBuilder result = new StringBuilder();
		for (int i = sections.length - 1; i >= 0; i--) {
			String section = sections[i];
			result.append(toSentence(section)).append(" ");
		}
		return result.toString().trim();
	}

	private static String toSentence(String path) {
		return Arrays.stream(path.split("_"))
				.map(ItemTagLangProvider::uppercase)
				.collect(Collectors.joining(" "));
	}

	private static String uppercase(String word) {
		char first = word.charAt(0);
		char upper = Character.toUpperCase(first);
		return upper + word.substring(1);
	}
}
