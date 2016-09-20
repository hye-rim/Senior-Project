package com.onpuri.Activity.SideTab.Act.ActTest;

/**
 * Created by kutemsys on 2016-09-20.
 */
public class ActTestData {
    String testId, testDate, testCorrect;

    public ActTestData(String testId, String testDate, String testCorrect) {
        this.testId = testId;
        this.testDate = testDate;
        this.testCorrect = testCorrect;
    }

    public String getTestDate() {
        return testDate;
    }

    public String getTestCorrect() {
        return testCorrect;
    }

    public String getTestId() {

        return testId;
    }
}
