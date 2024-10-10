package rosatech.common.items

import appeng.items.materials.ItemMaterial
import gregtech.api.GTValues.M
import gregtech.api.items.metaitem.MetaItem
import gregtech.api.items.metaitem.StandardMetaItem
import gregtech.api.unification.material.Materials
import gregtech.api.unification.stack.ItemMaterialInfo
import gregtech.api.unification.stack.MaterialStack
import net.minecraft.item.Item
import rosatech.common.items.behavior.BackpackBehavior
import rosatech.common.items.behavior.DimensionalTunerBehavior
import rosatech.common.items.behavior.SmartBackpackBehavior

private lateinit var ITEMS: MetaItem<*>

lateinit var LEATHER_BACKPACK: MetaItem<*>.MetaValueItem
lateinit var PROCESSED_LEATHER_BACKPACK: MetaItem<*>.MetaValueItem
lateinit var INDUSTRIAL_LEATHER_BACKPACK: MetaItem<*>.MetaValueItem

lateinit var SMART_BACKPACK_LV: MetaItem<*>.MetaValueItem
lateinit var SMART_BACKPACK_HV: MetaItem<*>.MetaValueItem

lateinit var DIMENSIONAL_FREQUENCY_TUNER: MetaItem<*>.MetaValueItem

class RosaMetaItems {
    companion object {

        fun preInit() {
            ITEMS = StandardMetaItem()
            // todo ???
            (ITEMS as? Item)?.setRegistryName("rt_item")
        }

        fun registerItems() {
            LEATHER_BACKPACK = ITEMS.addItem(0, "leather.backpack").addComponents(BackpackBehavior(27)).setMaxStackSize(1)
            PROCESSED_LEATHER_BACKPACK = ITEMS.addItem(1, "processed_leather.backpack").addComponents(BackpackBehavior(45)).setMaxStackSize(1)
            INDUSTRIAL_LEATHER_BACKPACK = ITEMS.addItem(2, "industrial_leather.backpack").addComponents(BackpackBehavior(63)).setMaxStackSize(1)

            SMART_BACKPACK_LV = ITEMS.addItem(3, "smart_backpack.lv").addComponents(SmartBackpackBehavior(63, 1, 4)).setMaxStackSize(1)
            SMART_BACKPACK_HV = ITEMS.addItem(4, "smart_backpack.hv").addComponents(SmartBackpackBehavior(72, 2, 8)).setMaxStackSize(1)

            DIMENSIONAL_FREQUENCY_TUNER = ITEMS.addItem(5, "dimensional_frequency_tuner").addComponents(DimensionalTunerBehavior()).setMaxStackSize(1)

        }
    }
}