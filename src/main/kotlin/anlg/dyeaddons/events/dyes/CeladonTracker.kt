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
import anlg.dyeaddons.settings.categories.Dyes
import anlg.dyeaddons.utils.SkyblockUtils

object CeladonTracker {

    private val dye = Dye.CELADON

    private val BACTE_PATTERN = Regex("""BACTE DOWN!""")

    private val LEECH_PATTERN = Regex("""LEECH SUPREME DOWN!""")

    private val BLOBBERCYST_PATTERN = Regex("""§aBlobbercyst §ekilled by (?:§.\[[^]]+]\s)?([A-Za-z0-9_]+)§e!""")

    private enum class CeladonMob(val baseChance: Double) {
        BACTE(dye.getDropRate("Bacte")),
        BLOBBERCYST(dye.getDropRate("Blobbercyst")),
        LEECH_SUPREME(dye.getDropRate("Leech Supreme")),
        LEECH_ALPHA(dye.getDropRate("Leech Alpha")),
        LEECH_SWARM(dye.getDropRate("Leech Swarm")),
    }

    fun init() {
        EventBus.subscribe(ChatEvent::class, ::onChat)
        EventBus.subscribe(MobKillEvent::class, ::onMobKill)
    }

    private fun onChat(event: ChatEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "The Rift") return

        if (BACTE_PATTERN.matches(event.unformattedText.trim())) {
            updateDyeStats(CeladonMob.BACTE)
            updateDyeProgress(CeladonMob.BACTE)
        }

        if (LEECH_PATTERN.matches(event.unformattedText.trim())) {
            updateDyeStats(CeladonMob.LEECH_SUPREME)
            updateDyeProgress(CeladonMob.LEECH_SUPREME)
        }

        val blobbercystMatch = BLOBBERCYST_PATTERN.find(event.formattedText)?.groupValues?.get(1)

        if (blobbercystMatch == mc.player?.name?.string) {
            updateDyeStats(CeladonMob.BLOBBERCYST)
            updateDyeProgress(CeladonMob.BLOBBERCYST)
        }
    }

    private fun onMobKill(event: MobKillEvent) {
        if (!SkyblockUtils.hypixelMain ||
            !SkyblockUtils.isInSkyblock() ||
            SkyblockUtils.getWorldName() != "The Rift") return

        when (event.mobName) {
            "Leech Alpha" -> {
                updateDyeStats(CeladonMob.LEECH_ALPHA)
                updateDyeProgress(CeladonMob.LEECH_ALPHA)
            }
            "Leech Swarm" -> {
                updateDyeStats(CeladonMob.LEECH_SWARM)
                updateDyeProgress(CeladonMob.LEECH_SWARM)
            }
        }

    }

    private fun updateDyeStats(mobType : CeladonMob) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        DyeAddons.debug("Tracked $mobType kill", DebugCategories.DYE_PROGRESS_EVENT)

        when (mobType) {
            CeladonMob.BACTE -> stats.incrementStat(dye, "Bacte Kills")
            CeladonMob.BLOBBERCYST -> stats.incrementStat(dye, "Blobbercyst Kills")
            CeladonMob.LEECH_SUPREME -> stats.incrementStat(dye, "Leech Supreme Kills")
            CeladonMob.LEECH_SWARM -> stats.incrementStat(dye, "Leech Swarm Kills")
            CeladonMob.LEECH_ALPHA -> stats.incrementStat(dye, "Leech Alpha Kills")
        }
    }

    private fun updateDyeProgress(mobType : CeladonMob) {
        val stats = ProfileStorage.lastPlayedProfile() ?: return

        val dropRate = (1.0 / mobType.baseChance) *
                if (mobType in listOf(CeladonMob.BACTE, CeladonMob.LEECH_SUPREME)) {
                    stats.getDyeMultiplier(
                        dye,
                        DyeMultiplier.VINCENT,
                        DyeMultiplier.PAINT_PALETTE)
                } else {
                    stats.getDyeMultiplier(dye)
                }

        ProfileStorage.lastPlayedProfile()?.dyeData[dye]?.progress += dropRate

        val fakeDropRate = if (Dyes.fakeDyeLegacyDropRate) {
            when (mobType) {
                CeladonMob.BACTE -> dye.getDropRate("Bacte (Legacy)")
                CeladonMob.BLOBBERCYST -> dye.getDropRate("Blobbercyst (Legacy)")
                else -> mobType.baseChance
            }
        } else {
            mobType.baseChance
        }
        val fakeDropChance = 1.0 / fakeDropRate *
                if (mobType in listOf(CeladonMob.BACTE, CeladonMob.LEECH_SUPREME)) {
                    stats.getDyeMultiplier(
                        dye,
                        DyeMultiplier.VINCENT,
                        DyeMultiplier.PAINT_PALETTE)
                } else {
                    stats.getDyeMultiplier(dye)
                }

        if (mobType in listOf(CeladonMob.BACTE, CeladonMob.LEECH_SUPREME)) {
            FakeDyeDrop.rollFakeDyeDrop(
                dye,
                fakeDropRate,
                fakeDropChance,
            )
        } else {
            FakeDyeDrop.rollFakeDyeDrop(
                dye,
                fakeDropRate,
                fakeDropChance,
                stats.getMagicFind(dye, DyeMultiplier.MAGIC_FIND)
            )
        }
    }
}