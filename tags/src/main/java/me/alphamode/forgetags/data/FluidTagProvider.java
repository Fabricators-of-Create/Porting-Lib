package me.alphamode.forgetags.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

public class FluidTagProvider extends FabricTagProvider.FluidTagProvider {
    public FluidTagProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateTags() {
//        if(Registry.FLUID.get(new Identifier("c:milk")) != null) {
//            getOrCreateTagBuilder(Tags.Fluids.MILK).add(Registry.FLUID.get(new Identifier("c:milk")));
//        }
    }
}
