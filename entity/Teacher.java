/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

/**
 *
 * @author Sabrina
 */
public class Teacher {
    private String name, consultationHours, consultationDay, currentLocation, roomNumber, picturePath;
    private int linkId, roomId;
    
    public Teacher(){}

    public Teacher(String name){
        this.name = name;
        this.consultationDay = "";
        this.consultationHours = "";
        this.roomNumber = "";
        this.roomId = 0;
    }
    public Teacher(int linkId, String name) {
        this.name = name;
        this.linkId = linkId;
    }
    
    public Teacher(String name, String consultationHours, String consultationDay, String roomNumber) {
        this.name = name;
        this.consultationHours = consultationHours;
        this.consultationDay = consultationDay;
        this.roomNumber = roomNumber;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getLinkId() {
        return linkId;
    }

    public void setLinkId(int linkId) {
        this.linkId = linkId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConsultationHours() {
        return consultationHours;
    }

    public void setConsultationHours(String consultationHours) {
        this.consultationHours = consultationHours;
    }

    public String getConsultationDay() {
        return consultationDay;
    }

    public void setConsultationDay(String consultationDay) {
        this.consultationDay = consultationDay;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath() {
        this.picturePath = "https://www.htl-leonding.at/wp-content/uploads/intern_use_only/tuerschilder/lehrer/" + name.split(" ")[1]
                .substring(0,1)
                .toLowerCase()
                 + "_" + name
                .split(" ")[0]
                .toLowerCase()
                .replace("ä", "ae")
                .replace("ü", "ue")
                .replace("ö", "oe") + ".jpg";
                //"platzhalter.png";
    }
}
