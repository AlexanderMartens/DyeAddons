package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.events.models.MobKillEvent
import anlg.dyeaddons.features.dye.FakeDyeDrop
import anlg.dyeaddons.settings.categories.DebugCategories
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

    private val FINAL_BLOW_PATTERN = Regex("""(?:\[[^]]+]\s)?([A-Za-z0-9_]+) dealt the final blow\.""")

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

        DyeAddons.debug("Tracked $mobName Kill, Type: $mobType", DebugCategories.DYE_PROGRESS_EVENT)
        updateDyeStats(mobType)
        updateDyeProgress(mobType)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "Hub") return

        val player = FINAL_BLOW_PATTERN.matchEntire(event.unformattedText.trim())?.groupValues?.get(1) ?: return
        if (player != mc.player?.name?.string) return

        updateDyeStats(SpookyCreature.HORSEMAN)
        updateDyeProgress(SpookyCreature.HORSEMAN)
    }

    private fun updateDyeStats(mobType: SpookyCreature) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        when (mobType) {
            SpookyCreature.COMMON -> stats.incrementStat(dye, "Common Sea Creatures")
            SpookyCreature.UNCOMMON -> stats.incrementStat(dye, "Uncommon Sea Creatures")
            SpookyCreature.RARE -> stats.incrementStat(dye, "Rare Sea Creatures")
            SpookyCreature.EPIC -> stats.incrementStat(dye, "Epic Sea Creatures")
            SpookyCreature.LEGENDARY -> stats.incrementStat(dye, "Legendary Sea Creatures")
            SpookyCreature.MYTHIC -> stats.incrementStat(dye, "Mythic Sea Creatures")
            SpookyCreature.SPOOKY -> stats.incrementStat(dye, "Spooky Mob Kills")
            SpookyCreature.BAT -> stats.incrementStat(dye, "Bat Pinata Kills")
            SpookyCreature.HORSEMAN -> stats.incrementStat(dye, "Headless Horseman Kills")
        }
    }

    private fun updateDyeProgress(mobType: SpookyCreature) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = (1.0 / mobType.baseChance) * stats.getDyeMultiplier(dye)

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate
        FakeDyeDrop.rollFakeDyeDrop(dye,
            mobType.baseChance,
            dropRate,
            stats.getMagicFind(dye, DyeMultiplier.MAGIC_FIND))
    }
}