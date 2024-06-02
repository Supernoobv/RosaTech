package rosatech.client.renderer.textures

import gregtech.api.gui.resources.TextureArea

object RosaGuiTextures {

    lateinit var BACKPACK_RESTOCKING: TextureArea
    lateinit var BACKPACK_DEPOSITING: TextureArea
    lateinit var BACKPACK_AUTOPICKUP: TextureArea

    fun preInit() {
        BACKPACK_RESTOCKING = TextureArea.fullImage("textures/gui/buttons/backpack_restocking.png")
        BACKPACK_DEPOSITING = TextureArea.fullImage("textures/gui/buttons/backpack_depositing.png")
        BACKPACK_AUTOPICKUP = TextureArea.fullImage("textures/gui/buttons/backpack_autopickup.png")

    }
}