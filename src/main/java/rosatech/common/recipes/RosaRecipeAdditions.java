package rosatech.common.recipes;

import rosatech.api.util.RosaMods;
import rosatech.modules.blood_magic.recipes.BloodMagicRecipeAdditions;
import rosatech.modules.botania.recipes.BotaniaRecipeAdditions;

public class RosaRecipeAdditions {
    public static void init() {
        if (RosaMods.Botania.isModLoaded()) {
            BotaniaRecipeAdditions.init();
        }
        if (RosaMods.BloodMagic.isModLoaded()) {
            BloodMagicRecipeAdditions.init();
        }

    }
}
