package com.di.battlemaniaV5.models;

public class ChatData {

    String id;
    String msg;
    String senderId;

    public ChatData(){

    }

    public ChatData(String id, String msg, String senderId) {
        this.id = id;
        this.msg = msg;
        this.senderId = senderId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}

