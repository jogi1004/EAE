package com.example.eaeprojekt;

public class RecipeDTO {
    private int id;
    private String title;
    private int portions;
    private int duration;
    private int isFavorite;

    public RecipeDTO(int id, String title, int portions, int duration, int isFavorite) {
        this.id = id;
        this.title = title;
        this.portions = portions;
        this.duration = duration;
        this.isFavorite = isFavorite;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getPortions() {
        return portions;
    }

    public int getDuration() {
        return duration;
    }

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPortions(int portions) {
        this.portions = portions;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }
}

