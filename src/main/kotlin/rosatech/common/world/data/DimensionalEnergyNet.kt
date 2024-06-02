package rosatech.common.world.data

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants
import rosatech.api.world.data.interfaces.DimensionalNet
import java.math.BigInteger
import java.util.*

class DimensionalEnergyNet() : DimensionalNet<Array<Long>> {

    lateinit var capacity: Array<Long>

    override lateinit var storage: Array<Long>

    override lateinit var owner: UUID

    override val name: String = "Energy"

    val NBT_SIZE = "Size"
    val NBT_STORAGE = "Storage"

    override fun serializeNBT(): NBTTagCompound {
        val compound = NBTTagCompound()
        compound.setInteger(NBT_SIZE, storage.size - 1)
        compound.setUniqueId("owner", owner)

        val list = NBTTagList()
        for (i in storage.indices) {
            val containerCompound = NBTTagCompound()
            containerCompound.setLong("storage", storage[i])
            containerCompound.setLong("capacity", capacity[i])
            list.appendTag(containerCompound)
        }

        compound.setTag(NBT_STORAGE, list)
        compound.setString("type", this::class.java.name)
        compound.setString("name", "energy")

        return compound
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        val size = nbt.getInteger(NBT_SIZE)

        this.storage = Array(size) { 0 }
        this.capacity = Array(size) { 0 }

        this.owner = nbt.getUniqueId("owner")!!

        val list = nbt.getTagList(NBT_STORAGE, Constants.NBT.TAG_COMPOUND)
        for (i in 0 until size) {
            val compound = list.getCompoundTagAt(i)
            this.storage[i] = compound.getLong("storage")
            this.capacity[i] = compound.getLong("capacity")
        }
    }

    override fun empty(): DimensionalEnergyNet {
        val net = DimensionalEnergyNet()
        net.storage = Array(8) { 0 }
        net.capacity = Array(8) { 8589934592 }
        return net
    }

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
}
