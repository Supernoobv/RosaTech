package rosatech

import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import rosatech.common.blocks.RosaBlocks

@SideOnly(Side.CLIENT)
@EventBusSubscriber(modid = MODID, value = [Side.CLIENT])
class ClientProxy : CommonProxy() {
    override fun preLoad() {
        super.preLoad()
    }

    override fun load() {
        super.load()
    }

    companion object {
        @SubscribeEvent
        fun registerModels(event: ModelRegistryEvent) {
            RosaBlocks.registerItemModels()
        }
    }
}
