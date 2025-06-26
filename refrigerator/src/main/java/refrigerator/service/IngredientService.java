package refrigerator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import refrigerator.Exceptions.ResourceNotFoundException;
import refrigerator.model.DishIngredient;
import refrigerator.model.Ingredient;
import refrigerator.model.dto.IngredientDto;
import refrigerator.model.dto.MeasurementUnitDto;
import refrigerator.repositories.IngredientRepository;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientService {

    Logger logger = LoggerFactory.getLogger(IngredientService.class);

    @Autowired
    private IngredientRepository ingredientRepository;

    @Transactional
    public Ingredient createIngredient(Ingredient ingredient) {
        logger.debug("Creating ingredient with id {}", ingredient.getId());
        return ingredientRepository.save(ingredient);
    }

    @Transactional
    public IngredientDto findIngredientById(Long id) {
        logger.debug("Find ingredient with id " + id);
        Optional<Ingredient> ingredientIdFromDB = ingredientRepository.findById(id);
        if (ingredientIdFromDB.isPresent()) {
            Ingredient ingredient = ingredientIdFromDB.get();
            return convertIngredientToDto(ingredient);
        } else throw new ResourceNotFoundException("Не найдено");
    }

    @Transactional
    public IngredientDto findIngredientByName(String name) {
        logger.debug("Find ingredient by name");
        Ingredient byName = ingredientRepository.findByNameIgnoreCase(name);
        if (byName != null) {
            return convertIngredientToDto(byName);
        } else throw new ResourceNotFoundException("Ingredient не найдено");

    }

    @Transactional
    public IngredientDto editIngredient(Long id, IngredientDto ingredientDto) {
        logger.debug("Edit ingredient");
        Optional<Ingredient> optionalIngredient = ingredientRepository.findById(id);
        if (optionalIngredient.isPresent()) {
            Ingredient ingredientFromDB = optionalIngredient.get();
            List<DishIngredient> dishes = ingredientFromDB.getDishes();
            Ingredient ingredient = convertDtoToIngredient(ingredientDto, dishes);
            ingredientFromDB.setName(ingredient.getName());
            ingredientFromDB.setCategory(ingredient.getCategory());
            ingredientFromDB.setDescription(ingredient.getDescription());
            ingredientRepository.save(ingredientFromDB);
            return ingredientDto;
        } else throw new RuntimeException("Ингридиент не найден");
    }

    @Transactional
    public void deleteIngredientById(Long id) {
        logger.debug("Delete ingredient");
        ingredientRepository.deleteById(id);
    }

    private IngredientDto convertIngredientToDto(Ingredient ingredient) {
        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setName(ingredient.getName());
        ingredientDto.setDescription(ingredient.getDescription());
        ingredientDto.setCategory(ingredient.getCategory());
        List<DishIngredient> dishes = ingredient.getDishes();
        MeasurementUnitDto measurementUnitDto = new MeasurementUnitDto();
        for (DishIngredient dish : dishes) {
            measurementUnitDto.setAmount(dish.getAmount());
            measurementUnitDto.setName(dish.getMeasurementUnit().getName());
        }
        ingredientDto.setMeasurementUnitDto(measurementUnitDto);
        return ingredientDto;
    }

    private Ingredient convertDtoToIngredient(IngredientDto ingredientDto, List<DishIngredient> dishIngredients) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientDto.getName());
        ingredient.setCategory(ingredientDto.getCategory());
        ingredient.setDescription(ingredientDto.getDescription());
        ingredient.setDishes(dishIngredients);
        return ingredient;
    }




}
