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

object MidnightTracker {

    private val dye = Dye.MIDNIGHT

    private val commonMobs = setOf(
        "Jumpin' Jack"
    )
    private val uncommonMobs = setOf(
        "Scarecrow"
    )
    private val rareMobs = setOf(
        "Nightmare",
    )
    private val epicMobs = setOf(
        "Werewolf",
    )
    private val legendaryMobs = setOf(
        "Phantom Fisher"
    )
    private val mythicMobs = setOf(
        "Grim Reaper"
    )
    private val spookyMobs = setOf(
        "Trick or Treater",
        "Wither Gourd",
        "Phantom Spirit",
        "Scary Jerry",
        "Wraith",
        "Crazy Witch"
    )
    private val batMobs = setOf(
        "Bat Piñata",
        "Mega Bat",
    )

    private enum class SpookyCreature (val baseChance : Double){
        COMMON(dye.getDropRate("Common")),
        UNCOMMON(dye.getDropRate("Uncommon")),
        RARE(dye.getDropRate("Rare")),
        EPIC(dye.getDropRate("Epic")),
        LEGENDARY(dye.getDropRate("Legendary")),
        MYTHIC(dye.getDropRate("Mythic")),
        SPOOKY(dye.getDropRate("Spooky")),
        BAT(dye.getDropRate("Bat")),
        HORSEMAN(dye.getDropRate("Horseman"))
    }

    private val HORSEMAN_KILL_PATTERN = Regex("""Your Damage: ([\d,]+) \(Position #\d+\)""")

    fun init() {
        EventBus.subscribe(MobKillEvent::class, ::onMobKillEvent)
        EventBus.subscribe(ChatEvent::class, ::onChat)
    }

    private fun onMobKillEvent(event: MobKillEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock()) return

        val mobName = event.mobName

        val mobType = when (mobName) {
            in commonMobs -> SpookyCreature.COMMON
            in uncommonMobs -> SpookyCreature.UNCOMMON
            in rareMobs -> SpookyCreature.RARE
            in epicMobs -> SpookyCreature.EPIC
            in legendaryMobs -> SpookyCreature.LEGENDARY
            in mythicMobs -> SpookyCreature.MYTHIC
            in spookyMobs -> SpookyCreature.SPOOKY
            in batMobs -> SpookyCreature.BAT
            else -> return
        }

        DyeAddons.debug("Tracked $mobName Kill, Type: $mobType, Lootshare: ${event.lootshared}", DebugCategories.DYE_PROGRESS_EVENT)
        updateDyeStats(mobType, event.lootshared)
        updateDyeProgress(mobType, event.lootshared)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Hub") return

        val damage = HORSEMAN_KILL_PATTERN.matchEntire(event.unformattedText.trim())
            ?.groupValues?.get(1)?.replace(",","")?.toIntOrNull() ?: return
        if (damage == 0) return

        DyeAddons.debug("Tracked Horseman Kill, Type: ${SpookyCreature.HORSEMAN}", DebugCategories.DYE_PROGRESS_EVENT)

        updateDyeStats(SpookyCreature.HORSEMAN, false)
        updateDyeProgress(SpookyCreature.HORSEMAN, false)
    }

    private fun updateDyeStats(mobType: SpookyCreature, lootshare: Boolean) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val stat = when (mobType) {
            SpookyCreature.COMMON -> "Common Sea Creatures"
            SpookyCreature.UNCOMMON -> "Uncommon Sea Creatures"
            SpookyCreature.RARE -> "Rare Sea Creatures"
            SpookyCreature.EPIC -> "Epic Sea Creatures"
            SpookyCreature.LEGENDARY -> "Legendary Sea Creatures"
            SpookyCreature.MYTHIC -> "Mythic Sea Creatures"
            SpookyCreature.SPOOKY -> "Spooky Mob Kills"
            SpookyCreature.BAT -> "Bat Pinata Kills"
            SpookyCreature.HORSEMAN -> "Headless Horseman Kills"
        }

        stats.incrementStat(dye, "$stat${if (lootshare) " (Lootshare)" else ""}")
    }

    private fun updateDyeProgress(mobType: SpookyCreature, lootshare: Boolean) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = (1.0 / mobType.baseChance) * stats.getDyeMultiplier(dye) * if (lootshare) 0.2 else 1.0

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate

        val fakeDropRate = if (Dyes.fakeDyeLegacyDropRate) {
            when (mobType) {
                SpookyCreature.COMMON -> dye.getDropRate("Common (Legacy)")
                SpookyCreature.UNCOMMON -> dye.getDropRate("Uncommon (Legacy)")
                SpookyCreature.RARE -> dye.getDropRate("Rare (Legacy)")
                SpookyCreature.EPIC -> dye.getDropRate("Epic (Legacy)")
                SpookyCreature.LEGENDARY -> dye.getDropRate("Legendary (Legacy)")
                SpookyCreature.MYTHIC -> dye.getDropRate("Mythic (Legacy)")
                SpookyCreature.SPOOKY -> dye.getDropRate("Spooky (Legacy)")
                SpookyCreature.BAT -> dye.getDropRate("Spooky (Legacy)")
                SpookyCreature.HORSEMAN -> dye.getDropRate("Horseman (Legacy)")
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