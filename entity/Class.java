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
public class Class {
    private int linkIdClass;
    private int linkIdTeacher;
    private String name;

    public Class(int linkId, int linkIdTeacher, String name) {
        this.linkIdClass = linkId;
        this.linkIdTeacher = linkIdTeacher;
        this.name = name;
    }

    
    
    
    public int getLinkClassId() {
        return linkIdClass;
    }

    public void setLinkClassId(int linkId) {
        this.linkIdClass = linkId;
    }


    public int getLinkIdTeacher() {
        return linkIdTeacher;
    }

    public void setLinkIdTeacher(int linkIdTeacher) {
        this.linkIdTeacher = linkIdTeacher;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
}
