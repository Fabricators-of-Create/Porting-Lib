package io.github.fabricators_of_create.porting_lib.fluids.sound;

/**
 * A utility holding common sound actions.
 */
public final class SoundActions {
	private SoundActions() {
		throw new AssertionError("SoundActions should not be instantiated.");
	}

	/**
	 * When a bucket is being filled by a fluid.
	 */
	public static final SoundAction BUCKET_FILL = SoundAction.get("bucket_fill");

	/**
	 * When a bucket is emptying a fluid.
	 */
	public static final SoundAction BUCKET_EMPTY = SoundAction.get("bucket_empty");

	/**
	 * When the fluid is being vaporized.
	 */
	public static final SoundAction FLUID_VAPORIZE = SoundAction.get("fluid_vaporize");
}
