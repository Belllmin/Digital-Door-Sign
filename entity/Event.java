package entity;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Event {

    private int id; //Auto Increment in DB
    private String name;
    private String date;
    private String startTime;
    private String endTime;
    private String roomNumbers;
    private String information;

    public Event() { }
    public Event(String name, String date, String startTime, String endTime, String roomNumbers, String information) {
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomNumbers = roomNumbers;
        this.information = information;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getRoomNumbers() { return roomNumbers; }
    public void setRoomNumbers(String roomNumbers) { this.roomNumbers = roomNumbers; }
    public String getInformation() { return information; }
    public void setInformation(String information) { this.information = information; }
}
