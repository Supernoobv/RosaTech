package rosatech.integration.thaumcraft.recipe

import com.google.common.collect.ImmutableTable
import gregtech.api.recipes.Recipe
import gregtech.api.recipes.RecipeBuilder
import gregtech.api.util.ValidationResult
import rosatech.api.RosaLog
import rosatech.integration.thaumcraft.recipe.property.EssentiaProperty
import thaumcraft.api.aspects.Aspect
import thaumcraft.api.aspects.AspectList

class EssentiaRecipeBuilder : RecipeBuilder<EssentiaRecipeBuilder> {
    private val internalInputMap: MutableMap<Aspect, Int> = mutableMapOf()
    private val internalOutputMap: MutableMap<Aspect, Int> = mutableMapOf()

    constructor(): super()

    constructor(builder: EssentiaRecipeBuilder) : super(builder)

    override fun copy(): EssentiaRecipeBuilder = EssentiaRecipeBuilder(this)

    override fun applyProperty(key: String, value: Any?): Boolean {
        if (key == EssentiaProperty.KEY) {
            if (value is EssentiaProperty.EssentiaEntry) {
                return applyProperty(EssentiaProperty.instance, value)
            }
        }

        return super.applyProperty(key, value)
    }

    fun aspect(tag: String, amount: Int, isOutput: Boolean): EssentiaRecipeBuilder {
        val aspect = Aspect.getAspect(tag)
        if (aspect != null) {
            aspect(aspect, amount, isOutput)
        } else RosaLog.logger.error("EssentiaRecipeBuilder throws: Aspect $tag is null! this aspect will not show up in your recipe.")

        return this
    }

    fun aspect(tag: String, amount: Int): EssentiaRecipeBuilder {
        aspect(tag, amount, false)
        return this
    }

    fun aspect(aspect: Aspect, amount: Int, isOutput: Boolean): EssentiaRecipeBuilder {
        if (isOutput) {
            internalOutputMap.put(aspect, amount)
        } else {
            internalInputMap.put(aspect, amount)
        }

        return this
    }

    fun aspect(aspect: Aspect, amount: Int): EssentiaRecipeBuilder {
        aspect(aspect, amount, false)

        return this
    }

    override fun build(): ValidationResult<Recipe> {
        val inputAspectList = AspectList()
        val outputAspectList = AspectList()

        for (aspectEntry in internalInputMap) {
            inputAspectList.add(aspectEntry.key, aspectEntry.value)
        }

        for (aspectEntry in internalOutputMap) {
            outputAspectList.add(aspectEntry.key, aspectEntry.value)
        }
        super.applyProperty(EssentiaProperty.instance, EssentiaProperty.EssentiaEntry(inputAspectList, outputAspectList))

        return super.build()
    }


}