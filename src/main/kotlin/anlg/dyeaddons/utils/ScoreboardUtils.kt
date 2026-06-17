package anlg.dyeaddons.utils

import anlg.dyeaddons.DyeAddons
import anlg.dyeaddons.utils.ChatUtils.removeFormatting
import net.minecraft.world.scores.DisplaySlot
import net.minecraft.world.scores.PlayerTeam

object ScoreboardUtils {
    /*
     * Get the Scoreboard line after the specified startsWith string. No formatting preserved.
     * E.g. getLineAfter("Purse:") will return the trimmed text after the "Purse:" string.
     * @param startsWith The string to search for.
     * @returns {String} The line after the specified startsWith string.
     */
    fun getLineAfter(startsWith: String): String {
        val scoreboard = DyeAddons.mc.level?.scoreboard ?: return ""
        val objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR) ?: return ""

        scoreboard.listPlayerScores(objective)
            .filter { entry -> !entry.isHidden }
            .map { entry ->
                val team = scoreboard.getPlayersTeam(entry.owner)
                PlayerTeam.formatNameForTeam(team, entry.ownerName()).string.removeFormatting()
            }.forEach { text ->
                if (text.contains(startsWith)) {
                    val entryIndex = text.indexOf(startsWith)
                    if (entryIndex != -1) {
                        val value = text.substring(entryIndex + startsWith.length).removeFormatting().trim()
                        return value.ifEmpty { "" }
                    }
                }
            }
        return ""
    }
}