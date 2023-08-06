package io.github.fabricators_of_create.porting_lib.util;

public class ConsumableValue<T> {
	private T value;

	public ConsumableValue() {
	}

	public ConsumableValue(T value) {
		this.value = value;
	}

	public T consume() {
		T value = this.value;
		this.value = null;
		return value;
	}

	public T get() {
		return this.value;
	}

	public void set(T value) {
		this.value = value;
	}
}
