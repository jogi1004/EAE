package com.example.eaeprojekt;

public class IngredientAmountDTO {
    private int id;
    private int amount;
    private int recipeId;
    private int ingredientId;
    private int onShoppingList;

    public IngredientAmountDTO(int id, int amount, int recipeId, int ingredientId, int onShoppingList) {
        this.id = id;
        this.amount = amount;
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.onShoppingList = onShoppingList;
    }

    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public int getIngredientId() {
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

