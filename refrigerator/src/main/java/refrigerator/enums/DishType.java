package refrigerator.enums;

import lombok.Getter;

@Getter
public enum DishType {
    BREAKFAST("Завтрак"),
    DINNER("Ужин"),
    LUNCH("Обед"),
    DESSERT("Десерт"),
    DRINK("Напиток"),
    SALAD("Салат"),
    APPETIZER("Закуска");


    private String value;

    DishType(String value) {
        this.value = value;
    }
}
