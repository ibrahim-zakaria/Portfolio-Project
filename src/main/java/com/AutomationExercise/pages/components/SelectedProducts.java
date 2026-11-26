package com.AutomationExercise.pages.components;

import java.math.BigDecimal;

public record SelectedProducts(
        String id,
        String name,
        BigDecimal price,
        int qty,
        BigDecimal total
) {
}
