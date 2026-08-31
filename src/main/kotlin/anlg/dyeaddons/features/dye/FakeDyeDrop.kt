package anlg.dyeaddons.features.dye

import anlg.dyeaddons.DyeAddons.Companion.mc
import anlg.dyeaddons.config.ConfigManager
import anlg.dyeaddons.config.DyeMultiplier
import anlg.dyeaddons.config.ProfileStorage
import anlg.dyeaddons.data.ColorCodes
import anlg.dyeaddons.data.Dye
import anlg.dyeaddons.data.Sounds
import anlg.dyeaddons.events.EventBus
import anlg.dyeaddons.settings.categories.Dyes
import anlg.dyeaddons.utils.ChatUtils
import anlg.dyeaddons.utils.SoundUtils
import java.math.RoundingMode
import kotlin.random.Random

object FakeDyeDrop {

    private val flavorTexts = listOf(
        "An incredibly limited",
        "A ridiculously tiny",
        "An unbelievably minute",
        "A remarkably peculiar",
        "A miniscule",
        "An extremely rare",
        "An astonishingly small",
        "An infinitesimal",
        "An extraordinarily scarce",
        "A remarkably unusual",
        "An astonishingly peculiar",
        "An exceedingly limited",
        "An impossibly small",
        "An absurdly diminutive",
        "A strikingly exceptional",
        "An unfathomably tiny",
        "A fantastically unusual",
        "A laughably minuscule",
        "A crazily minuscule",
        "A ludicrously uncommon",
        "A mind-bogglingly unique",
        "An outrageously unique",
        "A bizarrely diminutive",
        "A shockingly small",
        "A nuclearly small",
        "An uncommonly rare",
        "A mind-numbingly unique",
        "An immensely tiny",
        "A fantastically odd",
        "An astoundingly exceptional",
        "A crazily head-bonkers",
        "A phenomenally scarce",
        "An insanely rare",
        "A jaw-droppingly low",
        "A surprisingly rare",
        "A staggeringly low",
        "A breathtakingly scarce",
    )

    private val dyeColors = mapOf(
        Dye.AQUAMARINE to ColorCodes.AQUA,
        Dye.ARCHFIEND to ColorCodes.DARK_RED,
        Dye.BONE to ColorCodes.WHITE,
        Dye.BRICK_RED to ColorCodes.RED,
        Dye.BYZANTIUM to ColorCodes.DARK_PURPLE,
        Dye.CARMINE to ColorCodes.DARK_RED,
        Dye.CELADON to ColorCodes.GREEN,
        Dye.CELESTE to ColorCodes.AQUA,
        Dye.COPPER to ColorCodes.DARK_GRAY,
        Dye.CYCLAMEN to ColorCodes.RED,
        Dye.DUNG to ColorCodes.DARK_GRAY,
        Dye.EMERALD to ColorCodes.DARK_GREEN,
        Dye.FLAME to ColorCodes.GOLD,
        Dye.FOSSIL to ColorCodes.DARK_GRAY,
        Dye.FROSTBITTEN to ColorCodes.DARK_AQUA,
        Dye.HOLLY to ColorCodes.DARK_GREEN,
        Dye.ICEBERG to ColorCodes.DARK_AQUA,
        Dye.JADE to ColorCodes.DARK_GREEN,
        Dye.LIVID to ColorCodes.GRAY,
        Dye.MANGO to ColorCodes.GOLD,
        Dye.MATCHA to ColorCodes.DARK_GREEN,
        Dye.MIDNIGHT to ColorCodes.DARK_PURPLE,
        Dye.MOCHA to ColorCodes.DARK_GRAY,
        Dye.MYTHOLOGICAL to ColorCodes.DARK_GREEN,
        Dye.NADESHIKO to ColorCodes.LIGHT_PURPLE,
        Dye.NECRON to ColorCodes.GOLD,
        Dye.NYANZA to ColorCodes.GREEN,
        Dye.PEARLESCENT to ColorCodes.DARK_GREEN,
        Dye.PELT to ColorCodes.GRAY,
        Dye.PERIWINKLE to ColorCodes.DARK_AQUA,
        Dye.SANGRIA to ColorCodes.DARK_RED,
        Dye.SECRET to ColorCodes.GRAY,
        Dye.TENTACLE to ColorCodes.DARK_PURPLE,
        Dye.TREASURE to ColorCodes.GOLD,
        Dye.WILD_STRAWBERRY to ColorCodes.LIGHT_PURPLE,
    )

