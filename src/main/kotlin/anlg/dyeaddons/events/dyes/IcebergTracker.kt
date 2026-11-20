package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
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
    private val mythicMobs = setOf(
        "Reindrake"
    )

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

        DyeAddons.debug("Tracked $mobName Kill, Type: $mobType, Lootshare: ${event.lootshared}", DebugCategories.DYE_PROGRESS_EVENT)
        updateDyeStats(mobType, event.lootshared)
        updateDyeProgress(mobType, event.lootshared)
    }

    private fun updateDyeStats(mobType: SeaCreature, lootshare: Boolean) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val stat = when (mobType) {
            SeaCreature.COMMON -> "Common Sea Creatures"
            SeaCreature.UNCOMMON -> "Uncommon Sea Creatures"
            SeaCreature.RARE -> "Rare Sea Creatures"
            SeaCreature.EPIC -> "Epic Sea Creatures"
            SeaCreature.LEGENDARY -> "Legendary Sea Creatures"
            SeaCreature.MYTHIC -> "Mythic Sea Creatures"
        }

        stats.incrementStat(dye, "$stat${if (lootshare) " (Lootshare)" else ""}")
    }

    private fun updateDyeProgress(mobType: SeaCreature, lootshare: Boolean) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = 1.0 / mobType.baseChance * stats.getDyeMultiplier(dye) * if (lootshare) 0.2 else 1.0

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
            mobType.baseChance * if (lootshare) 5.0 else 1.0
        }
        val fakeDropChance = 1.0 / fakeDropRate * stats.getDyeMultiplier(dye)

        FakeDyeDrop.rollFakeDyeDrop(dye,
            fakeDropRate,
            fakeDropChance,
            stats.getMagicFind(dye, DyeMultiplier.MAGIC_FIND),
            lootshare)
    }
}