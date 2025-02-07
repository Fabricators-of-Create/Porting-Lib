package io.github.fabricators_of_create.porting_lib.util.client;

import io.github.fabricators_of_create.porting_lib.PortingLib;
import java.util.function.Function;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

/**
 * A helper that lets you bake quads that won't be used with an atlas.
 */
public class UnitSprite extends Sprite {
	public static final UnitSprite INSTANCE = new UnitSprite();
	public static final Identifier LOCATION = new Identifier(PortingLib.ID, "unit");
	public static final Function<SpriteIdentifier, Sprite> GETTER = (x) -> INSTANCE;

	private UnitSprite() {
		super(new SpriteAtlasTexture(LOCATION),
				new Info(LOCATION, 1, 1, AnimationResourceMetadata.EMPTY),
				0, 1, 1,
				0, 0, new NativeImage(1, 1, false));
	}

	@Override
	public float getFrameU(double u) {
		return (float) u / 16;
	}

	@Override
	public float getFrameV(double v) {
		return (float) v / 16;
	}
}
