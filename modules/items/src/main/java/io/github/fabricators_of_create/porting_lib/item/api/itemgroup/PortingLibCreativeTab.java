package io.github.fabricators_of_create.porting_lib.item.api.itemgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import io.github.fabricators_of_create.porting_lib.item.api.extensions.CreativeModeTabExt;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

public class PortingLibCreativeTab {
	public static PortingLibCreativeTabBuilder builder() {
		return new PortingLibCreativeTabBuilder();
	}

	public static class PortingLibCreativeTabBuilder extends CreativeModeTab.Builder {
		public static final ResourceLocation CREATIVE_TABS_LOCATION = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");

		private boolean hasDisplayName = false;
		private ResourceLocation tabsImage = CREATIVE_TABS_LOCATION;
		private int labelColor = 4210752;
		private int slotColor = -2130706433;
		private final List<ResourceLocation> tabsBefore = new ArrayList<>();
		private final List<ResourceLocation> tabsAfter = new ArrayList<>();

		public PortingLibCreativeTabBuilder() {
			// Set when building.
			super(null, -1);
		}

		@Override
		public PortingLibCreativeTabBuilder title(Component displayName) {
			hasDisplayName = true;
			return (PortingLibCreativeTabBuilder) super.title(displayName);
		}

		@Override
		public CreativeModeTab build() {
			if (!hasDisplayName) {
				throw new IllegalStateException("No display name set for ItemGroup");
			}

			CreativeModeTab tab = super.build();
			((CreativeModeTabExt) tab).setPortingData(new TabData(tabsImage, labelColor, slotColor, tabsBefore, tabsAfter));
			return tab;
		}

		/**
		 * Sets the image of the tab to a custom resource location, instead of an item's texture.
		 */
		public PortingLibCreativeTabBuilder withTabsImage(ResourceLocation tabsImage) {
			this.tabsImage = tabsImage;
			return this;
		}

		/**
		 * Sets the color of the tab label.
		 */
		public PortingLibCreativeTabBuilder withLabelColor(int labelColor) {
			this.labelColor = labelColor;
			return this;
		}

		/**
		 * Sets the color of tab's slots.
		 */
		public PortingLibCreativeTabBuilder withSlotColor(int slotColor) {
			this.slotColor = slotColor;
			return this;
		}

		/** Define tabs that should come <i>before</i> this tab. This tab will be placed <strong>after</strong> the {@code tabs}. **/
		public PortingLibCreativeTabBuilder withTabsBefore(ResourceLocation... tabs) {
			this.tabsBefore.addAll(List.of(tabs));
			return this;
		}

		/** Define tabs that should come <i>after</i> this tab. This tab will be placed <strong>before</strong> the {@code tabs}.**/
		public PortingLibCreativeTabBuilder withTabsAfter(ResourceLocation... tabs) {
			this.tabsAfter.addAll(List.of(tabs));
			return this;
		}

		/** Define tabs that should come <i>before</i> this tab. This tab will be placed <strong>after</strong> the {@code tabs}. **/
		@SafeVarargs
		public final PortingLibCreativeTabBuilder withTabsBefore(ResourceKey<CreativeModeTab>... tabs) {
			Stream.of(tabs).map(ResourceKey::location).forEach(this.tabsBefore::add);
			return this;
		}

		/** Define tabs that should come <i>after</i> this tab. This tab will be placed <strong>before</strong> the {@code tabs}.**/
		@SafeVarargs
		public final PortingLibCreativeTabBuilder withTabsAfter(ResourceKey<CreativeModeTab>... tabs) {
			Stream.of(tabs).map(ResourceKey::location).forEach(this.tabsAfter::add);
			return this;
		}
	}

	public record TabData(ResourceLocation tabsImage, int labelColor, int slotColor, List<ResourceLocation> tabsBefore, List<ResourceLocation> tabsAfter) {}
}
