package com.masstudio.selmy.tmc.POJO;

/**
 * Created by tech lap on 09/04/2017.
 */

public class Instruction {
    String text;
    String senderId;
    long time;
    public Instruction() {
    }

    public Instruction(String text, String senderId, long time) {
        this.text = text;
        this.senderId = senderId;
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
