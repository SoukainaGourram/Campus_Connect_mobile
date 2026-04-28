package com.example.campusconnect.models;

public class User {
    private String uid;
    private String name;
    private String email;
    private String role; // student ou admin
    private String filiere;
    private String annee;
    private String photoUrl;

    public User() {
    }

    public User(String uid, String name, String email, String role,
                String filiere, String annee, String photoUrl) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.role = role;
        this.filiere = filiere;
        this.annee = annee;
        this.photoUrl = photoUrl;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }

    public String getAnnee() { return annee; }
    public void setAnnee(String annee) { this.annee = annee; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
