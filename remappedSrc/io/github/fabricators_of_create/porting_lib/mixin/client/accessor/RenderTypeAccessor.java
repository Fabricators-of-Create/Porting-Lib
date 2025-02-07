package io.github.fabricators_of_create.porting_lib.mixin.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;

@Environment(EnvType.CLIENT)
@Mixin(RenderLayer.class)
public interface RenderTypeAccessor {
	@Invoker("create")
	static RenderLayer.MultiPhase port_lib$create(String string, VertexFormat vertexFormat, VertexFormat.DrawMode mode, int i, boolean bl, boolean bl2, RenderLayer.MultiPhaseParameters compositeState) {
		throw new AssertionError("Mixin application failed!");
	}
}
