package rosatech.api.util

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.client.config.GuiUtils
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GLSync
import thaumcraft.api.ThaumcraftApiHelper
import thaumcraft.api.aspects.Aspect
import thaumcraft.api.aspects.AspectList
import thaumcraft.api.aspects.IAspectContainer
import thaumcraft.api.aspects.IEssentiaTransport
import thaumcraft.api.aura.AuraHelper
import thaumcraft.common.tiles.essentia.TileJarFillable
import java.awt.Color

class RosaThaumcraftUtils {

    companion object {
        /**
         * Fills the container specified with essentia from nearby connectable tiles.
         *
         * @param transport The container to fill
         * @param world
         * @param pos
         * @param spill If flux should be released into the world if any leftovers remain.
         */
        fun fill(transport: IEssentiaTransport, world: World, pos: BlockPos, spill: Boolean) {
            for (face in EnumFacing.VALUES) {
                if (!transport.canInputFrom(face)) continue

                val te = ThaumcraftApiHelper.getConnectableTile(world, pos, face)
                if (te != null && te is IEssentiaTransport) {
                    if (!te.canOutputTo(face.opposite)) continue

                    var currentSuction = transport.getSuctionType(face)
                    if (currentSuction == null) {
                        if (te.getEssentiaAmount(face.opposite) > 0 && te.getSuctionAmount(face.opposite) < transport.getSuctionAmount(face) && transport.getSuctionAmount(face) >= te.minimumSuction) {
                            currentSuction = te.getEssentiaType(face.opposite)
                        }
                    }
                    if (te.getEssentiaType(face.opposite) == currentSuction &&
                        te.getEssentiaAmount(face.opposite) > 0 &&
                        transport.getSuctionAmount(face) > te.getSuctionAmount(face.opposite) &&
                        transport.getSuctionAmount(face) >= te.minimumSuction
                        ) {

                        val taken = te.takeEssentia(currentSuction, transport.getSuctionAmount(face), face.opposite)
                        val leftOver: Float = transport.addEssentia(currentSuction, taken, face).toFloat()
                        if (leftOver > 0 && spill) {
                            AuraHelper.polluteAura(world, pos, leftOver, true)
                        }
                    }
                }
            }
        }

        /**
         * Drains the container specified, and outputs it's essentia into other nearby containers.
         *
         * @param transport The container to drain
         * @param world
         * @param pos
         * @param spill If flux should be released into the world if any leftovers remain.
         */
        
        fun drain(transport: IEssentiaTransport, world: World, pos: BlockPos, spill: Boolean) {
            for (face in EnumFacing.VALUES) {
                if (!transport.canOutputTo(face)) continue

                val te = ThaumcraftApiHelper.getConnectableTile(world, pos, face)
                if (te != null && te is IEssentiaTransport) {
                    if (!te.canInputFrom(face.opposite)) continue

                    var currentSuction = transport.getSuctionType(face)
                    if (currentSuction == null) {
                        if (transport.getEssentiaAmount(face.opposite) > 0 && te.getSuctionAmount(face.opposite) < transport.getSuctionAmount(face) && te.getSuctionAmount(face) >= transport.minimumSuction) {
                            currentSuction = te.getEssentiaType(face.opposite)
                        }
                    }
                    if (currentSuction == null) {
                        continue
                    }

                    if (te.getEssentiaType(face.opposite) == currentSuction &&
                        transport.getEssentiaAmount(face.opposite) > 0 &&
                        transport.getSuctionAmount(face) >= te.getSuctionAmount(face.opposite) &&
                        transport.getSuctionAmount(face) >= te.minimumSuction
                        ) {

                        val taken = transport.takeEssentia(currentSuction, te.getSuctionAmount(face.opposite), face)
                        val leftOver: Float = te.addEssentia(currentSuction, taken, face.opposite).toFloat()
                        if (leftOver > 0 && spill) {
                            AuraHelper.polluteAura(world, pos, leftOver, true)
                        }
                    }
                }
            }
        }
        
        fun fillContainers(toFill: List<IAspectContainer>, from: AspectList) {
            for (container in toFill) {
                for (aspectEntry in from.aspects) {
                    val aspect = aspectEntry.key
                    val count = aspectEntry.value

                    if (!container.doesContainerAccept(aspect)) continue
                    val leftOver = container.addToContainer(aspect, count)
                    if (leftOver <= 0) {
                        from.reduce(aspect, count)
                    } else {
                        from.reduce(aspect, count - leftOver)
                    }
                }
            }
        }

        fun drainContainers(toDrain: List<IAspectContainer>, with: AspectList): Boolean {
            for (container in toDrain) {
                for (aspectEntry in with.aspects) {
                    val aspect = aspectEntry.key
                    val count = aspectEntry.value

                    if (!container.takeFromContainer(aspect, count)) return false
                    with.reduce(aspect, count)
                }
            }

            return true
        }

        fun doContainersContain(containers: List<IAspectContainer>, with: AspectList): Boolean {
            var contains = true
            for (container in containers) {
                for (aspectEntry in with.aspects) {
                    if (!container.doesContainerContainAmount(aspectEntry.key, aspectEntry.value)) contains = false
                }
            }

            return contains
        }

        fun doContainersAccept(containers: List<IAspectContainer>, with: AspectList): Boolean {
            var accepts = true
            for (container in containers) {
                for (aspectEntry in with.aspects) {
                    if (!container.doesContainerAccept(aspectEntry.key)) accepts = false
                }
            }

            return accepts
        }

        @SideOnly(Side.CLIENT)
        fun renderAspectInJei(
            aspect: Aspect,
            count: Int,
            minecraft: Minecraft,
            x: Int, y: Int,
            color: Int,
            value: Any,
            mouseX: Int, mouseY: Int,

            extraText: String?) {
            // Rendering time!
            GlStateManager.enableBlend()
            GlStateManager.enableDepth()
            GlStateManager.disableRescaleNormal()

            GlStateManager.pushMatrix()
            minecraft.renderEngine.bindTexture(aspect.image)
            val Color = Color(aspect.color)
            GlStateManager.color(Color.red / 255.0F, Color.green / 255.0F, Color.blue / 255.0F, 1.0F)
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0F, 0F, 16, 16, 16F, 16F)
            GlStateManager.color(1F, 1F, 1F, 1F)
            GlStateManager.scale(0.5, 0.5, 0.5)
            if (extraText != null) {
                minecraft.fontRenderer.drawStringWithShadow(extraText,
                    ((x + 5) * 2 - minecraft.fontRenderer.getStringWidth(extraText) + 19).toFloat(),
                    (y + 15) * 2.toFloat(),
                    0xFFFFFF
                )
            }
            GlStateManager.enableAlpha()
            GlStateManager.popMatrix()
            if (mouseX >= x && mouseY >= y && x + 16 > mouseX && y + 16 > mouseY) {
                GlStateManager.colorMask(true, true, true, false)
                Gui.drawRect(
                    x,
                    y,
                    x + 16,
                    y + 16,
                    -2130706433
                )
                GlStateManager.color(1F, 1F, 1F, 1F)
                GlStateManager.enableBlend()
                GlStateManager.colorMask(true, true, true, true)
                GlStateManager.enableDepth()
            }
        }


        @SideOnly(Side.CLIENT)
        fun renderAspectTooltip(
            aspect: Aspect,
            count: Int,
            minecraft: Minecraft,
            x: Int, y: Int,
            color: Int,
            value: Any,
            mouseX: Int, mouseY: Int
        )
        {
            if (minecraft.currentScreen != null) {
                GlStateManager.pushMatrix()
                GlStateManager.translate(0.0, 0.0, 600.0)
                GuiUtils.drawHoveringText(
                    mutableListOf(aspect.name, count.toString()),
                    mouseX,
                    mouseY,
                    minecraft.currentScreen!!.width,
                    minecraft.currentScreen!!.height,
                    -1,
                    minecraft.fontRenderer
                )
                GlStateManager.popMatrix()
                GlStateManager.disableLighting()
            }
        }

    }
}