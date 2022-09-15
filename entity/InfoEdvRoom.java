/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.Date;

/**
 *
 * @author Sabrina
 */
public class InfoEdvRoom {
    private String roomNumber,roomName, currentClass, subject, startTime, endTime, lessonInfo;
    private String[] teachers;
    private Date date;
    private boolean isExam, isSubstitution, isEvent;
    
    public InfoEdvRoom(){}

    public InfoEdvRoom(String roomNumber, String roomName, String currentClass, String subject, String startTime, String endTime, Date date, String lessonInfo, boolean isExam,boolean isEvent, boolean isSubstitution) {
        this.roomNumber = roomNumber;
        this.roomName = roomName;
        this.currentClass = currentClass;
        this.subject = subject;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lessonInfo = lessonInfo;
        this.isExam = isExam;
        this.isEvent = isEvent;
        this.date = date;
        this.isSubstitution = isSubstitution;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(String currentClass) {
        this.currentClass = currentClass;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLessonInfo() {
        return lessonInfo;
    }

    public void setLessonInfo(String lessonInfo) {
        this.lessonInfo = lessonInfo;
    }

    public String[] getTeachers() {
        return teachers;
    }

    public void setTeachers(String[] teachers) {
        this.teachers = teachers;
    }

    public boolean isIsExam() {
        return isExam;
    }

    public void setIsExam(boolean isExam) {
        this.isExam = isExam;
    }

    public boolean isIsSubstitution() {
        return isSubstitution;
    }

    public void setIsSubstitution(boolean isSubstitution) {
        this.isSubstitution = isSubstitution;
    }

    public boolean isEvent() {
        return isEvent;
    }

    public void setEvent(boolean event) {
        isEvent = event;
    }
}
