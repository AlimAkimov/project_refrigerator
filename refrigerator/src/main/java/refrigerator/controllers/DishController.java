package refrigerator.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import refrigerator.enums.DifficultyLevel;
import refrigerator.enums.DishType;
import refrigerator.model.Dish;
import refrigerator.model.dto.DishDto;
import refrigerator.model.dto.IngredientDto;
import refrigerator.service.DishService;

import java.util.List;

@RestController
@RequestMapping(path = "/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping(path = "/create")
    public ResponseEntity createDish(@RequestBody DishDto dishDto) {
        dishService.createDish(dishDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/find-by-id/{id}")
    public ResponseEntity<DishDto> findDishById(@PathVariable Long id) {
        return ResponseEntity.ok(dishService.findDishById(id));
    }

    @GetMapping(path = "/find-by-name/{name}")
    public ResponseEntity<DishDto> findDishByName(@PathVariable String name) {
        return ResponseEntity.ok(dishService.findDishByName(name));
    }

    @PutMapping(path = "/edit")
    public ResponseEntity<Dish> editDish(@RequestBody Dish dish) {
        return ResponseEntity.ok(dishService.editDish(dish));
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<Dish> deleteDish(@PathVariable Long id) {
        dishService.deleteDishById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/filter-by-dishType")
    public ResponseEntity<List<DishDto>> filteByDishType(@RequestParam DishType dishType) {
        List<DishDto> dishes = dishService.filterByDishType(dishType);
        return ResponseEntity.ok(dishes);
    }

    @GetMapping(path = "/filter-by-difficulty-level")
    public ResponseEntity<List<DishDto>> filteByDifficultyLevel(@RequestParam DifficultyLevel difficultyLevel) {
        List<DishDto> dishes = dishService.filterByDifficultyLevel(difficultyLevel);
        return ResponseEntity.ok(dishes);
    }

    @GetMapping(path = "/ingredients-for-a-dish/{id}")
    public ResponseEntity<List<IngredientDto>> ingredientDtoList(@PathVariable Long id) {
        List<IngredientDto> ingredientDtoList = dishService.getIngredientsForADish(id);
        return ResponseEntity.ok(ingredientDtoList);
    }

    @GetMapping(path = "/find-dish-by-exact-ingredients")
    public ResponseEntity<List<DishDto>> findByExactIngredients(@RequestParam List<String> ingredients) {
        List<DishDto> dishDtos = dishService.searchForFishesByExactMatchOfIngredients(ingredients);
        return ResponseEntity.ok(dishDtos);
    }

    @GetMapping(path = "/find-dish-by-partial-ingredients")
    public ResponseEntity<List<DishDto>> findByPartialIngredients(@RequestParam List<String> ingredients) {
        List<DishDto> dishDtos = dishService.searchForFishesByPartialIngredients(ingredients);
        return ResponseEntity.ok(dishDtos);
    }

}
