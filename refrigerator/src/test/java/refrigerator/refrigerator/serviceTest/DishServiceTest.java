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
        // Подготовка тестовых данных
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

        //мокирование репозиториев
        Ingredient mockIngredient = new Ingredient();
        mockIngredient.setName("Tomato");

        MeasurementUnit mockUnit = new MeasurementUnit();
        mockUnit.setName("Gram");

        when(ingredientRepository.findByNameIgnoreCase("Tomato")).thenReturn(mockIngredient);
        when(measurementUnitRepository.findByName("Gram")).thenReturn(mockUnit);
        when(dishRepository.save(any(Dish.class))).thenReturn(dish);


        //вызов тестируемого метода
        dishService.createDish(dishDto);

        verify(dishRepository).save(argThat(savedDish -> {
            // Проверяем основное блюдо
            assertEquals("Pasta", savedDish.getName());
            assertEquals(DifficultyLevel.MEDIUM, savedDish.getDifficultyLevel());
            assertEquals("Delicious pasta", savedDish.getDescription());

            // Проверяем ингредиенты
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
        // Arrange
        Long id = 1L;
        String name = "Test";
        Dish dish = new Dish();
        dish.setId(id);
        dish.setName(name);
        when(dishRepository.findById(id)).thenReturn(Optional.of(dish));

        // Act
        DishDto result = dishService.findDishById(id);

        // Assert
        assertNotNull(result);
        assertEquals("Test", result.getName());
        verify(dishRepository, times(1)).findById(id);
    }

    @Test
    void findDishById_WhenNotExists_ShouldThrowException() {
        // Arrange
        Long id = 999L;
        when(dishRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> dishService.findDishById(id));
        verify(dishRepository, times(1)).findById(id);
    }

    @Test
    void findDishByName_ShouldReturnDish() {
        // Arrange
        String name = "Test Dish";
        Dish dish = new Dish();
        dish.setName(name);
        when(dishRepository.findByNameIgnoreCase(name)).thenReturn(dish);

        // Act
        DishDto result = dishService.findDishByName(name);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(dishRepository, times(1)).findByNameIgnoreCase(name);
    }

    @Test
    void editDish_WhenExists_ShouldReturnUpdatedDish() {
        // Arrange
        Dish dish = new Dish();
        dish.setId(1L);
        dish.setName("Updated Dish");
        when(dishRepository.findById(dish.getId())).thenReturn(Optional.of(dish));
        when(dishRepository.save(dish)).thenReturn(dish);

        // Act
        Dish result = dishService.editDish(dish);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Dish", result.getName());
        verify(dishRepository, times(1)).findById(dish.getId());
        verify(dishRepository, times(1)).save(dish);
    }

    @Test
    void editDish_WhenNotExists_ShouldThrowException() {
        // Arrange
        Dish dish = new Dish();
        dish.setId(999L);
        when(dishRepository.findById(dish.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> dishService.editDish(dish));
        verify(dishRepository, times(1)).findById(dish.getId());
        verify(dishRepository, never()).save(any());
    }

    @Test
    void deleteDishById_ShouldCallRepository() {
        // Arrange
        Long id = 1L;
        doNothing().when(dishRepository).deleteById(id);

        // Act
        dishService.deleteDishById(id);

        // Assert
        verify(dishRepository, times(1)).deleteById(id);
    }

    @Test
    void filterByDishType() {
        //Arrange
        ArrayList<Dish> dishes = new ArrayList<>();
        Dish dish = new Dish();
        DishType dishType = DishType.BREAKFAST;
        dish.setDishType(dishType);
        dishes.add(dish);

        when(dishRepository.findByDishType(dishType)).thenReturn(dishes);

        //Act
        List<DishDto> result = dishService.filterByDishType(dishType);

        //Assert
        assertNotNull(result);
        assertEquals(dishType, result.getFirst().getDishType());
        verify(dishRepository, times(1)).findByDishType(dishType);
    }

    @Test
    void filterByDifficultyLevel() {
        //Arrange
        ArrayList<Dish> dishes = new ArrayList<>();
        Dish dish = new Dish();
        DifficultyLevel difficultyLevel = DifficultyLevel.HARD;
        dish.setDifficultyLevel(difficultyLevel);
        dishes.add(dish);

        when(dishRepository.findByDifficultyLevel(difficultyLevel)).thenReturn(dishes);

        //Act
        List<DishDto> result = dishService.filterByDifficultyLevel(difficultyLevel);

        //Assert
        assertNotNull(result);
        assertEquals(difficultyLevel, result.getFirst().getDifficultyLevel());
        verify(dishRepository, times(1)).findByDifficultyLevel(difficultyLevel);
    }
}
