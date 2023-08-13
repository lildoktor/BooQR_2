package com.example.nice_login;

public class DataClass2 {
    private int type;
    private String title;
    private int pageNum;
    private String dataPath;
    private String lastEdit;
    private String key;

    public DataClass2(int type, String title, int pageNum, String dataPath, String lastEdit) {
        this.type = type;
        this.title = title;
        this.pageNum = pageNum;
        this.dataPath = dataPath;
        this.lastEdit = lastEdit;
    }

    public DataClass2() {
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
