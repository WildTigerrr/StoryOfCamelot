package com.wildtigerrr.StoryOfCamelot.database.schema.enums;

public enum  Stats {
    STRENGTH (
            "силы"
    ),
    HEALTH (
            "здоровья"
    ),
    AGILITY (
            "ловкости"
    ),
    CHARISMA (
            "харизмы"
    ),
    INTELLIGENCE (
            "интеллекта"
    ),
    ENDURANCE (
            "выносливости"
    ),
    LUCK (
            "удачи"
    );

    private final String which;

    Stats(String text) {
        this.which = text;
    }

    public String which() {
        return which;
    }
}
