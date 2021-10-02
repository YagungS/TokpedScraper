package com.tokped.scraper.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
@JsonPropertyOrder({"name", "description", "imageLink", "price", "rating", "merchant", "link", "total_sales"})
public class Product {

    public static final String SEPARATOR = ",";

    public static final String DOUBLE_QUOTES = "\"";

    @JsonIgnore
    private String name;

    private String description;

    private String imageLink;

    private double price;

    private double rating;

    private String merchant;

    private String link;

    private long total_sales;

    public String toString() {
        return name + SEPARATOR + DOUBLE_QUOTES + description + DOUBLE_QUOTES + SEPARATOR + imageLink + SEPARATOR
                + price + SEPARATOR + rating + "/5" + SEPARATOR + merchant;
    }

    public abstract static class ProductFormat {

        @JsonProperty("Product Name")
        abstract String getName();

        @JsonProperty("Description")
        abstract String getDescription();

        @JsonProperty("Image Link")
        abstract String getImageLink();

        @JsonProperty("Price")
        abstract String getPrice();

        @JsonProperty("Rating")
        abstract String getRating();

        @JsonProperty("Merchant Name")
        abstract String getMerchant();

        @JsonProperty("Product Link")
        abstract String getLink();
    }
}