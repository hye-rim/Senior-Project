package com.onpuri.Data;

import java.util.ArrayList;

/**
 * Created by HYERIM on 2016-07-08.
 */
public class NoteData {

    private String name;
    private ArrayList<String> data;

    public NoteData(String name) {
        this.name = name;
        data = new ArrayList<String>();
    }

    public NoteData(ArrayList<String> data) {
        this.data = data;
    }

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
