package rosatech.integration.thaumcraft.recipe.logic

import gregtech.api.capability.impl.MultiblockRecipeLogic
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController
import gregtech.api.recipes.Recipe
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.items.IItemHandlerModifiable
import rosatech.api.util.RosaThaumcraftUtils
import rosatech.integration.thaumcraft.metatileentites.multiblock.ThaumcraftMultiblockAbility
import rosatech.integration.thaumcraft.recipe.property.EssentiaProperty
import thaumcraft.api.aspects.AspectList

class EssentiaMultiblockRecipeLogic(
    metaTileEntity: RecipeMapMultiblockController
) : MultiblockRecipeLogic(metaTileEntity) {

    var aspectsOutput: AspectList? = null

    override fun checkRecipe(recipe: Recipe): Boolean {
        if (!super.checkRecipe(recipe)) return false

        if (!recipe.hasProperty(EssentiaProperty.instance)) return true

        val metaTileEntity = metaTileEntity as RecipeMapMultiblockController
        val aspectEntry = recipe.getProperty(EssentiaProperty.instance, null)

        val exportContainers = metaTileEntity.getAbilities(ThaumcraftMultiblockAbility.EXPORT_ESSENTIA)
        val importContainers = metaTileEntity.getAbilities(ThaumcraftMultiblockAbility.IMPORT_ESSENTIA)

        if (!RosaThaumcraftUtils.doContainersAccept(exportContainers, aspectEntry.outputEssentia)) return false
        if (!RosaThaumcraftUtils.doContainersContain(importContainers, aspectEntry.inputEssentia)) return false

        return true
    }

    override fun setupAndConsumeRecipeInputs(recipe: Recipe, importInventory: IItemHandlerModifiable): Boolean {
        if (!super.setupAndConsumeRecipeInputs(recipe, importInventory)) return false
        if (!recipe.hasProperty(EssentiaProperty.instance)) return true

        val metaTileEntity = metaTileEntity as RecipeMapMultiblockController

        val property = recipe.getProperty(EssentiaProperty.instance, null)
        val importContainers = metaTileEntity.getAbilities(ThaumcraftMultiblockAbility.IMPORT_ESSENTIA)

        return RosaThaumcraftUtils.drainContainers(importContainers, property.inputEssentia)
    }

    override fun setupRecipe(recipe: Recipe) {
        super.setupRecipe(recipe)
        if (recipe.hasProperty(EssentiaProperty.instance)) {
            val property  = recipe.getProperty(EssentiaProperty.instance, null)
            if (property.outputEssentia.size() != 0) {
                this.aspectsOutput = property.outputEssentia
            }
        }
    }

    override fun completeRecipe() {
        super.completeRecipe()

        if (aspectsOutput != null) {
            val metaTileEntity = metaTileEntity as RecipeMapMultiblockController
            val containers = metaTileEntity.getAbilities(ThaumcraftMultiblockAbility.EXPORT_ESSENTIA)

            RosaThaumcraftUtils.fillContainers(containers, aspectsOutput!!)

            aspectsOutput = null
        }
    }

    override fun invalidate() {
        super.invalidate()
        aspectsOutput = null
    }

    override fun serializeNBT(): NBTTagCompound {
        val compound = super.serializeNBT()

        if (this.progressTime > 0) {
            aspectsOutput?.writeToNBT(compound, "output")
        }

        return compound
    }

    override fun deserializeNBT(compound: NBTTagCompound) {
        super.deserializeNBT(compound)
        if (this.progressTime > 0) {
            aspectsOutput?.readFromNBT(compound.getCompoundTag("output"))
        }
    }

}