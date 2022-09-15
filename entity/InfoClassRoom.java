/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.sql.Array;
import java.util.List;

/**
 *
 * @author Sabrina
 */
public class InfoClassRoom {
    private String roomNumber, mainTeacher, mainClass, currentClass, subject, startTime, endTime, lessonInfo, currentLocationMainClass;
    private String[] teachers;
    private boolean isExam, isSubstitution, isEvent;
    
    public InfoClassRoom (){
    
    }

    public InfoClassRoom(String roomNumber, String mainTeacher, String mainClass, String currentClass, String subject, String startTime, String endTime, String lessonInfo, String currentLocationMainClass, boolean isExam, boolean isEvent, boolean isSubstitution) {
        this.roomNumber = roomNumber;
        this.mainTeacher = mainTeacher;
        this.mainClass = mainClass;
        this.currentClass = currentClass;
        this.subject = subject;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lessonInfo = lessonInfo;
        this.currentLocationMainClass = currentLocationMainClass;
        this.isExam = isExam;
        this.isEvent = isEvent;
        this.isSubstitution = isSubstitution;
    }

    public InfoClassRoom(String roomNumber, String mainTeacher, String mainClass) {
        this.roomNumber = roomNumber;
        this.mainTeacher = mainTeacher;
        this.mainClass = mainClass;
        this.currentClass = "";
        this.subject = "";
        this.startTime = "";
        this.endTime = "";
        this.lessonInfo = "";
        this.currentLocationMainClass = "";
        this.isExam = false;
        this.isEvent = false;
        this.isSubstitution = false;
        this.teachers = new String[]{"Raum frei"};
    }

    public String[] getTeachers() {
        return teachers;
    }

    public void setTeachers(String[] teachers) {
        this.teachers = teachers;
    }

    public String getCurrentLocationMainClass() {
        return currentLocationMainClass;
    }

    public void setCurrentLocationMainClass(String currentLocationMainClass) {
        this.currentLocationMainClass = currentLocationMainClass;
    }

    public boolean isEvent() {
        return isEvent;
    }

    public void setEvent(boolean event) {
        isEvent = event;
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

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getMainTeacher() {
        return mainTeacher;
    }

    public void setMainTeacher(String mainTeacher) {
        this.mainTeacher = mainTeacher;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
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
    
    
}
