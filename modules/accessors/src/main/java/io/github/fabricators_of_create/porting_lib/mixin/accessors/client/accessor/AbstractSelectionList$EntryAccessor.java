package io.github.fabricators_of_create.porting_lib.mixin.accessors.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.AbstractSelectionList;

@Environment(EnvType.CLIENT)
@Mixin(AbstractSelectionList.Entry.class)
public interface AbstractSelectionList$EntryAccessor<E extends AbstractSelectionList.Entry<E>> {
	@Accessor("list")
	AbstractSelectionList<E> port_lib$getList();
}
