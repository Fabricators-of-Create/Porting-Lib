package io.github.fabricators_of_create.porting_lib.util;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.util.thread.ThreadExecutor;
import net.minecraft.world.World;

public class LogicalSidedProvider<T> {
	public static final LogicalSidedProvider<ThreadExecutor<? super ServerTask>> WORKQUEUE = new LogicalSidedProvider<>(Supplier::get, Supplier::get);
	public static final LogicalSidedProvider<Optional<World>> CLIENTWORLD = new LogicalSidedProvider<>((c)-> Optional.of(c.get().world), (s)->Optional.empty());

	private static Supplier<MinecraftClient> client;
	private static Supplier<MinecraftServer> server;

	// INTERNAL, DO  NOT CALL
	public static void setClient(Supplier<MinecraftClient> client)
	{
		LogicalSidedProvider.client = client;
	}
	public static void setServer(Supplier<MinecraftServer> server)
	{
		LogicalSidedProvider.server = server;
	}

	private LogicalSidedProvider(Function<Supplier<MinecraftClient>, T> clientSide, Function<Supplier<MinecraftServer>, T> serverSide)
	{
		this.clientSide = clientSide;
		this.serverSide = serverSide;
	}

	private final Function<Supplier<MinecraftClient>, T> clientSide;
	private final Function<Supplier<MinecraftServer>, T> serverSide;

	public T get(final EnvType side) {
		return side==EnvType.CLIENT ? clientSide.apply(client) : serverSide.apply(server);
	}
}