    /**
     * Uses Random.nextDouble() to determine if the user gets a fake dye drop message.
     * @param dye The dye they rolled.
     * @param baseChance The base chance of the dye. Do not include vincent multiplier. Should be more than 1.
     * @param realChance The real chance the user rolled. Include all buffs/multipliers. Should be between 0-1.
     * @param magicFind The magic find/overbloom that gets paired with the message. Null if not present.
     */
    fun rollFakeDyeDrop(dye: Dye, baseChance: Double, realChance: Double, magicFind: Float? = null) {
        if (Dyes.fakeDyeDropRate == 0.0) return
        //DyeAddons.debug("Rolled fake $dye Dye. Base chance: ${baseChance.toInt()}, Real Chance: 1/${(1.0 / realChance).toInt()}, Magic Find: $magicFind")
        val displayedBaseChance = baseChance /
                (ProfileStorage.lastPlayedProfile()?.getDyeMultiplier(dye, DyeMultiplier.VINCENT)?.toDouble() ?: 1.0) /
                Dyes.fakeDyeDropRate
        if (Random.nextDouble() < realChance * Dyes.fakeDyeDropRate) {
            EventBus.runNextTick{ sendFakeDyeDrop(dye, displayedBaseChance, magicFind) }
        }
    }

    /**
     * Sends a fake dye message and plays the dye drop sound to the user.
     * @param dye The dye they "dropped",
     * @param baseChance The chance to be displayed to the user,
     * @param magicFind Any magic find/overbloom that is paired with the message. Null if not present.
     */
    private fun sendFakeDyeDrop(dye: Dye, baseChance: Double, magicFind: Float? = null) {
        val rank = ConfigManager.data.players[mc.player?.uuid]?.playerRankText
        val name = mc.player?.name?.string
        val an = if (dye.name.getOrNull(0)?.lowercaseChar() in setOf('a', 'e', 'i', 'o', 'u')) {
            "an"
        } else {
            "a"
        }

        val flavorText = flavorTexts.random()
        val oddsText = "${ColorCodes.GREEN}1${ColorCodes.DARK_GRAY}/${ColorCodes.GREEN}" + when (baseChance) {
            in Double.MIN_VALUE..<1.0 -> {
                "1"
            }
            in 1.0..<1_000.0 -> {
                if (baseChance % 1.0 == 0.0) {
                    "${baseChance.toInt()}"
                } else {
                    "%.1f".format(baseChance.toBigDecimal().setScale(1, RoundingMode.HALF_UP))
                }
            }
            in 1_000.0..<1_000_000.0 -> {
                if (baseChance / 1_000.0 % 1.0 == 0.0) {
                    "${(baseChance / 1_000.0).toInt()}k"
                } else {
                    "${"%.1f".format((baseChance / 1_000.0).toBigDecimal().setScale(1, RoundingMode.HALF_UP))}k"
                }
            }
            in 1_000_000.0..<1_000_000_000.0 -> {
                if (baseChance / 1_000_000.0 % 1.0 == 0.0) {
                    "${(baseChance / 1_000_000.0).toInt()}M"
                } else {
                    "${"%.1f".format((baseChance / 1_000_000.0).toBigDecimal().setScale(1, RoundingMode.HALF_UP))}M"
                }            }
            in 1_000_000_000.0..<1_000_000_000_000.0 -> {
                if (baseChance / 1_000_000_000.0 % 1.0 == 0.0) {
                    "${(baseChance / 1_000_000_000.0).toInt()}B"
                } else {
                    "${"%.1f".format((baseChance / 1_000_000_000.0).toBigDecimal().setScale(1, RoundingMode.HALF_UP))}B"
                }            }
            else -> ""
        } + " ${ColorCodes.DARK_GRAY}(${"%.7f".format((1.0 / baseChance * 100.0)
            .coerceAtMost(100.0)).trimEnd('0').trimEnd('.')}%)"

        val magicFindText = if (magicFind != null) {
            if (dye in listOf(Dye.DUNG, Dye.WILD_STRAWBERRY)) {
                "${ColorCodes.YELLOW}(+${"%.2f".format(magicFind).trimEnd('0').trimEnd('.')}☀)"
            } else {
                "${ColorCodes.AQUA}(+${magicFind.toInt()}% ★ Magic Find)"
            }
        } else ""

        ChatUtils.addLocalChatMessage("§d§lWOW! $rank$name§f §6did NOT find $an ${dyeColors[dye]}$dye Dye§6!")
        ChatUtils.addLocalChatMessage("${ColorCodes.GRAY}$flavorText $oddsText ${ColorCodes.GRAY}chance! $magicFindText")
        ChatUtils.addLocalChatMessage("${ColorCodes.YELLOW}This is a dye jumpscare from DyeAddons! You can disable this feature in /dyeaddons config.")
        SoundUtils.playCustomSound(Sounds.DYE_DROP)
    }
}