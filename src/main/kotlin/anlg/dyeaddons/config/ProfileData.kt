package anlg.dyeaddons.config

import anlg.dyeaddons.data.Dye

class ProfileData {
    val dyeData : MutableMap<Dye, DyeData> = Dye.entries.associateWith { DyeData() }.toMutableMap()
}