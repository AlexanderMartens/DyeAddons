package anlg.dyeaddons.mixin;

import anlg.dyeaddons.events.EventBus;
import anlg.dyeaddons.events.models.SoundPlayEvent;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
	@Inject(at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"), method = "play")
	private void dyeaddons$onPlay(SoundInstance instance, CallbackInfoReturnable<SoundEngine.PlayResult> cir) {
		if (instance == null) return;
		EventBus.INSTANCE.publish(
				new SoundPlayEvent(
						instance,
						instance.getVolume(),
						instance.getPitch(),
						instance.getX(),
						instance.getY(),
						instance.getZ()
				)
		);
	}
}