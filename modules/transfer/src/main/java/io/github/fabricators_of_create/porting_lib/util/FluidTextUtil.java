package io.github.fabricators_of_create.porting_lib.util;

import java.text.NumberFormat;
import java.util.Locale;

import com.google.common.math.LongMath;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;

/**
 * A few helpers to display fluids.
 * Stolen from Modern-Industrialization
 */
public class FluidTextUtil {

	public static final Format NUMBER_FORMAT = new Format();

	public static class Format extends SimplePreparableReloadListener<Unit> implements IdentifiableResourceReloadListener {
		public static final ResourceLocation ID = PortingLib.id("format_reload_listener");
		private NumberFormat format = NumberFormat.getNumberInstance(Locale.ROOT);

		private Format() {}

		public NumberFormat get() {
			return format;
		}

		public void update() {
			format = NumberFormat.getInstance();
			format.setMaximumFractionDigits(2);
			format.setMinimumFractionDigits(0);
			format.setGroupingUsed(true);
		}

		@Override
		protected Unit prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
			return Unit.INSTANCE;
		}

		@Override
		protected void apply(Unit object, ResourceManager resourceManager, ProfilerFiller profiler) {
			update();
		}

		@Override
		public ResourceLocation getFabricId() {
			return ID;
		}
	}

	static String format(double d) {
		return NUMBER_FORMAT.get()
				.format(d).replace("\u00A0", " ");
	}

	/**
	 * Return a unicode string representing a fraction, like ¹⁄₈₁.
	 */
	public static String getUnicodeFraction(long numerator, long denominator, boolean simplify) {
		if (numerator < 0 || denominator < 0)
			throw new IllegalArgumentException("Numerator and denominator must be non negative.");

		if (simplify && denominator != 0) {
			long g = LongMath.gcd(numerator, denominator);
			numerator /= g;
			denominator /= g;
		}

		StringBuilder numString = new StringBuilder();

		while (numerator > 0) {
			numString.append(SUPERSCRIPT[(int) (numerator % 10)]);
			numerator /= 10;
		}

		StringBuilder denomString = new StringBuilder();

		while (denominator > 0) {
			denomString.append(SUBSCRIPT[(int) (denominator % 10)]);
			denominator /= 10;
		}

		return numString.reverse().toString() + FRACTION_BAR + denomString.reverse().toString();
	}

	/**
	 * Convert a non negative fluid amount in droplets to a unicode string
	 * representing the amount in millibuckets. For example, passing 163 will result
	 * in
	 *
	 * <pre>
	 * 2 ¹⁄₈₁
	 * </pre>
	 *
	 * .
	 */
	public static String getUnicodeMillibuckets(long droplets, FluidUnit unit, boolean simplify) {
		if (unit == FluidUnit.DROPLETS)
			return format(droplets);
		@SuppressWarnings("IntegerDivisionInFloatingPointContext")
		String result = format(droplets / unit.getOneBucketAmount());

		if (droplets % 81 != 0 && !simplify) {
			result += " " + getUnicodeFraction(droplets % unit.getOneBucketAmount(), unit.getOneBucketAmount(), true);
		}

		return result;
	}

	private static final char[] SUPERSCRIPT = new char[] { '\u2070', '\u00b9', '\u00b2', '\u00b3', '\u2074', '\u2075', '\u2076', '\u2077', '\u2078',
			'\u2079' };
	private static final char FRACTION_BAR = '\u2044';
	private static final char[] SUBSCRIPT = new char[] { '\u2080', '\u2081', '\u2082', '\u2083', '\u2084', '\u2085', '\u2086', '\u2087', '\u2088',
			'\u2089' };

}
