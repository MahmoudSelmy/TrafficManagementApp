package com.masstudio.selmy.tmc.retrofit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tech lap on 14/03/2017.
 */

public class Stats {
    @SerializedName("text")
    private String text;
    @SerializedName("value")
    private int value;

    public Stats(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
