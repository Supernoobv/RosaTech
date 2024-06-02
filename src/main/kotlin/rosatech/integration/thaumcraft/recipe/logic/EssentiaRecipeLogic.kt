package rosatech.integration.thaumcraft.recipe.logic

import gregtech.api.capability.IEnergyContainer
import gregtech.api.capability.impl.RecipeLogicEnergy
import gregtech.api.metatileentity.MetaTileEntity
import gregtech.api.recipes.Recipe
import gregtech.api.recipes.RecipeMap
import net.minecraftforge.items.IItemHandlerModifiable
import rosatech.integration.thaumcraft.recipe.property.EssentiaProperty
import thaumcraft.api.aspects.AspectList
import thaumcraft.api.aspects.IAspectContainer
import java.util.function.Supplier

class EssentiaRecipeLogic(
    metaTileEntity: MetaTileEntity,
    recipeMap: RecipeMap<*>,
    energyContainer: Supplier<IEnergyContainer>
) : RecipeLogicEnergy(metaTileEntity, recipeMap, energyContainer) {

    var aspectsOutput: AspectList? = null

    override fun setupAndConsumeRecipeInputs(recipe: Recipe, importInventory: IItemHandlerModifiable): Boolean {
        if (!super.setupAndConsumeRecipeInputs(recipe, importInventory)) return false
        if (metaTileEntity !is IAspectContainer) return true

        if (recipe.hasProperty(EssentiaProperty.instance)) {
            val aspectEntry = recipe.getProperty(EssentiaProperty.instance, null)
            if (aspectEntry != null) {
                val container = metaTileEntity as IAspectContainer

                for (aspectMapEntry in aspectEntry.inputEssentia.aspects) {
                    if (!container.takeFromContainer(aspectMapEntry.key, aspectMapEntry.value)) {
                        return false
                    }
                }
            }
        }

        return true
    }

    override fun setupRecipe(recipe: Recipe) {
        super.setupRecipe(recipe)
        if (metaTileEntity !is IAspectContainer) return
        if (recipe.hasProperty(EssentiaProperty.instance)) {
            val aspectEntry = recipe.getProperty(EssentiaProperty.instance, null)
            if (aspectEntry != null && aspectEntry.outputEssentia.aspects.isNotEmpty()) {
                aspectsOutput = aspectEntry.outputEssentia
            }
        }
    }

    override fun checkRecipe(recipe: Recipe): Boolean {
        if (!super.checkRecipe(recipe)) return false
        if (!recipe.hasProperty(EssentiaProperty.instance)) return true

        if (metaTileEntity !is IAspectContainer) return true

        val aspectEntry = recipe.getProperty(EssentiaProperty.instance, null)
        val container = metaTileEntity as IAspectContainer

        if (aspectEntry != null) {
            for (aspectMapEntry in aspectEntry.inputEssentia.aspects) {
                if (!container.doesContainerContainAmount(aspectMapEntry.key, aspectMapEntry.value)) return false
            }

            for (aspectMapEntry in aspectEntry.outputEssentia.aspects) {
                if (!container.doesContainerAccept(aspectMapEntry.key)) return false
            }

        }

        return true
    }

    override fun completeRecipe() {
        super.completeRecipe()
        if (aspectsOutput != null) {
            if (metaTileEntity !is IAspectContainer) return

            val container = metaTileEntity as IAspectContainer

            for (aspectEntry in aspectsOutput!!.aspects) {
                val leftOver: Float = container.addToContainer(aspectEntry.key, aspectEntry.value).toFloat()
                // TODO flux venting hatches? idk
            }
        }
    }



}