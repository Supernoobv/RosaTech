package rosatech.integration.thaumcraft.recipe

import gregtech.api.gui.GuiTextures
import gregtech.api.gui.widgets.ProgressWidget.MoveType
import gregtech.api.recipes.RecipeMap
import gregtech.core.sound.GTSoundEvents

lateinit var ESSENTIA_GENERATOR_RECIPES: RecipeMap<EssentiaRecipeBuilder>
lateinit var ESSENTIA_SMELTERY_RECIPES: RecipeMap<EssentiaRecipeBuilder>

class RosaThaumcraftRecipeMaps {

    companion object {
        fun preInit() {
            ESSENTIA_GENERATOR_RECIPES = RecipeMap("essentia_generator", 1, 0, 0, 0, EssentiaRecipeBuilder(), false)
                .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, MoveType.HORIZONTAL)
                .setSound(GTSoundEvents.ARC)
                .allowEmptyOutput()

            ESSENTIA_SMELTERY_RECIPES = RecipeMap("essentia_smeltery", 1, 0, 0, 0, EssentiaRecipeBuilder(), true)
                .setSound(GTSoundEvents.ARC)
                .allowEmptyOutput()
        }
    }
}