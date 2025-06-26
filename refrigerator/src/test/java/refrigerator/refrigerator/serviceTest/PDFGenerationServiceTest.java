package refrigerator.refrigerator.serviceTest;
import com.itextpdf.text.DocumentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import refrigerator.model.dto.DishDto;
import refrigerator.model.dto.IngredientDto;
import refrigerator.model.dto.MeasurementUnitDto;
import refrigerator.service.DishService;
import refrigerator.service.PDFGenerationService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PDFGenerationServiceTest {

        @Mock
        private DishService dishService;

        @InjectMocks
        private PDFGenerationService pdfGenerationService;

        @Test
        void convertToPdf_ShouldGenerateValidPdf() throws IOException, DocumentException {
            Long dishId = 1L;

            DishDto dishDto = new DishDto();
            dishDto.setName("Тестовое блюдо");

            IngredientDto ingredientDto = new IngredientDto();
            ingredientDto.setName("Тестовый ингредиент");
            ingredientDto.setDescription("Описание");
            ingredientDto.setCategory("Категория");

            MeasurementUnitDto measurementUnitDto = new MeasurementUnitDto();
            measurementUnitDto.setName("грамм");
            measurementUnitDto.setAmount(100);
            ingredientDto.setMeasurementUnitDto(measurementUnitDto);

            List<IngredientDto> ingredients = Collections.singletonList(ingredientDto);

            when(dishService.findDishById(dishId)).thenReturn(dishDto);
            when(dishService.getIngredientsForADish(dishId)).thenReturn(ingredients);

            byte[] result = pdfGenerationService.convertToPdf(dishId);

            assertNotNull(result);
            assertTrue(result.length > 0);

            verify(dishService).findDishById(dishId);
            verify(dishService).getIngredientsForADish(dishId);
        }

    @Test
    void convertToPdf_WhenDishNotFound_ShouldThrowException() {
        Long dishId = 999L;
        when(dishService.findDishById(dishId)).thenReturn(null);

        assertThrows(Exception.class, () -> {
            pdfGenerationService.convertToPdf(dishId);
        });
    }

        @Test
        void convertToPdf_WhenNoIngredients_ShouldGeneratePdf() throws IOException, DocumentException {
            Long dishId = 2L;
            DishDto dishDto = new DishDto();
            dishDto.setName("Блюдо без ингредиентов");

            when(dishService.findDishById(dishId)).thenReturn(dishDto);
            when(dishService.getIngredientsForADish(dishId)).thenReturn(Collections.emptyList());

            byte[] result = pdfGenerationService.convertToPdf(dishId);

            assertNotNull(result);
            assertTrue(result.length > 0);
        }
    }
