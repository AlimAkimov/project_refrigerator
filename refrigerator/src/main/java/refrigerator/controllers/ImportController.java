package refrigerator.controllers;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import refrigerator.InvalidExcelFormatException;
import refrigerator.enums.DifficultyLevel;
import refrigerator.enums.DishType;
import refrigerator.model.Dish;
import refrigerator.service.ExcelToDatabaseImporter;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {
    private static final Logger logger = LoggerFactory.getLogger(ExcelToDatabaseImporter.class);

    @Autowired
    private ExcelToDatabaseImporter excelImporter;

    @PostMapping(value = "/import", consumes = "multipart/form-data")
    public ResponseEntity<String> importDishes(@RequestParam("file") MultipartFile file) {

        try {
            excelImporter.importFromExcel(file.getInputStream());
            return ResponseEntity.ok("Импорт блюд успешно завершен");
        } catch (Exception e) {
            logger.error("Ошибка при импорте Excel: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при импорте: " + e.getMessage());
        }
    }
}
