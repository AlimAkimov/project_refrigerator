package refrigerator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.Iterator;
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

    @Transactional
    public void createDish(DishDto dishDto) {
        Dish save = dishRepository.save(convertDTOToDish(dishDto));
        logger.debug("Creating dish with id {}", save.getId());
    }

    @Transactional
    public DishDto findDishById(Long id) {
        logger.debug("Find dish with id " + id);
        Optional<Dish> dishFromDB = dishRepository.findById(id);
        if (dishFromDB.isPresent()) {
            return convertDishToDTO(dishFromDB.get());
        } else throw new ResourceNotFoundException("Не найдено");
    }

    @Transactional
    public DishDto findDishByName(String name) {
        logger.debug("Find dish by name");
        Dish dishByName = dishRepository.findByNameIgnoreCase(name);
        if (dishByName != null) {
            return convertDishToDTO(dishByName);
        } else throw new ResourceNotFoundException("Dish не найден");

    }

    @Transactional
    public DishDto editDish(Long id, DishDto dishDto) {
        logger.debug("Edit dish");
        Optional<Dish> optionalDish = dishRepository.findById(id);
        if (optionalDish.isPresent()) {
            Dish dishByIdFromDB = optionalDish.get();
            List<DishIngredient> convertedDishIngredients = convertDishIngredientsForDtoToDish(dishDto, dishByIdFromDB);
            dishByIdFromDB.setDifficultyLevel(dishDto.getDifficultyLevel());
            dishByIdFromDB.setName(dishDto.getName());
            dishByIdFromDB.setInstructions(dishDto.getInstructions());
            dishByIdFromDB.setDescription(dishDto.getDescription());
            dishByIdFromDB.setCookingTime(dishDto.getCookingTime());
            addDishIngredients(dishByIdFromDB.getDishIngredients(), convertedDishIngredients);
            dishByIdFromDB.getDishIngredients().addAll(convertedDishIngredients);
            for (DishIngredient dishIngredient : dishByIdFromDB.getDishIngredients()) {
                dishIngredient.getDish().setId(id);
            }
            return convertDishToDTO(dishRepository.save(dishByIdFromDB));
        } else throw new RuntimeException("Блюдо не найдено");
    }

    @Transactional
    public void deleteDishById(Long id) {
        logger.debug("Delete dish");
        dishRepository.deleteById(id);
    }

    @Transactional
    public List<DishDto> filterByDishType(DishType dishType) {
        logger.debug("filter by dish type");
        List<DishDto> dishDtos = new ArrayList<>();
        List<Dish> byDishType = dishRepository.findByDishType(dishType);
        for (Dish dish : byDishType) {
            DishDto dishDto = convertDishToDTO(dish);
            dishDtos.add(dishDto);
        }
        return dishDtos;
    }

    @Transactional
    public List<DishDto> filterByDifficultyLevel(DifficultyLevel difficultyLevel) {
        logger.debug("filter by difficulty level");
        List<DishDto> dishDtos = new ArrayList<>();
        List<Dish> byDifficultyLevel = dishRepository.findByDifficultyLevel(difficultyLevel);
        for (Dish dish : byDifficultyLevel) {
            DishDto dishDto = convertDishToDTO(dish);
            dishDtos.add(dishDto);
        }
        return dishDtos;
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
        dish.setDishIngredients(convertDishIngredientsForDtoToDish(dishDto, dish));
        return dish;
    }

    public List<DishIngredient> convertDishIngredientsForDtoToDish(DishDto dishDto, Dish dish) {
        List<IngredientDto> ingredientDtos = dishDto.getIngredientDtos();
        ArrayList<DishIngredient> dishIngredients = new ArrayList<>();
        for (IngredientDto ingredientDto : ingredientDtos) {
            DishIngredient dishIngredient = new DishIngredient();
            Ingredient ingredientEntity = ingredientRepository.findByNameIgnoreCase(ingredientDto.getName());
            String measurementUnitName = ingredientDto.getMeasurementUnitDto().getName();
            Integer amount = ingredientDto.getMeasurementUnitDto().getAmount();
            MeasurementUnit measurementUnitEntity = measurementUnitRepository.findByName(measurementUnitName);
            dishIngredient.setIngredient(ingredientEntity);
            dishIngredient.setMeasurementUnit(measurementUnitEntity);
            dishIngredient.setDish(dish);
            dishIngredient.setAmount(amount);
            dishIngredients.add(dishIngredient);
        }
        return dishIngredients;
    }

    @Transactional
    private void addDishIngredients(List<DishIngredient> dishIngredientsFromDb, List<DishIngredient> addedDishIngredients) {
        Iterator<DishIngredient> iterator1 = dishIngredientsFromDb.iterator();
        while (iterator1.hasNext()) {
            DishIngredient dishIngredient = iterator1.next();
            Optional<DishIngredient> any = addedDishIngredients.stream().filter(e -> e.getIngredient().getId().equals(dishIngredient.getIngredient().getId())).findAny();
            if (any.isPresent()) {
                addedDishIngredients.removeIf(e -> e.getIngredient().equals(any.get().getIngredient()));
            } else {
                iterator1.remove();
            }
        }

    }

    @Transactional
    public List<IngredientDto> getIngredientsForADish(Long id) {
        DishDto dishById = findDishById(id);
        List<IngredientDto> ingredientDtos = dishById.getIngredientDtos();
        return ingredientDtos;

    }

    @Transactional
    public List<DishDto> searchForFishesByExactMatchOfIngredients(List<String> ingredients) {
        List<String> nameIngredients = new ArrayList<>();
        for (String ingredient : ingredients) {
            Ingredient byName = ingredientRepository.findByNameIgnoreCase(ingredient);
            if (byName != null) {
                nameIngredients.add(byName.getName());
            } else throw new ResourceNotFoundException("ингредиент " + ingredient + " не найден");
        }
        List<Dish> dishByExactIngredients = dishRepository.findByExactIngredients(nameIngredients);
        List<DishDto> dishDtoByExactIngredients = new ArrayList<>();
        for (Dish dishByExactIngredient : dishByExactIngredients) {
            DishDto dishDto = convertDishToDTO(dishByExactIngredient);
            dishDtoByExactIngredients.add(dishDto);
        }
        return dishDtoByExactIngredients;
    }

    @Transactional
    public List<DishDto> searchForFishesByPartialIngredients(List<String> ingredients) {
        List<String> nameIngredients = new ArrayList<>();
        for (String ingredient : ingredients) {
            Ingredient byName = ingredientRepository.findByNameIgnoreCase(ingredient);
            if (byName != null) {
                nameIngredients.add(byName.getName());
            } else throw new ResourceNotFoundException("ингредиент " + ingredient + " не найден");
        }
        List<Dish> dishByExactIngredients = dishRepository.findByPartialIngredients(nameIngredients);
        List<DishDto> dishDtoByExactIngredients = new ArrayList<>();
        for (Dish dishByExactIngredient : dishByExactIngredients) {
            DishDto dishDto = convertDishToDTO(dishByExactIngredient);
            dishDtoByExactIngredients.add(dishDto);
        }
        return dishDtoByExactIngredients;
    }


}
