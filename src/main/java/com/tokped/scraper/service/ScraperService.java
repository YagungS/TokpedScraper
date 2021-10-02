package com.tokped.scraper.service;

import com.tokped.scraper.model.Product;
import com.tokped.scraper.util.ExcelExporter;
import com.tokped.scraper.util.TokPedScraper;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Service
public class ScraperService {

    private static final String UNDERSCORE = "_";
    private static final String PRODUCT = "Product";
    private static final String CSV_EXT = ".csv";
    private static final String XLSX_EXT = ".xlsx";

    private TokPedScraper scraper = new TokPedScraper();

    public List<Product> getProductList(TokPedScraper.Category category, int count){
        return scraper.getProducts(category, count);
    }

    public String fileName(){
        return PRODUCT + UNDERSCORE + "HANDPHONE"
                + UNDERSCORE + System.currentTimeMillis() + CSV_EXT;
    }

    public String excelFileName(){
        return PRODUCT + UNDERSCORE + "HANDPHONE"
                + UNDERSCORE + System.currentTimeMillis() + XLSX_EXT;
    }

    public ICsvBeanWriter exportCsv(TokPedScraper.Category category, int count, PrintWriter writer) throws IOException {
        ICsvBeanWriter csvWriter = new CsvBeanWriter(writer, CsvPreference.STANDARD_PREFERENCE);
        csvWriter.writeHeader(productHeaders());

        getProductList(category,count).stream().forEach(product->{
            try {
                csvWriter.write(product, productFields());
            } catch (IOException e) {
                LoggerFactory.getLogger(this.getClass()).error(e.getMessage());
            }
        });

        return csvWriter;
    }

    public ByteArrayInputStream exportExcel(TokPedScraper.Category category, int count) {
        List<Product> products = getProductList(category, count);

        ExcelExporter<Product> productExcelExporter = new ExcelExporter<Product>(Product.class.getName(), products);

        productExcelExporter.createTable(productHeaders(), productFields());

        return productExcelExporter.export();
    }

    public String[] productHeaders(){
        return scraper.headers();
    }

    public String[] productFields(){
        return scraper.fields();
    }

}