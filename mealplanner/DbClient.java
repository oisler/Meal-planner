package mealplanner;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class DbClient {

    private static final String URL = "jdbc:postgresql:meals_db";
    private static final String USER = "postgres";
    private static final String PASS = "1111";

    private static int MEAL_COUNTER = 0;
    private static int INGREDIENT_COUNTER = 0;

    private final Connection connection;

    public DbClient() throws SQLException {
        this.connection = DriverManager.getConnection(URL, USER, PASS);
        connection.setAutoCommit(true);
    }

    public void start() throws SQLException {
        runIncrements(this.connection);
        initCounters();
    }

    public void addMealWithIngredients(Meal meal) throws SQLException {
        MEAL_COUNTER++;

        String insert = "INSERT INTO meals (meal_id, category, meal) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insert)) {
            preparedStatement.setInt(1, MEAL_COUNTER);
            preparedStatement.setString(2, meal.getCategory().label);
            preparedStatement.setString(3, meal.getName());

            preparedStatement.executeUpdate();
        }

        insert = "INSERT INTO ingredients (ingredient_id, ingredient, meal_id) VALUES (?, ?, ?)";
        for (String ingredient : meal.getIngredients()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(insert)) {
                preparedStatement.setInt(1, INGREDIENT_COUNTER);
                preparedStatement.setString(2, ingredient);
                preparedStatement.setInt(3, MEAL_COUNTER);

                preparedStatement.executeUpdate();
            }

            INGREDIENT_COUNTER++;
        }

        System.out.println("The meal has been added!");
    }

    public void addMealWithDayAndCategoryToPlan(String meal, int mealId, String dayOfWeek, Category category) throws SQLException {
        String query = "INSERT INTO plan (day, category, meal, meal_id) values (?,?,?,?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, dayOfWeek);
            preparedStatement.setString(2, category.label);
            preparedStatement.setString(3, meal);
            preparedStatement.setInt(4, mealId);

            preparedStatement.executeUpdate();
        }

    }

    public void printMealsByCategoryOrderedById(Category category) throws SQLException {
        int counter = 0;

        String selectMealsByCategory = "SELECT * FROM MEALS WHERE CATEGORY = (?) ORDER BY MEAL_ID;";
        try (PreparedStatement selectMealsStatement = connection.prepareStatement(selectMealsByCategory)) {
            selectMealsStatement.setString(1, category.toString().toLowerCase());

            ResultSet meals = selectMealsStatement.executeQuery();
            while (meals.next()) {
                if (counter == 0) {
                    System.out.println("Category: " + category.label);
                }
                System.out.println();
                System.out.println("Name: " + meals.getString("meal").toLowerCase());

                String selectIngredients = "SELECT * FROM INGREDIENTS WHERE MEAL_ID = ? ORDER BY INGREDIENT_ID;";
                try (PreparedStatement selectIngredientsStatement = connection.prepareStatement(selectIngredients)) {
                    selectIngredientsStatement.setInt(1, meals.getInt("meal_id"));

                    ResultSet ingredients = selectIngredientsStatement.executeQuery();
                    System.out.println("Ingredients: ");
                    while (ingredients.next()) {
                        System.out.println(ingredients.getString("ingredient").trim());
                    }
                }

                counter++;
            }

            if (counter == 0) {
                System.out.println("No meals found.");
            }
        }

    }

    public void printMealPLan() throws SQLException {
        String query = "SELECT * FROM PLAN WHERE DAY = (?);";
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            for (DayOfWeek day : DayOfWeek.values()) {
                String dayOfWeek = day.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                statement.setString(1, dayOfWeek);

                ResultSet rs = statement.executeQuery();
                System.out.println(dayOfWeek);
                while (rs.next()) {
                    String category = rs.getString("category");
                    String meal = rs.getString("meal");
                    System.out.println(category + ": " + meal);
                }
                System.out.println();
            }
        }
    }

    public Map<Integer, String> getMealsByCategoryOrderedByMeal(Category category) throws SQLException {
        Map<Integer, String> meals = new HashMap<>();

        String selectMealsByCategory = "SELECT * FROM MEALS WHERE CATEGORY = (?) ORDER BY MEAL;";
        try (PreparedStatement selectMealsStatement = connection.prepareStatement(selectMealsByCategory)) {
            selectMealsStatement.setString(1, category.toString().toLowerCase());

            ResultSet rs = selectMealsStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("meal_id");
                String meal = rs.getString("meal");
                meals.put(id, meal);
            }
        }

        return meals;
    }

    public boolean planExists() throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM PLAN;")) {
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }


    public Map<String, Integer> getShoppingList() throws SQLException {
        String query = """
                 select i.ingredient, count(*)\s
                 from plan p inner join ingredients i on p.meal_id = i.meal_id\s
                 group by i.ingredient;\s
                """;
        Map<String, Integer> shoppingList = new HashMap<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String ingredient = rs.getString("ingredient");
                int count = rs.getInt(2);
                shoppingList.put(ingredient, count);
            }
        }
        return shoppingList;
    }

    private void runIncrements(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("create table if not exists meals (" +
                    "meal_id integer NOT NULL," +
                    "category varchar(1024) NOT NULL," +
                    "meal varchar(1024) NOT NULL" +
                    ")");

            statement.executeUpdate("create table if not exists ingredients ("
                    + "ingredient_id integer NOT NULL,"
                    + "ingredient varchar(1024) NOT NULL,"
                    + "meal_id integer NOT NULL"
                    + ")");

            statement.executeUpdate("create table if not exists plan ("
                    + "id SERIAL PRIMARY KEY,"
                    + "day varchar(1024) NOT NULL,"
                    + "category varchar(1024) NOT NULL,"
                    + "meal varchar(1024) NOT NULL,"
                    + "meal_id integer NOT NULL"
                    + ")");
        }
    }

    public void truncatePLan() throws SQLException {
        try (Statement statement = this.connection.createStatement()) {
            statement.executeUpdate("TRUNCATE TABLE PLAN;");
        }
    }

    private void initCounters() throws SQLException {
        try (Statement st = connection.createStatement()) {
            ResultSet resultSet = st.executeQuery("SELECT MAX(MEAL_ID) FROM MEALS;");
            resultSet.next();
            MEAL_COUNTER = resultSet.getInt(1);
        }

        try (Statement st = connection.createStatement()) {
            ResultSet resultSet = st.executeQuery("SELECT MAX(INGREDIENT_ID) FROM INGREDIENTS;");
            resultSet.next();
            INGREDIENT_COUNTER = resultSet.getInt(1);
        }
    }

    public void end() throws SQLException {
//        try (Statement statement = this.connection.createStatement()) {
//            statement.executeUpdate("DELETE FROM MEALS;");
//            statement.executeUpdate("DELETE FROM INGREDIENTS;");
//            statement.executeUpdate("DELETE FROM PLAN;");
//            statement.executeUpdate("DROP TABLE INGREDIENTS;");
//            statement.executeUpdate("DROP TABLE MEALS;");
//            statement.executeUpdate("DROP TABLE PLAN;");
//        }
        this.connection.close();
    }
}
