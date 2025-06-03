package refrigerator.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MeasurementUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "abbreviation ")
    private String abbreviation  ;

    @OneToMany(mappedBy = "measurementUnit")
    private List<DishIngredient> dishIngredients = new ArrayList<>();
}
