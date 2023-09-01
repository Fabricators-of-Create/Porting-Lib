package io.github.fabricators_of_create.porting_lib.client_events.event.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.texture.TextureAtlas;

public interface TextureStitchCallback {
  Event<Post> POST = EventFactory.createArrayBacked(Post.class, callbacks -> atlas -> {
    for(Post e : callbacks)
      e.stitch(atlas);
  });

  @Environment(EnvType.CLIENT)
  interface Post {
    void stitch(TextureAtlas atlas);
  }
}
