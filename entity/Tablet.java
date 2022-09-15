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
public class Tablet {
    
    private String tabletId;
    private int roomLinkId;
    private String roomNumber;
    private String roomType;
    private String name;

    public Tablet(){}
    
    public Tablet(String tabletId, String roomNumber) {
        this.tabletId = tabletId;
        this.roomNumber = roomNumber;
    }

    public Tablet(String tabletId) {
        this.tabletId = tabletId;
    }

    public Tablet(String tabletId, int roomLinkId, String roomNumber) {
        this.tabletId = tabletId;
        this.roomLinkId = roomLinkId;
        this.roomNumber = roomNumber;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getTabletId() {
        return tabletId;
    }

    public void setTabletId(String tabletId) {
        this.tabletId = tabletId;
    }

    public int getRoomLinkId() {
        return roomLinkId;
    }

    public void setRoomLinkId(int roomLinkId) {
        this.roomLinkId = roomLinkId;
    }

}
