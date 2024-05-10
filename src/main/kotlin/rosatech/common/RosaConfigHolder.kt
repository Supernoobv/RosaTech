package rosatech.common

import net.minecraftforge.common.config.Config
import rosatech.MODID

@Config(modid = MODID, name = "$MODID/$MODID")
class RosaConfigHolder {

    companion object {
        @Config.Comment("Machine options for RosaTech Machines")
        @Config.Name("Machine Options")
        @Config.RequiresMcRestart
        val machineOptions: MachineOptions = MachineOptions()
    }

    class MachineOptions {

        @Config.Comment(
            "Whether to enable Dimensional Hatches, which connect to a wireless network  and supply/request energy/items/fluids.",
            "Default: true"
        )
        var enableDimensionalHatches: Boolean = true

        @Config.Comment(
            "Whether to enable Higher tier dimensional hatches, such as 64/256/1024 Amperage energy hatches.",
            "Default: false"
        )
        var enableHigherDimensionalHatches: Boolean = false
    }
}
