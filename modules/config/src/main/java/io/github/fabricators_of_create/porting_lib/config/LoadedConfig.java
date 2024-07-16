package io.github.fabricators_of_create.porting_lib.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import java.nio.file.Path;
import org.jetbrains.annotations.Nullable;

record LoadedConfig(CommentedConfig config, @Nullable Path path, ModConfig modConfig) implements IConfigSpec.ILoadedConfig {
	@Override
	public void save() {
		if (path != null) {
			ConfigTracker.writeConfig(path, config);
		}
		modConfig.lock.lock();
		try {
			new ModConfigEvent.Reloading(modConfig).sendEvent();
		} finally {
			modConfig.lock.unlock();
		}
	}
}
