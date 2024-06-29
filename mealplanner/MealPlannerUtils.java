package mealplanner;

import java.util.Arrays;

public final class MealPlannerUtils {

    private MealPlannerUtils() {
    }

    public static String getAvailableActions() {
        return String.join(", ", Arrays
                .stream(Action.values())
                .filter(v -> v != Action.UNKNOWN)
                .map(v -> v.label)
                .toList()
        );
    }

    public static String getAvailableCategories() {
        return String.join(", ", Arrays
                .stream(Category.values())
                .filter(c -> c != Category.UNKNOWN)
                .map(c -> c.label)
                .toList()
        );
    }

    public static boolean isInvalidInput(String input) {
        if (input == null || input.isEmpty() || " ".equals(input)) {
            return true;
        }

        String removeWhiteSpaces = input.replace(" ", "");
        boolean onlyValidLetters = true;
        for (char c : removeWhiteSpaces.toCharArray()) {
            if (!Character.isAlphabetic(c)) {
                onlyValidLetters = false;
                break;
            }
        }

        return !onlyValidLetters;
    }

    public static boolean isInvalidCategory(String userInput) {
        try {
            Category.valueOf(userInput.toUpperCase());
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

}
