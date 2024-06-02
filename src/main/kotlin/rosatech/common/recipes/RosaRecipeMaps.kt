package rosatech.common.recipes

import gregtech.api.gui.GuiTextures
import gregtech.api.gui.widgets.ProgressWidget.MoveType
import gregtech.api.recipes.RecipeMap
import gregtech.core.sound.GTSoundEvents
import net.minecraftforge.fml.common.Loader
import rosatech.integration.thaumcraft.recipe.EssentiaRecipeBuilder
import rosatech.integration.thaumcraft.recipe.RosaThaumcraftRecipeMaps


class RosaRecipeMaps {

    companion object {
        fun preInit() {

            if (Loader.isModLoaded("thaumcraft")) {
                RosaThaumcraftRecipeMaps.preInit()
            }

        }
    }
}