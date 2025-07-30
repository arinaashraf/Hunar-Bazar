
package com.example.hunarbazar7.model;

public class Order {
    private String orderId, name, sellerName, imageUrl, price, status, sellerId;

    public Order() {}

    public Order(String productName, String sellerName, String imageUrl, String price, String status, String sellerId) {
        this.name = productName;
        this.sellerName = sellerName;
        this.imageUrl = imageUrl;
        this.price = price;
        this.status = status;
        this.sellerId = sellerId;
    }

    // GETTERS
    public String getName() { return name; }
    public String getSellerName() { return sellerName; }
    public String getImageUrl() { return imageUrl; }
    public String getPrice() { return price; }
    public String getStatus() { return status; }
    public String getSellerId() { return sellerId; }
    public String getOrderId() { return orderId; }

    // SETTERS (add these)
    public void setName(String name) { this.name = name; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setPrice(String price) { this.price = price; }
    public void setStatus(String status) { this.status = status; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
}
