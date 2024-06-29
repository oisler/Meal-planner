package mealplanner;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class MealPlanner {

    private final DbClient dbClient;
    private final MainMenu mainMenu;

    public MealPlanner() throws SQLException {
        this.dbClient = new DbClient();
        this.mainMenu = new MainMenu();
    }

    public void run() throws SQLException, IOException {
        dbClient.start();

        Action action;
        do {
            action = mainMenu.askUserForAction();
            if (Action.ADD == action) {
                doAdd();
            } else if (Action.SHOW == action) {
                doShow();
            } else if (Action.PLAN == action) {
                doPlan();
            } else if (Action.SAVE == action) {
                doSave();
            } else if (Action.EXIT == action) {
                doExit();
            }
        } while (Action.EXIT != action);
    }

    private void doAdd() throws SQLException {
        Meal meal = new Meal(mainMenu.askUserForCategory(), mainMenu.askUserForMealName());
        mainMenu.askForIngredients(meal);
        dbClient.addMealWithIngredients(meal);
    }

    private void doShow() throws SQLException {
        Category category = mainMenu.askUserForCategoryToPrint();
        dbClient.printMealsByCategoryOrderedById(category);
    }

    private void doPlan() throws SQLException {
        dbClient.truncatePLan();

        for (DayOfWeek day : DayOfWeek.values()) {
            String dayOfWeek = day.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            System.out.println(dayOfWeek);

            List<Category> categories = Arrays
                    .stream(Category.values())
                    .filter(c -> c != Category.UNKNOWN)
                    .toList();
            for (Category category : categories) {
                Map<Integer, String> meals = dbClient.getMealsByCategoryOrderedByMeal(category);
                meals.values().stream().sorted().forEach(System.out::println);

                System.out.printf("Choose the %s for %s from the list above:\n", category.label, dayOfWeek);
                String chosenMeal = mainMenu.askUserForMeal();
                while (!meals.containsValue(chosenMeal)) {
                    System.out.println("This meal doesn’t exist. Choose a meal from the list above.");
                    chosenMeal = mainMenu.askUserForMeal();
                }

                String finalChosenMeal = chosenMeal;
                int mealId = meals
                        .entrySet()
                        .stream()
                        .filter(e -> finalChosenMeal.equals(e.getValue()))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElseThrow();

                dbClient.addMealWithDayAndCategoryToPlan(chosenMeal, mealId, dayOfWeek, category);
            }

            System.out.printf("Yeah! We planned the meals for %s.\n\n", dayOfWeek);
        }

        dbClient.printMealPLan();
    }

    private void doSave() throws SQLException, IOException {
        if (!dbClient.planExists()) {
            System.out.println("Unable to save. Plan your meals first.");
            return;
        }

        Map<String, Integer> shoppingList = dbClient.getShoppingList();
        String fileName = mainMenu.askUserForFileName();
        File file = new File(fileName);
        PrintWriter writer = new PrintWriter(file);
        shoppingList.forEach((key, value) -> writer.printf("%s%s\n", key, value > 1 ? " x" + value : ""));
        writer.close();
        System.out.println("Saved!");
    }


    private void doExit() throws SQLException {
        System.out.println("Bye!");
        dbClient.end();
    }
}
