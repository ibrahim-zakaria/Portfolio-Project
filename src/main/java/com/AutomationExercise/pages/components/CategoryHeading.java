package com.AutomationExercise.pages.components;

public enum CategoryHeading {

    WOMEN_DRESS("WOMEN - DRESS PRODUCTS"),
    WOMEN_TOPS("WOMEN - TOPS PRODUCTS"),
    WOMEN_SAREE("WOMEN - SAREE PRODUCTS"),
    MEN_TSHIRTS("MEN - TSHIRTS PRODUCTS"),
    MEN_JEANS("MEN - JEANS PRODUCTS"),
    KIDS_DRESS("KIDS - DRESS PRODUCTS"),
    KIDS_TOPS_SHIRTS("KIDS - TOPS & SHIRTS PRODUCTS"),

    BRAND_POLO("BRAND - POLO PRODUCTS"),
    BRAND_HM("BRAND - H&M PRODUCTS"),
    BRAND_MADAME("BRAND - MADAME PRODUCTS"),
    BRAND_MAST_HARBOUR("BRAND - MAST & HARBOUR PRODUCTS"),
    BRAND_BABYHUG("BRAND - BABYHUG PRODUCTS"),
    BRAND_ALLEN_SOLLY("BRAND - ALLEN SOLLY JUNIOR PRODUCTS"),
    BRAND_KOOKIE_KIDS("BRAND - KOOKIE KIDS PRODUCTS"),
    BRAND_BIBA("BRAND - BIBA PRODUCTS");


    private final String text;

    CategoryHeading(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
