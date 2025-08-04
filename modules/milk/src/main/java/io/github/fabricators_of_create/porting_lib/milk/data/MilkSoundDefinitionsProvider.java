package io.github.fabricators_of_create.porting_lib.milk.data;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import io.github.fabricators_of_create.porting_lib.data.SoundDefinitionsProvider;
import io.github.fabricators_of_create.porting_lib.milk.PortingLibMilk;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

public class MilkSoundDefinitionsProvider extends SoundDefinitionsProvider {
	protected MilkSoundDefinitionsProvider(FabricDataOutput output, ExistingFileHelper helper) {
		super(output, helper);
	}

	@Override
	public void registerSounds() {
		this.add(PortingLibMilk.BUCKET_EMPTY_MILK.unwrapKey().orElseThrow().location(), definition().subtitle("subtitles.item.bucket.empty")
				.with(sound("item/bucket/empty1"), sound("item/bucket/empty1").pitch(0.9),
						sound("item/bucket/empty2"), sound("item/bucket/empty3")));
		this.add(PortingLibMilk.BUCKET_FILL_MILK.unwrapKey().orElseThrow().location(), definition().subtitle("subtitles.item.bucket.fill")
				.with(sound("item/bucket/fill1"), sound("item/bucket/fill2"), sound("item/bucket/fill3")));
	}
}
