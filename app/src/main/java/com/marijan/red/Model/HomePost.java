package com.marijan.red.Model;

public class HomePost {

    private String postimage, category, postid, text, title, userid,type, publisher, videoUrl;

    public HomePost(String postimage, String category, String postid, String text, String title, String userid, String type, String publisher, String videoUrl) {
        this.postimage = postimage;
        this.category = category;
        this.postid = postid;
        this.text = text;
        this.title = title;
        this.userid = userid;
        this.type = type;
        this.publisher= publisher;
        this.videoUrl = videoUrl;
    }

    public HomePost(){

    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
