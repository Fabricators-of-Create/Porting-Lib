package io.github.fabricators_of_create.porting_lib.mixin_extensions.injectors.wrap_variable;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.injection.code.Injector;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo.AnnotationType;
import org.spongepowered.asm.mixin.injection.struct.InjectionInfo.HandlerPrefix;
import org.spongepowered.asm.mixin.transformer.MixinTargetContext;

@AnnotationType(WrapVariable.class)
@HandlerPrefix("wrapVariable")
public class WrapVariableInjectionInfo extends InjectionInfo {
	public WrapVariableInjectionInfo(MixinTargetContext mixin, MethodNode method, AnnotationNode annotation) {
		super(mixin, method, annotation);
	}

	@Override
	protected Injector parseInjector(AnnotationNode injectAnnotation) {
		return new WrapVariableInjector(this);
	}
}
