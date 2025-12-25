package io.github.fabricators_of_create.porting_lib.item.itemgroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.github.fabricators_of_create.porting_lib.item.extensions.CreativeModeTabExt;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import org.jspecify.annotations.Nullable;

public class PortingLibCreativeTab {
	private static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace("container/creative_inventory/scroller");
	private static final Identifier SCROLLER_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/creative_inventory/scroller_disabled");

	public static PortingLibCreativeTabBuilder builder() {
		return new PortingLibCreativeTabBuilder();
	}

	public static class PortingLibCreativeTabBuilder extends CreativeModeTab.Builder {
		private static final net.minecraft.resources.Identifier CREATIVE_INVENTORY_TABS_IMAGE = net.minecraft.resources.Identifier.withDefaultNamespace("textures/gui/container/creative_inventory/tabs.png");
		private static final net.minecraft.resources.Identifier CREATIVE_ITEM_SEARCH_BACKGROUND = CreativeModeTab.createTextureLocation("item_search");

		private @Nullable Identifier spriteScrollerLocation;
		private boolean hasSearchBar = false;
		private int searchBarWidth = 89;
		private Identifier tabsImage = CREATIVE_INVENTORY_TABS_IMAGE;
		private int labelColor = -12566464;
		private int slotColor = -2130706433;
		private Function<PortingLibCreativeTabBuilder, CreativeModeTab> tabFactory = null;
		private final List<Identifier> tabsBefore = new ArrayList<>();
		private final List<Identifier> tabsAfter = new ArrayList<>();

		public PortingLibCreativeTabBuilder() {
			// Set when building.
			super(null, -1);
		}

		@Override
		public PortingLibCreativeTabBuilder title(Component title) {
			super.title(title);
			return this;
		}

		@Override
		public PortingLibCreativeTabBuilder icon(Supplier<ItemStack> icon) {
			super.icon(icon);
			return this;
		}

		@Override
		public PortingLibCreativeTabBuilder displayItems(CreativeModeTab.DisplayItemsGenerator displayItemsGenerator) {
			super.displayItems(displayItemsGenerator);
			return this;
		}

		@Override
		public PortingLibCreativeTabBuilder alignedRight() {
			super.alignedRight();
			return this;
		}

		@Override
		public PortingLibCreativeTabBuilder hideTitle() {
			super.hideTitle();
			return this;
		}

		@Override
		public PortingLibCreativeTabBuilder noScrollBar() {
			super.noScrollBar();
			return this;
		}

		@Override
		public PortingLibCreativeTabBuilder backgroundTexture(Identifier identifier) {
			super.backgroundTexture(identifier);
			return this;
		}

		@Override
		public CreativeModeTab build() {
			CreativeModeTab tab;
			if (tabFactory != null)
				tab = tabFactory.apply(this);
			else
				tab = super.build();
			((CreativeModeTabExt) tab).setPortingData(new TabData(hasSearchBar, searchBarWidth, tabsImage, labelColor, slotColor, spriteScrollerLocation, tabsBefore, tabsAfter));
			return tab;
		}

		@Override
		protected PortingLibCreativeTabBuilder type(CreativeModeTab.Type type) {
			super.type(type);
			if (type == CreativeModeTab.Type.SEARCH)
				return this.withSearchBar();
			return this;
		}

		/**
		 * Gives this tab a search bar.
		 * <p>Note that, if using a custom {@link #backgroundTexture(net.minecraft.resources.Identifier) background image}, you will need to make sure that your image contains the input box and the scroll bar.</p>
		 */
		public PortingLibCreativeTabBuilder withSearchBar() {
			this.hasSearchBar = true;
			if (this.backgroundTexture == CreativeModeTab.DEFAULT_BACKGROUND)
				return this.backgroundTexture(CREATIVE_ITEM_SEARCH_BACKGROUND);
			return this;
		}

		/**
		 * Gives this tab a search bar, with a specific width.
		 * @param searchBarWidth the width of the search bar
		 */
		public PortingLibCreativeTabBuilder withSearchBar(int searchBarWidth) {
			this.searchBarWidth = searchBarWidth;
			return withSearchBar();
		}

		/**
		 * Sets the location of the scroll bar background.
		 */
		public PortingLibCreativeTabBuilder withScrollBarSpriteLocation(Identifier scrollBarSpriteLocation) {
			this.spriteScrollerLocation = scrollBarSpriteLocation;
			return this;
		}

		/**
		 * Sets the image of the tab to a custom resource location, instead of an item's texture.
		 */
		@Deprecated(forRemoval = true) // Currently does nothing for neo and fabric
		public PortingLibCreativeTabBuilder withTabsImage(Identifier tabsImage) {
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

		public PortingLibCreativeTabBuilder withTabFactory(Function<PortingLibCreativeTabBuilder, CreativeModeTab> tabFactory) {
			this.tabFactory = tabFactory;
			return this;
		}

		/** Define tabs that should come <i>before</i> this tab. This tab will be placed <strong>after</strong> the {@code tabs}. **/
		public PortingLibCreativeTabBuilder withTabsBefore(Identifier... tabs) {
			this.tabsBefore.addAll(java.util.List.of(tabs));
			return this;
		}

		/** Define tabs that should come <i>after</i> this tab. This tab will be placed <strong>before</strong> the {@code tabs}.**/
		public PortingLibCreativeTabBuilder withTabsAfter(Identifier... tabs) {
			this.tabsAfter.addAll(java.util.List.of(tabs));
			return this;
		}

		/** Define tabs that should come <i>before</i> this tab. This tab will be placed <strong>after</strong> the {@code tabs}. **/
		@SafeVarargs
		public final PortingLibCreativeTabBuilder withTabsBefore(ResourceKey<CreativeModeTab>... tabs) {
			Stream.of(tabs).map(ResourceKey::identifier).forEach(this.tabsBefore::add);
			return this;
		}

		/** Define tabs that should come <i>after</i> this tab. This tab will be placed <strong>before</strong> the {@code tabs}.**/
		@SafeVarargs
		public final PortingLibCreativeTabBuilder withTabsAfter(ResourceKey<CreativeModeTab>... tabs) {
			java.util.stream.Stream.of(tabs).map(ResourceKey::identifier).forEach(this.tabsAfter::add);
			return this;
		}

		/**
		 * Helper to set this tabs contents to everything in the supplied Collection of Holders.
		 * Intended for use with {@link io.github.fabricators_of_create.porting_lib.registry.DeferredRegister#getEntries()}, for Item or Block DeferredRegisters.
		 * Entries added through this method are filtered out if {@link ItemLike#asItem()} returns {@link Items.AIR} or if disabled via {@link Item#isEnabled(FeatureFlagSet)}.
		 * Note that like the vanilla method this overrides any other calls for this tab to {@link #displayItems}.
		 */
		public PortingLibCreativeTabBuilder displayItems(Collection<? extends Holder<? extends ItemLike>> collection) {
			return this.displayItems((p, o) -> collection.stream()
					.map(net.minecraft.core.Holder::value)
					.map(ItemLike::asItem)
					.filter(i -> i != Items.AIR)
					.filter(i -> i.isEnabled(p.enabledFeatures()))
					.forEach(o::accept));
		}
	}

	public record TabData(boolean hasSearchBar, int searchBarWidth, Identifier tabsImage, int labelColor, int slotColor, Identifier scrollerSprite, List<Identifier> tabsBefore, List<Identifier> tabsAfter) {}
}
