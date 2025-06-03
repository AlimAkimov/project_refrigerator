package refrigerator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import refrigerator.Exceptions.NotFoundException;
import refrigerator.model.Ingredient;
import refrigerator.repositories.IngredientRepository;

import java.util.Optional;

@Service
public class IngredientService {

    Logger logger = LoggerFactory.getLogger(IngredientService.class);

    @Autowired
    private IngredientRepository ingredientRepository;

    public Ingredient createIngredient(Ingredient ingredient) {
        logger.debug("Creating ingredient with id {}", ingredient.getId());
        return ingredientRepository.save(ingredient);
    }

    public Ingredient findIngredientById(Long id) {
        logger.debug("Find ingredient with id " + id);
        Optional<Ingredient> ingredientIdFromDB = ingredientRepository.findById(id);
        if (ingredientIdFromDB.isPresent()) {
            return ingredientIdFromDB.get();
        } else throw new NotFoundException("Не найдено");
    }

    public Ingredient findIngredientByName(String name) {
        logger.debug("Find ingredient by name");
        Ingredient byName = ingredientRepository.findByName(name);
        if (byName != null) {
            return byName;
        } else throw new NotFoundException("Ingredient не найдено");

    }

    public Ingredient editIngredient(Ingredient ingredient) {
        logger.debug("Edit ingredient");
        Optional<Ingredient> ingredientFromDB = ingredientRepository.findById(ingredient.getId());
        if (ingredientFromDB.isPresent()) {
            return ingredientRepository.save(ingredient);
        } else throw new RuntimeException("Ингридиент не найден");
    }

    public void deleteIngredientById(Long id) {
        logger.debug("Delete ingredient");
        ingredientRepository.deleteById(id);
    }
}
