package rosatech

import net.minecraftforge.fml.common.Mod.EventBusSubscriber

@EventBusSubscriber(modid = MODID)
open class CommonProxy {
    open fun preLoad() {}

    open fun load() {}
}
