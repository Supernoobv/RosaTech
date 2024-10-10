package rosatech.api.world.net

import net.minecraft.nbt.NBTTagCompound
import rosatech.api.world.data.interfaces.DimensionalNet

abstract class DimensionalNetBase(
    override val definition: DimensionalNetDefinition,
    override var frequency: Int
) : DimensionalNet {

    abstract override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound
    abstract override fun readFromNBT(compound: NBTTagCompound)

}