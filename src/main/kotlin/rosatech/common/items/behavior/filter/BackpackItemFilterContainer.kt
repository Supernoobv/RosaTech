package rosatech.common.items.behavior.filter

import gregtech.api.gui.GuiTextures
import gregtech.api.gui.Widget
import gregtech.api.gui.widgets.LabelWidget
import gregtech.api.gui.widgets.SlotWidget
import gregtech.api.util.IDirtyNotifiable
import gregtech.common.covers.filter.FilterTypeRegistry
import gregtech.common.covers.filter.ItemFilterContainer
import net.minecraft.item.ItemStack
import net.minecraftforge.items.ItemStackHandler
import java.util.function.Consumer

class BackpackItemFilterContainer(dirtyNotifiable: IDirtyNotifiable) : ItemFilterContainer(dirtyNotifiable) {
    val filterInventoryClient: ItemStackHandler

    var clientSide: Boolean = true

    init {
        this.filterInventoryClient = object : ItemStackHandler(1) {
            override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
                return FilterTypeRegistry.getItemFilterForStack(stack) != null;
            }

            override fun getSlotLimit(slot: Int): Int {
                return 1
            }

            override fun onLoad() {
                onFilterSlotChange(false)
            }

            override fun onContentsChanged(slot: Int) {
                onFilterSlotChange(true)
            }
        }
    }

    override fun initUI(y: Int, widgetGroup: Consumer<Widget>) {
        widgetGroup.accept(LabelWidget(10, y, "cover.conveyor.item_filter.title"))
        widgetGroup.accept(SlotWidget(if (clientSide) filterInventoryClient else filterInventory, 0, 10, y + 15).setBackgroundTexture(GuiTextures.SLOT, GuiTextures.FILTER_SLOT_OVERLAY))

        this.filterWrapper.initUI(y + 38, widgetGroup)
        this.filterWrapper.blacklistUI(y + 38, widgetGroup) { true }
    }
}