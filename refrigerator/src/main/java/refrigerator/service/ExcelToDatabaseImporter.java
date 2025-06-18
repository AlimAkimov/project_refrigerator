package refrigerator.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import refrigerator.enums.DifficultyLevel;
import refrigerator.enums.DishType;
import refrigerator.model.Dish;
import refrigerator.model.DishIngredient;
import refrigerator.model.Ingredient;
import refrigerator.model.MeasurementUnit;
import refrigerator.repositories.DishRepository;
import refrigerator.repositories.IngredientRepository;
import refrigerator.repositories.MeasurementUnitRepository;

import java.io.InputStream;
import java.util.*;


@Service
@RequiredArgsConstructor
public class ExcelToDatabaseImporter {
    private static final Logger logger = LoggerFactory.getLogger(ExcelToDatabaseImporter.class);

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private MeasurementUnitRepository measurementUnitRepository;

    @Transactional
    public void importFromExcel(InputStream excelInputStream) throws Exception {
        logger.info("Начало импорта данных из Excel в базу данных");
        Workbook workbook = new XSSFWorkbook(excelInputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        // Пропускаем заголовочную строку
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }

        Map<String, Dish> dishMap = new HashMap<>();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            processExcelRow(row, dishMap);
        }

        // Сохраняем все блюда
        for (Dish dish : dishMap.values()) {
            dishRepository.save(dish);
            logger.debug("Сохранено блюдо: {}", dish.getName());
        }

        workbook.close();
        logger.info("Импорт данных из Excel успешно завершен");
    }

    private void processExcelRow(Row row, Map<String, Dish> dishMap) {
        String dishName = getCellStringValue(row.getCell(0));
        if (dishName == null || dishName.trim().isEmpty()) {
            logger.warn("Пропущена строка с пустым названием блюда");
            return;
        }

        Dish dish = dishMap.computeIfAbsent(dishName, k -> createDishFromRow(row));
        addIngredientToDish(row, dish);
    }

    private Dish createDishFromRow(Row row) {
        Dish dish = new Dish();
        dish.setName(getCellStringValue(row.getCell(0)));
        dish.setDescription(getCellStringValue(row.getCell(1)));
        dish.setInstructions(getCellStringValue(row.getCell(2)));
        dish.setCookingTime(getCellIntegerValue(row.getCell(3)));

        String difficulty = getCellStringValue(row.getCell(4));
        if (difficulty != null) {
            try {
                dish.setDifficultyLevel(DifficultyLevel.valueOf(difficulty.toUpperCase()));
            } catch (IllegalArgumentException e) {
                logger.warn("Недопустимый уровень сложности '{}' для блюда '{}', устанавливается null", difficulty, dish.getName());
            }
        }

        String dishType = getCellStringValue(row.getCell(5));
        if (dishType != null) {
            try {
                dish.setDishType(DishType.valueOf(dishType.toUpperCase()));
            } catch (IllegalArgumentException e) {
                logger.warn("Недопустимый тип блюда '{}' для блюда '{}', устанавливается null", dishType, dish.getName());
            }
        }

        dish.setDishIngredients(new ArrayList<>());
        return dish;
    }

    private void addIngredientToDish(Row row, Dish dish) {
        String ingredientName = getCellStringValue(row.getCell(6));
        if (ingredientName == null || ingredientName.trim().isEmpty()) {
            return; // В строке нет ингредиента
        }

        // Поиск или создание ингредиента
        Ingredient ingredient = ingredientRepository.findByNameIgnoreCase(ingredientName);
        if (ingredient == null) {
            ingredient = new Ingredient();
            ingredient.setName(ingredientName);
            ingredient.setDescription(getCellStringValue(row.getCell(7)));
            ingredient.setCategory(getCellStringValue(row.getCell(8)));
            ingredient = ingredientRepository.save(ingredient);
            logger.debug("Создан новый ингредиент: {}", ingredientName);
        }

        // Поиск или создание единицы измерения
        String measurementUnitName = getCellStringValue(row.getCell(9));
        MeasurementUnit measurementUnit = measurementUnitRepository.findByName(measurementUnitName);
        if (measurementUnit == null) {
            measurementUnit = new MeasurementUnit();
            measurementUnit.setName(measurementUnitName);
            measurementUnit.setAbbreviation(getAbbreviationForUnit(measurementUnitName));
            measurementUnit = measurementUnitRepository.save(measurementUnit);
            logger.debug("Создана новая единица измерения: {}", measurementUnitName);
        }

        // Создание связи DishIngredient
        DishIngredient dishIngredient = new DishIngredient();
        dishIngredient.setDish(dish);
        dishIngredient.setIngredient(ingredient);
        dishIngredient.setMeasurementUnit(measurementUnit);
        dishIngredient.setAmount(getCellIntegerValue(row.getCell(10)));
        dishIngredient.setNotes(""); // Можно добавить логику для заметок, если требуется

        dish.getDishIngredients().add(dishIngredient);
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return null;
        }
    }

    private Integer getCellIntegerValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    private String getAbbreviationForUnit(String unitName) {
        // Простая логика для сокращений, можно расширить
        switch (unitName.toLowerCase()) {
            case "граммы":
                return "г";
            case "миллилитры":
                return "мл";
            case "штуки":
                return "шт";
            case "зубчики":
                return "зуб.";
            default:
                return unitName;
        }
    }
//    public boolean isValidExcelFile(MultipartFile file) {
//        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//    }
//
//    public static List<Dish> getDishDataFromExcel(InputStream inputStream) {
//        List<Dish> dishes = new ArrayList<>();
//        try {
//            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
//            XSSFSheet sheetDish = workbook.getSheet("Dishes");
//            MeasurementUnit measurementUnit = new MeasurementUnit();
//            Ingredient ingredient = new Ingredient();
//
//            Dish dish = new Dish();
//            List<DishIngredient> dishIngredients = new ArrayList<>();
//            DishIngredient dishIngredient = new DishIngredient();
//
//            int rowIndexDishes = 0;
//            for (Row row : sheetDish) {
//                if (rowIndexDishes == 0) {
//                    rowIndexDishes++;
//                    continue;
//                }
//                Iterator<Cell> cellIterator = row.iterator();
//                int cellIndexDishes = 0;
//                while (cellIterator.hasNext()) {
//                    Cell cell = cellIterator.next();
//                    switch (cellIndexDishes) {
//                        case 0 -> dish.setName(cell.getStringCellValue());
//                        case 1 -> dish.setDescription(cell.getStringCellValue());
//                        case 2 -> dish.setInstructions(cell.getStringCellValue());
//                        case 3 -> dish.setCookingTime((int) cell.getNumericCellValue());
//                        case 4-> dish.setDifficultyLevel(DifficultyLevel.valueOf(cell.getStringCellValue()));
//                        case 5-> dish.setDishType(DishType.valueOf(cell.getStringCellValue()));
//                        case 6 -> ingredient.setName(cell.getStringCellValue());
//                        case 7 -> ingredient.setDescription(cell.getStringCellValue());
//                        case 8 -> ingredient.setCategory(cell.getStringCellValue());
//                        case 9 -> measurementUnit.setName(cell.getStringCellValue());
//                        case 10 -> dishIngredient.setAmount((int) cell.getNumericCellValue());
//
//                    }
//                    cellIndexDishes++;
//                }
//                dishIngredient.setDish(dish);
//            }
//
//
//        } catch (IOException e) {
//            e.getStackTrace();
//        }
//        return dishes;

    }
