package com.AutomationExercise.utils.data.model;

public record PaymentCardData(
        String nameOnCard,
        String cardNumber,
        String cvc,
        String expirationMonth,
        String expirationYear
) {
}
