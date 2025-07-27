package io.github.fabricators_of_create.porting_lib.resources.injections;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;

import io.github.fabricators_of_create.porting_lib.resources.data_maps.DataMapType;

import io.github.fabricators_of_create.porting_lib.resources.data_maps.IWithData;

import org.jetbrains.annotations.Nullable;

public interface HolderInjection<T> extends IWithData<T> {
	@Nullable
	default <A> A getData(DataMapType<T, A> type) {
		throw PortingLib.createMixinException("HolderInjection.getData");
	}
}
