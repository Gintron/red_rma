package com.marijan.red.Model;

public class Post {
    private String postid;
    private String postimage;
    private String description;
    private String time;
    private String date;
    private String location;
    private String publisher;
    private String city99;

    public Post(String postid, String postimage, String description, String publisher,
    String time, String date, String location, String city99) {
        this.postid = postid;
        this.postimage = postimage;
        this.description = description;
        this.date = date;
        this.location = location;
        this.time = time;
        this.publisher = publisher;
        this.city99 = city99;
    }

    public Post() {
    }

    public String getCity99() {
        return city99;
    }

    public void setCity99(String city99) {
        this.city99 = city99;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
