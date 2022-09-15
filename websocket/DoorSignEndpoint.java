package websocket;

import WebUntis.WebuntisManager;
import db.DatabaseRepository;
import entity.Event;
import entity.EventTablet;
import entity.Tablet;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.json.JSONObject;
import repository.Repository;

@ServerEndpoint("/info")
public class DoorSignEndpoint {
    Repository repo;
    DatabaseRepository databaseRepo;

    @OnOpen
    public void onOpen(Session session) throws InterruptedException {
        try {
            System.out.println("TEST ON OPEN");
            databaseRepo = DatabaseRepository.getInstance();
        } catch (SQLException ex) {
            JSONObject jo = new JSONObject();
            jo.put("type", "dbError");
            jo.put("data", "Konnte keine Verbindung mit der Datenbank aufbauen");
            try {
                session.getBasicRemote().sendObject(jo);
            } catch (IOException | EncodeException ex1) {
                Logger.getLogger(DoorSignEndpoint.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        Repository.getInstance().addSession(session, getId());
        try {
            session.getBasicRemote().sendText("You are now connected. Please log in.");
        } catch (IOException ex) {
            Logger.getLogger(DoorSignEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @OnMessage
    public void distribute(String message, Session session) {
        try {
            JSONObject obj = new JSONObject(message);
            String optString = obj.optString("type", "error").toString();
            String tabletId = "";
            if (optString.equals("init")) {
                tabletId = obj.getString("data").toString();
                System.out.println(tabletId + " from data");
                if (tabletId.equals("null") || tabletId.equals("")) {
                    System.out.println("new initialize");
                    JSONObject jo = new JSONObject();
                    jo.put("type", "initUid");
                    tabletId = getId();
                    Repository.getInstance().updateSessionId(session, tabletId);
                    DatabaseRepository.getInstance().addTablet(tabletId);
                    Thread.sleep(1000);
                    jo.put("data", tabletId);
                    jo.put("message", "Tablet with id " + tabletId + " created. Ready for initialization.");
                    try {
                        session.getBasicRemote().sendObject(jo);
                    } catch (EncodeException ex) {
                        Logger.getLogger(DoorSignEndpoint.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return;
                }

                JSONObject eventTabletJSON = new JSONObject();
                tabletId = obj.getString("data");
                EventTablet eventTablet = DatabaseRepository.getInstance().getEventTabletById(tabletId);
                Event[] events = DatabaseRepository.getInstance().getEvents();
                EventTablet[] eArray = new EventTablet[1];
                eArray[0] = eventTablet;

                if (eArray[0].getTabletId() != null) { //if there is an EventTablet for this tablet
                    for (Event event : events) {
                        if (eArray[0].getEventName().equals(event.getName())) {
                            String current_date = LocalDate.now().toString();
                            current_date = convertCurrentDateFormat(current_date);
                            String event_date = event.getDate();
                            if (current_date.equals(event_date)) {
                                eventTabletJSON.put("info", eArray);
                                eventTabletJSON.put("type", "eventTablet");
                                eventTabletJSON.put("name", eventTablet.getEventName());
                                eventTabletJSON.put("title", eventTablet.getTitle());
                                eventTabletJSON.put("description", eventTablet.getDescription());
                                eventTabletJSON.put("template", eventTablet.getTemplate());
                                eventTabletJSON.put("tabletId", eventTablet.getTabletId());
                                eventTabletJSON.put("pdfName", eventTablet.getPdfName());
                                try {
                                    session.getBasicRemote().sendObject(eventTabletJSON);
                                    return;
                                } catch (EncodeException ex) {
                                    Logger.getLogger(DoorSignEndpoint.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                }
                Tablet t = DatabaseRepository.getInstance().getTablet(tabletId);
                if (t.getTabletId() == null) {
                    JSONObject jo = new JSONObject();
                    jo.put("type", "initUid");
                    tabletId = getId();
                    Repository.getInstance().updateSessionId(session, tabletId);
                    DatabaseRepository.getInstance().addTablet(tabletId);

                    jo.put("data", tabletId);
                    jo.put("message", "Tablet with id " + tabletId + " created. Ready for initialization.");
                    try {
                        session.getBasicRemote().sendObject(jo);
                    } catch (EncodeException ex) {
                        Logger.getLogger(DoorSignEndpoint.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return;
                }
                t = DatabaseRepository.getInstance().getTablet(tabletId);
                Repository.getInstance().updateSessionId(session, t.getTabletId());
                JSONObject jo = new JSONObject();

                if (t.getRoomLinkId() == 0) {
                    jo.put("type", "notSet");
                    jo.put("data", "Tablet id: " + t.getTabletId() + " connected. Roomnumber not set");
                } else {
                    jo.put("type", "sendData");
                    jo.put("data", "Tablet id: " + t.getTabletId() + " connected. Roomnumber: " + t.getRoomNumber());
                    if (t.getRoomType().equals("class")) {
                        jo.put("info", DatabaseRepository.getInstance().getCurrentLesson(t.getRoomNumber()));
                        jo.put("roomType", "class");
                    } else if (t.getRoomType().equals("edv")) {
                        jo.put("info", DatabaseRepository.getInstance().getEdvSchedule(t.getRoomNumber()));
                        jo.put("currentInfo", DatabaseRepository.getInstance().getCurrentLesson(t.getRoomNumber()));
                        jo.put("roomType", "edv");
                    } else if (t.getRoomType().equals("teacher")) {
                        jo.put("info", DatabaseRepository.getInstance().getTeachersForRoom(t.getRoomNumber()));
                        jo.put("roomType", "teacher");
                    }
                }
                try {
                    session.getBasicRemote().sendObject(jo);
                } catch (EncodeException ex) {
                    Logger.getLogger(DoorSignEndpoint.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (optString.equals("initDatabase")) {
                WebuntisManager w = new WebuntisManager();
                w.init();
            } else if (optString.equals("schedules")) {
                WebuntisManager w = new WebuntisManager();
                w.actualiseSchedules();
            }
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(DoorSignEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            JSONObject jo = new JSONObject();
            jo.put("type", "dbError");
            jo.put("data", "Konnte keine Verbindung mit der Datenbank aufbauen");
            try {
                session.getBasicRemote().sendObject(jo);
            } catch (IOException | EncodeException ex1) {
                Logger.getLogger(DoorSignEndpoint.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    @OnError
    public void onError(Throwable t) {
        System.out.println("TEST ON ERROR");
        t.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("TEST ON CLOSE");
        repo.getSessions().remove(session);
    }

    public String getId() {
        System.out.println("TEST ON GET ID");
        String returnString = UUID.randomUUID().toString();
        System.out.println(returnString);
        return returnString;
    }

    public String convertCurrentDateFormat(String currentDate) {
        String day = currentDate.substring(8, 10);
        if (day.startsWith("0")) {
            day = day.substring(1);
        }
        String month = currentDate.substring(5, 7);
        if (month.startsWith("0")) {
            month = month.substring(1);
        }
        String year = currentDate.substring(0, 4);
        String newDate = day + "." + month + "." + year;
        return newDate;
    }
}