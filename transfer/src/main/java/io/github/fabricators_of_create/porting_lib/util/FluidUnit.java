package io.github.fabricators_of_create.porting_lib.util;

public enum FluidUnit {
	DROPLETS(1, "generic.unit.droplets"),
	MILLIBUCKETS(81, "generic.unit.millibuckets");

	private final int oneBucket;
	private final String langKey;

	FluidUnit(int bucketAmount, String lang) {
		this.oneBucket = bucketAmount;
		this.langKey = lang;
	}

	public int getOneBucketAmount() {
		return oneBucket;
	}

	public String getTranslationKey() {
		return langKey;
	}
}
