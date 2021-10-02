package com.tokped.scraper.controller;

import com.tokped.scraper.model.Product;
import com.tokped.scraper.service.ScraperService;
import com.tokped.scraper.util.TokPedScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ScrapperController {

    @Autowired
    ScraperService scraperService;

    @GetMapping("/products")
    public List<Product> productList() {
        if(scraperService == null)
            scraperService = new ScraperService();
        return scraperService.getProductList(TokPedScraper.Category.HANDPHONE,100);
    }

    @GetMapping("products/download")
    public ResponseEntity<Resource> exportCSV(){
        String csvFileName = scraperService.fileName();

        // setting HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvFileName);
        // defining the custom Content-Type
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");

        return new ResponseEntity<Resource>(
                scraperService.exportCsv(scraperService.getProductList(TokPedScraper.Category.HANDPHONE,100)),
                headers,
                HttpStatus.OK
        );
    }

}