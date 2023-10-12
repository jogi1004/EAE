package com.example.eaeprojekt;

public class IngredientAmountDTO {
    private int id;
    private double amount;
    private long recipeId;
    private long ingredientId;
    private int onShoppingList;

    public IngredientAmountDTO(int id, double amount, long recipeId, long ingredientId, int onShoppingList) {
        this.id = id;
        this.amount = amount;
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.onShoppingList = onShoppingList;
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public long getIngredientId() {
        return ingredientId;
    }

    public int getOnShoppingList() {
        return onShoppingList;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public void setOnShoppingList(int onShoppingList) {
        this.onShoppingList = onShoppingList;
    }
}

