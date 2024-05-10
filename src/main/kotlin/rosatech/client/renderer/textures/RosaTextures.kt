package rosatech.client.renderer.textures

import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer

object RosaTextures {
    lateinit var DIMENSIONAL_ENERGY_MULTI: SimpleOverlayRenderer
    lateinit var DIMENSIONAL_ENERGY_4A: SimpleOverlayRenderer
    lateinit var DIMENSIONAL_ENERGY_16A: SimpleOverlayRenderer
    lateinit var DIMENSIONAL_ENERGY_64A: SimpleOverlayRenderer
    lateinit var DIMENSIONAL_ENERGY_256A: SimpleOverlayRenderer
    lateinit var DIMENSIONAL_ENERGY_1024A: SimpleOverlayRenderer

    fun preInit() {
        DIMENSIONAL_ENERGY_MULTI = SimpleOverlayRenderer("overlay/machine/overlay_dimensional_energy_hatch")
        DIMENSIONAL_ENERGY_4A = SimpleOverlayRenderer("overlay/machine/overlay_dimensional_energy_hatch_4a")
        DIMENSIONAL_ENERGY_16A = SimpleOverlayRenderer("overlay/machine/overlay_dimensional_energy_hatch_16a")
        DIMENSIONAL_ENERGY_64A = SimpleOverlayRenderer("overlay/machine/overlay_dimensional_energy_hatch_64a")
        DIMENSIONAL_ENERGY_256A = SimpleOverlayRenderer("overlay/machine/overlay_dimensional_energy_hatch_256a")
        DIMENSIONAL_ENERGY_1024A = SimpleOverlayRenderer("overlay/machine/overlay_dimensional_energy_hatch_1024a")
    }
}
