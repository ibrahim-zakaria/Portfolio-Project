package com.AutomationExercise.pages.components;

public record CartProducts(
        String productId,
        String imageUrl,
        String title,
        String category,
        java.math.BigDecimal unitPrice,
        int quantity,
        java.math.BigDecimal totalPrice
) {
}
