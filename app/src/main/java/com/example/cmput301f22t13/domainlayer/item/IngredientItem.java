package com.example.cmput301f22t13.domainlayer.item;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Public class representing an ingredient - options for constructing, getting and setting
*
*  */

public class IngredientItem extends Item implements Serializable {

    private String description;
    private Double amount;
    private String unit;
    private String category;
    private GregorianCalendar bbd;
    private String location;

    private boolean fromShoppingList;

    /**
     * Constructor that initializes all ingredient fields to default values
     */
    public IngredientItem () {
        this.description = "";
        this.amount = 0.0;
        this.unit = "";
        this.category = "";
        this.bbd = new GregorianCalendar();
        this.location = "";
        this.fromShoppingList = false;
    }


    /**
     * Constructor that initializes all ingredient fields to values that are passed in as arguments
     * @param name name of ingredient
     * @param description description of ingredient
     * @param amount number of units of this ingredient
     * @param unit unit type for the ingredient (eg. kg, lb)
     * @param category the category that this ingredient belongs to
     * @param bbd the best before date that this ingredient has
     * @param photo the uri string for storing images for the ingredient
     * @param location the location of the ingredient stored
     */
    public IngredientItem (String name, String description, Double amount, String unit, String category, GregorianCalendar bbd, String photo, String location) {
        super(name, photo);
        this.description = description;
        this.amount = amount;
        this.unit = unit;
        this.category = category;
        this.bbd = bbd;
        this.location = location;
        this.fromShoppingList = false;

        int timestamp = new Timestamp(new Date()).getNanoseconds();
        String timeStampString = Integer.toString(timestamp);
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] encodedhash = digest.digest(
                timeStampString.getBytes(StandardCharsets.UTF_8));

    }

    /**
     * Copy constructor for ingredient item. Creates a deep copy
     * @param ingredientItem ingredient to create a deep copy of
     */
    public IngredientItem(IngredientItem ingredientItem) {
        this(ingredientItem.getName(), ingredientItem.getDescription(), ingredientItem.getAmount(),
                ingredientItem.getUnit(), ingredientItem.getCategory(), ingredientItem.getBbd(),
                ingredientItem.getPhoto(), ingredientItem.getLocation());

        setHashId(ingredientItem.getHashId());
    }

    /**
     * Gets the description of the ingredient
     * @return description of ingredient
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description of the ingredient
     * @param description new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the amount/number of units there are of this ingredient
     * @return amount
     */
    public Double getAmount() {
        return amount;
    }

    /**
     * Sets the amount/number of units there are of this ingredient
     * @param amount new amount
     */
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    /**
     * Gets the unit of measure for the ingredient
     * @return unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Sets the unit of measure for the ingredient
     * @param unit new unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Gets the category that this ingredient belongs to
     * @return category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category that this ingredient belongs to
     * @param category new category
     */
    public void setCategory(String category) {
        this.category = category;
    }
    /**
     * Gets the best before date the ingredient
     * @return bbd
     */
    public GregorianCalendar getBbd() {
        return bbd;
    }
    /**
     * Sets the best before date of the ingredient it belongs to
     * @param bbd new bbd
     */
    public void setBbd(GregorianCalendar bbd) {
        this.bbd = bbd;
    }
    /**
     * Gets the location of this ingredient it belongs to
     * @return location
     */
    public String getLocation() {
        return location;
    }
    /**
     * Sets the location of the ingredient it belongs to
     * @param location new location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isFromShoppingList() {
        return fromShoppingList;
    }

    public void setFromShoppingList(boolean fromShoppingList) {
        this.fromShoppingList = fromShoppingList;
    }
}


