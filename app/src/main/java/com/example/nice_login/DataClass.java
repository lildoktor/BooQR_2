package com.example.nice_login;

public class DataClass {
    private String dataTitle;
    private String dataDesc;
    private String dataImage;
    private String lastEdit;
    private String key;

    public DataClass(String dataTitle, String dataDesc, String dataImage, String lastEdit) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataImage = dataImage;
        this.lastEdit = lastEdit;
    }

    public DataClass() {
    }

    public String getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(String lastEdit) {
        this.lastEdit = lastEdit;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDataTitle() {
        return dataTitle;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public String getDataImage() {
        return dataImage;
    }
}