package com.hudongwx.test.instance;

/**
 * Created by hudongwx on 16-11-19.
 */
public class History {
    private int id;
    private String name;
    private String url;
    private String date;

    public History(String name, String url, String date) {
        this.name = name;
        this.url = url;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
