package io.github.fabricators_of_create.porting_lib.util;

import java.util.function.Consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

/**
 * Use {@link NetworkHooks} instead.
 */
@Deprecated(forRemoval = true)
public class NetworkUtil {
	public static final ResourceLocation OPEN_ID = NetworkHooks.OPEN_ID;

	public static void openGui(ServerPlayer player, MenuProvider containerProvider, BlockPos pos) {
		NetworkHooks.openScreen(player, containerProvider, pos);
	}

	public static void openGui(ServerPlayer player, MenuProvider factory, Consumer<FriendlyByteBuf> extraDataWriter) {
		NetworkHooks.openScreen(player, factory, extraDataWriter);
	}
}
