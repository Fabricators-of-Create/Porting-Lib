package io.github.fabricators_of_create.porting_lib.mixin.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.EntryListWidget;

@Environment(EnvType.CLIENT)
@Mixin(EntryListWidget.Entry.class)
public interface AbstractSelectionList$EntryAccessor<E extends EntryListWidget.Entry<E>> {
	@Accessor("list")
	EntryListWidget<E> port_lib$getList();
}
