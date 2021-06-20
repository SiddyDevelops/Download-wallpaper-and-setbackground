package com.siddydevelops.siddyswallpapers.models;

public class WallpaperModel {

    private int id;
    private String originalUrl;
    private String mediumUrl;
    private String photographerName;

    public WallpaperModel(){

    }

    public WallpaperModel(int id, String originalUrl, String mediumUrl, String photographerName) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.mediumUrl = mediumUrl;
        this.photographerName = photographerName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getMediumUrl() {
        return mediumUrl;
    }

    public void setMediumUrl(String mediumUrl) {
        this.mediumUrl = mediumUrl;
    }

    public String getPhotographerName() {
        return photographerName;
    }

    public void setPhotographerName(String photographerName) {
        this.photographerName = photographerName;
    }
}
