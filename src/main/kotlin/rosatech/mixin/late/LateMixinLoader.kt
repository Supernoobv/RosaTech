package rosatech.mixin.late

import zone.rong.mixinbooter.ILateMixinLoader
import java.util.*

@Suppress("unused")
class LateMixinLoader : ILateMixinLoader {
    override fun getMixinConfigs(): List<String> {
        val configs = arrayOf<String>()
        return Arrays.asList(*configs)
    }

    override fun shouldMixinConfigQueue(mixinConfig: String): Boolean {
        return when (mixinConfig) {
            else -> super.shouldMixinConfigQueue(mixinConfig)
        }
    }
}
