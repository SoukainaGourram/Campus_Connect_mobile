package com.example.campusconnect.models;

import com.google.firebase.Timestamp;

public class Message {

    private String senderId;
    private String senderName;
    private String content;
    private Timestamp timestamp;

    // Constructeur vide OBLIGATOIRE pour Firestore
    public Message() {}

    // Constructeur complet
    public Message(String senderId, String senderName, String content, Timestamp timestamp) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters
    public String getSenderId()     { return senderId; }
    public String getSenderName()   { return senderName; }
    public String getContent()      { return content; }
    public Timestamp getTimestamp() { return timestamp; }

    // Setters
    public void setSenderId(String senderId)        { this.senderId = senderId; }
    public void setSenderName(String senderName)    { this.senderName = senderName; }
    public void setContent(String content)          { this.content = content; }
    public void setTimestamp(Timestamp timestamp)   { this.timestamp = timestamp; }
}