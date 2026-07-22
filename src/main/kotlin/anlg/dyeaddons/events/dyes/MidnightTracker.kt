package anlg.dyeaddons.events.dyes

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.events.models.ChatEvent
import anlg.dyeaddons.events.models.MobKillEvent
import anlg.dyeaddons.settings.categories.DebugCategories
import anlg.dyeaddons.utils.SkyblockUtils
import anlg.dyeaddons.utils.extensions.incrementInt

object MidnightTracker {

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
        "Bat Piñata",
        "Mega Bat",
        "Trick or Treater",
        "Wither Gourd",
        "Phantom Spirit",
        "Scary Jerry",
        "Wraith",
        "Crazy Witch"
    )

    private enum class SpookyCreature (val baseChance : Float){
        COMMON(1_000_000f),
        UNCOMMON(1_000_000f),
        RARE(1_000_000f),
        EPIC(1_000_000f),
        LEGENDARY(50_000f),
        MYTHIC(50_000f),
        SPOOKY(500_000f),
        HORSEMAN(50_000f)
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
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.MIDNIGHT]?.statistics ?: return

        when (mobType) {
            SpookyCreature.COMMON -> stats.incrementInt("Common/Uncommon/Rare/Epic Sea Creature Kills")
            SpookyCreature.UNCOMMON -> stats.incrementInt("Common/Uncommon/Rare/Epic Sea Creature Kills")
            SpookyCreature.RARE -> stats.incrementInt("Common/Uncommon/Rare/Epic Sea Creature Kills")
            SpookyCreature.EPIC -> stats.incrementInt("Common/Uncommon/Rare/Epic Sea Creature Kills")
            SpookyCreature.LEGENDARY -> stats.incrementInt("Legendary/Mythic Sea Creature Kills")
            SpookyCreature.MYTHIC -> stats.incrementInt("Legendary/Mythic Sea Creature Kills")
            SpookyCreature.SPOOKY -> stats.incrementInt("Spooky Mob Kills")
            SpookyCreature.HORSEMAN -> stats.incrementInt("Headless Horseman Kills")
        }
    }

    private fun updateDyeProgress(mobType: SpookyCreature) {
        val dyeRotation = ConfigManager.data.config.currentDyeRotation
        val multiplier = dyeRotation?.getMultiplier(Dye.MIDNIGHT) ?: 1
        val stats = ProfileStorage.lastPlayedProfile()?.dyeData[Dye.MIDNIGHT]?.statistics ?: return

        val magicFind = stats["Magic Find"]?.asFloat() ?: 0f
        val looting = stats["Looting"]?.asInt() ?: 0

        val dropRate = 1.0 / mobType.baseChance * (1.0 + magicFind / 100.0) * (1.0 + looting * 0.15)

        ProfileStorage.lastPlayedProfile()?.dyeData[Dye.MIDNIGHT]?.progress += dropRate * multiplier
    }

}