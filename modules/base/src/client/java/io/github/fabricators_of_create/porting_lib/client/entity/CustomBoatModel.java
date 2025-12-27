package io.github.fabricators_of_create.porting_lib.client.entity;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.model.ListModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

public interface CustomBoatModel {
	Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat);
}
