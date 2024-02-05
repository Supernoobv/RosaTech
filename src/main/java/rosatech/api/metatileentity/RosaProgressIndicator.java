package rosatech.api.metatileentity;

import gregtech.api.gui.resources.TextureArea;
import gregtech.api.gui.widgets.ProgressWidget;

public class RosaProgressIndicator {
    public TextureArea progressBarTexture;
    public ProgressWidget.MoveType progressMoveType;

    public RosaProgressIndicator(TextureArea progressBarTexture, ProgressWidget.MoveType progressMoveType) {
        this.progressBarTexture = progressBarTexture;
        this.progressMoveType = progressMoveType;
    }
}
