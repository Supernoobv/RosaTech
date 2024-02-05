package rosatech.modules.botania.recipes;

import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.ore.OrePrefix;

import static gregtech.api.unification.material.Materials.*;

public class BotaniaTestRecipe {

    public static void init() {
        BotaniaRecipeMaps.MANA_VACUUM_RECIPES.recipeBuilder().EUt(32).duration(200)
                .inputs(OreDictUnifier.get(OrePrefix.dust, Clay, 2))
                .outputs(OreDictUnifier.get(OrePrefix.dust, Brick, 1))
                .manaRequired(1000)
                .buildAndRegister();

        BotaniaRecipeMaps.MANA_VACUUM_RECIPES.recipeBuilder().EUt(32).duration(200)
                .inputs(OreDictUnifier.get(OrePrefix.dust, Brick, 1))
                .outputs(OreDictUnifier.get(OrePrefix.dust, Clay, 2))
                .manaRequired(-1000)
                .buildAndRegister();

    }
}
