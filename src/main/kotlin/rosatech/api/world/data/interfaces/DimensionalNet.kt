package rosatech.api.world.data.interfaces

import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable
import java.util.UUID

interface DimensionalNet<T> : INBTSerializable<NBTTagCompound> {
    var storage: T
    var owner: UUID

    val name: String

    override fun serializeNBT(): NBTTagCompound
    override fun deserializeNBT(nbt: NBTTagCompound)

    fun empty(): DimensionalNet<T>
}
