package mealplanner;

public enum Action {

    ADD("add"),
    SHOW("show"),
    PLAN("plan"),
    SAVE("save"),
    EXIT("exit"),
    UNKNOWN("unknown");

    public final String label;

    Action(String label) {
        this.label = label;
    }

}
