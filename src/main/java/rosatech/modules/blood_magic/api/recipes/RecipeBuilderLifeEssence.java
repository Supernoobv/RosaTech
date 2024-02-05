package rosatech.modules.blood_magic.api.recipes;

import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMap;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import rosatech.modules.blood_magic.api.recipes.recipeproperties.LifeEssenceProperty;

public class RecipeBuilderLifeEssence extends RecipeBuilder<RecipeBuilderLifeEssence> {
    public RecipeBuilderLifeEssence() {}

    public RecipeBuilderLifeEssence(Recipe recipe, RecipeMap<RecipeBuilderLifeEssence> recipeMap) {
        super(recipe, recipeMap);
    }

    public RecipeBuilderLifeEssence(RecipeBuilderLifeEssence recipeBuilder) {
        super(recipeBuilder);
    }

    @Override
    public RecipeBuilderLifeEssence copy() {
        return new RecipeBuilderLifeEssence(this);
    }

    @Override
    public boolean applyProperty(@NotNull String key, Object value) {
        if (key.equals(LifeEssenceProperty.KEY)) {
            this.lifeEssence(((Number) value).intValue());
            return true;
        }
        return super.applyProperty(key, value);
    }

    public RecipeBuilderLifeEssence lifeEssence(int lifeEssence) {
        this.applyProperty(LifeEssenceProperty.getInstance(), lifeEssence);
        return this;
    }

    public int getLifeEssence() {
        return this.recipePropertyStorage == null ? 0 :
                this.recipePropertyStorage.getRecipePropertyValue(LifeEssenceProperty.getInstance(), 0);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append(LifeEssenceProperty.getInstance().getKey(), getLifeEssence())
                .toString();
    }
}
