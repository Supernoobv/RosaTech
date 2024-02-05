package rosatech.api.util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;

import java.util.List;

// CREDITS: GTCEu Devs & GTCEu Repo
public class ModIncompabilityException extends CustomModLoadingErrorDisplayException {

    @SuppressWarnings("all")
    private static final long serialVersionUID = 1L;

    private final List<String> messages;

    public ModIncompabilityException(List<String> messages) {
        this.messages = messages;
    }

    @Override
    public void initGui(GuiErrorScreen guiErrorScreen, FontRenderer fontRenderer) {}

    @Override
    public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseX, int mouseY, float time) {
        int x = errorScreen.width / 2;
        int y = 75;
        for (String message : messages) {
            errorScreen.drawCenteredString(fontRenderer, message, x, y, 0xFFFFFF);
            y += 15;
        }
    }
}
