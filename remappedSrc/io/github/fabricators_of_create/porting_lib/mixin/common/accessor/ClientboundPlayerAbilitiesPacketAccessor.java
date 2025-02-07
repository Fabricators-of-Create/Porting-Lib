package io.github.fabricators_of_create.porting_lib.mixin.common.accessor;

import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerAbilitiesS2CPacket.class)
public interface ClientboundPlayerAbilitiesPacketAccessor {
	@Accessor("flyingSpeed")
	void port_lib$setFlyingSpeed(float speed);
}
