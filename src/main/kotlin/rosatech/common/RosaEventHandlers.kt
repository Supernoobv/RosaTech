package rosatech.common

import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.event.entity.player.EntityItemPickupEvent
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import rosatech.api.world.data.DimensionalNetData
import rosatech.common.items.behavior.SmartBackpackBehavior.Companion.insertIntoInventory
import rosatech.common.items.behavior.SmartBackpackBehavior.Companion.isSmartBackpack
import rosatech.common.items.behavior.SmartBackpackBehavior.Companion.testItemStack

class RosaEventHandlers {

    @SubscribeEvent
    fun onWorldLoadEvent(event: WorldEvent.Load) {
        DimensionalNetData.initializeStorage(event.world)
    }

    @SubscribeEvent
    fun onItemPickup(event: EntityItemPickupEvent) {
        if (event.isCanceled || event.result == Event.Result.ALLOW) {
            return
        }

        val player: EntityPlayer = event.entityPlayer
        val entityItem: EntityItem = event.item
        val itemStack = entityItem.item
        if (itemStack.isEmpty) return

        for (item in player.inventory.mainInventory) {
            if (itemStack.isEmpty) break

            if (item.isEmpty || !isSmartBackpack(item)) continue

            if (!item.tagCompound?.getBoolean("autoPickup")!!) continue

            if (!testItemStack(item, itemStack)) continue

            val succeeded: Boolean = insertIntoInventory(item, itemStack)

            if (succeeded) {
                event.result = Event.Result.ALLOW
            }
        }
    }
}
