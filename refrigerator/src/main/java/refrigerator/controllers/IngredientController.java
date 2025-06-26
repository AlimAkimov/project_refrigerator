package refrigerator.controllers;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import refrigerator.model.Dish;
import refrigerator.model.Ingredient;
import refrigerator.model.dto.IngredientDto;
import refrigerator.service.IngredientService;

@RestController
@RequestMapping(path = "/ingredient")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @PostMapping(path = "/create")
    public ResponseEntity<Ingredient> createIngredient(@RequestBody Ingredient ingredient) {
        return ResponseEntity.ok(ingredientService.createIngredient(ingredient));
    }

    @GetMapping(path = "/find-by-id/{id}")
    public ResponseEntity<IngredientDto> findIngredientById(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientService.findIngredientById(id));
    }

    @GetMapping(path = "/findByName/{name}")
    public ResponseEntity<IngredientDto> findIngredientByName(@PathVariable String name) {
        return ResponseEntity.ok(ingredientService.findIngredientByName(name));
    }

    @PutMapping(path = "/edit/{id}")
    public ResponseEntity<IngredientDto> editIngredient(@PathVariable Long id,
                                                        @RequestBody IngredientDto ingredientDto) {
        return ResponseEntity.ok(ingredientService.editIngredient(id, ingredientDto));
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<Ingredient> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredientById(id);
        return ResponseEntity.ok().build();
    }


}
