package refrigerator.refrigerator.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import refrigerator.service.DishService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DishServiceTest {

    @Mock
    private MeasurementUnitRepository measurementUnitRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private DishRepository dishRepository;

    @InjectMocks
    private DishService dishService;

    @Test
    void createDish_ShouldCorrectlyMapAndSaveDishWithIngredients() {
        Dish dish = new Dish();
        dish.setId(999L);
        dish.setName("Pasta");
        dish.setDifficultyLevel(DifficultyLevel.MEDIUM);
        dish.setDescription("Delicious pasta");

        DishDto dishDto = new DishDto();
        dishDto.setName("Pasta");
        dishDto.setDifficultyLevel(DifficultyLevel.MEDIUM);
        dishDto.setDescription("Delicious pasta");

        IngredientDto ingredientDto = new IngredientDto();
        ingredientDto.setName("Tomato");

        MeasurementUnitDto unitDto = new MeasurementUnitDto();
        unitDto.setName("Gram");
        unitDto.setAmount(200);
        ingredientDto.setMeasurementUnitDto(unitDto);

        ArrayList<IngredientDto> ingredients = new ArrayList<>();
        ingredients.add(ingredientDto);
        dishDto.setIngredientDtos(ingredients);

        Ingredient mockIngredient = new Ingredient();
        mockIngredient.setName("Tomato");

        MeasurementUnit mockUnit = new MeasurementUnit();
        mockUnit.setName("Gram");

        when(ingredientRepository.findByNameIgnoreCase("Tomato")).thenReturn(mockIngredient);
        when(measurementUnitRepository.findByName("Gram")).thenReturn(mockUnit);
        when(dishRepository.save(any(Dish.class))).thenReturn(dish);

        dishService.createDish(dishDto);

        verify(dishRepository).save(argThat(savedDish -> {
            assertEquals("Pasta", savedDish.getName());
            assertEquals(DifficultyLevel.MEDIUM, savedDish.getDifficultyLevel());
            assertEquals("Delicious pasta", savedDish.getDescription());

            assertEquals(1, savedDish.getDishIngredients().size());

            DishIngredient dishIngredient = savedDish.getDishIngredients().get(0);
            assertEquals(mockIngredient, dishIngredient.getIngredient());
            assertEquals(mockUnit, dishIngredient.getMeasurementUnit());
            assertEquals(200, dishIngredient.getAmount());

            return true;
        }));
    }

    @Test
    void findDishById_WhenExists_ShouldReturnDish() {
        Long id = 1L;
        String name = "Test";
        Dish dish = new Dish();
        dish.setId(id);
        dish.setName(name);
        when(dishRepository.findById(id)).thenReturn(Optional.of(dish));

        DishDto result = dishService.findDishById(id);

        assertNotNull(result);
        assertEquals("Test", result.getName());
        verify(dishRepository, times(1)).findById(id);
    }

    @Test
    void findDishById_WhenNotExists_ShouldThrowException() {
        Long id = 999L;
        when(dishRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dishService.findDishById(id));
        verify(dishRepository, times(1)).findById(id);
    }

    @Test
    void findDishByName_ShouldReturnDish() {
        String name = "Test Dish";
        Dish dish = new Dish();
        dish.setName(name);
        when(dishRepository.findByNameIgnoreCase(name)).thenReturn(dish);

        DishDto result = dishService.findDishByName(name);

        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(dishRepository, times(1)).findByNameIgnoreCase(name);
    }

    @Test
    public void editDish_WhenDishExists_ShouldReturnUpdatedDishDto() {
        Long dishId = 1L;
        DishDto inputDto = new DishDto();
        inputDto.setName("Updated Name");
        inputDto.setIngredientDtos(new ArrayList<>());

        Dish existingDish = new Dish();
        existingDish.setId(dishId);
        existingDish.setDishIngredients(new ArrayList<>());

        when(dishRepository.findById(dishId)).thenReturn(Optional.of(existingDish));
        when(dishRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DishDto result = dishService.editDish(dishId, inputDto);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        verify(dishRepository).findById(dishId);
    }

    @Test
    void deleteDishById_ShouldCallRepository() {
        Long id = 1L;
        doNothing().when(dishRepository).deleteById(id);

        dishService.deleteDishById(id);

        verify(dishRepository, times(1)).deleteById(id);
    }

    @Test
    void filterByDishType() {
        ArrayList<Dish> dishes = new ArrayList<>();
        Dish dish = new Dish();
        DishType dishType = DishType.BREAKFAST;
        dish.setDishType(dishType);
        dishes.add(dish);

        when(dishRepository.findByDishType(dishType)).thenReturn(dishes);

        List<DishDto> result = dishService.filterByDishType(dishType);

        assertNotNull(result);
        assertEquals(dishType, result.getFirst().getDishType());
        verify(dishRepository, times(1)).findByDishType(dishType);
    }

    @Test
    void filterByDifficultyLevel() {
        ArrayList<Dish> dishes = new ArrayList<>();
        Dish dish = new Dish();
        DifficultyLevel difficultyLevel = DifficultyLevel.HARD;
        dish.setDifficultyLevel(difficultyLevel);
        dishes.add(dish);

        when(dishRepository.findByDifficultyLevel(difficultyLevel)).thenReturn(dishes);

        List<DishDto> result = dishService.filterByDifficultyLevel(difficultyLevel);

        assertNotNull(result);
        assertEquals(difficultyLevel, result.getFirst().getDifficultyLevel());
        verify(dishRepository, times(1)).findByDifficultyLevel(difficultyLevel);
    }
}
