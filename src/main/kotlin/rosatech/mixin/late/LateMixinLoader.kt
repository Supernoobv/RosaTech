package rosatech.mixin.late

import net.minecraftforge.fml.common.Loader
import zone.rong.mixinbooter.ILateMixinLoader
import java.util.*

@Suppress("unused")
class LateMixinLoader : ILateMixinLoader {
    override fun getMixinConfigs(): List<String> {
        val configs = arrayOf<String>(
            "mixins.rosatech.late.json",
            "mixins.rosatech.late.thaumcraft.json"
        )
        return Arrays.asList(*configs)
    }

    override fun shouldMixinConfigQueue(mixinConfig: String): Boolean {
        return when (mixinConfig) {
            "mixins.rosatech.late.thaumcraft.json" -> Loader.isModLoaded("thaumcraft")
            else -> super.shouldMixinConfigQueue(mixinConfig)
        }
    }
}
