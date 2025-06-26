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

    public byte[] convertToPdf(Long id) throws IOException, DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        List<IngredientDto> ingredientsForADish = dishService.getIngredientsForADish(id);
        DishDto dishById = dishService.findDishById(id);

        BaseFont baseFont = BaseFont.createFont(
                "fonts/arial_bolditalicmt.ttf", // Путь к шрифту Arial в src/main/resources/fonts/
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED);
        Font boldFont = new Font(baseFont, 18, Font.BOLD);
        Font regularFont = new Font(baseFont, 12);

        try {

        document.open();

        document.add(new Paragraph(dishById.getName(),
                boldFont));

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
