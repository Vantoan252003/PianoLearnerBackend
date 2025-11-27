package com.piano.learn.PianoLearn.service.ai;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfToImageService {

    private static final int DPI = 150;

    public List<byte[]> convertFirstTwoPagesToImageBytes(MultipartFile pdfFile) throws IOException {
        List<byte[]> imageBytesList = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(pdfFile.getBytes())) {
            PDFRenderer renderer = new PDFRenderer(document);

            int numberOfPages = document.getNumberOfPages();
            int pagesToConvert = Math.min(numberOfPages, 2);

            for (int page = 0; page < pagesToConvert; page++) {
                BufferedImage image = renderer.renderImageWithDPI(page, DPI);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "PNG", baos);
                imageBytesList.add(baos.toByteArray());
            }
        }

        return imageBytesList;
    }
}
