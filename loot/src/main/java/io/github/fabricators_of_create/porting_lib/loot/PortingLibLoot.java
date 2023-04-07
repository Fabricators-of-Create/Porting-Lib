package io.github.fabricators_of_create.porting_lib.loot;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public class PortingLibLoot implements ModInitializer {
	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(LootModifierManager.INSTANCE);
		Registry.register(
				Registry.LOOT_CONDITION_TYPE,
				new ResourceLocation("forge:loot_table_id"),
				LootTableIdCondition.LOOT_TABLE_ID
		);
	}
}
