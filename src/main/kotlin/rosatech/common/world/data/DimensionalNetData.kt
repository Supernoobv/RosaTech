package rosatech.common.world.data

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.world.World
import net.minecraft.world.storage.WorldSavedData
import net.minecraftforge.common.util.Constants
import rosatech.MODID
import rosatech.api.world.data.interfaces.DimensionalNet
import java.util.*
import kotlin.reflect.full.primaryConstructor

class DimensionalNetData : WorldSavedData {

    constructor() : super(DATA_ID)

    constructor(name: String) : super(name)

    override fun readFromNBT(nbt: NBTTagCompound) {
        val size = nbt.getInteger("size")

        val list = nbt.getTagList("nets", Constants.NBT.TAG_COMPOUND)
        for (i in 0 until size) {
            val compound = list.getCompoundTagAt(i)

            val net: DimensionalNet<*> = getNetFromType(compound.getString("type"))
            net.deserializeNBT(compound)
            netMap[net.owner]?.put(compound.getString("name"), net)
        }
    }

    override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
        compound.setInteger("size", netMap.size)

        val list = NBTTagList()
        for (nets in netMap.values) {
            for (net in nets.values) {
                val tag = net.serializeNBT()
                list.appendTag(tag)
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

        val netMap: MutableMap<UUID, MutableMap<String, DimensionalNet<*>>> = mutableMapOf()

        fun initializeStorage(world: World) {
            val storage = world.mapStorage
            var instance = storage!!.getOrLoadData(DimensionalNetData::class.java, DATA_ID)

            if (instance == null) {
                instance = DimensionalNetData()
                storage.setData(DATA_ID, instance)
            }
        }

        /**
         * Retrieves a dimensional net from storage, and if there is none, creates one.
         *
         * @param id The owner's uuid
         * @param name The net name (for example "energy")
         * @param type The net's type, to create a new net of that type if none is found.
         */
        fun getNet(id: UUID, name: String, type: String): DimensionalNet<*> {
            val networks = netMap.getOrPut(id) { mutableMapOf() }
            val network = networks.getOrPut(name) { getNetFromType(type).empty() }

            return network
        }

        /**
         * Retrieves a DimensionalNet instance from a class's simple name.
         *
         * Useful for anything implementing said interface.
         *
         * @param type The net class's simple name.
         */
        fun getNetFromType(type: String): DimensionalNet<*> {
            val clazz = Class.forName(type)
            val constructor = clazz.getConstructor()
            val net: DimensionalNet<*> = constructor.newInstance() as DimensionalNet<*>

            return net
        }
    }
}
