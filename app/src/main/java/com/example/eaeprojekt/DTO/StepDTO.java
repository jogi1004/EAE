package com.example.eaeprojekt.DTO;

public class StepDTO {

    private int id;
    private String text;
    private int recipeId;

    public StepDTO(int id, String text, int recipeId) {
        this.id = id;
        this.text = text;
        this.recipeId = recipeId;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }
}
