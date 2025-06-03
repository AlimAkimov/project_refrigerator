package refrigerator.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import refrigerator.model.Dish;
import refrigerator.service.DishService;

@RestController
@RequestMapping(path = "/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping(path = "/create")
    public ResponseEntity<Dish> createDish(@RequestBody Dish dish) {
        return ResponseEntity.ok(dishService.createDish(dish));
    }

    @GetMapping(path = "/findByid/{id}")
    public ResponseEntity<Dish> findDishById(@PathVariable Long id) {
        return ResponseEntity.ok(dishService.findDishById(id));
    }

    @GetMapping(path = "/findByName/{name}")
    public ResponseEntity<Dish> findDishByName(@PathVariable String name) {
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


}
