package com.example.eaeprojekt.DTO;

public class IngredientAmountDTO {
    private long id;
    private double amount;
    private long recipeId;
    private long ingredientId;
    private int onShoppingList;
    private int isChecked;

    public IngredientAmountDTO(long id, double amount, long recipeId, long ingredientId, int onShoppingList, int isChecked) {
        this.id = id;
        this.amount = amount;
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
        this.onShoppingList = onShoppingList;
        this.isChecked = isChecked;
    }

    public long getId() {
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
    public int getIsChecked() { return isChecked; }

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
    public void setIsChecked(int isChecked) { this.isChecked = isChecked; }

    @Override
    public String toString() {
        return "IngredientAmountDTO{" +
                "id=" + id +
                ", amount=" + amount +
                ", recipeId=" + recipeId +
                ", ingredientId=" + ingredientId +
                ", onShoppingList=" + onShoppingList +
                ", isChecked=" + isChecked +
                '}';
    }
}

