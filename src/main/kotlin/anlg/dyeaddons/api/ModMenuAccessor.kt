package anlg.dyeaddons.api

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import net.minecraft.client.gui.screens.Screen
import anlg.dyeaddons.gui.DyesScreen

class ModMenuAccessor : ModMenuApi{
    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory { parent: Screen? ->
            DyesScreen(parent)
        }
    }
}