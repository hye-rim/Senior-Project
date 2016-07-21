package com.onpuri.Data;

import java.util.ArrayList;

/**
 * Created by HYERIM on 2016-07-21.
 */
public class NoteWordData extends ArrayList {
    private String name;
    private ArrayList<WordData> data;

    public NoteWordData(String name) {
        this.name = name;
        this.data = new ArrayList<WordData>();
    }

    public NoteWordData(ArrayList<WordData> data) {
        this.name = name;
        this.data = data;
    }

    public ArrayList<WordData> getData() {
        return data;
    }

    public void setData(ArrayList<WordData> data) {  this.data = data;   }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
