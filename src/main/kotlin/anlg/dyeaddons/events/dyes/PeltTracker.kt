package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.calc.TrapperAnimal
import anlg.dyeaddons.utils.extensions.incrementInt

object PeltTracker {

    private val TRAPPER_ANIMAL_PATTERN = Regex("""\[NPC] Trevor: You can find your (.+) animal near the .+\.""")

    private val ANIMAL_KILLED_PATTERN = Regex("""Killing the animal rewarded you \d+ pelts\.""")

    private var currentAnimal : TrapperAnimal? = null

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onChat(@Suppress("UNUSED_PARAMETER") event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "The Farming Islands") return

        if (TRAPPER_ANIMAL_PATTERN.matches(event.unformattedText.trim())) {
            val animal = TRAPPER_ANIMAL_PATTERN.find(event.unformattedText.trim())?.groupValues?.get(1)

            try {
                currentAnimal = TrapperAnimal.valueOf(animal!!)
                DyeAddons.debug("Tracked animal: $animal")
            } catch (_: IllegalArgumentException) {
                DyeAddons.debug("Could not parse animal: $animal")
            }
        }

        if (ANIMAL_KILLED_PATTERN.matches(event.unformattedText.trim())) {
            if (currentAnimal != null) {
                updateDyeStats(currentAnimal!!)
                updateDyeProgress(currentAnimal!!)
                DyeAddons.debug("Tracked dye progress: $currentAnimal")
                currentAnimal = null
            }
        }
    }

    private fun updateDyeStats(animal: TrapperAnimal) {
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.PELT]?.statistics ?: return

        when (animal) {
            TrapperAnimal.TRACKABLE -> stats.incrementInt("Trackable Animal Kills")
            TrapperAnimal.UNTRACKABLE -> stats.incrementInt("Untrackable Animal Kills")
            TrapperAnimal.UNDETECTED -> stats.incrementInt("Undetected Animal Kills")
            TrapperAnimal.ENDANGERED -> stats.incrementInt("Endangered Animal Kills")
            TrapperAnimal.ELUSIVE -> stats.incrementInt("Elusive Animal Kills")
        }
    }

    private fun updateDyeProgress(animal: TrapperAnimal) {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.PELT) ?: 1

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.PELT]?.progress += (1.0 / animal.baseChance) * multiplier
    }

}