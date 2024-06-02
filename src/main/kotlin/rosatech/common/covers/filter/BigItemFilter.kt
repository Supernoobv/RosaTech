package rosatech.common.covers.filter

import gregtech.api.gui.GuiTextures
import gregtech.api.gui.Widget
import gregtech.api.gui.widgets.PhantomSlotWidget
import gregtech.api.gui.widgets.ToggleButtonWidget
import gregtech.api.util.LargeStackSizeItemStackHandler
import gregtech.common.covers.filter.ItemFilter
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler
import java.util.function.Consumer
import kotlin.math.min

class BigItemFilter : ItemFilter() {

    val MAX_MATCH_SLOTS: Int = 25

    var ignoreDamage: Boolean = true
        set(value) {
            this.ignoreDamage = value
            markDirty()
        }

    var ignoreNBT: Boolean = true
        set(value) {
            this.ignoreNBT = value
            markDirty()
        }

    val itemFilterSlots: ItemStackHandler = object : LargeStackSizeItemStackHandler(MAX_MATCH_SLOTS) {
        override fun getSlotLimit(slot: Int): Int {
            return maxStackSize
        }
    }

    override fun onMaxStackSizeChange() {
        for (i in 0 until itemFilterSlots.slots) {
            val itemStack: ItemStack = itemFilterSlots.getStackInSlot(i)
            if (!itemStack.isEmpty) {
                itemStack.count = min(itemStack.count, maxStackSize)
            }
        }
    }

    override fun matchItemStack(itemStack: ItemStack): Any? {
        val itemFilterMatchIndex: Int = itemFilterMatch(itemFilterSlots, ignoreDamage, ignoreNBT, itemStack)
        return if (itemFilterMatchIndex == -1) null else itemFilterMatchIndex
    }

    override fun getSlotTransferLimit(matchSlot: Any, globalTransferLimit: Int): Int {
        val matchSlotIndex: Int = matchSlot as Int
        val stackInFilterSlot: ItemStack = itemFilterSlots.getStackInSlot(matchSlotIndex)
        return min(stackInFilterSlot.count, globalTransferLimit)
    }

    override fun showGlobalTransferLimitSlider(): Boolean {
        return false
    }

    override fun getTotalOccupiedHeight(): Int {
        return 51
    }

    override fun initUI(widgetGroup: Consumer<Widget>) {
        for (i in 0 until 25) {
            widgetGroup.accept(PhantomSlotWidget(itemFilterSlots, i, 10 + 18 * (i % 5), 18 * (i / 5))
                .setBackgroundTexture(GuiTextures.SLOT))
        }
        widgetGroup.accept(ToggleButtonWidget(74, 0, 20, 20, GuiTextures.BUTTON_FILTER_DAMAGE,
            { ignoreDamage },
            { value -> ignoreDamage = value })
            .setTooltipText("cover.item_filter.ignore_damage")
        )
        widgetGroup.accept(ToggleButtonWidget(99, 0, 20, 20, GuiTextures.BUTTON_FILTER_NBT,
            { ignoreDamage },
            { value -> ignoreNBT = value })
            .setTooltipText("cover.item_filter.ignore_nbt")
        )
    }

    override fun writeToNBT(nbt: NBTTagCompound) {
        nbt.setTag("ItemFilter", itemFilterSlots.serializeNBT())
        nbt.setBoolean("IgnoreDamage", ignoreDamage)
        nbt.setBoolean("IgnoreNBT", ignoreNBT)
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        this.itemFilterSlots.deserializeNBT(nbt)
        this.ignoreDamage = nbt.getBoolean("IgnoreDamage")
        this.ignoreNBT = nbt.getBoolean("IgnoreNBT")
    }

    companion object {
        fun itemFilterMatch(
            filterSlots: IItemHandler,
            ignoreDamage: Boolean,
            ignoreNBTData: Boolean,
            itemStack: ItemStack
        ): Int {
            for (i in 0 until filterSlots.slots) {
                val filterStack: ItemStack = filterSlots.getStackInSlot(i)
                if (!filterStack.isEmpty && areItemsEqual(ignoreDamage, ignoreNBTData, filterStack, itemStack)) {
                    return i
                }
            }

            return -1
        }

        fun areItemsEqual(
            ignoreDamage: Boolean,
            ignoreNBTData: Boolean,
            filterStack: ItemStack,
            itemStack: ItemStack
        ): Boolean {
            if (ignoreDamage) {
                if (!filterStack.isItemEqualIgnoreDurability(itemStack)) {
                    return false
                }
            } else if (!filterStack.isItemEqual(itemStack)) {
                return false
            }
            return ignoreNBTData || ItemStack.areItemStackTagsEqual(filterStack, itemStack)
        }
    }






}