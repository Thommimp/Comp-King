package com.example.compking.model;

public class Song {
    private String auther;
    private String bpm;
    private String id;
    private String name;

    private String description;

    public Song() {

    }

    public Song(String auther, String bpm, String id, String name, String description) {
        this.auther = auther;
        this.bpm = bpm;
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getAuther() {
        return auther;
    }

    public void setAuther(String auther) {
        this.auther = auther;
    }

    public String getBpm() {
        return bpm;
    }

    public void setBpm(String bpm) {
        this.bpm = bpm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}