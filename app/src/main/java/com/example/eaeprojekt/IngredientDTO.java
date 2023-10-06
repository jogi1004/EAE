package com.example.eaeprojekt;

public class IngredientDTO {

    private int id;
    private String name;
    private String unit;

    public IngredientDTO(int id, String name, String unit) {
        this.id = id;
        this.name = name;
        this.unit = unit;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
