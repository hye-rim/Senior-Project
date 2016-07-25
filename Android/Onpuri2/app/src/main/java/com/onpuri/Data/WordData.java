package com.onpuri.Data;

/**
 * Created by HYERIM on 2016-07-21.
 */
public class WordData {
    String word;
    String mean;

    public WordData() {
        this.word = new String("none");
        this.mean = new String("없음");
    }

    public WordData(String word, String mean) {
        this.word = word;
        this.mean = mean;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMean() {
        return mean;
    }

    public void setMean(String mean) {
        this.mean = mean;
    }


}
