package com.AutomationExercise.utils.data.generator;

import com.AutomationExercise.utils.data.model.PaymentCardData;
import net.datafaker.Faker;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class TestDataGenerator {
    public  record CompanyData(
        String name,
        String address
    ){}
    public record NameParts(
            String first,
            String last,
            String full
    ){}
    public record Birthday(
            String day,
            String month,
            String year
    ){}


    private static final ThreadLocal<Faker> FAKER = ThreadLocal.withInitial(Faker::new);

    private static Faker faker() {
        return FAKER.get();
    }
    public static String randFName() {
        return faker().name().firstName();
    }
    public static String randLName() {
        return faker().name().lastName();
    }
    public static String randFullName() {
        return faker().name().fullName();
    }
    public static String randEmailAddress() {
        return faker().internet().emailAddress();
    }
    public static String randPassword() {
        return faker().internet().password();
    }
    public static String randBirthday() {
        return  faker().timeAndDate().birthday(16,70,"d-MMMM-yyyy");
    }
    public static String randGender() {
        return faker().options().option("male","female");
    }
    public static NameParts randomNameParts() {
        String firstName = faker().name().firstName();
        String lastName = faker().name().lastName();
        return new NameParts(
                firstName,
                lastName,
                firstName + " " + lastName
        );
    }
 public static  String company() {
        return faker().company().name();
    }
    public static CompanyData randomCompany() {
        String companyName = faker().company().name();
        String companyAddress = faker().address().fullAddress();

        return new CompanyData(
                companyName,
                companyAddress
        );
    }
    public static Birthday randomBirthday() {
        String[] date = faker().timeAndDate().birthday(16,70,"d-MMMM-yyyy").split("-");
        return  new Birthday(
                date[0],
                date[1],
                date[2]
        );
    }
    public static String randText() {
        return faker().location().nature();
    }
    public static String address() {
        return faker().address().fullAddress();
    }
    public static String city() {
        return faker().address().city();
    }
    public static String country() {
        return faker().address().country();
    }
    public static String state(){
        return faker().address().state();
    }
    public static String zipcode() {
        return faker().address().zipCode();
    }
    public static String mobileNumber() {
        return faker().phoneNumber().phoneNumber();
    }
    public static String emailSubject(){
        return faker().internet().emailSubject();
    }
    public static PaymentCardData paymentData(){
        String monthAndYear = generateRandomExpiryDate();
        return new PaymentCardData(
                faker().name().nameWithMiddle(),
                faker().business().creditCardNumber(),
                faker().business().securityCode(),
                monthAndYear.split("/")[0],
                monthAndYear.split("/")[1]
        );
    }

    private static String generateRandomExpiryDate() {
        YearMonth expiry = YearMonth.now().plusMonths(
                ThreadLocalRandom.current().nextInt(1, 61)
        );
        return expiry.format(DateTimeFormatter.ofPattern("MM/yyyy"));
    }



}

