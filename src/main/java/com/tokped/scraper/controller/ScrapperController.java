package com.tokped.scraper.controller;

import com.tokped.scraper.model.Product;
import com.tokped.scraper.service.ScraperService;
import com.tokped.scraper.util.TokPedScraper;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ScrapperController {

    @Autowired
    ScraperService scraperService = new ScraperService();

    @GetMapping("/product/{count}")
    public List<Product> productList(@PathVariable int count) {
        return scraperService.getProductList(TokPedScraper.Category.HANDPHONE,count);
    }

    @GetMapping("product/csv/{count}")
    public ResponseEntity<Resource> exportCSV(@PathVariable int count){
        String csvFileName = scraperService.fileName();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvFileName);
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");

        return new ResponseEntity<Resource>(
                scraperService.exportCsv(scraperService.getProductList(TokPedScraper.Category.HANDPHONE,count)),
                headers,
                HttpStatus.OK
        );
    }

    @GetMapping("product/excel/{count}")
    public void exportExcel(HttpServletResponse response,
                            @PathVariable int count) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename="+ scraperService.excelFileName());

        ByteArrayInputStream stream = scraperService.exportExcel(scraperService.getProductList(TokPedScraper.Category.HANDPHONE,count));

        IOUtils.copy(stream, response.getOutputStream());
    }


}