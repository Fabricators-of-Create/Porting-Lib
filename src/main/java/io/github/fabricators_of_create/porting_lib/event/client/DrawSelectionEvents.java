package io.github.fabricators_of_create.porting_lib.event.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.HitResult;

public interface DrawSelectionEvents {
  Event<Block> BLOCK = EventFactory.createArrayBacked(Block.class, callbacks -> (context, info, target, partialTicks, matrix, buffers) -> {
    for(Block e : callbacks)
      if(e.onHighlightBlock(context, info, target, partialTicks, matrix, buffers))
        return true;
    return false;
  });

  Event<Entity> ENTITY = EventFactory.createArrayBacked(Entity.class, callbacks -> (context, info, target, partialTicks, matrix, buffers) -> {
    for(Entity e : callbacks)
      if(e.onHighlightEntity(context, info, target, partialTicks, matrix, buffers))
        return true;
    return false;
  });

  @FunctionalInterface
  interface Block {
    boolean onHighlightBlock(LevelRenderer context, Camera info, HitResult target, float partialTicks, PoseStack matrix, MultiBufferSource buffers);
  }

  @FunctionalInterface
  interface Entity {
    boolean onHighlightEntity(LevelRenderer context, Camera info, HitResult target, float partialTicks, PoseStack matrix, MultiBufferSource buffers);
  }
}
