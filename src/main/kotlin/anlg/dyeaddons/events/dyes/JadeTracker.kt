package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.events.models.SoundPlayEvent
import anlg.dyeaddons.features.dye.FakeDyeDrop
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt
import net.minecraft.world.phys.Vec3

object JadeTracker {

    private val NUCLEUS_PATTERN = Regex("""CRYSTAL NUCLEUS LOOT BUNDLE""")

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
        EventBus.subscribe(SoundPlayEvent::class, ::onSound)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Crystal Hollows") return

        if (NUCLEUS_PATTERN.matches(event.unformattedText.trim())) {
            val meter = ProfileStorage.lastPlayedProfile()?.rngMeters["nucleus"]
            val pityShard = ProfileStorage.lastPlayedProfile()?.dyeModifiers["Pity Level"] ?: 0
            val addedMeter = (1f + pityShard / 100f) * 1_000f
            meter?.progress += addedMeter.toInt()

            updateDyeStats()
            updateDyeProgress()
            DyeAddons.debug("Tracked nucleus run completed", DebugCategories.DYE_PROGRESS_EVENT)
        }
    }

    private fun onSound(event: SoundPlayEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Crystal Hollows") return

        val nucleusPos = Vec3(513.5, 106.0, 551.5)
        val soundPos = Vec3(event.x, event.y, event.z)

        if (soundPos.distanceToSqr(nucleusPos) > 49.0) return

        val soundName = event.sound.identifier.toString().removePrefix("minecraft:")
        if (soundName != "entity.item.pickup") return

        val profileStats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = (1.0 / 500_000.0) * profileStats.getDyeMultiplier(
            Dye.JADE,
            DyeMultiplier.METER,
            DyeMultiplier.VINCENT,
            DyeMultiplier.BUCKET_OF_DYE,
            DyeMultiplier.MIRACLE_CHANCE)

        FakeDyeDrop.rollFakeDyeDrop(
            Dye.JADE,
            500_000.0,
            dropRate
        )
    }

    private fun updateDyeStats() {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.JADE]?.statistics ?: return

        stats.incrementInt("Nucleus Runs Completed")
    }

    private fun updateDyeProgress() {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.JADE]?.statistics

        val molePet = stats?.get("Mole Pet Level")?.asInt() ?: 0
        val highRoller = stats?.get("High Roller Perk")?.asBool() ?: false
        val biggerBox = stats?.get("Bigger Box Level")?.asInt() ?: 0
        val echoBox = stats?.get("Echo of Box Level")?.asInt() ?: 0
        val echoEcho = stats?.get("Echo of Echo Level")?.asInt() ?: 0

        var extraItems = 0.0
        extraItems += molePet / 100.0
        if (highRoller) extraItems += 1.0
        extraItems += (biggerBox / 20.0) * (1.0 + (echoBox / 50.0) * (1.0 + (echoEcho / 20.0)))

        val profileStats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = (1.0 / 500_000.0) * (17.0 + extraItems) * profileStats.getDyeMultiplier(
            Dye.JADE,
            DyeMultiplier.METER,
            DyeMultiplier.VINCENT,
            DyeMultiplier.BUCKET_OF_DYE,
            DyeMultiplier.MIRACLE_CHANCE)

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.JADE]?.progress += dropRate
    }

}