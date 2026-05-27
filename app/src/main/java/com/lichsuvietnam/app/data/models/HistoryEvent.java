package com.lichsuvietnam.app.data.models;

public class HistoryEvent {
    private String year;
    private String title;
    private String description;
    private String imageUrl;
    private String location;

    public HistoryEvent(String year, String title, String description, String imageUrl) {
        this.year = year;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public HistoryEvent(String year, String title, String description, String imageUrl, String location) {
        this(year, title, description, imageUrl);
        this.location = location;
    }

    public String getYear() { return year; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getLocation() { return location; }
}
