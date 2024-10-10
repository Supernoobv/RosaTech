package rosatech.common.items.behavior

import gregtech.api.items.metaitem.stats.IItemBehaviour
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity
import gregtech.client.utils.TooltipHelper
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import rosatech.api.world.data.DimensionalNetData
import rosatech.api.world.data.interfaces.DimensionalNet
import rosatech.api.world.net.mte.IDimensionalNetHolder

class DimensionalTunerBehavior : IItemBehaviour {

    override fun onItemUseFirst(
        player: EntityPlayer,
        world: World,
        pos: BlockPos,
        side: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float,
        hand: EnumHand
    ): EnumActionResult? {
        if (!world.isRemote && !world.isAirBlock(pos)) {

            val tileEntity = world.getTileEntity(pos)

            if (tileEntity is IGregTechTileEntity && tileEntity.metaTileEntity is IDimensionalNetHolder) {
                val itemStack = player.getHeldItem(hand)

                val metaTileEntity = tileEntity.metaTileEntity
                val frequencyHolder = metaTileEntity as IDimensionalNetHolder

                val sneaking = player.isSneaking
                if (sneaking) {
                    val frequency = frequencyHolder.getFrequency()

                    val compound = itemStack.tagCompound ?: NBTTagCompound()

                    compound.setInteger("frequency", frequency)
                    itemStack.tagCompound = compound

                    player.sendMessage(TextComponentTranslation("rosatech.behavior.dimensional_tuner.copied_frequency"))
                } else {
                    val compound = itemStack.tagCompound
                    if (compound == null || !compound.hasKey("frequency")) {
                        player.sendMessage(TextComponentTranslation("rosatech.behavior.dimensional_tuner.no_frequency"))
                        return EnumActionResult.SUCCESS
                    }

                    val frequency = compound.getInteger("frequency")

                    frequencyHolder.updateFrequency(frequency)
                    player.sendMessage(TextComponentTranslation("rosatech.behavior.dimensional_tuner.pasted_frequency"))

                }
            }
        }

        return EnumActionResult.SUCCESS
    }

    override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?>? {
        val itemStack = player.getHeldItem(hand)
        if (!world.isRemote) {
            if (player.isSneaking) {
                val compound = itemStack.tagCompound ?: NBTTagCompound()
                if (!compound.hasKey("frequency")) {
                    var size = DimensionalNetData.netMap.size
                    var frequency: Int = size++

                    if (DimensionalNetData.netMap.containsKey(frequency)) {
                        frequency++
                    }

                    DimensionalNetData.netMap[frequency] = mutableMapOf()

                    compound.setInteger("frequency", frequency)
                    itemStack.tagCompound = compound

                    player.sendMessage(TextComponentTranslation("rosatech.behavior.dimensional_tuner.create_frequency"))

                    return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack)
                }

                itemStack.tagCompound = NBTTagCompound()
                player.sendMessage(TextComponentTranslation("rosatech.behavior.dimensional_tuner.reset_frequency"))

                return ActionResult.newResult(EnumActionResult.SUCCESS, itemStack)
            }
        }

        return ActionResult.newResult(EnumActionResult.PASS, itemStack)
    }

    override fun addInformation(itemStack: ItemStack, lines: MutableList<String>) {
        super.addInformation(itemStack, lines)

        val compound = itemStack.tagCompound
        if (compound != null && compound.hasKey("frequency")) {
            lines.add(I18n.format("rosatech.tooltip.dimensional_tuner.current_frequency", compound.getInteger("frequency")))
        } else {
            lines.add(I18n.format("rosatech.tooltip.dimensional_tuner.no_frequency"))
        }

        if (TooltipHelper.isShiftDown()) {
            lines.add(I18n.format("rosatech.tooltip.dimensional_tuner.right_click"))
            lines.add(I18n.format("rosatech.tooltip.dimensional_tuner.shift_right_click"))
            lines.add(I18n.format("rosatech.tooltip.dimensional_tuner.shift_right_click_air"))
        } else {
            lines.add(I18n.format("rosatech.tooltip.dimensional_tuner.hold_shift"))
        }
    }
}