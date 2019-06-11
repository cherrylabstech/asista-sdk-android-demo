package com.asista.android.demo.asista_pns.model;

/**
 * Created by Benjamin J on 31-05-2019.
 */
public class Message {
    private String Id;
    private String title;
    private String body;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
