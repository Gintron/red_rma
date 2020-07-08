package com.marijan.red.Notifications;

public class Data {
    private String receiverId;
    private String receiverName;
    private String receiverImage;
    private int icon;
    private String body;
    private String title;
    private String sented;
    private String messageKey;
    private String type;
    private String postid;
    private String senderId;


    public Data( String receiverId, String receiverName, String receiverImage, int icon, String body, String title, String sented, String messageKey, String type, String postid, String senderId) {
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.receiverImage = receiverImage;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.sented = sented;
        this.messageKey = messageKey;
        this.type = type;
        this.postid = postid;
        this.senderId = senderId;
    }

    public Data( String uid, String currentUserName, String currentUserImage, int ic_launcher, String s, String new_message, String receiver, String type ) {
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverImage() {
        return receiverImage;
    }

    public int getIcon() {
        return icon;
    }

    public String getBody() {
        return body;
    }

    public String getTitle() {
        return title;
    }

    public String getSented() {

        return sented;
    }

    public String getmessageKey() {
        return messageKey;
    }

    public String getType(){return  type; }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}