package rosatech.modules.blood_magic.api.recipes.recipeproperties;

import gregtech.api.recipes.recipeproperties.RecipeProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import rosatech.modules.botania.api.recipes.recipeproperties.ManaProperty;

public class LifeEssenceProperty extends RecipeProperty<Integer> {
    public static final String KEY = "life_essence";

    private static LifeEssenceProperty INSTANCE;


    private LifeEssenceProperty() {
        super(KEY, Integer.class);
    }

    public static LifeEssenceProperty getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LifeEssenceProperty();
        }
        return INSTANCE;
    }

    public int returnLifeEssenceValue(int value) {
        return value > 0 ? value : -value;
    }

    public String returnLifeEssenceType(int value) {
        return value > 0 ? "rosatech.recipe.life_essence.input" : "rosatech.recipe.life_essence.output";
    }
    @Override
    public void drawInfo(Minecraft minecraft, int x, int y, int color, Object value) {
        minecraft.fontRenderer.drawString(I18n.format(returnLifeEssenceType(castValue(value)), returnLifeEssenceValue(castValue(value))), x, y, color);
    }
}
