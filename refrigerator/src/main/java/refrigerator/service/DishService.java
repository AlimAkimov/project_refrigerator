package refrigerator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import refrigerator.Exceptions.NotFoundException;
import refrigerator.model.Dish;
import refrigerator.repositories.DishRepository;

import java.util.Optional;


@Service
public class DishService {

    Logger logger = LoggerFactory.getLogger(DishService.class);

    @Autowired
     private DishRepository dishRepository;

    public Dish createDish(Dish dish) {
        logger.debug("Creating dish with id {}", dish.getId());
        return dishRepository.save(dish);
    }

    public Dish findDishById(Long id) {
        logger.debug("Find dish with id " + id);
        Optional<Dish> dishIdFromDB = dishRepository.findById(id);
        if (dishIdFromDB.isPresent()) {
            return dishIdFromDB.get();
        } else throw new NotFoundException("Не найдено");
    }

    public Dish findDishByName(String name) {
        logger.debug("Find dish by name");
        Dish byName = dishRepository.findByName(name);
        if (byName != null) {
            return byName;
        } else throw new NotFoundException("Dish не найден");

    }

    public Dish editDish(Dish dish) {
        logger.debug("Edit dish");
        Optional<Dish> dishFromDB = dishRepository.findById(dish.getId());
        if (dishFromDB.isPresent()) {
            return dishRepository.save(dish);
        } else throw new RuntimeException("Блюдо не найдено");
    }

    public void deleteDishById(Long id) {
        logger.debug("Delete dish");
        dishRepository.deleteById(id);
    }

}
