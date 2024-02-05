package rosatech.modules.botania.api.recipes;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.recipes.Recipe;
import org.jetbrains.annotations.NotNull;
import rosatech.modules.botania.api.capability.IManaTank;
import rosatech.modules.botania.api.metatileentity.multiblock.BotaniaMultiblockAbility;
import rosatech.modules.botania.api.recipes.recipeproperties.ManaProperty;

import java.util.List;

public class RecipeLogicMana extends MultiblockRecipeLogic {
    public RecipeLogicMana(RecipeMapMultiblockController tileEntity) {
        super(tileEntity);
    }

    @Override
    protected void setupRecipe(Recipe recipe) {
        super.setupRecipe(recipe);
        if (recipe.hasProperty(ManaProperty.getInstance())) {
            int toDrain = recipe.getProperty(ManaProperty.getInstance(), 0);
            fillManaTanks(toDrain, toDrain < 0);
        }
    }

    @Override
    public boolean checkRecipe(@NotNull Recipe recipe) {
        if (!super.checkRecipe(recipe)) {
            return false;
        }

        if (!recipe.hasProperty(ManaProperty.getInstance())) {
            return true;
        }

        int toDrain = recipe.getProperty(ManaProperty.getInstance(), 0);
        return toDrain > 0 ? (getTotalManaStorage(false) > toDrain) : (getTotalManaStorage(true) < getTotalManaCapacity(true));
    }



    public int getTotalManaStorage(boolean isExport) {
        RecipeMapMultiblockController controller = (RecipeMapMultiblockController) metaTileEntity;
        int toReturn = 0;

        List<IManaTank> manaTanks = isExport ? controller.getAbilities(BotaniaMultiblockAbility.EXPORT_MANA) : controller.getAbilities(BotaniaMultiblockAbility.IMPORT_MANA);
        for (int i = 0; i < manaTanks.size(); i++) {
            IManaTank tank = manaTanks.get(i);
            toReturn += tank.getAmount();
        }


        return toReturn;
    }

    public int getTotalManaCapacity(boolean isExport) {
        RecipeMapMultiblockController controller = (RecipeMapMultiblockController) metaTileEntity;

        int toReturn = 0;

        List<IManaTank> manaTanks = isExport ? controller.getAbilities(BotaniaMultiblockAbility.EXPORT_MANA) : controller.getAbilities(BotaniaMultiblockAbility.IMPORT_MANA);
        for (int i = 0; i < manaTanks.size(); i++) {
            IManaTank tank = manaTanks.get(i);
            toReturn += tank.getCapacity();
        }

        return toReturn;
    }

    public void fillManaTanks(int total, boolean isExport) {
        RecipeMapMultiblockController controller = (RecipeMapMultiblockController) metaTileEntity;

        int toFill = total;

        List<IManaTank> manaTanks = isExport ? controller.getAbilities(BotaniaMultiblockAbility.EXPORT_MANA) : controller.getAbilities(BotaniaMultiblockAbility.IMPORT_MANA);
        for (int i = 0; i < manaTanks.size(); i++) {
            IManaTank tank = manaTanks.get(i);
            if (isExport && tank.getAmount() < tank.getCapacity()) {
                toFill -= tank.fill(-toFill, true);
            } else if (!isExport && tank.getAmount() > 0) {
                toFill -= tank.drain(toFill, true);
            }
        }
    }
}
