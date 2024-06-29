package mealplanner;

import java.io.IOException;
import java.sql.SQLException;

public final class Main {

  public static void main(String[] args) throws SQLException, IOException {
    new MealPlanner().run();
  }
}
