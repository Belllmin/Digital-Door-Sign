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
public class RoomName implements Comparable{
    private String room, name;

    public RoomName(String room, String name) {
        this.room = room;
        this.name = name;
    }
    
    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Object t) {
        return this.name.compareTo(((RoomName)t).name);
    }
}
