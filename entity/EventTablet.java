package entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EventTablet {
    private int eventTabletId; //Auto increment in DB
    private String tabletId;
    private String title;
    private String description;
    private String template;
    private String eventName;
    private String pdfName;

    public EventTablet() {}
    public EventTablet(String tabletId, String title, String description, String template, String eventName) {
        this.tabletId = tabletId;
        this.title = title;
        this.description = description;
        this.template = template;
        this.eventName = eventName;
    }
    public EventTablet(String tabletId, String title, String description, String template, String eventName, String pdfName) {
        this.tabletId = tabletId;
        this.title = title;
        this.description = description;
        this.template = template;
        this.eventName = eventName;
        this.pdfName = pdfName;
    }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTemplate() { return template; }
    public void setTemplate(String template) { this.template = template; }
    public String getTabletId() { return tabletId; }
    public void setTabletId(String tabletId) { this.tabletId = tabletId; }
    public String getPdfName() { return pdfName; }
    public void setPdfName(String pdfName) { this.pdfName = pdfName; }
}

