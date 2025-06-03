package refrigerator.refrigerator.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import refrigerator.Exceptions.NotFoundException;
import refrigerator.model.Ingredient;
import refrigerator.repositories.IngredientRepository;
import refrigerator.service.IngredientService;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private IngredientService ingredientService;

    @Test
    void create_ShouldReturnSavedIngredient() {
        //Arrange
        Ingredient ingredient = new Ingredient();
        ingredient.setName("testIngredient");
        when(ingredientRepository.save(ingredient)).thenReturn(ingredient);

        //Act
        Ingredient result = ingredientService.createIngredient(ingredient);

        //Assert
        assertNotNull(result);
        assertEquals("testIngredient", result.getName());
        verify(ingredientRepository, timeout(1)).save(ingredient);

    }

    @Test
    void findIngredientById_WhenExists_ShouldReturnIngredient() {
        //Arrange
        Long id = 3L;
        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);
        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredient));

        //Act
        Ingredient resul = ingredientService.findIngredientById(id);

        //Assert
        assertNotNull(resul);
        assertEquals(3L, resul.getId());
        verify(ingredientRepository, times(1)).findById(id);
    }

    @Test
    void findIngredientById_WhenNotExists_ShouldThrowException() {
        //Arrange
        Long id = 999L;
        when(ingredientRepository.findById(id)).thenReturn(Optional.empty());

        //Act && Assert
        assertThrows(NotFoundException.class, () -> ingredientService.findIngredientById(id));
        verify(ingredientRepository, times(1)).findById(id);
    }

    @Test
    void findIngredientByName_ShouldReturnIngredient() {
        //Arrange
        String testName = "Test ingredient";
        Ingredient ingredient = new Ingredient();
        ingredient.setName(testName);
        when(ingredientRepository.findByName(testName)).thenReturn(ingredient);

        //Act
        Ingredient result = ingredientService.findIngredientByName(testName);

        //Assert
        assertNotNull(result);
        assertEquals("Test ingredient", result.getName());
        verify(ingredientRepository, times(1)).findByName(testName);
    }

    @Test
    void editIngredient_WhenExists_ShouldReturnUpdatedIngredient() {
        //Arrange
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1l);
        ingredient.setName("Update ingredient");
        when(ingredientRepository.findById(ingredient.getId())).thenReturn(Optional.of(ingredient));
        when(ingredientRepository.save(ingredient)).thenReturn(ingredient);

        //Act
        Ingredient result = ingredientService.editIngredient(ingredient);

        //Assert
        assertNotNull(result);
        assertEquals("Update ingredient", result.getName());
        verify(ingredientRepository, times(1)).findById(ingredient.getId());
        verify(ingredientRepository, times(1)).save(ingredient);
    }

    @Test
    void editIngredient_WhenNotExists_ShouldThrowException() {
        //Arrange
        Ingredient ingredient = new Ingredient();
        ingredient.setId(999L);
        when(ingredientRepository.findById(ingredient.getId())).thenReturn(Optional.empty());

        //Act && Assert
        assertThrows(RuntimeException.class, () -> ingredientService.editIngredient(ingredient));
        verify(ingredientRepository, times(1)).findById(ingredient.getId());
        verify(ingredientRepository, never()).save(any());

    }

    @Test
    void deleteDishById_ShouldCallRepository() {
        //Arrange
        Long id = 1L;
        doNothing().when(ingredientRepository).deleteById(id);

        //Act
        ingredientService.deleteIngredientById(id);

        //Assert
        verify(ingredientRepository, times(1)).deleteById(id);
    }

}
