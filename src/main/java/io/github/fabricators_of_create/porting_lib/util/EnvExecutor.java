package io.github.fabricators_of_create.porting_lib.util;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class EnvExecutor {
	public static void runWhenOn(EnvType env, Supplier<Runnable> toRun) {
		if (FabricLoader.getInstance().getEnvironmentType() == env) {
			toRun.get().run();
		}
	}

	public static <T> T callWhenOn(EnvType dist, Supplier<Callable<T>> toRun) {
		if (dist == FabricLoader.getInstance().getEnvironmentType()) {
			try {
				return toRun.get().call();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	public static <T> T unsafeRunForDist(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
		switch (FabricLoader.getInstance().getEnvironmentType()) {
			case CLIENT:
				return clientTarget.get().get();
			case SERVER:
				return serverTarget.get().get();
			default:
				throw new IllegalArgumentException("UNSIDED?");
		}
	}
}
