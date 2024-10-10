package rosatech.api.world.net.nets

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants
import rosatech.api.world.data.interfaces.DimensionalNet
import rosatech.api.world.net.DimensionalNetBase
import rosatech.api.world.net.DimensionalNetDefinition
import java.math.BigInteger
import java.util.*

class DimensionalEnergyNet(
    definition: DimensionalNetDefinition,
    frequency: Int,
    var storage: Array<Long>
) : DimensionalNetBase(definition, frequency) {

    var capacity: Array<Long> = arrayOf(536870912, 536870912, 536870912, 536870912, 536870912, 536870912, 536870912, 536870912)

    fun fill(voltage: Long): Long {
        var remainder = voltage
        var voltageAccepted: Long = 0

        if (voltage > 0L) {
            for (i in storage.indices) {
                if (remainder <= 0) return voltageAccepted

                val accepted = fillContainer(remainder, i)
                remainder -= accepted
                voltageAccepted += accepted
            }
        }
        return voltageAccepted
    }

    fun fillContainer(voltage: Long, container: Int): Long {
        val canAccept: Long = capacity[container] - storage[container]

        var voltageAccepted: Long = 0
        if (voltage > 0L) {
            if (canAccept >= voltage) {
                storage[container] += voltage
                voltageAccepted = voltage
            } else {
                storage[container] += canAccept
                voltageAccepted = canAccept
            }
        }

        return voltageAccepted
    }

    fun drain(voltage: Long): Long {
        var voltageDrained: Long = 0

        var remainder = voltage
        if (voltage > 0L) {
            for (i in storage.indices) {
                if (remainder <= 0) return voltageDrained

                val drained = drainContainer(remainder, i)
                remainder -= drained
                voltageDrained += drained
            }
        }

        return voltageDrained
    }

    fun drainContainer(voltage: Long, container: Int): Long {
        val energyStored = storage[container]

        var energyDrained: Long = 0
        if (voltage > 0L) {
            if (energyStored >= voltage) {
                storage[container] -= voltage
                energyDrained = voltage
            } else {
                storage[container] = 0
                energyDrained = energyStored
            }
        }

        return energyDrained
    }

    fun getCapacity(): BigInteger {
        var totalCapacity = BigInteger.valueOf(0)

        for (amount in capacity) {
            totalCapacity = totalCapacity.add(BigInteger.valueOf(amount))
        }

        return totalCapacity
    }

    fun getStored(): BigInteger {
        var totalStorage = BigInteger.valueOf(0)

        for (amount in storage) {
            totalStorage = totalStorage.add(BigInteger.valueOf(amount))
        }

        return totalStorage
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setInteger("size", storage.size - 1)

        val list = NBTTagList()
        for (i in storage.indices) {
            val containerCompound = NBTTagCompound()
            containerCompound.setLong("storage", storage[i])
            containerCompound.setLong("capacity", capacity[i])
            list.appendTag(containerCompound)
        }

        compound.setTag("storage", list)

        return compound
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        val size = compound.getInteger("size")

        this.storage = Array(size) { 0 }
        this.capacity = Array(size) { 0 }

        val list = compound.getTagList("storage", Constants.NBT.TAG_COMPOUND)
        for (i in 0 until size) {
            val listCompound = list.getCompoundTagAt(i)
            this.storage[i] = listCompound.getLong("storage")
            this.capacity[i] = listCompound.getLong("capacity")
        }
    }
}