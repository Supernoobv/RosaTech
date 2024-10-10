package rosatech.api.world.data

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraft.world.storage.WorldSavedData
import net.minecraftforge.common.util.Constants
import rosatech.MODID
import rosatech.api.world.data.interfaces.DimensionalNet
import rosatech.api.world.net.DimensionalNetDefinition
import java.util.NoSuchElementException
import kotlin.collections.set
import kotlin.jvm.java
import kotlin.ranges.until
import kotlin.to

class DimensionalNetData : WorldSavedData {

    constructor() : super(DATA_ID)

    constructor(name: String) : super(name)

    override fun readFromNBT(nbt: NBTTagCompound) {
        val size = nbt.getInteger("size")

        val list = nbt.getTagList("nets", Constants.NBT.TAG_COMPOUND)
        for (i in 0 until size) {
            val compound = list.getCompoundTagAt(i)

            val id = ResourceLocation(compound.getString("id"))
            val frequency = compound.getInteger("frequency")

            val definition = DimensionalNetDefinition.getNetById(id) ?: continue
            val net = definition.createNetwork(frequency)

            net.frequency = frequency
            net.readFromNBT(compound)

            if (netMap[frequency] == null) {
                netMap[frequency] = mutableMapOf(
                    id.toString() to net
                )
            } else {
                netMap[frequency]!![id.toString()] = net
            }
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setInteger("size", netMap.size)

        val list = NBTTagList()
        for (nets in netMap.values) {
            for (net in nets.values) {
                val tag = NBTTagCompound()

                val definition = net.definition
                tag.setString("id", definition.resourceLocation.toString())
                tag.setInteger("frequency", net.frequency)

                net.writeToNBT(tag)
            }
        }

        compound.setTag("nets", list)

        return compound
    }

    override fun isDirty(): Boolean {
        return true
    }

    companion object {
        const val DATA_ID = "$MODID.dimensional_net_data"

        val netMap: MutableMap<Int, MutableMap<String, DimensionalNet>> = mutableMapOf()

        fun initializeStorage(world: World) {
            val storage = world.mapStorage
            var instance = storage!!.getOrLoadData(DimensionalNetData::class.java, DATA_ID)

            if (instance == null) {
                instance = DimensionalNetData()
                storage.setData(DATA_ID, instance)
            }
        }

        fun getOrCreateNetwork(frequency: Int, id: ResourceLocation): DimensionalNet {
            if (netMap.containsKey(frequency) && netMap[frequency]!!.containsKey(id.toString())) {
                return netMap[frequency]!![id.toString()]!!
            }

            if (!netMap.containsKey(frequency)) netMap[frequency] = mutableMapOf()
            val definition = DimensionalNetDefinition.getNetById(id) ?: throw NoSuchElementException("Dimensional network with id $id does not exist!")
            val net = definition.createNetwork(frequency)
            netMap[frequency]!![id.toString()] = net

            return net

        }

    }
}
