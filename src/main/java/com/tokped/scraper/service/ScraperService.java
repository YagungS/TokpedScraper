package com.tokped.scraper.service;

import com.tokped.scraper.model.Product;
import com.tokped.scraper.util.TokPedScraper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ScraperService {

    private static final String UNDERSCORE = "_";
    private static final String PRODUCT = "Product";
    private static final String CSV_EXT = ".csv";

    private TokPedScraper scraper = new TokPedScraper();

    public List<Product> getProductList(TokPedScraper.Category category, int count){
        return scraper.getProducts(category, count);
    }

    public String fileName(){
        return PRODUCT + UNDERSCORE + "HANDPHONE"
                + UNDERSCORE + System.currentTimeMillis() + CSV_EXT;
    }

    public InputStreamResource exportCsv(List<Product> products){
        String[] csvHeader = {
                "Product Name", "Description", "Image Link", "Price", "Rating", "Store"
        };

        List<List<String>> csvBody = new ArrayList<>();
        for (Product product : products)
            csvBody.add(Arrays.asList(
                    product.getName(),
                    product.getDescription(),
                    product.getImageLink(),
                    String.valueOf(product.getPrice()),
                    String.valueOf(product.getRating()),
                    product.getMerchant()));

        ByteArrayInputStream byteArrayOutputStream;

        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                // defining the CSV printer
                CSVPrinter csvPrinter = new CSVPrinter(
                        new PrintWriter(out),
                        // withHeader is optional
                        CSVFormat.DEFAULT.withHeader(csvHeader)
                )
        ) {
            // populating the CSV content
            for (List<String> record : csvBody)
                csvPrinter.printRecord(record);

            // writing the underlying stream
            csvPrinter.flush();

            byteArrayOutputStream = new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        return new InputStreamResource(byteArrayOutputStream);
    }
}