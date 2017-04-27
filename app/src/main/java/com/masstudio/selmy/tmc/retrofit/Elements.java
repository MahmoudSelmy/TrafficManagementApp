package com.masstudio.selmy.tmc.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by tech lap on 14/03/2017.
 */

public class Elements {

    @SerializedName("elements")
    private List<Element> elements;

    public Elements(List<Element> elements) {
        this.elements = elements;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }
}
