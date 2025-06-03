package refrigerator.controllers;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import refrigerator.model.Dish;
import refrigerator.model.Ingredient;
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

    @GetMapping(path = "/findByid/{id}")
    public ResponseEntity<Ingredient> findIngredientById(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientService.findIngredientById(id));
    }

    @GetMapping(path = "/findByName/{name}")
    public ResponseEntity<Ingredient> findIngredientByName(@PathVariable String name) {
        return ResponseEntity.ok(ingredientService.findIngredientByName(name));
    }

    @PutMapping(path = "/edit")
    public ResponseEntity<Ingredient> editIngredient(@RequestBody Ingredient ingredient) {
        return ResponseEntity.ok(ingredientService.editIngredient(ingredient));
    }

    @DeleteMapping(path = "/delete/{id}")
    public ResponseEntity<Ingredient> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredientById(id);
        return ResponseEntity.ok().build();
    }


}
