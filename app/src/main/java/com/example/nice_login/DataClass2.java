package com.example.nice_login;

import androidx.annotation.Nullable;

import java.util.List;

public class DataClass2 {
    private int type;
    private String title;
    private int pageNum;
    private String dataPath;
    private String lastEdit;
    private String key;

    @Nullable
    private Integer answerNumber;
    @Nullable
    private Integer correctAnswer;
    @Nullable
    private List<String> answers;

    public DataClass2(int type, String title, int pageNum, String dataPath, String lastEdit) {
        this.type = type;
        this.title = title;
        this.pageNum = pageNum;
        this.dataPath = dataPath;
        this.lastEdit = lastEdit;
    }

    public DataClass2(int type, String title, int pageNum, String dataPath, String lastEdit,
                      Integer answerNumber, Integer correctAnswer, List<String> answers) {
        this.type = type;
        this.title = title;
        this.pageNum = pageNum;
        this.dataPath = dataPath;
        this.lastEdit = lastEdit;
        this.answerNumber = answerNumber;
        this.correctAnswer = correctAnswer;
        this.answers = answers;
    }

    public DataClass2() {
    }

    public Integer getAnswerNumber() {
        return answerNumber;
    }

    public void setAnswerNumber(Integer answerNumber) {
        this.answerNumber = answerNumber;
    }

    public Integer getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Integer correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    @Nullable
    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(@Nullable List<String> answers) {
        this.answers = answers;
    }

    public String getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(String lastEdit) {
        this.lastEdit = lastEdit;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
