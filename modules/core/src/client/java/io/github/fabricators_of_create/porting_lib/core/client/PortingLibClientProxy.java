package io.github.fabricators_of_create.porting_lib.core.client;

import io.github.fabricators_of_create.porting_lib.core.util.PortingLibProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.util.thread.BlockableEventLoop;

public class PortingLibClientProxy extends PortingLibProxy {
	@Override
	public BlockableEventLoop<Runnable> getClientExecutor() {
		return Minecraft.getInstance();
	}
}
