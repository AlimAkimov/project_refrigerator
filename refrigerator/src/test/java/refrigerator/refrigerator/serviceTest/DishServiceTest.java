package refrigerator.refrigerator.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import refrigerator.Exceptions.NotFoundException;
import refrigerator.model.Dish;
import refrigerator.repositories.DishRepository;
import refrigerator.service.DishService;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DishServiceTest {

    @Mock
    private DishRepository dishRepository;

    @InjectMocks
    private DishService dishService;

    @Test
    void createDish_ShouldReturnSavedDish() {
        // Arrange
        Dish dish = new Dish();
        dish.setName("Test Dish");
        when(dishRepository.save(dish)).thenReturn(dish);

        // Act
        Dish result = dishService.createDish(dish);

        // Assert
        assertNotNull(result);
        assertEquals("Test Dish", result.getName());
        verify(dishRepository, times(1)).save(dish);
    }

    @Test
    void findDishById_WhenExists_ShouldReturnDish() {
        // Arrange
        Long id = 1L;
        Dish dish = new Dish();
        dish.setId(id);
        when(dishRepository.findById(id)).thenReturn(Optional.of(dish));

        // Act
        Dish result = dishService.findDishById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(dishRepository, times(1)).findById(id);
    }

    @Test
    void findDishById_WhenNotExists_ShouldThrowException() {
        // Arrange
        Long id = 999L;
        when(dishRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> dishService.findDishById(id));
        verify(dishRepository, times(1)).findById(id);
    }

    @Test
    void findDishByName_ShouldReturnDish() {
        // Arrange
        String name = "Test Dish";
        Dish dish = new Dish();
        dish.setName(name);
        when(dishRepository.findByName(name)).thenReturn(dish);

        // Act
        Dish result = dishService.findDishByName(name);

        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(dishRepository, times(1)).findByName(name);
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
}
