package com.example.campusconnect.models;

import com.google.firebase.Timestamp;
import java.util.List;

public class Group {

    private String id;
    private String name;
    private String description;
    private String filiere;
    private String createdBy;
    private String creatorName;
    private Timestamp createdAt;
    private List<String> members;


    public Group() {}

    public Group(String id, String name, String description, String filiere,
                 String createdBy, String creatorName,
                 Timestamp createdAt, List<String> members) {
        this.id          = id;
        this.name        = name;
        this.description = description;
        this.filiere     = filiere;
        this.createdBy   = createdBy;
        this.creatorName = creatorName;
        this.createdAt   = createdAt;
        this.members     = members;
    }

    public String getId()           { return id; }
    public String getName()         { return name; }
    public String getDescription()  { return description; }
    public String getFiliere()      { return filiere; }
    public String getCreatedBy()    { return createdBy; }
    public String getCreatorName()  { return creatorName; }
    public Timestamp getCreatedAt() { return createdAt; }
    public List<String> getMembers(){ return members; }

    public void setId(String id)                    { this.id = id; }
    public void setName(String name)                { this.name = name; }
    public void setDescription(String description)  { this.description = description; }
    public void setFiliere(String filiere)          { this.filiere = filiere; }
    public void setCreatedBy(String createdBy)      { this.createdBy = createdBy; }
    public void setCreatorName(String creatorName)  { this.creatorName = creatorName; }
    public void setCreatedAt(Timestamp createdAt)   { this.createdAt = createdAt; }
    public void setMembers(List<String> members)    { this.members = members; }
}