package refrigerator.controllers;

import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import refrigerator.Exceptions.ResourceNotFoundException;
import refrigerator.service.PDFGenerationService;

import java.io.IOException;

@RestController
@RequestMapping(path = "/dish")
public class PDFGenerationController {

    @Autowired
    PDFGenerationService pdfGenerationService;

    @GetMapping(path = "{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> convertToPdf(@PathVariable Long id) {
        try {
            byte[] pdfBytes = pdfGenerationService.convertToPdf(id);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=recipe_" + id + ".pdf")
                    .body(pdfBytes);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(("Ошибка: " + e.getMessage()).getBytes());
        }
    }

}
