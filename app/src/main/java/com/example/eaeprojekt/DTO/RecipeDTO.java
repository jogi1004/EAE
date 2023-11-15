package com.example.eaeprojekt.DTO;

public class RecipeDTO {
    private long id;
    private String title;
    private int portions;
    private int duration;
    private int isFavorite;
    private String imagePath;

    public RecipeDTO(long id, String title, int portions, int duration, int isFavorite, String imagePath) {
        this.id = id;
        this.title = title;
        this.portions = portions;
        this.duration = duration;
        this.isFavorite = isFavorite;
        this.imagePath = imagePath;
    }

    public long getId() {
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

    public void setId(long id) {
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}

