package rosatech.api

import gregtech.api.util.GTControlledRegistry
import net.minecraft.util.ResourceLocation
import rosatech.api.world.data.interfaces.DimensionalNet
import rosatech.api.world.net.DimensionalNetDefinition



fun initNets() {

}

object RosaTechAPI {

    val DIMENSIONAL_NET_REGISTRY: GTControlledRegistry<ResourceLocation, DimensionalNetDefinition> = GTControlledRegistry(
        Short.MAX_VALUE.toInt()
    )

}