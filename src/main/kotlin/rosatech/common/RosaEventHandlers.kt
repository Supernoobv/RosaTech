package rosatech.common

import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import rosatech.common.world.data.DimensionalNetData

class RosaEventHandlers {

    @SubscribeEvent
    fun onWorldLoadEvent(event: WorldEvent.Load) {
        DimensionalNetData.initializeStorage(event.world)
    }
}
