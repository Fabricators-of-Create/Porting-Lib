package io.github.fabricators_of_create.porting_lib_build;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

public interface PortingLibExtension {
	Property<Boolean> getFabricAsm();
	Property<Boolean> getDatagen();
	Property<Boolean> getTestMod();
	ListProperty<String> getModuleDependencies();
}
