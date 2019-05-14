package com.wildtigerrr.StoryOfCamelot.database.schema.enums;

public enum Stats {
    STRENGTH(
            "силы", "сила"
    ),
    HEALTH(
            "здоровья", "здоровье"
    ),
    AGILITY(
            "ловкости", "ловкость"
    ),
    CHARISMA(
            "харизмы", "харизма"
    ),
    INTELLIGENCE(
            "интеллекта", "интеллект"
    ),
    ENDURANCE(
            "выносливости", "выносливость"
    ),
    LUCK(
            "удачи", "удача"
    );

    public static Stats getStat(String character) {
        character = character.toUpperCase();
        switch (character) {
            case "S":
                return STRENGTH;
            case "H":
                return HEALTH;
            case "A":
                return AGILITY;
            case "C":
                return CHARISMA;
            case "I":
                return INTELLIGENCE;
            case "E":
                return ENDURANCE;
            case "L":
                return LUCK;
            default:
                return null;
        }
    }

    private final String which;
    private final String what;

    Stats(String which, String what) {
        this.which = which;
        this.what = what;
    }

    public String which() {
        return which;
    }

    public String what() {
        return what;
    }
}
