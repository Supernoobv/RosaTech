package rosatech.integration.thaumcraft.recipe.logic

import gregtech.api.capability.IHeatingCoil
import gregtech.api.capability.IMultipleTankHandler
import gregtech.api.capability.impl.HeatingCoilRecipeLogic
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController
import gregtech.api.recipes.Recipe
import gregtech.api.util.GTUtility
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.items.IItemHandlerModifiable
import rosatech.api.util.RosaThaumcraftUtils
import rosatech.client.renderer.textures.RosaTextures
import rosatech.integration.thaumcraft.metatileentites.multiblock.ThaumcraftMultiblockAbility
import rosatech.integration.thaumcraft.recipe.ESSENTIA_SMELTERY_RECIPES
import rosatech.integration.thaumcraft.recipe.RosaThaumcraftRecipeMaps
import rosatech.integration.thaumcraft.recipe.property.EssentiaProperty
import thaumcraft.api.aspects.AspectList
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager
import kotlin.math.min

class EssentiaSmelteryRecipeLogic(
    metaTileEntity: RecipeMapMultiblockController
) : HeatingCoilRecipeLogic(metaTileEntity)
{
    var aspectsOutput: AspectList? = null

    override fun checkRecipe(recipe: Recipe): Boolean {
        if (!super.checkRecipe(recipe)) return false

        if (!recipe.hasProperty(EssentiaProperty.instance)) return true

        val metaTileEntity = metaTileEntity as RecipeMapMultiblockController
        val aspectEntry = recipe.getProperty(EssentiaProperty.instance, null)
        val containers = metaTileEntity.getAbilities(ThaumcraftMultiblockAbility.EXPORT_ESSENTIA)

        return RosaThaumcraftUtils.doContainersAccept(containers, aspectEntry.outputEssentia)
    }

    override fun setupRecipe(recipe: Recipe) {
        super.setupRecipe(recipe)
        if (recipe.hasProperty(EssentiaProperty.instance)) {
            this.aspectsOutput = recipe.getProperty(EssentiaProperty.instance, null).outputEssentia
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

        forceRecipeRecheck()
    }

    override fun invalidate() {
        super.invalidate()
        aspectsOutput = null
    }


    override fun findRecipe(
        maxVoltage: Long,
        inputs: IItemHandlerModifiable,
        fluidInputs: IMultipleTankHandler
    ): Recipe? {

        for (i in 0 until inputs.slots) {
            val itemStack = inputs.getStackInSlot(i)

            val aspectList = ThaumcraftCraftingManager.getObjectTags(itemStack)
            if (aspectList != null && aspectList.size() != 0) {

                val builder = ESSENTIA_SMELTERY_RECIPES.recipeBuilder()
                    .inputs(GTUtility.copy(1, itemStack))
                    .duration(128).EUt(128)

                for (aspectEntry in aspectList.aspects) {
                    builder.aspect(aspectEntry.key, aspectEntry.value * min(parallelLimit, itemStack.count), true)
                }

                return builder.build().result

            }
        }

        return null
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

    override fun getParallelLimit(): Int {
        val metaTileEntity = metaTileEntity

        if (metaTileEntity is RecipeMapMultiblockController && metaTileEntity is IHeatingCoil) {
                return 10 * metaTileEntity.currentTemperature / 1800
        }

        return 10
    }



}