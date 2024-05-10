package rosatech.api.metatileentity

import gregtech.api.gui.resources.TextureArea
import gregtech.api.gui.widgets.ProgressWidget.MoveType

data class RosaProgressIndicator(
    var progressBarTexture: TextureArea,
    var progressMoveType: MoveType
)
