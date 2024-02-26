package io.github.fabricators_of_create.porting_lib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class PortingLibItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {
	/** A function that resolves block tag builders. */
	private final CompletableFuture<TagLookup<Block>> blockTags;
	private final Map<TagKey<Block>, TagKey<Item>> tagsToCopy = new HashMap<>();

	public PortingLibItemTagsProvider(PackOutput p_275343_, CompletableFuture<HolderLookup.Provider> p_275729_, CompletableFuture<TagsProvider.TagLookup<Block>> p_275322_, String modId) {
		super(p_275343_, Registries.ITEM, p_275729_, (p_255790_) -> {
			return p_255790_.builtInRegistryHolder().key();
		});
		this.blockTags = p_275322_;
	}

	/**
	 * Copies the entries from a block tag into an item tag.
	 */
	protected void copy(TagKey<Block> pBlockTag, TagKey<Item> pItemTag) {
		this.tagsToCopy.put(pBlockTag, pItemTag);
	}

	protected CompletableFuture<HolderLookup.Provider> createContentsProvider() {
		return super.createContentsProvider().thenCombineAsync(this.blockTags, (p_274766_, p_274767_) -> {
			this.tagsToCopy.forEach((p_274763_, p_274764_) -> {
				TagBuilder tagbuilder = this.getOrCreateRawBuilder(p_274764_);
				Optional<TagBuilder> optional = p_274767_.apply(p_274763_);
				optional.orElseThrow(() -> {
					return new IllegalStateException("Missing block tag " + p_274764_.location());
				}).build().forEach(tagbuilder::add);
			});
			return p_274766_;
		});
	}
}
