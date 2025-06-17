package refrigerator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import refrigerator.Exceptions.ResourceNotFoundException;
import refrigerator.enums.DifficultyLevel;
import refrigerator.enums.DishType;
import refrigerator.model.Dish;
import refrigerator.model.DishIngredient;
import refrigerator.model.Ingredient;
import refrigerator.model.MeasurementUnit;
import refrigerator.model.dto.DishDto;
import refrigerator.model.dto.IngredientDto;
import refrigerator.model.dto.MeasurementUnitDto;
import refrigerator.repositories.DishRepository;
import refrigerator.repositories.IngredientRepository;
import refrigerator.repositories.MeasurementUnitRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class DishService {

    Logger logger = LoggerFactory.getLogger(DishService.class);

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private MeasurementUnitRepository measurementUnitRepository;

    public void createDish(DishDto dishDto) {
        Dish save = dishRepository.save(convertDTOToDish(dishDto));
        logger.debug("Creating dish with id {}", save.getId());
    }

    public DishDto findDishById(Long id) {
        logger.debug("Find dish with id " + id);
        Optional<Dish> dishFromDB = dishRepository.findById(id);
        if (dishFromDB.isPresent()) {
            return convertDishToDTO(dishFromDB.get());
        } else throw new ResourceNotFoundException("Не найдено");
    }

    public DishDto findDishByName(String name) {
        logger.debug("Find dish by name");
        Dish dishByName = dishRepository.findByNameIgnoreCase(name);
        if (dishByName != null) {
            return convertDishToDTO(dishByName);
        } else throw new ResourceNotFoundException("Dish не найден");

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

    public List<Dish> filterByDishType(DishType dishType) {
        logger.debug("filter by dish type");
        return dishRepository.findByDishType(dishType);
    }

    public List<Dish> filterByDifficultyLevel(DifficultyLevel difficultyLevel) {
        logger.debug("filter by difficulty level");
        return dishRepository.findByDifficultyLevel(difficultyLevel);
    }

    private DishDto convertDishToDTO(Dish dish) {
        DishDto dishDto = new DishDto();
        dishDto.setName(dish.getName());
        dishDto.setInstructions(dish.getInstructions());
        dishDto.setDescription(dish.getDescription());
        dishDto.setDishType(dish.getDishType());
        dishDto.setCookingTime(dish.getCookingTime());
        dishDto.setDifficultyLevel(dish.getDifficultyLevel());
        List<IngredientDto> ingredientDtoList = new ArrayList<>();
        for (DishIngredient dishIngredient : dish.getDishIngredients()) {
            IngredientDto ingredientDto = new IngredientDto();
            ingredientDto.setName(dishIngredient.getIngredient().getName());
            ingredientDto.setCategory(dishIngredient.getIngredient().getCategory());
            ingredientDto.setDescription(dishIngredient.getIngredient().getDescription());
            MeasurementUnitDto measurementUnitDto = new MeasurementUnitDto();
            measurementUnitDto.setName(dishIngredient.getMeasurementUnit().getName());
            measurementUnitDto.setAmount(dishIngredient.getAmount());
            ingredientDto.setMeasurementUnitDto(measurementUnitDto);
            ingredientDtoList.add(ingredientDto);
        }
        dishDto.setIngredientDtos(ingredientDtoList);
        return dishDto;
    }

    private Dish convertDTOToDish(DishDto dishDto) {
        Dish dish = new Dish();
        dish.setName(dishDto.getName());
        dish.setDifficultyLevel(dishDto.getDifficultyLevel());
        dish.setDishType(dishDto.getDishType());
        dish.setDescription(dishDto.getDescription());
        dish.setInstructions(dishDto.getInstructions());
        dish.setCookingTime(dishDto.getCookingTime());
        List<IngredientDto> ingredientDtos = dishDto.getIngredientDtos();
        ArrayList<DishIngredient> dishIngredients = new ArrayList<>();
        for (IngredientDto ingredientDto : ingredientDtos) {
            DishIngredient dishIngredient = new DishIngredient();
            Ingredient ingredientEntity = ingredientRepository.findByName(ingredientDto.getName());
            String measurementUnitName = ingredientDto.getMeasurementUnitDto().getName();
            Integer amount = ingredientDto.getMeasurementUnitDto().getAmount();
            MeasurementUnit measurementUnitEntity = measurementUnitRepository.findByName(measurementUnitName);
            dishIngredient.setIngredient(ingredientEntity);
            dishIngredient.setMeasurementUnit(measurementUnitEntity);
            dishIngredient.setDish(dish);
            dishIngredient.setAmount(amount);
            dishIngredients.add(dishIngredient);
        }
        dish.setDishIngredients(dishIngredients);
        return dish;
    }

    public List<IngredientDto> getIngredientsForADish(Long id) {
        DishDto dishById = findDishById(id);
        List<IngredientDto> ingredientDtos = dishById.getIngredientDtos();
        return ingredientDtos;

    }


}
