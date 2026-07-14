package anlg.dyeaddons.mixin;

import anlg.dyeaddons.events.EventBus;
import anlg.dyeaddons.events.models.PacketReceivedEvent;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public class ConnectionMixin {

    @Inject(method = "genericsFtw", at = @At(value = "HEAD"))
    private static void handlePacket$Inject$HEAD(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        if (packet instanceof BundlePacket<?> bundle) {
            for (Packet<?> subPacket : bundle.subPackets()) {
                EventBus.INSTANCE.publish(new PacketReceivedEvent(subPacket));
            }
        } else {
            EventBus.INSTANCE.publish(new PacketReceivedEvent(packet));
        }
    }
}