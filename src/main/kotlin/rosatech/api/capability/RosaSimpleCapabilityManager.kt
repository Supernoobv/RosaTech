package rosatech.api.capability

import gregtech.api.capability.SimpleCapabilityManager.registerCapabilityWithNoDefault
import gregtech.common.covers.filter.ItemFilterContainer
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager

class RosaSimpleCapabilityManager {


    companion object {

        @CapabilityInject(ItemFilterContainer::class)
        var CAPABILITY_ITEM_FILTER_CONTAINER: Capability<ItemFilterContainer>? = null

        fun init() {
            registerCapabilityWithNoDefault(ItemFilterContainer::class.java)
        }
    }
}