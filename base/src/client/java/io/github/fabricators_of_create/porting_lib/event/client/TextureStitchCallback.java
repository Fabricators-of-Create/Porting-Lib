package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public interface TextureStitchCallback {
  Event<Pre> PRE = EventFactory.createArrayBacked(Pre.class, callbacks -> (atlas, spriteAdder) -> {
    for(Pre e : callbacks)
      e.stitch(atlas, spriteAdder);
  });

  Event<Post> POST = EventFactory.createArrayBacked(Post.class, callbacks -> atlas -> {
    for(Post e : callbacks)
      e.stitch(atlas);
  });

  @Environment(EnvType.CLIENT)
  interface Pre {
    void stitch(TextureAtlas atlas, Consumer<ResourceLocation> spriteAdder);
  }

  @Environment(EnvType.CLIENT)
  interface Post {
    void stitch(TextureAtlas atlas);
  }
}
