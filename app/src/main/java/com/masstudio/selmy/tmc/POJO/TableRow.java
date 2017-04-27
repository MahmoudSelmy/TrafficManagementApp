package com.masstudio.selmy.tmc.POJO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tech lap on 14/04/2017.
 */

public class TableRow {
    private Long time;
    private List<TableElement> elements;
    private String date;

    public TableRow() {
        elements = new ArrayList<>();
    }

    public TableRow(List<TableElement> elements, String date, Long time) {
        this.elements = elements;
        this.date = date;
        this.time = time;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public List<TableElement> getElements() {
        return elements;
    }

    public void setElements(List<TableElement> elements) {
        this.elements = elements;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
