package rosatech.modules.botania.recipes;

import gregtech.api.recipes.RecipeMap;
import gregtech.core.sound.GTSoundEvents;
import rosatech.modules.botania.api.recipes.RecipeBuilderMana;

public class BotaniaRecipeMaps {

    public static final RecipeMap<RecipeBuilderMana> MANA_VACUUM_RECIPES = new RecipeMap<>("test_vacuum", 1, 1, 1, 1,  new RecipeBuilderMana(), false)
            .setSound(GTSoundEvents.COOLING);


}
