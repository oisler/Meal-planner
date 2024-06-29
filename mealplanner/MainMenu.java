package mealplanner;

import java.util.Arrays;
import java.util.Scanner;

public final class MainMenu {

    private final Scanner scanner;

    public MainMenu() {
        this.scanner = new Scanner(System.in);
    }

    public Action askUserForAction() {
        Action action = Action.UNKNOWN;

        while (action == Action.UNKNOWN) {
            String question = String.format("What would you like to do (%s)?", MealPlannerUtils.getAvailableActions());
            System.out.println(question);
            try {
                action = Action.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        return action;
    }

    public Category askUserForCategory() {
        String question = String.format("Which meal do you want to add (%s)?", MealPlannerUtils.getAvailableCategories());
        System.out.println(question);

        Category category = Category.UNKNOWN;
        while (category == Category.UNKNOWN) {
            try {
                category = Category.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                String message = String.format("Wrong meal category! Choose from: %s.", MealPlannerUtils.getAvailableCategories());
                System.out.println(message);
            }
        }

        return category;
    }

    public Category askUserForCategoryToPrint() {
        String question = String.format("Which category do you want to print (%s)?", MealPlannerUtils.getAvailableCategories());
        System.out.println(question);

        String userInputForCategory;
        boolean doAgain;
        do {
            userInputForCategory = scanner.nextLine();
            doAgain = MealPlannerUtils.isInvalidCategory(userInputForCategory);
            if (doAgain) {
                String msg = String.format("Wrong meal category! Choose from: %s.", MealPlannerUtils.getAvailableCategories());
                System.out.println(msg);
            }
        } while (doAgain);

        return Category.valueOf(userInputForCategory.toUpperCase());
    }

    public String askUserForMealName() {
        System.out.println("Input the meal's name:");

        String input = "";
        while (MealPlannerUtils.isInvalidInput(input)) {
            input = scanner.nextLine();
            if (MealPlannerUtils.isInvalidInput(input)) {
                String message = "Wrong format. Use letters only!";
                System.out.println(message);
            }
        }

        return input;
    }

    public void askForIngredients(Meal meal) {
        System.out.println("Input the ingredients:");

        String[] ingredients;
        boolean doAgain;
        do {
            doAgain = false;
            ingredients = scanner.nextLine().split(",");

            if (ingredients.length == 1) {
                String ingredient = ingredients[0];
                if ("".equals(ingredient) || " ".equals(ingredient)) {
                    String message = "Wrong format. Use letters only!";
                    System.out.println(message);
                    doAgain = true;
                    continue;
                }
            }

            for (String ingredient : ingredients) {
                String removeWhiteSpaces = ingredient.replace(" ", "");
                if (MealPlannerUtils.isInvalidInput(removeWhiteSpaces)) {
                    String message = "Wrong format. Use letters only!";
                    System.out.println(message);
                    doAgain = true;
                    break;
                }
            }
        } while (doAgain);

        Arrays.stream(ingredients).forEach(meal::add);


    }

    public String askUserForMeal() {
        return scanner.nextLine();
    }

    public String askUserForFileName() {
        System.out.println("Input a filename:");
        return scanner.nextLine();
    }
}
