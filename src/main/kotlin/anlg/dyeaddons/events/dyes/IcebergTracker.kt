package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.events.models.MobKillEvent
import anlg.dyeaddons.features.dye.FakeDyeDrop
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.settings.categories.Dyes
import anlg.dyeaddons.utils.SkyblockUtils

object IcebergTracker {

    private val dye = Dye.ICEBERG

    private val commonMobs = setOf(
        "Frozen Steve"
    )
    private val uncommonMobs = setOf(
        "Frosty",
    )
    private val rareMobs = setOf(
        "Grinch"
    )
    private val epicMobs = setOf(
        "Nutcracker"
    )
    private val legendaryMobs = setOf(
        "Yeti"
    )
    private val mythicMobs = setOf<String>()

    private val REINDRAKE_PATTERN = Regex("""DEFEATED! A Reindrake was slain and dropped all its loot!""")

    private enum class SeaCreature (val baseChance : Double){
        COMMON(dye.getDropRate("Common")),
        UNCOMMON(dye.getDropRate("Uncommon")),
        RARE(dye.getDropRate("Rare")),
        EPIC(dye.getDropRate("Epic")),
        LEGENDARY(dye.getDropRate("Legendary")),
        MYTHIC(dye.getDropRate("Mythic"))
    }

    fun init() {
        EventBus.subscribe(MobKillEvent::class, ::onMobKillEvent)
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onMobKillEvent(event: MobKillEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock()) return

        val mobName = event.mobName

        val mobType = when (mobName) {
            in commonMobs -> SeaCreature.COMMON
            in uncommonMobs -> SeaCreature.UNCOMMON
            in rareMobs -> SeaCreature.RARE
            in epicMobs -> SeaCreature.EPIC
            in legendaryMobs -> SeaCreature.LEGENDARY
            in mythicMobs -> SeaCreature.MYTHIC
            else -> return
        }

        DyeAddons.debug("Tracked $mobName Kill, Type: $mobType", DebugCategories.DYE_PROGRESS_EVENT)
        updateDyeStats(mobType)
        updateDyeProgress(mobType)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Jerry's Workshop") return

        if (!REINDRAKE_PATTERN.matches(event.unformattedText.trim())) return

        DyeAddons.debug("Tracked Reindrake Kill, Type: ${SeaCreature.MYTHIC}", DebugCategories.DYE_PROGRESS_EVENT)
        updateDyeStats(SeaCreature.MYTHIC)
        updateDyeProgress(SeaCreature.MYTHIC)
    }

    private fun updateDyeStats(mobType: SeaCreature) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        when (mobType) {
            SeaCreature.COMMON -> stats.incrementStat(dye, "Common Sea Creatures")
            SeaCreature.UNCOMMON -> stats.incrementStat(dye, "Uncommon Sea Creatures")
            SeaCreature.RARE -> stats.incrementStat(dye, "Rare Sea Creatures")
            SeaCreature.EPIC -> stats.incrementStat(dye, "Epic Sea Creatures")
            SeaCreature.LEGENDARY -> stats.incrementStat(dye, "Legendary Sea Creatures")
            SeaCreature.MYTHIC -> stats.incrementStat(dye, "Mythic Sea Creatures")
        }
    }

    private fun updateDyeProgress(mobType: SeaCreature) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = (1.0 / mobType.baseChance) * stats.getDyeMultiplier(dye)

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate

        val fakeDropRate = if (Dyes.fakeDyeLegacyDropRate) {
            when (mobType) {
                SeaCreature.COMMON -> dye.getDropRate("Common (Legacy)")
                SeaCreature.UNCOMMON -> dye.getDropRate("Uncommon (Legacy)")
                SeaCreature.RARE -> dye.getDropRate("Rare (Legacy)")
                SeaCreature.EPIC -> dye.getDropRate("Epic (Legacy)")
                SeaCreature.LEGENDARY -> dye.getDropRate("Legendary (Legacy)")
                SeaCreature.MYTHIC -> dye.getDropRate("Mythic (Legacy)")
            }
        } else {
            mobType.baseChance
        }
        val fakeDropChance = 1.0 / fakeDropRate * stats.getDyeMultiplier(dye)

        FakeDyeDrop.rollFakeDyeDrop(dye,
            fakeDropRate,
            fakeDropChance,
            stats.getMagicFind(dye, DyeMultiplier.MAGIC_FIND))
    }
}