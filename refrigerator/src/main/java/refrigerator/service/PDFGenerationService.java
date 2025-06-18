package refrigerator.service;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import refrigerator.model.dto.DishDto;
import refrigerator.model.dto.IngredientDto;

import java.io.*;
import java.util.List;

@Service
public class PDFGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelToDatabaseImporter.class);
    @Autowired
    DishService dishService;

    public PDFGenerationService(DishService dishService) {
        this.dishService = dishService;
    }

//    public byte[] convertToPdf(Long id) throws DocumentException, IOException {
//        logger.info("Создание PDF для блюда с ID: {}", id);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        Document document = new Document();
//
//        try {
//            PdfWriter.getInstance(document, outputStream);
//            document.open();
//
//            // Настройка шрифта с поддержкой кириллицы
//            BaseFont baseFont = BaseFont.createFont(
//                    "fonts/arialmt.ttf", // Путь к шрифту Arial в src/main/resources/fonts/
//                    BaseFont.IDENTITY_H,
//                    BaseFont.EMBEDDED
//            );
//            Font boldFont = new Font(baseFont, 18, Font.BOLD);
//            Font regularFont = new Font(baseFont, 12);
//
//            DishDto dishById = dishService.findDishById(id);
//            List<IngredientDto> ingredientsForADish = dishService.getIngredientsForADish(id);
//
//            // Логирование данных для диагностики
//            logger.info("Блюдо: {}, Количество ингредиентов: {}", dishById.getName(), ingredientsForADish.size());
//            for (IngredientDto ingredient : ingredientsForADish) {
//                logger.info("Ингредиент: name={}, description={}, category={}, amount={}, unit={}",
//                        ingredient.getName(),
//                        ingredient.getDescription(),
//                        ingredient.getCategory(),
//                        ingredient.getMeasurementUnitDto() != null ? ingredient.getMeasurementUnitDto().getAmount() : null,
//                        ingredient.getMeasurementUnitDto() != null ? ingredient.getMeasurementUnitDto().getName() : null);
//            }
//
//            // Добавляем заголовок
//            document.add(new Paragraph(dishById.getName(), boldFont));
//
//            // Добавляем ингредиенты
//            document.add(new Paragraph("Ингредиенты:", boldFont));
//            for (IngredientDto ingredient : ingredientsForADish) {
//                String name = ingredient.getName() != null ? ingredient.getName() : "Не указано";
//                String description = ingredient.getDescription() != null ? ingredient.getDescription() : "";
//                String category = ingredient.getCategory() != null ? ingredient.getCategory() : "";
//                String amount = ingredient.getMeasurementUnitDto() != null && ingredient.getMeasurementUnitDto().getAmount() != null
//                        ? ingredient.getMeasurementUnitDto().getAmount().toString()
//                        : "0";
//                String unit = ingredient.getMeasurementUnitDto() != null && ingredient.getMeasurementUnitDto().getName() != null
//                        ? ingredient.getMeasurementUnitDto().getName()
//                        : "";
//
//                // Формируем строку ингредиента
//                String ingredientLine = String.format("- %s: %s, %s: %s %s", name, description, category, amount, unit);
//                document.add(new Paragraph(ingredientLine, regularFont));
//            }
//        } finally {
//            document.close();
//        }
//
//        byte[] pdfBytes = outputStream.toByteArray();
//        logger.info("PDF создан, размер: {} байт", pdfBytes.length);
//        return pdfBytes;
//    }

    public byte[] convertToPdf(Long id) throws IOException, DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        List<IngredientDto> ingredientsForADish = dishService.getIngredientsForADish(id);
        DishDto dishById = dishService.findDishById(id);

        BaseFont baseFont = BaseFont.createFont(
                "fonts/arialmt.ttf", // Путь к шрифту Arial в src/main/resources/fonts/
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED);
        Font boldFont = new Font(baseFont, 18, Font.BOLD);
        Font regularFont = new Font(baseFont, 12);

        try {

        document.open();

        // Добавляем заголовок
        document.add(new Paragraph(dishById.getName(),
                boldFont));

        // Добавляем ингредиенты ПО ОДНОМУ
        document.add(new Paragraph("Ингредиенты:",
                boldFont));

        for (IngredientDto ingredient : ingredientsForADish) {

            document.add(new Paragraph(
                    "- " + ingredient.getName() + ": " +
                            ingredient.getDescription() + ": " +
                            ingredient.getCategory() + ": " +
                            ingredient.getMeasurementUnitDto().getAmount() + " " +
                            ingredient.getMeasurementUnitDto().getName(), regularFont
            ));

        }
    } finally {
            document.close();
        }
        byte[] pdfBytes = outputStream.toByteArray();
        logger.info("PDF создан, размер: {} байт", pdfBytes.length);
        return pdfBytes;
        }


}
