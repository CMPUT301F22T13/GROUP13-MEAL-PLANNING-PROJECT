package com.example.cmput301f22t13.domainlayer.item;

import java.util.HashMap;
import java.util.Map;

public class CountedIngredient extends IngredientItem {

    private double count;

    public CountedIngredient() {
        super();
        this.count = 0;
    }

    public CountedIngredient(IngredientItem ingredient, double count) {
        super(ingredient.getName(), ingredient.getDescription(), ingredient.getAmount(), ingredient.getUnit(), ingredient.getCategory(), ingredient.getBbd(), ingredient.getPhoto(), ingredient.getLocation());
        this.count = count;
    }


    public void incrementIngredient() {
        this.count += 1;
    }

    public void incrementIngredient(int incrementer) {
        this.count += incrementer;
    }

    public void decrementIngredient() {
        this.count -= 1;
    }

    public void decrementIngredient(int decrementer) {
        this.count -= decrementer;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }
}
