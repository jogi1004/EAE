package com.example.eaeprojekt.DTO;

public class StepDTO {

    private int id;
    private String text;
    private long recipeId;

    public StepDTO(int id, String text, long recipeId) {
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

    public long getRecipeId() {
        return recipeId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }
}
