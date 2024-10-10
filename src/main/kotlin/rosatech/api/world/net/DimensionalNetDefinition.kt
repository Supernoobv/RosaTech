package rosatech.api.world.net

import net.minecraft.util.ResourceLocation
import rosatech.api.RosaTechAPI.DIMENSIONAL_NET_REGISTRY
import rosatech.api.world.data.interfaces.DimensionalNet

data class DimensionalNetDefinition(val resourceLocation: ResourceLocation, val creator: DimensionalNetCreator) {

    fun createNetwork(frequency: Int) = creator.create(this, frequency)

    @FunctionalInterface
    fun interface DimensionalNetCreator {
        fun create(definition: DimensionalNetDefinition, frequency: Int): DimensionalNet
    }

    companion object {
        fun getNetById(id: ResourceLocation) = DIMENSIONAL_NET_REGISTRY.getObject(id)
    }

}
