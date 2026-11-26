package com.AutomationExercise.utils.data.model;

public record AccountData(
        String email,
    String username,
    String gender,
    String password,
    String day,
    String month,
    String year,
    String firstName,
    String lastName,
    String company,
    String address1,
    String address2,
    String country,
    String state,
    String city,
    String zipcode,
    String mobileNumber,
    boolean newsletter,
    boolean receiveOffers
){}