package io.github.fabricators_of_create.porting_lib.model;

public enum EmptyModelData implements IModelData {
	INSTANCE;

	@Override
	public boolean hasProperty(ModelProperty<?> prop) {
		return false;
	}

	@Override
	public <T> T getData(ModelProperty<T> prop) {
		return null;
	}

	@Override
	public <T> T setData(ModelProperty<T> prop, T data) {
		return null;
	}
}
