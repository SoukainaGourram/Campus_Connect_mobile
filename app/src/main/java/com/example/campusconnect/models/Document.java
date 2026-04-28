package com.example.campusconnect.models;

import com.google.firebase.Timestamp;

public class Document {

    private String name;          // nom du fichier (ex: "TD_Algo_S3.pdf")
    private String downloadUrl;   // URL Firebase Storage pour télécharger
    private String type;          // "PDF", "DOC", "Image", etc.
    private String uploadedBy;    // nom de la personne qui a uploadé
    private Timestamp timestamp;  // date d'upload

    public Document() {}

    public Document(String name, String downloadUrl, String type,
                    String uploadedBy, Timestamp timestamp) {
        this.name = name;
        this.downloadUrl = downloadUrl;
        this.type = type;
        this.uploadedBy = uploadedBy;
        this.timestamp = timestamp;
    }

    public String getName()           { return name; }
    public String getDownloadUrl()    { return downloadUrl; }
    public String getType()           { return type; }
    public String getUploadedBy()     { return uploadedBy; }
    public Timestamp getTimestamp()   { return timestamp; }

    public void setName(String name)                    { this.name = name; }
    public void setDownloadUrl(String downloadUrl)      { this.downloadUrl = downloadUrl; }
    public void setType(String type)                    { this.type = type; }
    public void setUploadedBy(String uploadedBy)        { this.uploadedBy = uploadedBy; }
    public void setTimestamp(Timestamp timestamp)       { this.timestamp = timestamp; }
}