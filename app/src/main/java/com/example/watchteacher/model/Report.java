package com.example.watchteacher.model;

public class Report {

    int id;
    int teacherId;
    String teacherName;
    String report;
    String dateTime;

    //for notifications
    public Report(int id, int teacherId, String teacherName, String report, String dateTime) {
        this.id = id;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.report = report;
        this.dateTime = dateTime;
    }

    //for getting reports by teacher id
    public Report(String teacherName, String report, String date) {
        this.teacherName = teacherName;
        this.report = report;
        this.dateTime = date;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    //todo update this function based on dateTime string format to return only time
    public String getTime(){
        return dateTime;
    }
}
