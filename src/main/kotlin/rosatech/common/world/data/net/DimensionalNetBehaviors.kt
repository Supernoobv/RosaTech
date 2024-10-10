package rosatech.common.world.data.net

import net.minecraft.util.ResourceLocation
import rosatech.RosaTech
import rosatech.api.RosaTechAPI.DIMENSIONAL_NET_REGISTRY
import rosatech.api.world.net.DimensionalNetDefinition
import rosatech.api.world.net.nets.DimensionalEnergyNet

object DimensionalNetBehaviors {

    var rollingId: Int = 0

    fun init() {
        registerNet(RosaTech.ID("energy")) { definition, frequency ->
            DimensionalEnergyNet(definition, frequency, arrayOf(0, 0, 0, 0, 0, 0, 0, 0))
        }
    }

    fun registerNet(coverId: ResourceLocation, creator: DimensionalNetDefinition.DimensionalNetCreator) {
        val definition = DimensionalNetDefinition(coverId, creator)
        DIMENSIONAL_NET_REGISTRY.register(rollingId++, coverId, definition)
    }
}