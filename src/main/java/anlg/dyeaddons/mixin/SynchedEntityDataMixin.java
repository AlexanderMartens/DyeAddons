package anlg.dyeaddons.mixin;

import anlg.dyeaddons.events.ArmorStandHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;

/**
 * Credits: Feesh
 */
@Mixin(SynchedEntityData.class)
public class SynchedEntityDataMixin {

    @Shadow
    @Final
    private SyncedDataHolder entity;

    @Shadow
    @Final
    private SynchedEntityData.DataItem<?>[] itemsById;

    @Unique
    private Component dyeaddons$previousCustomName;

    @Inject(method = "assignValues", at = @At("HEAD"))
    private void dyeaddons$beforeAssignValues(List<SynchedEntityData.DataValue<?>> packet, CallbackInfo ci) {
        this.dyeaddons$previousCustomName = null;
        if (!(this.entity instanceof ArmorStand armorStand)) return;
        if (!dyeaddons$isPacketUpdatingCustomName(packet)) return;

        this.dyeaddons$previousCustomName = armorStand.getCustomName();
    }

    @Inject(method = "assignValues", at = @At("TAIL"))
    private void dyeaddons$onAssignValues(List<SynchedEntityData.DataValue<?>> packet, CallbackInfo ci) {
        if (!(this.entity instanceof ArmorStand armorStand)) return;
        if (!dyeaddons$isPacketUpdatingCustomName(packet)) return;

        ArmorStandHandler.onArmorStandCustomNameChanged(armorStand, this.dyeaddons$previousCustomName);
        this.dyeaddons$previousCustomName = null;
    }

    @Unique
    private boolean dyeaddons$isPacketUpdatingCustomName(List<SynchedEntityData.DataValue<?>> packet) {
        for (SynchedEntityData.DataValue<?> value : packet) {
            SynchedEntityData.DataItem<?> item = this.itemsById[value.id()];
            if (item != null && item.getAccessor() == EntityAccessor.dyeaddons$getDataCustomName()) {
                return true;
            }
        }
        return false;
    }
}
