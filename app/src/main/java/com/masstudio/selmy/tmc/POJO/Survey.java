package com.masstudio.selmy.tmc.POJO;

/**
 * Created by tech lap on 13/04/2017.
 */

public class Survey {
    String segment,path;
    int secoundRow,accident;
    Long time;
    public Survey() {
    }

    public Survey(String segment, String path, Long time, int secoundRow, int accident) {
        this.segment = segment;
        this.path = path;
        this.time = time;
        this.secoundRow = secoundRow;
        this.accident = accident;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public int getSecoundRow() {
        return secoundRow;
    }

    public void setSecoundRow(int secoundRow) {
        this.secoundRow = secoundRow;
    }

    public int getAccident() {
        return accident;
    }

    public void setAccident(int accident) {
        this.accident = accident;
    }
}
