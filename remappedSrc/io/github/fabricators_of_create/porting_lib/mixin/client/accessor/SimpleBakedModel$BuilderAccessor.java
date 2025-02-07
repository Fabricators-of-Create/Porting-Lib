package io.github.fabricators_of_create.porting_lib.mixin.client.accessor;

import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BasicBakedModel.Builder.class)
public interface SimpleBakedModel$BuilderAccessor {
	@Invoker("<init>")
	static BasicBakedModel.Builder port_lib$create(boolean hasAmbientOcclusion, boolean usesBlockLight,
													boolean isGui3d, ModelTransformation transforms,
													ModelOverrideList overrides) {
		throw new RuntimeException("mixin failed!");
	}
}
