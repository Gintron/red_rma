package com.marijan.red.Model;

public class VideoM {
    private String VideoName;
    private String VideoUri;

    private VideoM(){}

    public VideoM(String name, String uri){
        if(name.trim().equals("")){
            name = "not available";
        }
        VideoName = name;
        VideoUri = uri;
    }

    public String getVideoName() {
        return VideoName;
    }

    public void setVideoName(String videoName) {
        VideoName = videoName;
    }

    public String getVideoUri() {
        return VideoUri;
    }

    public void setVideoUri(String videoUri) {
        VideoUri = videoUri;
    }
}
