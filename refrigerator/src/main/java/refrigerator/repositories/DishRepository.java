package refrigerator.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import refrigerator.enums.DifficultyLevel;
import refrigerator.enums.DishType;
import refrigerator.model.Dish;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {

    Dish findByNameIgnoreCase(String name);

    List<Dish> findByDishType(DishType type);

    List<Dish> findByDifficultyLevel(DifficultyLevel difficultyLevel);

    @Query("SELECT d FROM Dish d JOIN d.dishIngredients di JOIN di.ingredient i "
    + "WHERE i.name IN :ingredients "
    + "GROUP BY d "
    + "HAVING COUNT(DISTINCT i) = (SELECT COUNT(DISTINCT i2) FROM Ingredient i2 WHERE i2.name IN :ingredients) "
    + "AND COUNT(DISTINCT i) = SIZE(d.dishIngredients)")
    List<Dish> findByExactIngredients(@Param("ingredients") List<String> ingredients);

    @Query("SELECT d FROM Dish d JOIN d.dishIngredients di JOIN di.ingredient i " +
            "WHERE i.name IN :ingredients " +
            "GROUP BY d " +
            "HAVING COUNT(DISTINCT i) >= SIZE(d.dishIngredients) * 0.7")
    List<Dish> findByPartialIngredients(@Param("ingredients") List<String> ingredients);
}

