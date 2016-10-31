package com.onpuri.Data;

/**
 * Created by kutemsys on 2016-09-19.
 */
public class CreatedTestData {
    private String problem;
    private String example1, example2, example3, example4;
    private int correctNum;

    public CreatedTestData(String problem, String example1, String example2, String example3, String example4, int correctNum) {
        this.problem = problem;
        this.example1 = example1;
        this.example2 = example2;
        this.example3 = example3;
        this.example4 = example4;
        this.correctNum = correctNum;
    }

    //getter
    public String getProblem() {
        return problem;
    }

    public String getExample1() {
        return example1;
    }
    public String getExample2() {
        return example2;
    }
    public String getExample3() {
        return example3;
    }
    public String getExample4() {
        return example4;
    }

    public int getCorrectNum() {
        return correctNum;
    }

    public String toStringExmaple() {
        return String.valueOf(correctNum) + '+'
                + example1 + '+'
                + example2 + '+'
                + example3 + '+'
                + example4;    }
}
