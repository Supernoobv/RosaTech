package rosatech.modules.blood_magic.recipes;

import gregtech.api.recipes.RecipeMap;
import gregtech.core.sound.GTSoundEvents;
import rosatech.modules.blood_magic.api.recipes.RecipeBuilderLifeEssence;

public class BloodMagicRecipeMaps {

    public static final RecipeMap<RecipeBuilderLifeEssence> LIFE_ESSENCE_VACUUM_RECIPES = new RecipeMap<>("test_vacuum_lf", 1, 1, 1, 1,  new RecipeBuilderLifeEssence(), false)
            .setSound(GTSoundEvents.COOLING);
}
