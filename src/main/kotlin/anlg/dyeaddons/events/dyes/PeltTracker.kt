package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.features.dye.FakeDyeDrop
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.calc.TrapperAnimal

object PeltTracker {

    private val dye = Dye.PELT

    private val TRAPPER_ANIMAL_PATTERN = Regex("""\[NPC] Trevor: You can find your (.+) animal near the .+\.""")

    private val ANIMAL_KILLED_PATTERN = Regex("""Killing the animal rewarded you \d+ pelts\.""")

    private var currentAnimal : TrapperAnimal? = null

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "The Farming Islands") return

        if (TRAPPER_ANIMAL_PATTERN.matches(event.unformattedText.trim())) {
            val animal = TRAPPER_ANIMAL_PATTERN.find(event.unformattedText.trim())?.groupValues?.get(1)

            try {
                currentAnimal = TrapperAnimal.valueOf(animal!!)
                DyeAddons.debug("Tracked animal: $animal", DebugCategories.DYE_PROGRESS_EVENT)
            } catch (_: IllegalArgumentException) {
                DyeAddons.debug("Could not parse animal: $animal", DebugCategories.ERROR)
            }
        }

        if (ANIMAL_KILLED_PATTERN.matches(event.unformattedText.trim())) {
            if (currentAnimal != null) {
                updateDyeStats(currentAnimal!!)
                updateDyeProgress(currentAnimal!!)
                DyeAddons.debug("Tracked dye progress: $currentAnimal", DebugCategories.DYE_PROGRESS_EVENT)
                currentAnimal = null
            }
        }
    }

    private fun updateDyeStats(animal: TrapperAnimal) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        when (animal) {
            TrapperAnimal.TRACKABLE -> stats.incrementStat(dye, "Trackable Animal Kills")
            TrapperAnimal.UNTRACKABLE -> stats.incrementStat(dye, "Untrackable Animal Kills")
            TrapperAnimal.UNDETECTED -> stats.incrementStat(dye, "Undetected Animal Kills")
            TrapperAnimal.ENDANGERED -> stats.incrementStat(dye, "Endangered Animal Kills")
            TrapperAnimal.ELUSIVE -> stats.incrementStat(dye, "Elusive Animal Kills")
        }
    }

    private fun updateDyeProgress(animal: TrapperAnimal) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / animal.baseChance * stats.getDyeMultiplier(
            dye,
            DyeMultiplier.VINCENT,
            DyeMultiplier.BUCKET_OF_DYE,
            DyeMultiplier.MIRACLE_CHANCE)

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate
        FakeDyeDrop.rollFakeDyeDrop(dye,
            animal.baseChance.toDouble(),
            dropRate)
    }
}