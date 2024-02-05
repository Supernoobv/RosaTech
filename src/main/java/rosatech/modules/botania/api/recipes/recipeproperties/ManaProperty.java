package rosatech.modules.botania.api.recipes.recipeproperties;

import gregtech.api.recipes.recipeproperties.RecipeProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class ManaProperty extends RecipeProperty<Integer> {

    public static final String KEY = "mana";

    private static ManaProperty INSTANCE;


    private ManaProperty() {
        super(KEY, Integer.class);
    }

    public static ManaProperty getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ManaProperty();
        }
        return INSTANCE;
    }

    public int returnManaValue(int value) {
        return value > 0 ? value : -value;
    }

    public String returnManaType(int value) {
        return value > 0 ? "rosatech.recipe.mana.input" : "rosatech.recipe.mana.output";
    }
    @Override
    public void drawInfo(Minecraft minecraft, int x, int y, int color, Object value) {
        minecraft.fontRenderer.drawString(I18n.format(returnManaType(castValue(value)), returnManaValue(castValue(value)) ), x, y, color);
    }
}
