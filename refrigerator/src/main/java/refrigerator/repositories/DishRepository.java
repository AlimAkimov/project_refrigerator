package refrigerator.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
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

}
