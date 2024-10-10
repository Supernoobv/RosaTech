package rosatech.api.world.data.interfaces

import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable
import rosatech.api.world.net.DimensionalNetDefinition
import java.util.UUID

interface DimensionalNet {
    val definition: DimensionalNetDefinition

    var frequency: Int

    fun writeToNBT(compound: NBTTagCompound): NBTTagCompound

    fun readFromNBT(compound: NBTTagCompound)

}
