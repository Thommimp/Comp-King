package com.example.compking.model;

public class Song {
    private String auther;
    private int bpm;
    private String id;
    private String name;
    private String description;
    private String downloadurl;

    public Song() {

    }

    public Song(String auther, int bpm, String id, String name, String description, String downloadurl) {
        this.auther = auther;
        this.bpm = bpm;
        this.id = id;
        this.name = name;
        this.description = description;
        this.downloadurl = downloadurl;
    }

    public String getAuther() {
        return auther;
    }

    public void setAuther(String auther) {
        this.auther = auther;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
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

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }
}