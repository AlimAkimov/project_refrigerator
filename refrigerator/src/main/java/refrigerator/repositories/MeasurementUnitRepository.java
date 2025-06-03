package refrigerator.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import refrigerator.model.Ingredient;
import refrigerator.model.MeasurementUnit;

@Repository
public interface MeasurementUnitRepository extends JpaRepository<MeasurementUnit, Long> {
    MeasurementUnit findByName(String name);
}
