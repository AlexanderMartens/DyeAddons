package anlg.dyeaddons.features.qol

import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.SubmitEntityNameTag
import anlg.dyeaddons.settings.categories.QOL
import anlg.dyeaddons.utils.ChatUtils.getFormattedString
import anlg.dyeaddons.utils.SkyblockUtils
//? if >=26.2 {
import net.minecraft.world.entity.EntityTypes
//?} else {
/*import net.minecraft.world.entity.EntityType
*///?}

object RunicMobHelper {

    private val RUNIC_PATTERN = Regex("""§5\[§dLv\d+§5]""")
    private val RUNIC_DUNGEON_MOB_PATTERN = Regex("""§5[a-z A-Z\-']+ §d""")

    fun init() {
        EventBus.subscribe(SubmitEntityNameTag::class, ::onSubmitEntityNameTag)
    }

    private fun onSubmitEntityNameTag(event: SubmitEntityNameTag) {
        if (!SkyblockUtils.isInSkyblock() || !QOL.biggerRunicNametags) return

        val state = event.state

        //? if >=26.2 {
        if (state.entityType != EntityTypes.ARMOR_STAND) return
        //?} else {
        /*if (state.entityType != EntityType.ARMOR_STAND) return
        *///?}

        val vec3 = state.nameTagAttachment ?: return
        val nameTag = state.nameTag ?: return

        val formattedName = nameTag.getFormattedString()

        if (!RUNIC_PATTERN.containsMatchIn(formattedName) &&
            !RUNIC_DUNGEON_MOB_PATTERN.containsMatchIn(formattedName)) return

        val distanceScale = (state.distanceToCameraSq.toFloat() / 100f).coerceIn(2f, 5f)

        event.callbackInfo.cancel()

        event.poseStack.pushPose()
        event.poseStack.scale(distanceScale, distanceScale, distanceScale)

        event.nodeCollector.submitNameTag(
            event.poseStack,
            vec3,
            event.offset,
            nameTag,
            !state.isDiscrete,
            state.lightCoords,
            //? if <26.2 {
            /*state.distanceToCameraSq,
            *///?}
            event.camera
        )

        event.poseStack.popPose()
    }
}