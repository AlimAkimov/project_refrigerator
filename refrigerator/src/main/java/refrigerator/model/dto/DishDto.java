package refrigerator.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import refrigerator.enums.DifficultyLevel;
import refrigerator.enums.DishType;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DishDto {

    private String name;

    private String description;

    private String instructions;

    private Integer cookingTime;

    private DifficultyLevel difficultyLevel;

    private DishType dishType;

    private List<IngredientDto> ingredientDtos;


}
