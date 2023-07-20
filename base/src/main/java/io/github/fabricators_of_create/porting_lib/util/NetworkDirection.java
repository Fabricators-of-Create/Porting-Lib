package io.github.fabricators_of_create.porting_lib.util;

import net.fabricmc.api.EnvType;

public enum NetworkDirection {
	PLAY_TO_SERVER(EnvType.CLIENT, 1),
	PLAY_TO_CLIENT(EnvType.SERVER, 0);

	private final EnvType logicalSide;
	private final int otherWay;

	NetworkDirection(EnvType logicalSide, int otherWay) {
		this.logicalSide = logicalSide;
		this.otherWay = otherWay;
	}

	public NetworkDirection reply() {
		return NetworkDirection.values()[this.otherWay];
	}

	public EnvType getOriginationSide() {
		return logicalSide;
	}

	public EnvType getReceptionSide() {
		return reply().logicalSide;
	};
}
