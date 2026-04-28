package com.example.campusconnect.models;

public class ScheduleItem {

    private String subject;     // nom de la matière
    private String room;        // salle (ex: "Salle A12")
    private String professor;   // nom du professeur
    private String dayOfWeek;   // "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"
    private String startTime;   // ex: "08:30"
    private String endTime;     // ex: "10:30"

    // Constructeur vide OBLIGATOIRE pour Firestore
    public ScheduleItem() {}

    // Constructeur complet
    public ScheduleItem(String subject, String room, String professor,
                        String dayOfWeek, String startTime, String endTime) {
        this.subject = subject;
        this.room = room;
        this.professor = professor;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters
    public String getSubject()    { return subject; }
    public String getRoom()       { return room; }
    public String getProfessor()  { return professor; }
    public String getDayOfWeek()  { return dayOfWeek; }
    public String getStartTime()  { return startTime; }
    public String getEndTime()    { return endTime; }

    // Setters
    public void setSubject(String subject)       { this.subject = subject; }
    public void setRoom(String room)             { this.room = room; }
    public void setProfessor(String professor)   { this.professor = professor; }
    public void setDayOfWeek(String dayOfWeek)   { this.dayOfWeek = dayOfWeek; }
    public void setStartTime(String startTime)   { this.startTime = startTime; }
    public void setEndTime(String endTime)       { this.endTime = endTime; }
}