package refrigerator.refrigerator.serviceTest;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import refrigerator.model.Dish;
import refrigerator.model.Ingredient;
import refrigerator.model.MeasurementUnit;
import refrigerator.repositories.DishRepository;
import refrigerator.repositories.IngredientRepository;
import refrigerator.repositories.MeasurementUnitRepository;
import refrigerator.service.ExcelToDatabaseImporter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExcelToDatabaseImporterTest {

        @Mock
        private DishRepository dishRepository;

        @Mock
        private IngredientRepository ingredientRepository;

        @Mock
        private MeasurementUnitRepository measurementUnitRepository;

        @InjectMocks
        private ExcelToDatabaseImporter excelImporter;

    @Test
    void importFromExcel_ShouldSaveDishWithIngredients() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Dishes");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Dish Name");
        header.createCell(6).setCellValue("Ingredient");
        header.createCell(9).setCellValue("Unit");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("Тестовое блюдо");
        dataRow.createCell(6).setCellValue("Тестовый ингредиент");
        dataRow.createCell(9).setCellValue("граммы");
        dataRow.createCell(10).setCellValue(100);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        InputStream inputStream = new ByteArrayInputStream(out.toByteArray());

        when(ingredientRepository.findByNameIgnoreCase("Тестовый ингредиент")).thenReturn(null);
        when(measurementUnitRepository.findByName("граммы")).thenReturn(null);
        when(dishRepository.save(any(Dish.class))).thenAnswer(inv -> inv.getArgument(0));
        when(ingredientRepository.save(any(Ingredient.class))).thenAnswer(inv -> inv.getArgument(0));
        when(measurementUnitRepository.save(any(MeasurementUnit.class))).thenAnswer(inv -> inv.getArgument(0));

        excelImporter.importFromExcel(inputStream);

        verify(dishRepository).save(any(Dish.class));
        verify(ingredientRepository).save(any(Ingredient.class));
        verify(measurementUnitRepository).save(any(MeasurementUnit.class));
    }
    }

