package refrigerator.refrigerator.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import refrigerator.Exceptions.ResourceNotFoundException;
import refrigerator.model.Ingredient;
import refrigerator.model.dto.IngredientDto;
import refrigerator.repositories.IngredientRepository;
import refrigerator.service.IngredientService;

import java.util.ArrayList;
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

        Ingredient ingredient = new Ingredient();
        ingredient.setName("testIngredient");
        when(ingredientRepository.save(ingredient)).thenReturn(ingredient);

        Ingredient result = ingredientService.createIngredient(ingredient);

        assertNotNull(result);
        assertEquals("testIngredient", result.getName());
        verify(ingredientRepository, timeout(1)).save(ingredient);

    }

    @Test
    void findIngredientById_WhenExists_ShouldReturnIngredientDto() {
        Long id = 1L;
        String name = "Test Ingredient";
        String category = "Test Category";

        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setName(name);
        ingredient.setCategory(category);

        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredient));

        IngredientDto result = ingredientService.findIngredientById(id);

        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(category, result.getCategory());
        verify(ingredientRepository, times(1)).findById(id);
    }

    @Test
    void findIngredientById_WhenNotExists_ShouldThrowException() {
        Long id = 999L;
        when(ingredientRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            ingredientService.findIngredientById(id);
        });

        verify(ingredientRepository, times(1)).findById(id);
    }

    @Test
    void findIngredientByName_ShouldReturnIngredient() {
        String testName = "Test ingredient";
        Ingredient ingredient = new Ingredient();
        ingredient.setName(testName);
        when(ingredientRepository.findByNameIgnoreCase(testName)).thenReturn(ingredient);

        IngredientDto result = ingredientService.findIngredientByName(testName);

        assertNotNull(result);
        assertEquals("Test ingredient", result.getName());
        verify(ingredientRepository, times(1)).findByNameIgnoreCase(testName);
    }

    @Test
    void editIngredient_WhenIngredientExists_ShouldUpdateAndReturnDto() {
        Long id = 1L;
        IngredientDto inputDto = new IngredientDto();
        inputDto.setName("New Name");
        inputDto.setCategory("New Category");
        inputDto.setDescription("New Description");

        Ingredient existingIngredient = new Ingredient();
        existingIngredient.setId(id);
        existingIngredient.setName("Old Name");
        existingIngredient.setCategory("Old Category");
        existingIngredient.setDescription("Old Description");
        existingIngredient.setDishes(new ArrayList<>());

        when(ingredientRepository.findById(id)).thenReturn(Optional.of(existingIngredient));
        when(ingredientRepository.save(any(Ingredient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IngredientDto result = ingredientService.editIngredient(id, inputDto);

        assertNotNull(result);
        assertEquals(inputDto.getName(), result.getName());
        assertEquals(inputDto.getCategory(), result.getCategory());
        assertEquals(inputDto.getDescription(), result.getDescription());

        verify(ingredientRepository).findById(id);
        verify(ingredientRepository).save(existingIngredient);
    }

    @Test
    void editIngredient_WhenIngredientNotExists_ShouldThrowException() {
        Long id = 1L;
        IngredientDto inputDto = new IngredientDto();

        when(ingredientRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            ingredientService.editIngredient(id, inputDto);
        });

        verify(ingredientRepository, never()).save(any());
    }

    @Test
    void deleteDishById_ShouldCallRepository() {
        Long id = 1L;
        doNothing().when(ingredientRepository).deleteById(id);

        ingredientService.deleteIngredientById(id);

        verify(ingredientRepository, times(1)).deleteById(id);
    }

}
