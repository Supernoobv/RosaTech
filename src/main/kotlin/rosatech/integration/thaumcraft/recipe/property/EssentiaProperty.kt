package rosatech.integration.thaumcraft.recipe.property

import gregtech.api.recipes.recipeproperties.RecipeProperty
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraftforge.fml.client.config.GuiUtils
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import rosatech.api.util.RosaThaumcraftUtils
import rosatech.integration.thaumcraft.recipe.property.EssentiaProperty.EssentiaEntry
import thaumcraft.api.aspects.Aspect
import thaumcraft.api.aspects.AspectList
import java.awt.Color


class EssentiaProperty private constructor(): RecipeProperty<EssentiaEntry>(KEY, EssentiaEntry::class.java) {



    companion object {
        const val KEY = "aspect_internal"

        val instance: EssentiaProperty by lazy { EssentiaProperty() }

    }

    @SideOnly(Side.CLIENT)
    override fun drawInfo(minecraft: Minecraft, x: Int, y: Int, color: Int, value: Any, mouseX: Int, mouseY: Int) {
        val entry = castValue(value)
        if (entry.inputEssentia.aspects.isNotEmpty() || entry.outputEssentia.aspects.isNotEmpty()) {
            minecraft.fontRenderer.drawString(
                I18n.format("rosatech.machine.property.essentia.jei_header"),
                x,
                y,
                color
            )
            val y = y + 10
            var yOffset = 0

            var xOffset = x

            var tooltipToRender: (() -> Unit)? = null

            for (aspectEntry in entry.inputEssentia.aspects) {
                val aspect = aspectEntry.key
                val count = aspectEntry.value


                RosaThaumcraftUtils.renderAspectInJei(aspect, count, minecraft, x + xOffset, y + yOffset, color, value, mouseX, mouseY, "Input")
                if (mouseX >= x + xOffset && mouseY >= y + yOffset && x + xOffset + 16 > mouseX && y + yOffset + 16 > mouseY) {
                    tooltipToRender = {
                        RosaThaumcraftUtils.renderAspectTooltip(aspect, count, minecraft, x + xOffset, y + yOffset, color, value, mouseX, mouseY)
                    }
                }
                xOffset += 18
                if (xOffset % 180 == 0) {
                    yOffset += 18
                    xOffset = x
                }

            }

            for (aspectEntry in entry.outputEssentia.aspects) {
                val aspect = aspectEntry.key
                val count = aspectEntry.value

                RosaThaumcraftUtils.renderAspectInJei(aspect, count, minecraft, x + xOffset, y + yOffset, color, value, mouseX, mouseY, "Output")
                if (mouseX >= x + xOffset && mouseY >= y + yOffset && x + xOffset + 16 > mouseX && y + yOffset + 16 > mouseY) {
                    tooltipToRender = {
                        RosaThaumcraftUtils.renderAspectTooltip(aspect, count, minecraft, x + xOffset, y + yOffset, color, value, mouseX, mouseY)
                    }
                }

                xOffset += 18
                if (xOffset % 180 == 0) {
                    yOffset += 18
                    xOffset = x
                }
            }

            if (tooltipToRender != null) tooltipToRender.invoke()
        }
    }

    @SideOnly(Side.CLIENT)
    override fun drawInfo(p0: Minecraft?, p1: Int, p2: Int, p3: Int, p4: Any?) {}


    data class EssentiaEntry(val inputEssentia: AspectList, val outputEssentia: AspectList)

}