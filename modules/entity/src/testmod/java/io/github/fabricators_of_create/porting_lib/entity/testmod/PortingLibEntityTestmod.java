package io.github.fabricators_of_create.porting_lib.entity.testmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Monster;

public class PortingLibEntityTestmod implements ModInitializer {

	public static final EntityType<CustomSlime> CUSTOM_SLIME = FabricEntityTypeBuilder
			.create(MobCategory.MONSTER, CustomSlime::new)
			.dimensions(EntityDimensions.scalable(2.04f, 2.04f))
			.trackRangeChunks(10)
			.build();

	@Override
	public void onInitialize() {
		Registry.register(
				BuiltInRegistries.ENTITY_TYPE,
				ResourceLocation.fromNamespaceAndPath("porting_lib", "custom_slime"),
				CUSTOM_SLIME
		);
		FabricDefaultAttributeRegistry.register(CUSTOM_SLIME, Monster.createMonsterAttributes());
	}
}
