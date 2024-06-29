package mealplanner;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

public final class Meal {

    private final Category category;
    private final String name;
    private final Collection<String> ingredients;

    public Meal(Category category, String name) {
        this.category = category;
        this.name = name;
        this.ingredients = new LinkedHashSet<>();
    }

    public void add(String ingredient) {
        if (ingredient == null) {
            return;
        }

        this.ingredients.add(ingredient);
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public Collection<String> getIngredients() {
        return Collections.unmodifiableCollection(ingredients);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Meal meal = (Meal) o;

        if (!getName().equals(meal.getName()))
            return false;
        if (getCategory() != meal.getCategory())
            return false;
        return getIngredients().equals(meal.getIngredients());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getCategory().hashCode();
        result = 31 * result + getIngredients().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "name='" + name + '\'' +
                ", category=" + category +
                ", ingredients=" + ingredients +
                '}';
    }
}
