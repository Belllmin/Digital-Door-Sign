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
public class ClassLocationDTO {
    private String className;
    private String roomNumber;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    
    public ClassLocationDTO(String className, String roomNumber) {
        this.className = className;
        this.roomNumber = roomNumber;
    }
    
}
