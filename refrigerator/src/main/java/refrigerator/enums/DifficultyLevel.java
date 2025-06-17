package refrigerator.enums;

import lombok.Getter;

@Getter
public enum DifficultyLevel {
    EASY("Легко"),
    MEDIUM("Средне"),
    HARD("Сложно");


    private String value;

    DifficultyLevel(String value) {
        this.value = value;
    }
}
