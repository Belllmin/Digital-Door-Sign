package service;

import WebUntis.WebuntisManager;
import db.DatabaseRepository;
import entity.*;

import java.io.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import repository.Repository;
import websocket.DoorSignEndpoint;

@Path("tablet")
public class LessonService {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("eventTablets") //getAllEventTablets
    public EventTablet[] getAllEventTablets() {
        try {
            return DatabaseRepository.getInstance().getAllEventTablets();
        } catch (SQLException e) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    @POST
    @Path("pdfs/{name}") //uploadPdfFile
    public void fileUpload(@PathParam("name") String temp, File file) {
        String name = temp.replaceAll("\\s+", "");
        String pdfName = name.split("\\+")[0];
        String currentdate = convertCurrentDateFormat(LocalDate.now().toString());
        String eventName = name.split("\\+")[1];
        try {
            String roomNumber = DatabaseRepository.getInstance().getRoomNumberByPdfName(pdfName);
            String eventNameWithDate = roomNumber + "_" + eventName + "_" + currentdate + ".pdf";
            File fileToCreate = new File("./pdfs/" + eventNameWithDate);

            OutputStream os = new FileOutputStream(fileToCreate);
            byte[] b = DatabaseRepository.getInstance().readFileToByteArray(file);
            os.write(b);

            os.close();
            DatabaseRepository.getInstance().addPdfFileNameToDatabase(eventNameWithDate, pdfName);
        } catch (SQLException | IOException e) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, e);
            System.out.println("message: " + e.getMessage());
        }
    }

    @GET
    @Path("pdfs")
    @Produces(MediaType.APPLICATION_JSON)
    public String[] getAllPdfNames() {
        try {
            return DatabaseRepository.getInstance().getAllPdfNames();
        } catch (SQLException ex) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/eventTablets") // addNewEventTablets
    public void addNewEventTablets(EventTablet eventTablets) {
        try {
            DatabaseRepository.getInstance().addNewEventTablets(eventTablets);
        } catch (SQLException ex) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @GET
    @Path("eventTablets/{eventName}") // getEventTabletsByEventName
    @Produces(MediaType.APPLICATION_JSON)
    public EventTablet[] getEventTabletsByEventName(@PathParam("eventName") String eventName) {
        try {
            return DatabaseRepository.getInstance().getEventTabletsByEventName(eventName);
        } catch (SQLException ex) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("eventTablets/{eventName}") // deleteEventTabletsByEventName
    public void deleteEventTabletsByEventName(@PathParam("eventName") String eventName) {
        try {
            DatabaseRepository.getInstance().deleteEventTabletsByEventName(eventName);
        } catch (SQLException ex) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("eventTablets/{tabletId}") // updateEventTablet
    public void updateEventTablet(@PathParam("tabletId") String tabletId, EventTablet eventTablet) {
        try {
            DatabaseRepository.getInstance().updateEventTablet(eventTablet, tabletId);
        } catch (SQLException ex) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/events") // addNewEvent
    public void addNewEvent(Event newEvent) throws SQLException {
        Event[] events = null;
        boolean found = false;
        try {
            events = DatabaseRepository.getInstance().getEvents();
        } catch (SQLException ex) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Event event : events) {
            if (event.getName().equals(newEvent.getName())) {
                found = true;
            }
            if (found) break;
        }
        if (!found) {
            DatabaseRepository.getInstance().addNewEvent(newEvent);
        } else System.out.println("LessonService, addNewEvent: The event is already in DB");
    }

    @GET
    @Path("events") // getEvents
    @Produces(MediaType.APPLICATION_JSON)
    public Event[] getEvents() throws IOException, ParseException {
        try {
            DatabaseRepository.getInstance().addEventsFromWebuntis();
            Event[] events = DatabaseRepository.getInstance().getEvents();
            return events;
        } catch (SQLException ex) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("events/{name}") // getEvent
    public Event getEvent(@PathParam("name") String name) {
        try {
            return DatabaseRepository.getInstance().getEvent(name);
        } catch (SQLException ex) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("events/{eventName}") // updateEvent
    public void updateEvent(@PathParam("eventName") String eventName, Event event) {
        try {
            DatabaseRepository.getInstance().updateEvent(event, eventName);
        } catch (SQLException ex) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("events/{name}") // removeEvent
    public void removeEvent(@PathParam("name") String name) {
        try {
            DatabaseRepository.getInstance().removeEvent(name);
        } catch (SQLException ex) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("events") // removeAllEvents
    public void removeAllEvents() {
        try {
            DatabaseRepository.getInstance().removeAllEvents();
        } catch (SQLException ex) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("tablets/{roomNumber}") // getTabletByRoomNumber
    public Tablet getTabletByRoomnumber(@PathParam("roomNumber") String roomNumber) {
        try {
            return DatabaseRepository.getInstance().getTabletByRoomNumber(roomNumber);
        } catch (SQLException e) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("message")
    public String message() {
        return "Hallo";
    }

    @GET
    @Path("init")
    @Produces(MediaType.APPLICATION_JSON)
    public void init() throws IOException, ParseException {
        WebuntisManager w = new WebuntisManager();
        w.init();
    }

    @GET
    @Path("actualise")
    @Produces(MediaType.APPLICATION_JSON)
    public void actualise() throws IOException, ParseException {
        WebuntisManager w = new WebuntisManager();
        w.actualiseSchedules();
        System.out.println("Fertig");
    }

    @GET
    @Path("getAllTablets")
    @Produces(MediaType.APPLICATION_JSON)
    public Tablet[] getAllTablets() throws IOException, ParseException {
        try {
            return DatabaseRepository.getInstance().getAllTablets();
        } catch (SQLException ex) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @GET
    @Path("getAllRooms")
    @Produces(MediaType.APPLICATION_JSON)
    public RoomName[] getAllRooms() throws IOException, ParseException {
        try {
            return DatabaseRepository.getInstance().getRoomsForAdmin().toArray(new RoomName[0]);
        } catch (SQLException ex) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /*Example :
    getAllTablets() : Observable<Tablet[]> {
        return this.http.get<Tablet[]>("http://localhost:9080/rest/tablet/getAllTablets");
      }
    */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("update/{id}")
    public void updateTablet(@PathParam("id") String id, Tablet tablet) throws IOException, ParseException, SQLException {
        DatabaseRepository.getInstance().updateTablet(id, tablet);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ex) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex);
        }
        tablet = DatabaseRepository.getInstance().getTablet(id);
        Session session = Repository.getInstance().getSessionFromMap(id);

        JSONObject jo = new JSONObject();
        jo.put("type", "sendData");
        jo.put("data", "Tablet id: " + tablet.getTabletId() + " connected. Roomnumber: " + tablet.getRoomNumber());
        if (tablet.getRoomType().equals("class")) {
            jo.put("info", DatabaseRepository.getInstance().getCurrentLesson(tablet.getRoomNumber()));
            jo.put("roomType", "class");
        } else if (tablet.getRoomType().equals("edv")) {
            jo.put("info", DatabaseRepository.getInstance().getEdvSchedule(tablet.getRoomNumber()));
            jo.put("currentInfo", DatabaseRepository.getInstance().getCurrentLesson(tablet.getRoomNumber()));
            jo.put("roomType", "edv");
        } else if (tablet.getRoomType().equals("teacher")) {
            jo.put("info", DatabaseRepository.getInstance().getTeachersForRoom(tablet.getRoomNumber()));
            jo.put("roomType", "teacher");
        }
        try {
            session.getBasicRemote().sendObject(jo);
            session.notify();
        } catch (EncodeException ex) {
            Logger.getLogger(DoorSignEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /* Example :
    updateTablet(body:Tablet,tabletId) {
        this.http.put("http://localhost:9080/rest/tablet/update/" +tabletId, body);
    }
    */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("find/{id}")
    public Tablet find(@PathParam("id") String id) {
        try {
            return DatabaseRepository.getInstance().getTablet(id);

        } catch (SQLException ex) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /* Example :
    getTabletById (id: string): Observable<Tablet> {
        return this.http.get<Tablet>(http://localhost:9080/rest/tablet/find/"+id);
    }
    */
    @DELETE
    @Path("delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void delete(@PathParam("id") String tabletId) {
        try {
            DatabaseRepository.getInstance().delete(tabletId);
            Session session = Repository.getInstance().getSessionFromMap(tabletId);
            JSONObject jo = new JSONObject();
            jo.put("type", "sendData");
            try {
                session.getBasicRemote().sendObject(jo);
                session.notify();
            } catch (EncodeException ex) {
                Logger.getLogger(DoorSignEndpoint.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /* Example :
    deleteTablet(tabletId) {
        this.http.delete("http://localhost:9080/rest/tablet/delete/" +tabletId).subscribe();
    }
    */
    @GET
    @Path("CurrentLesson")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Teacher> getCurrent() throws IOException, ParseException {
        try {
            return DatabaseRepository.getInstance().getTeachersForRoom("206");
        } catch (SQLException ex) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @GET
    @Path("CurrentLocations")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ClassLocationDTO> getClassToRoom() throws IOException, ParseException {
        try {
            return DatabaseRepository.getInstance().getCurrentLocations();
        } catch (SQLException ex) {
            Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
/*
    @GET
    @Path("roomNumbers/{pdfName}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getRoomNumberByPdfName(@PathParam("pdfName") String pdfName){
        try{
            String roomNumber = DatabaseRepository.getInstance().getRoomNumberByPdfName(pdfName);
            System.out.println("RoomNumber in LessonService: "+ roomNumber);
            return roomNumber;
        }
         catch (SQLException ex) {
        Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }*/

/*@GET
    @Produces("application/pdf")
    @Path("pdfs/{tabletId}") // getPdfByTabletId/{tabletId}
    public Response getPdfByTabletId(@PathParam("tabletId") String tabletId){

        Event[] events = null;
        EventTablet eventTablet = null;
        try{
            eventTablet = DatabaseRepository.getInstance().getEventTabletById(tabletId);
            events = DatabaseRepository.getInstance().getEvents();
        }catch (SQLException ex){ Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, ex); }

        if(eventTablet.getTabletId() != null){
            for(int i = 0; i < events.length; i++){
                if(eventTablet.getEventName().equals(events[i].getName())){
                    String current_date = LocalDate.now().toString();
                    current_date = convertCurrentDateFormat(current_date);
                    String event_date = events[i].getDate();
                    if(current_date.equals(event_date)){
                        try{
                            File file = DatabaseRepository.getInstance().getPdfByTabletId(tabletId);
                            System.out.println("FILE: " + file);
                            Response.ResponseBuilder response = Response.ok((Object) file);
                            response.header("Content-Disposition", "attachment; filename=" + file);
                            return response.build();
                        }catch (SQLException e) { Logger.getLogger(LessonService.class.getName()).log(Level.SEVERE, null, e); }
                    }
                }
            }
        }
        return null;
    }*/
