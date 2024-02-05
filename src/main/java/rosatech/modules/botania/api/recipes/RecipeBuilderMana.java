package rosatech.modules.botania.api.recipes;

import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMap;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import rosatech.modules.botania.api.recipes.recipeproperties.ManaProperty;

public class RecipeBuilderMana extends RecipeBuilder<RecipeBuilderMana> {

    public RecipeBuilderMana() {}

    public RecipeBuilderMana(Recipe recipe, RecipeMap<RecipeBuilderMana> recipeMap) {
        super(recipe, recipeMap);
    }

    public RecipeBuilderMana(RecipeBuilderMana recipeBuilder) {
        super(recipeBuilder);
    }

    @Override
    public RecipeBuilderMana copy() {
        return new RecipeBuilderMana(this);
    }

    @Override
    public boolean applyProperty(@NotNull String key, Object value) {
        if (key.equals(ManaProperty.KEY)) {
            this.manaRequired(((Number) value).intValue());
            return true;
        }
        return super.applyProperty(key, value);
    }

    public RecipeBuilderMana manaRequired(int manaRequired) {
        this.applyProperty(ManaProperty.getInstance(), manaRequired);
        return this;
    }

    public int getMana() {
        return this.recipePropertyStorage == null ? 0 :
                this.recipePropertyStorage.getRecipePropertyValue(ManaProperty.getInstance(), 0);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append(ManaProperty.getInstance().getKey(), getMana())
                .toString();
    }
}
