package rosatech.modules.blood_magic.api.recipes;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.recipes.Recipe;
import org.jetbrains.annotations.NotNull;
import rosatech.modules.blood_magic.api.capability.ISoulTank;
import rosatech.modules.blood_magic.api.metatileentity.multiblock.BloodMagicMultiblockAbility;
import rosatech.modules.blood_magic.api.recipes.recipeproperties.LifeEssenceProperty;

import java.util.List;

public class RecipeLogicLifeEssence extends MultiblockRecipeLogic {

    public RecipeLogicLifeEssence(RecipeMapMultiblockController tileEntity) {
        super(tileEntity);
    }

    @Override
    public boolean checkRecipe(@NotNull Recipe recipe) {
        if (!super.checkRecipe(recipe)) {
            return false;
        }

        if (!recipe.hasProperty(LifeEssenceProperty.getInstance())) {
            return true;
        }

        int toDrain = recipe.getProperty(LifeEssenceProperty.getInstance(), 0);
        return toDrain <= 0 || getTotalLifeEssenceStorage(false) > toDrain;
    }

    public int getTotalLifeEssenceStorage(boolean isExport) {
        RecipeMapMultiblockController controller = (RecipeMapMultiblockController) metaTileEntity;
        int toReturn = 0;

        List<ISoulTank> soulTanks = isExport ? controller.getAbilities(BloodMagicMultiblockAbility.EXPORT_LIFE_ESSENCE) : controller.getAbilities(BloodMagicMultiblockAbility.IMPORT_LIFE_ESSENCE);
        for (int i = 0; i < soulTanks.size(); i++) {
            ISoulTank tank = soulTanks.get(i);
            toReturn += tank.getAmount();
        }


        return toReturn;
    }

    public void sendPendingRequest(int total, boolean isExport) {
        RecipeMapMultiblockController controller = (RecipeMapMultiblockController) metaTileEntity;

        int toFill = total;

        List<ISoulTank> soulTanks = isExport ? controller.getAbilities(BloodMagicMultiblockAbility.EXPORT_LIFE_ESSENCE) : controller.getAbilities(BloodMagicMultiblockAbility.IMPORT_LIFE_ESSENCE);
        for (int i = 0; i < soulTanks.size(); i++) {
            ISoulTank tank = soulTanks.get(i);
            if (isExport && tank.getAmount() < tank.getCapacity()) {
                tank.pendRequest(-toFill);
            } else if (!isExport && tank.getAmount() > 0) {
                tank.pendRequest(toFill);
            }
        }
    }

    public void completePendingRequests(boolean isExport) {
        RecipeMapMultiblockController controller = (RecipeMapMultiblockController) metaTileEntity;

        List<ISoulTank> soulTanks = isExport ? controller.getAbilities(BloodMagicMultiblockAbility.EXPORT_LIFE_ESSENCE) : controller.getAbilities(BloodMagicMultiblockAbility.IMPORT_LIFE_ESSENCE);
        for (int i = 0; i < soulTanks.size(); i++) {
            ISoulTank tank = soulTanks.get(i);
            if (tank.hasPendingRequest()) {
                tank.completeRequest(isExport);
            }
        }
    }

    public void fillSoulTanks(int total, boolean isExport) {
        RecipeMapMultiblockController controller = (RecipeMapMultiblockController) metaTileEntity;

        int toFill = total;

        List<ISoulTank> soulTanks = isExport ? controller.getAbilities(BloodMagicMultiblockAbility.EXPORT_LIFE_ESSENCE) : controller.getAbilities(BloodMagicMultiblockAbility.IMPORT_LIFE_ESSENCE);
        for (int i = 0; i < soulTanks.size(); i++) {
            ISoulTank tank = soulTanks.get(i);
            if (isExport && tank.getAmount() < tank.getCapacity()) {
                toFill -= tank.fill(-toFill, true);
            } else if (!isExport && tank.getAmount() > 0) {
                toFill -= tank.drain(toFill, true);
            }
        }
    }

    @Override
    protected void setupRecipe(Recipe recipe) {
        super.setupRecipe(recipe);
        if (recipe.hasProperty(LifeEssenceProperty.getInstance())) {
            int toFill = recipe.getProperty(LifeEssenceProperty.getInstance(), 0);
            if (toFill < 0) {
                sendPendingRequest(toFill, true);
                return;
            }

            fillSoulTanks(toFill, false);
        }
    }

    @Override
    protected void completeRecipe() {
        super.completeRecipe();
        completePendingRequests(true);
    }
}
