package rosatech.modules.botania.api.recipes;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.recipes.Recipe;
import org.jetbrains.annotations.NotNull;
import rosatech.api.capability.IIntegerTank;
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
            if (toDrain < 0) {
                sendPendingRequest(toDrain, true);
                return;
            }
            fillManaTanks(toDrain, false);
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

        List<IIntegerTank> manaTanks = isExport ? controller.getAbilities(BotaniaMultiblockAbility.EXPORT_MANA) : controller.getAbilities(BotaniaMultiblockAbility.IMPORT_MANA);
        for (int i = 0; i < manaTanks.size(); i++) {
            IIntegerTank tank = manaTanks.get(i);
            toReturn += tank.getAmount();
        }


        return toReturn;
    }

    public int getTotalManaCapacity(boolean isExport) {
        RecipeMapMultiblockController controller = (RecipeMapMultiblockController) metaTileEntity;

        int toReturn = 0;

        List<IIntegerTank> manaTanks = isExport ? controller.getAbilities(BotaniaMultiblockAbility.EXPORT_MANA) : controller.getAbilities(BotaniaMultiblockAbility.IMPORT_MANA);
        for (int i = 0; i < manaTanks.size(); i++) {
            IIntegerTank tank = manaTanks.get(i);
            toReturn += tank.getCapacity();
        }

        return toReturn;
    }

    public void sendPendingRequest(int total, boolean isExport) {
        RecipeMapMultiblockController controller = (RecipeMapMultiblockController) metaTileEntity;

        int toFill = total;

        List<IIntegerTank> manaTanks = isExport ? controller.getAbilities(BotaniaMultiblockAbility.EXPORT_MANA) : controller.getAbilities(BotaniaMultiblockAbility.IMPORT_MANA);
        for (int i = 0; i < manaTanks.size(); i++) {
            IIntegerTank tank = manaTanks.get(i);
            if (isExport && tank.getAmount() < tank.getCapacity()) {
                tank.pendRequest(-toFill);
            } else if (!isExport && tank.getAmount() > 0) {
                tank.pendRequest(toFill);
            }
        }
    }

    public void completePendingRequests(boolean isExport) {
        RecipeMapMultiblockController controller = (RecipeMapMultiblockController) metaTileEntity;

        List<IIntegerTank> manaTanks = isExport ? controller.getAbilities(BotaniaMultiblockAbility.EXPORT_MANA) : controller.getAbilities(BotaniaMultiblockAbility.IMPORT_MANA);
        for (int i = 0; i < manaTanks.size(); i++) {
            IIntegerTank tank = manaTanks.get(i);
            if (tank.hasPendingRequest()) {
                tank.completeRequest(isExport);
            }
        }
    }
    
    public void fillManaTanks(int total, boolean isExport) {
        RecipeMapMultiblockController controller = (RecipeMapMultiblockController) metaTileEntity;

        int toFill = total;

        List<IIntegerTank> manaTanks = isExport ? controller.getAbilities(BotaniaMultiblockAbility.EXPORT_MANA) : controller.getAbilities(BotaniaMultiblockAbility.IMPORT_MANA);
        for (int i = 0; i < manaTanks.size(); i++) {
            IIntegerTank tank = manaTanks.get(i);
            if (isExport && tank.getAmount() < tank.getCapacity()) {
                toFill -= tank.fill(-toFill, true);
            } else if (!isExport && tank.getAmount() > 0) {
                toFill -= tank.drain(toFill, true);
            }
        }
    }

    @Override
    protected void completeRecipe() {
        super.completeRecipe();
        completePendingRequests(true);
    }
    
    
}
