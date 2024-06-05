package io.github.fabricators_of_create.porting_lib.gametest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import io.github.fabricators_of_create.porting_lib.gametest.infrastructure.CustomGameTestHelper;
import io.github.fabricators_of_create.porting_lib.gametest.infrastructure.ExtendedTestFunction;
import io.github.fabricators_of_create.porting_lib.gametest.infrastructure.GameTestGroup;
import io.github.fabricators_of_create.porting_lib.gametest.quickexport.AreaSelectorItem;
import io.github.fabricators_of_create.porting_lib.gametest.quickexport.QuickExportCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class PortingLibGameTest implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Porting Lib GameTest");
	public static final boolean AREA_SELECTOR_ENABLED = checkEnabled();
	public static final Item AREA_SELECTOR = AREA_SELECTOR_ENABLED ? new AreaSelectorItem(new Item.Properties()) : null;

	@Override
	public void onInitialize() {
		if (AREA_SELECTOR_ENABLED) {
			Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("porting_lib", "area_selector"), AREA_SELECTOR);
			QuickExportCommand.register();
		} else {
			LOGGER.info("Porting Lib GameTest: Area Selector and quickexport disabled.");
		}
	}

	/**
	 * Get all test functions from the given classes. This enables the functionality
	 * of {@link CustomGameTestHelper} and {@link GameTestGroup}.
	 */
	public static Collection<TestFunction> getTestsFrom(Class<?>... classes) {
		return Stream.of(classes)
				.map(Class::getDeclaredMethods)
				.flatMap(Stream::of)
				.map(ExtendedTestFunction::of)
				.filter(Objects::nonNull)
				.sorted(Comparator.comparing(TestFunction::testName))
				.toList();
	}

	private static boolean checkEnabled() {
		Path config = FabricLoader.getInstance().getConfigDir().resolve("porting_lib").resolve("gametest.json");
		if (!Files.exists(config))
			return true;
		try {
			JsonElement element = JsonParser.parseString(Files.readString(config));
			if (!(element instanceof JsonObject json))
				throw new JsonParseException("Config is not a JSON object");
			JsonElement disableElement = json.get("disable_area_selector");
			if (disableElement == null)
				return true;
			if (!(disableElement instanceof JsonPrimitive disabled) || !disabled.isBoolean())
				throw new JsonParseException("Element 'disable_area_selector' must be a boolean");
			return !disabled.getAsBoolean();
		} catch (IOException | JsonParseException e) {
			throw new RuntimeException("Porting Lib GameTest: Failed to read config", e);
		}
	}
}
