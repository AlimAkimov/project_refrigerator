package refrigerator.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import refrigerator.model.Dish;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {

    Dish findByName(String name);

}
