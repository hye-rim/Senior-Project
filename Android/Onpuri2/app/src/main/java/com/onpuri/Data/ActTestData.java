package com.onpuri.Data;

import android.util.Log;

/**
 * Created by kutemsys on 2016-09-20.
 */
public class ActTestData {

    String testId;
    String  testDate;
    String testCorrect;

    public ActTestData() {
        this.testId = new String("");
        this.testDate = new String("");
        this.testCorrect = new String("");
    }

    public ActTestData(String testId, String testDate, String testCorrect) {
        this.testId = new String(testId);
        this.testDate = new String(testDate);
        this.testCorrect = new String(testCorrect);
    }

    public String getTestId() {
        return testId;
    }
    public String getTestDate() {
        return testDate;
    }
    public String getTestCorrect() {
        return testCorrect;
    }

}
