package mealplanner;

public enum Category {

    BREAKFAST("breakfast"),
    LUNCH("lunch"),
    DINNER("dinner"),
    UNKNOWN("unknown");

    public final String label;

    Category(String label) {
        this.label = label;
    }

}
