package rosatech.integration.thaumcraft.recipe.logic

import gregtech.api.capability.IEnergyContainer
import gregtech.api.capability.impl.AbstractRecipeLogic
import gregtech.api.capability.impl.RecipeLogicEnergy
import gregtech.api.metatileentity.MetaTileEntity
import gregtech.api.metatileentity.multiblock.ParallelLogicType
import gregtech.api.recipes.Recipe
import gregtech.api.recipes.RecipeMap
import gregtech.api.recipes.recipeproperties.IRecipePropertyStorage
import net.minecraftforge.items.IItemHandlerModifiable
import rosatech.integration.thaumcraft.recipe.property.EssentiaProperty
import thaumcraft.api.aspects.IAspectContainer
import java.util.function.Supplier

class EssentiaFuelRecipeLogic(
    metaTileEntity: MetaTileEntity,
    recipeMap: RecipeMap<*>,
    energyContainer: Supplier<IEnergyContainer>
) : RecipeLogicEnergy(metaTileEntity, recipeMap, energyContainer) {

    override fun setupAndConsumeRecipeInputs(recipe: Recipe, importInventory: IItemHandlerModifiable): Boolean {
        if (!super.setupAndConsumeRecipeInputs(recipe, importInventory)) return false

        if (metaTileEntity !is IAspectContainer) return true

        if (recipe.hasProperty(EssentiaProperty.instance)) {
            val aspectEntry = recipe.getProperty(EssentiaProperty.instance, null)
            if (aspectEntry != null) {
                val container = metaTileEntity as IAspectContainer

                for (aspectEntry in aspectEntry.inputEssentia.aspects) {
                    if (!container.takeFromContainer(aspectEntry.key, aspectEntry.value)) {
                        return false
                    }
                }
            }
        }

        return true
    }

    override fun checkRecipe(recipe: Recipe): Boolean {
        if (!super.checkRecipe(recipe)) return false
        if (!recipe.hasProperty(EssentiaProperty.instance)) return true

        if (metaTileEntity !is IAspectContainer) return true

        val aspectEntry = recipe.getProperty(EssentiaProperty.instance, null)
        val container = metaTileEntity as IAspectContainer


        if (aspectEntry != null) {
            for (aspectEntry in aspectEntry.inputEssentia.aspects) {
                if (!container.doesContainerContainAmount(aspectEntry.key, aspectEntry.value)) return false
            }

            for (aspectMapEntry in aspectEntry.outputEssentia.aspects) {
                if (!container.doesContainerAccept(aspectMapEntry.key)) return false
            }

        }

        return true
    }

    override fun prepareRecipe(recipe: Recipe?): Boolean {
        if (recipe != null && setupAndConsumeRecipeInputs(recipe, inputInventory)) {
            setupRecipe(recipe)
            return true
        }

        return false
    }

    override fun getParallelLogicType(): ParallelLogicType = ParallelLogicType.MULTIPLY

    override fun consumesEnergy(): Boolean = false

    override fun hasEnoughPower(resultOverclock: IntArray): Boolean = true

    override fun modifyOverclockPost(overclockResults: IntArray, storage: IRecipePropertyStorage) {
        super.modifyOverclockPost(overclockResults, storage)

        overclockResults[0] = -overclockResults[0]
    }

    override fun getParallelLimit(): Int = Integer.MAX_VALUE

    override fun isAllowOverclocking(): Boolean = false
}