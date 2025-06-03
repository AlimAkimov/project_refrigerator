package refrigerator.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import refrigerator.model.Dish;
import refrigerator.model.Ingredient;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    Ingredient findByName(String name);
}
