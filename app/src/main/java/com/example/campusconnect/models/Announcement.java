package com.example.campusconnect.models;

import com.google.firebase.Timestamp;

public class Announcement {

    private String id;
    private String title;
    private String content;
    private String category;
    private String authorName;
    private Timestamp timestamp;
    private boolean isUrgent;

    public Announcement() {}


    public Announcement(String id, String title, String content, String category,
                        String authorName, Timestamp timestamp, boolean isUrgent) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.authorName = authorName;
        this.timestamp = timestamp;
        this.isUrgent = isUrgent;
    }

    // Getters
    public String getId()           { return id; }
    public String getTitle()        { return title; }
    public String getContent()      { return content; }
    public String getCategory()     { return category; }
    public String getAuthorName()   { return authorName; }
    public Timestamp getTimestamp() { return timestamp; }
    public boolean isUrgent()       { return isUrgent; }

    // Setters
    public void setId(String id)                    { this.id = id; }
    public void setTitle(String title)              { this.title = title; }
    public void setContent(String content)          { this.content = content; }
    public void setCategory(String category)        { this.category = category; }
    public void setAuthorName(String authorName)    { this.authorName = authorName; }
    public void setTimestamp(Timestamp timestamp)   { this.timestamp = timestamp; }
    public void setUrgent(boolean urgent)           { isUrgent = urgent; }
}