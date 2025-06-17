package refrigerator.model.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import refrigerator.model.DishIngredient;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class IngredientDto {

    private String name;

    private String description;

    private String category;

    private MeasurementUnitDto measurementUnitDto;


}
