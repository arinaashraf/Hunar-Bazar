
package com.example.hunarbazar7.model;

public class Order {
    private String productName, sellerName, imageUrl, price, status;

    public Order() {}

    public Order(String productName, String sellerName, String imageUrl, String price, String status) {
        this.productName = productName;
        this.sellerName = sellerName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.status = status;
    }

    public String getProductName() { return productName; }
    public String getSellerName() { return sellerName; }
    public String getImageUrl() { return imageUrl; }
    public String getPrice() { return price; }
    public String getStatus() { return status; }
}
