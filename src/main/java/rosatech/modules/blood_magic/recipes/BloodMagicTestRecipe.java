package rosatech.modules.blood_magic.recipes;

import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.ore.OrePrefix;
import rosatech.modules.botania.recipes.BotaniaRecipeMaps;

import static gregtech.api.unification.material.Materials.Brick;
import static gregtech.api.unification.material.Materials.Clay;

public class BloodMagicTestRecipe {
    public static void init() {
        BloodMagicRecipeMaps.LIFE_ESSENCE_VACUUM_RECIPES.recipeBuilder().EUt(32).duration(200)
                .inputs(OreDictUnifier.get(OrePrefix.dust, Clay, 2))
                .outputs(OreDictUnifier.get(OrePrefix.dust, Brick, 1))
                .lifeEssence(3000)
                .buildAndRegister();

        BloodMagicRecipeMaps.LIFE_ESSENCE_VACUUM_RECIPES.recipeBuilder().EUt(32).duration(200)
                .inputs(OreDictUnifier.get(OrePrefix.dust, Brick, 1))
                .outputs(OreDictUnifier.get(OrePrefix.dust, Clay, 2))
                .lifeEssence(-100000)
                .buildAndRegister();
    }
}
