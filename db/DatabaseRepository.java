/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import WebUntis.RoomIdToLink;
import WebUntis.RoomInfo;
import entity.*;

import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import entity.Class;
import org.apache.commons.lang3.time.DateUtils;

import javax.sql.rowset.serial.SerialBlob;

/**
 * @author Sabrina, Belmin
 */
public class DatabaseRepository {
    private static DatabaseRepository instance;
    private Connection connection;
    private Statement stat;
    private PreparedStatement p;

    public DatabaseRepository() throws SQLException {
        //http://0.0.0.0:9090/websockets/info
        String DRIVER_STRING = "com.mysql.jdbc.Driver";
        //String CONECTION_STRING = "jdbc:mysql://vm101.htl-leonding.ac.at:3306/dds_db?characterEncoding=latin1&useConfigs=maxPerformance";
        //String CONECTION_STRING = "jdbc:mysql://localhost:3306/dds_db?characterEncoding=latin1&useConfigs=maxPerformance";
        //String CONECTION_STRING = "jdbc:mysql://ddsdb:3306/dds_db?characterEncoding=latin1&useConfigs=maxPerformance";
        String CONECTION_STRING = "jdbc:mysql://localhost:3306/dds_db?serverTimezone=Europe/Vienna";
        String USER = "root";
        String PASSWORD = "ddsdb";
        connection = java.sql.DriverManager.getConnection(CONECTION_STRING, USER, PASSWORD);
        connection.setAutoCommit(true);
    }

    public static DatabaseRepository getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseRepository();
        }
        return instance;
    }

    public EventTablet[] getAllEventTablets() {
        List<EventTablet> eventTablets = new ArrayList<>();
        String getEventTabletsQuery = "select * from eventTablet";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getEventTabletsQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                EventTablet eventTablet = new EventTablet(
                        resultSet.getString("tabletId"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getString("template"),
                        resultSet.getString("eventName"),
                        resultSet.getString("pdfName"));
                eventTablets.add(eventTablet);
            }
        } catch (SQLException ex) {
            System.out.println("Get event tablet error: " + ex.getMessage());
        }
        return eventTablets.toArray(new EventTablet[0]);
    }

    public byte[] readFileToByteArray(File file) {
        FileInputStream fis = null;
        byte[] bArray = new byte[(int) file.length()];
        try {
            fis = new FileInputStream(file);
            fis.read(bArray);
            fis.close();
        } catch (IOException ex) {
            System.out.println("Read File To Byte Array - Error " + ex.getMessage());
        }
        return bArray;
    }

    public String[] getAllPdfNames() {
        List<String> pdfNames = new ArrayList<>();
        String getAllPdfNamesQuery = "select pdfName from eventTablet";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getAllPdfNamesQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String pdfName = resultSet.getString("pdfName");
                pdfNames.add(pdfName);
            }
        } catch (SQLException ex) {
            System.out.println("Get all pdf names error: " + ex.getMessage());
        }
        return pdfNames.toArray(new String[0]);
    }

    public void addNewEventTablets(EventTablet eventTablets) {
        String addNewEventTabletQuery = "insert into eventTablet (tabletId, title, description, template, eventName, pdfName) values (?,?,?,?,?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(addNewEventTabletQuery);
            preparedStatement.setString(1, eventTablets.getTabletId());
            preparedStatement.setString(2, eventTablets.getTitle());
            preparedStatement.setString(3, eventTablets.getDescription());
            preparedStatement.setString(4, eventTablets.getTemplate());
            preparedStatement.setString(5, eventTablets.getEventName());
            preparedStatement.setString(6, eventTablets.getPdfName());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Add new event tablet error: " + ex.getMessage());
        }
    }

    public void addPdfFileNameToDatabase(String eventNameWithDate, String pdfName) {
        String addPdfFileNameQuery = "update eventTablet set pdfName = ? where pdfName = '" + pdfName + "'";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(addPdfFileNameQuery);
            preparedStatement.setString(1, eventNameWithDate);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Add pdf file name to DB error: " + ex.getMessage());
        }
    }

    public EventTablet getEventTabletById(String tabletId) {
        EventTablet eventTablet = new EventTablet();
        String getEventTabletQuery = "select * from eventTablet where tabletId = '" + tabletId + "'";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getEventTabletQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                eventTablet.setTabletId(resultSet.getString("tabletId"));
                eventTablet.setTitle(resultSet.getString("title"));
                eventTablet.setDescription(resultSet.getString("description"));
                eventTablet.setTemplate(resultSet.getString("template"));
                eventTablet.setEventName(resultSet.getString("eventName"));
                eventTablet.setPdfName(resultSet.getString("pdfName"));
            }
        } catch (SQLException ex) {
            System.out.println("Get event tablet by tabletId error: " + ex.getMessage());
        }
        return eventTablet;
    }

    public EventTablet[] getEventTabletsByEventName(String eventName) {
        List<EventTablet> eventTablets = new ArrayList<>();
        String getEventTabletsQuery = "select * from eventTablet where eventName = '" + eventName + "'";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getEventTabletsQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                EventTablet eventTablet = new EventTablet(
                        resultSet.getString("tabletId"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getString("template"),
                        resultSet.getString("eventName"));
                resultSet.getString("pdfName");
                eventTablets.add(eventTablet);
            }
        } catch (SQLException ex) {
            System.out.println("Get event tablet error: " + ex.getMessage());
        }
        return eventTablets.toArray(new EventTablet[0]);
    }

    public void deleteEventTabletsByEventName(String eventName) {
        String deleteEventTabletQuery = "delete from eventTablet where eventName = '" + eventName + "'";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteEventTabletQuery);
            preparedStatement.execute();
        } catch (SQLException ex) {
            System.out.println("Remove event tablet error: " + ex.getMessage());
        }
    }

    public void updateEventTablet(EventTablet eventTablet, String tabletId) {
        String updateEventTabletQuery = "update eventTablet set tabletId = ?, title = ?, description = ?, template = ?, eventName = ? where tabletId = '" + tabletId + "'";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateEventTabletQuery);
            preparedStatement.setString(1, eventTablet.getTabletId());
            preparedStatement.setString(2, eventTablet.getTitle());
            preparedStatement.setString(3, eventTablet.getDescription());
            preparedStatement.setString(4, eventTablet.getTemplate());
            preparedStatement.setString(5, eventTablet.getEventName());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Update event tablet error: " + ex.getMessage());
        }
    }

    public String getRoomNumberByPdfName(String pdfName) {
        String roomNumber = "";
        String query = "select roomNumber from room r join tablet t on (r.linkIdRoom = t.linkIdRoom) join eventTablet e on (t.tabletId = e.tabletId) where e.pdfName = '" + pdfName + "'";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roomNumber = resultSet.getString("roomNumber");
            }
        } catch (SQLException ex) {
            System.out.println("Add new event error: " + ex.getMessage());
        }
        System.out.println("RoomNumber in getRoomNumberByPdfName: " + roomNumber);
        return roomNumber;
    }

    public void addNewEvent(Event event) {
        String addNewEventQuery = "insert into events (name, date, startTime, endTime, roomNumbers, information) values (?,?,?,?,?,?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(addNewEventQuery);
            preparedStatement.setString(1, event.getName());
            preparedStatement.setString(2, event.getDate());
            preparedStatement.setString(3, event.getStartTime());
            preparedStatement.setString(4, event.getEndTime());
            preparedStatement.setString(5, event.getRoomNumbers());
            preparedStatement.setString(6, event.getInformation());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Add new event error: " + ex.getMessage());
        }
    }

    public void addEventsFromWebuntis() {
        String getEventsFromWebunitsQuery = "select lessonInfo, lessonDate, startTime, endTime, room.roomNumber from classLessons join room on classLessons.linkIdRoom = room.linkIdRoom where isEvent = 1";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getEventsFromWebunitsQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Event newEvent = new Event(
                        resultSet.getString("lessonInfo"),
                        resultSet.getString("lessonDate"),
                        resultSet.getString("startTime"),
                        resultSet.getString("endTime"),
                        resultSet.getString("room.roomNumber"),
                        " ");
                Event eventData = getEvent(newEvent.getName());

               /* String eventsQuery = "select * from events";
                PreparedStatement eventsStatement = connection.prepareStatement(eventsQuery);
                ResultSet rsEvents = eventsStatement.executeQuery();
                while(rsEvents.next()){
                    Event dbEvent = new Event(
                            resultSet.getString("lessonInfo"),
                            resultSet.getString("lessonDate"),
                            resultSet.getString("startTime"),
                            resultSet.getString("endTime"),
                            resultSet.getString("room.roomNumber"),
                            " ");
                    Event dbEventName = getEvent(newEvent.getName());
                }*/
                if (eventData.getName() == null) {
                    addNewEvent(newEvent); //Events from Webuntis in DB speichern
                }
            }
        } catch (SQLException ex) {
            System.out.println("Get events from webunits error: " + ex.getMessage());
        }
    }

    public Event[] getEvents() {
        List<Event> events = new ArrayList<>();
        String getEventsQuery = "select * from events";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getEventsQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Event tempEvent = new Event(
                        resultSet.getString("name"),
                        resultSet.getString("date"),
                        resultSet.getString("startTime"),
                        resultSet.getString("endTime"),
                        resultSet.getString("roomNumbers"),
                        resultSet.getString("information"));
                events.add(tempEvent);
            }
        } catch (SQLException ex) {
            System.out.println("Get events error: " + ex.getMessage());
        }
        return events.toArray(new Event[0]);
    }

    public void removeEvent(String name) {
        String deleteEventQuery = "delete from events where name = '" + name + "'";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteEventQuery);
            preparedStatement.execute();
        } catch (SQLException ex) {
            System.out.println("Remove event error: " + ex.getMessage());
        }
    }

    public void removeAllEvents() {
        String deleteAllEventsQuery = "delete from events";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(deleteAllEventsQuery);
            preparedStatement.execute();
        } catch (SQLException ex) {
            System.out.println("Remove events error: " + ex.getMessage());
        }
    }

    public Event getEvent(String name) {
        Event getEvent = new Event();
        String getEventQuery = "select * from events where name = '" + name + "'";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getEventQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                getEvent.setName(resultSet.getString("name"));
                getEvent.setDate(resultSet.getString("date"));
                getEvent.setStartTime(resultSet.getString("startTime"));
                getEvent.setEndTime(resultSet.getString("endTime"));
                getEvent.setRoomNumbers(resultSet.getString("roomNumbers"));
                getEvent.setInformation(resultSet.getString("information"));
            }
        } catch (SQLException ex) {
            System.out.println("Get event error: " + ex.getMessage());
        }
        return getEvent;
    }

    public void updateEvent(Event event, String eventName) {
        String updateEventQuery = "update events set name = ?, date = ?, startTime = ?, endTime = ?, roomNumbers = ?, information = ? where name = '" + eventName + "'";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(updateEventQuery);
            preparedStatement.setString(1, eventName);
            preparedStatement.setString(2, event.getDate());
            preparedStatement.setString(3, event.getStartTime());
            preparedStatement.setString(4, event.getEndTime());
            preparedStatement.setString(5, event.getRoomNumbers());
            preparedStatement.setString(6, event.getInformation());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Update event error: " + ex.getMessage());
        }
    }

    public Tablet getTabletByRoomNumber(String roomNumber) {
        Tablet tablet = new Tablet();
        String query = "select * from tablet t join room r on t.linkIdRoom = r.linkIdRoom where r.roomNumber like '" + roomNumber + "'";
        try {
            stat = connection.createStatement();
            ResultSet rs = stat.executeQuery(query);
            if (rs.next()) {
                tablet.setTabletId(rs.getString(0));
                tablet.setRoomLinkId(rs.getInt(2));
                tablet.setRoomNumber(rs.getString(6));
                tablet.setRoomType(rs.getString(8));
                tablet.setName(rs.getString(3));
                rs.close();
                stat.close();
                return tablet;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Tablet[] getAllTablets() {
        List<Tablet> tabletList = new ArrayList<>();
        try {
            stat = connection.createStatement();
            ResultSet rs = stat.executeQuery("SELECT * FROM tablet JOIN room on tablet.linkIdRoom = room.linkIdRoom");
            while (rs.next()) {
                Tablet tablet = new Tablet();
                tablet.setTabletId(rs.getString(1));
                tablet.setRoomNumber(rs.getString(6));
                tablet.setRoomType(rs.getString(8));
                tablet.setName(rs.getString(3));
                tabletList.add(tablet);
            }
            stat.close();
            rs.close();

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tabletList.toArray(new Tablet[0]);
    }

    public Tablet getTablet(String tabletId) {
        Tablet tablet = new Tablet();
        try {
            stat = connection.createStatement();
            ResultSet rs = stat.executeQuery("SELECT * FROM tablet JOIN room on tablet.linkIdRoom = room.linkIdRoom where tabletId like '" + tabletId + "'");
            if (rs.next()) {
                tablet.setTabletId(tabletId);
                tablet.setRoomLinkId(rs.getInt(2));
                tablet.setRoomNumber(rs.getString(6));
                tablet.setRoomType(rs.getString(8));
                tablet.setName(rs.getString(3));
                rs.close();
                stat.close();
            } else {
                rs = stat.executeQuery("SELECT * FROM tablet where tabletId like '" + tabletId + "'");
                if (rs.next()) {
                    tablet.setTabletId(tabletId);
                    tablet.setName(rs.getString(2));
                }
            }
            stat.close();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tablet;
    }

    public void addTablet(String tabletId) {
        try {
            stat = connection.createStatement();
            stat.executeUpdate("insert into tablet (tabletId) values ('" + tabletId + "')");
            stat.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateTablet(String tabletId, Tablet tablet) {
        try {
            int roomLinkId = 0;
            stat = connection.createStatement();
            ResultSet rs = stat.executeQuery("SELECT linkIdRoom FROM room where roomNumber = '" + tablet.getRoomNumber() + "'");
            while (rs.next()) {
                roomLinkId = rs.getInt(1);
            }
            stat.close();
            rs.close();
            stat = connection.createStatement();
            stat.executeUpdate("UPDATE tablet SET linkIdRoom=" + roomLinkId + ", name='" + tablet.getName() + "'  WHERE tabletId = '" + tabletId + "'");
            stat.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addRoom(RoomIdToLink r) {
        try {
            stat = connection.createStatement();
            stat.executeUpdate("insert into room (linkIdRoom, linkIdClass, roomNumber, roomName, roomType) values (" + r.getLinkId() + "," + r.getLinkIdClass() + ", '" + r.getRoomDisplayId() + "', '" + r.getRoomName() + "', '" + r.getRoomType() + "')");
            stat.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addTeacher(Teacher t) {
        try {
            stat = connection.createStatement();
            stat.executeUpdate("insert into teacher (linkIdTeacher, teacherName, roomId, consultation) values (" + t.getLinkId() + ", '" + t.getName() + "', " + t.getRoomId() + ", '" + t.getConsultationDay() + " " + t.getConsultationHours() + "')");
            stat.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addClass(int linkIdClass, String className) {
        try {
            stat = connection.createStatement();
            stat.executeUpdate("insert into class (linkIdClass, className) values (" + linkIdClass + ", '" + className + "')");
            stat.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int addTeacherRoom(String roomNumber) {
        int id = 0;
        try {
            stat = connection.createStatement();
            ResultSet rs = stat.executeQuery("select max(linkIdRoom) from room");
            while (rs.next()) {
                id = rs.getInt(1);
            }
            stat.close();
            stat = connection.createStatement();
            stat.executeUpdate("insert into room (linkIdRoom, roomNumber, roomType) values (" + (id + 1) + ",'" + roomNumber + "', 'teacher')");

            stat.close();
            rs.close();

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return id;
    }

    public List<RoomName> getRoomsForAdmin() {
        List<RoomName> list = new ArrayList<RoomName>();
        try {
            stat = connection.createStatement();
            ResultSet rs = stat.executeQuery("SELECT roomNumber, roomName FROM room WHERE roomType=\"teacher\" OR roomType=\"class\" OR roomType=\"edv\" OR roomType=\"werk\"");
            while (rs.next()) {
                RoomName r = new RoomName(rs.getString(1), rs.getString(2));
                list.add(r);
            }
            Collections.sort(list);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public List<RoomName> getRooms() {
        List<RoomName> list = new ArrayList<RoomName>();
        try {
            stat = connection.createStatement();
            ResultSet rs = stat.executeQuery("select linkIdRoom, roomName from room");
            while (rs.next()) {
                RoomName r = new RoomName(rs.getString(1), rs.getString(2));
                list.add(r);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public int getClassId(int linkIdRoom) {
        int result = -1;
        try {
            p = connection.prepareStatement("select linkIdClass, count(*) from `classLessons`\n" +
                    "     where linkIdRoom = ?\n" +
                    "    group by linkIdClass\n" +
                    "      order by count(*) desc;");
            p.setInt(1, linkIdRoom);
            ResultSet res = p.executeQuery();

            if (res.next()) {
                result = res.getInt(1);
                if (result == 0) {
                    return -1;
                }
                int count = res.getInt(2);
                if (count < 14) {
                    return -1;
                }
            }
            p.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public void updateRoom(RoomIdToLink r) {
        try {
            stat = connection.createStatement();
            stat.executeUpdate("UPDATE room SET roomType='" + r.getRoomType() + "', linkIdClass='" + r.getLinkIdClass() + "', roomName='" + r.getRoomName() + "' WHERE linkIdRoom = '" + r.getLinkId() + "'");
            stat.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public RoomIdToLink getRoom(RoomIdToLink r) {
        try {
            p = connection.prepareStatement("select * from `room`\n" +
                    "     where roomNumber = ?");
            p.setString(1, r.getRoomDisplayId());
            ResultSet res = p.executeQuery();

            if (res.next()) {
                r.setLinkId(res.getInt(1));
                r.setRoomType(res.getString(5));
            }
            p.close();
            return r;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return r;
    }

    public void addLessonsClass(List<RoomInfo> roomInfo) {
        for (RoomInfo info : roomInfo) {
            try {
                p = connection.prepareStatement("insert into classLessons (linkIdClass, linkIdRoom, lessonDate, startTime, endTime, subject, isExam,isEvent, isSubstitution,linkIdTeacherArray, lessonInfo) values (?,?,?, ?, ?,?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                p.setInt(1, info.getLinkIdClass());
                p.setInt(2, info.getLinkIdRoom());
                p.setDate(3, Date.valueOf(info.getDate()));
                p.setInt(4, info.getStartTime() * 100);
                p.setInt(5, info.getEndTime() * 100);
                p.setString(6, info.getSubject());
                p.setBoolean(7, info.getIsExam());
                p.setBoolean(8, info.getIsEvent());
                p.setBoolean(9, info.getIsSubstitution());
                p.setString(10, info.getTeacher());
                p.setString(11, info.getLessonInfo());
                p.execute();

                ResultSet res = p.getGeneratedKeys();
                while (res.next()) {
                    int id = res.getInt(1);
                }
                p.close();
                stat.close();
                res.close();
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public List<InfoEdvRoom> getEdvSchedule(String roomNumber) {
        List<InfoEdvRoom> r = new ArrayList<>();
        try {
            p = connection.prepareStatement("SELECT room.roomNumber, room.roomName, class.className, classLessons.subject, \n" +
                    "classLessons.startTime, classLessons.endTime, classLessons.lessonDate, classLessons.lessonInfo, \n" +
                    "classLessons.isExam,classLessons.isEvent, classLessons.isSubstitution, classLessons.linkIdTeacherArray\n" +
                    "FROM classLessons \n" +
                    "JOIN room ON classLessons.linkIdRoom = room.linkIdRoom \n" +
                    "JOIN class ON classLessons.linkIdClass = class.linkIdClass \n" +
                    "JOIN teacher ON class.linkIdTeacher = teacher.linkIdTeacher \n" +
                    "WHERE room.roomNumber = ?");
            p.setString(1, roomNumber);

            ResultSet rs = p.executeQuery();
            //InfoClassRoom(roomNumber, mainTeacher, mainClass, currentClass, subject, startTime, endTime, lessonInfo, currentLocationMainClass, isExam, isSubstitution)
            while (rs.next()) {
                InfoEdvRoom info = new InfoEdvRoom(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getDate(7), rs.getString(8), rs.getBoolean(9), rs.getBoolean(10), rs.getBoolean(11));
                List<String> teachers = new ArrayList<>();
                for (String s : rs.getString(12).split(";")) {
                    Statement stat = connection.createStatement();
                    ResultSet rs1 = stat.executeQuery("select teacherName from teacher where linkIdTeacher = " + s);
                    if (rs1.next()) {
                        teachers.add(rs1.getString(1));
                    }
                    rs1.close();
                    stat.close();
                }
                info.setTeachers(teachers.toArray(new String[0]));
                r.add(info);
            }
            rs.close();
            p.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return r;
    }

    public List<InfoClassRoom> getCurrentLesson(String roomNumber) {
        List<InfoClassRoom> r = new ArrayList<>();
        InfoClassRoom info = new InfoClassRoom();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String myDateString = dateFormat.format(new java.util.Date());

        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String myTimeString = timeFormat.format(new java.util.Date());
        System.out.println(myDateString + " " + myTimeString);
        try {
            p = connection.prepareStatement("SELECT room.roomNumber, room.roomName, teacher.teacherName \n" +
                    "FROM room \n" +
                    "JOIN class ON room.linkIdClass = class.linkIdClass \n" +
                    "JOIN teacher ON class.linkIdTeacher = teacher.linkIdTeacher \n" +
                    "WHERE room.roomNumber = ?");
            p.setString(1, roomNumber);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                info = new InfoClassRoom(rs.getString(1), rs.getString(3), rs.getString(2));
            }
            p.close();
            rs.close();

            p = connection.prepareStatement("SELECT roomMainClass.roomNumber \n" +
                    "FROM room \n" +
                    "JOIN class ON room.linkIdClass = class.linkIdClass \n" +
                    "JOIN classLessons as mainClass ON mainClass.linkIdClass = room.linkIdClass \n" +
                    "JOIN class as mainClassName ON mainClassName.linkIdClass = mainClass.linkIdClass\n" +
                    "JOIN room as roomMainClass ON roomMainClass.linkIdRoom = mainClass.linkIdRoom\n" +
                    "WHERE room.roomNumber = ? \n" +
                    "AND mainClass.startTime <= ? \n" +
                    "AND mainClass.endTime >= ? \n" +
                    "AND mainClass.lessonDate = ?");
            p.setString(1, roomNumber);
            p.setString(2, myTimeString);
            p.setString(3, myTimeString);
            p.setString(4, myDateString);
            rs = p.executeQuery();
            //InfoClassRoom(roomNumber, mainTeacher, mainClass, currentClass, subject, startTime, endTime, lessonInfo, currentLocationMainClass, isExam, isSubstitution)
            if (rs.next()) {
                info.setCurrentLocationMainClass(" aktuell im Raum " + rs.getString(1));
            } else {
                info.setCurrentLocationMainClass(" aktuell Pause / nicht im Haus");
            }
            rs.close();
            p.close();

            p = connection.prepareStatement("SELECT class.className, classLessons.subject, \n" +
                    "classLessons.startTime, classLessons.endTime, classLessons.lessonInfo, \n" +
                    "classLessons.isExam, classLessons.isEvent,classLessons.isSubstitution, classLessons.linkIdTeacherArray\n" +
                    "FROM classLessons \n" +
                    "JOIN room ON classLessons.linkIdRoom = room.linkIdRoom \n" +
                    "JOIN class ON classLessons.linkIdClass = class.linkIdClass \n" +
                    "JOIN teacher ON class.linkIdTeacher = teacher.linkIdTeacher \n" +
                    "WHERE room.roomNumber = ? \n" +
                    "AND classLessons.startTime <= ? \n" +
                    "AND classLessons.endTime >= ? \n" +
                    "AND classLessons.lessonDate = ?");
            p.setString(1, roomNumber);
            p.setString(2, myTimeString);
            p.setString(3, myTimeString);
            p.setString(4, myDateString);

            rs = p.executeQuery();
            //InfoClassRoom(roomNumber, mainTeacher, mainClass, currentClass, subject, startTime, endTime, lessonInfo, currentLocationMainClass, isExam, isSubstitution)
            if (rs.next()) {
                info.setCurrentClass(rs.getString(1));
                info.setSubject(rs.getString(2));
                info.setStartTime(rs.getString(3));
                info.setEndTime(rs.getString(4));
                info.setLessonInfo(rs.getString(5));
                info.setIsExam(rs.getBoolean(6));
                info.setEvent(rs.getBoolean(7));
                info.setIsSubstitution(rs.getBoolean(8));
                List<String> teachers = new ArrayList<>();
                for (String s : rs.getString(9).split(";")) {
                    stat = connection.createStatement();
                    ResultSet rs1 = stat.executeQuery("select teacherName from teacher where linkIdTeacher = " + s);
                    if (rs1.next()) {
                        teachers.add(rs1.getString(1));
                    }
                    rs1.close();
                    stat.close();
                }
                info.setTeachers(teachers.toArray(new String[0]));

                rs.close();
                p.close();
            } else {
                java.util.Date d = new java.util.Date();
                java.util.Date dateAddMinutes = DateUtils.addMinutes(d, 15);
                myTimeString = timeFormat.format(dateAddMinutes);
                p = connection.prepareStatement("SELECT class.className, classLessons.subject, \n" +
                        "classLessons.startTime, classLessons.endTime, classLessons.lessonInfo, \n" +
                        "classLessons.isExam, classLessons.isEvent,classLessons.isSubstitution, classLessons.linkIdTeacherArray\n" +
                        "FROM classLessons \n" +
                        "JOIN room ON classLessons.linkIdRoom = room.linkIdRoom \n" +
                        "JOIN class ON classLessons.linkIdClass = class.linkIdClass \n" +
                        "JOIN teacher ON class.linkIdTeacher = teacher.linkIdTeacher \n" +
                        "WHERE room.roomNumber = ? \n" +
                        "AND classLessons.startTime <= ? \n" +
                        "AND classLessons.endTime >= ? \n" +
                        "AND classLessons.lessonDate = ?");
                p.setString(1, roomNumber);
                p.setString(2, myTimeString);
                p.setString(3, myTimeString);
                p.setString(4, myDateString);

                rs = p.executeQuery();
                if (rs.next()) {
                    info.setCurrentClass(rs.getString(1));
                    info.setSubject(rs.getString(2));
                    info.setStartTime(rs.getString(3));
                    info.setEndTime(rs.getString(4));
                    info.setLessonInfo(rs.getString(5));
                    info.setIsExam(rs.getBoolean(6));
                    info.setEvent(rs.getBoolean(7));
                    info.setIsSubstitution(rs.getBoolean(8));
                    List<String> teachers = new ArrayList<>();
                    for (String s : rs.getString(9).split(";")) {
                        stat = connection.createStatement();
                        ResultSet rs1 = stat.executeQuery("select teacherName from teacher where linkIdTeacher = " + s);
                        if (rs1.next()) {
                            teachers.add(rs1.getString(1));
                        }
                        rs1.close();
                        stat.close();
                    }
                    info.setTeachers(teachers.toArray(new String[0]));

                    rs.close();
                    p.close();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        r.add(info);
        return r;
    }

    public List<Class> getClasses() {
        List<Class> list = new ArrayList<Class>();
        try {
            stat = connection.createStatement();
            ResultSet rs = stat.executeQuery("select * from class");
            while (rs.next()) {
                Class c = new Class(rs.getInt(1), rs.getInt(3), rs.getString(2));
                list.add(c);
            }
            stat.close();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public List<Teacher> getTeachers() {
        List<Teacher> list = new ArrayList<Teacher>();
        try {
            stat = connection.createStatement();
            ResultSet rs = stat.executeQuery("select * from teacher");
            while (rs.next()) {
                Teacher t = new Teacher(rs.getInt(1), rs.getString(2));
                list.add(t);
            }
            stat.close();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public List<Teacher> getTeachersForRoom(String roomNumber) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String myDateString = dateFormat.format(new java.util.Date());
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String myTimeString = timeFormat.format(new java.util.Date());
        List<Teacher> list = new ArrayList<Teacher>();
        try {
            stat = connection.createStatement();
            p = connection.prepareStatement("SELECT teacher.teacherName, teacher.consultation, room.roomNumber FROM `teacher` \n" +
                    "JOIN room on room.linkIdRoom = teacher.roomId\n" +
                    "where room.roomNumber = ?");
            p.setString(1, roomNumber);
            ResultSet rs = p.executeQuery();
            while (rs.next()) {
                Teacher t = new Teacher(rs.getString(1), rs.getString(2).split(" ")[0] + rs.getString(2).split(" ")[1] + rs.getString(2).split(" ")[2], rs.getString(2).split(" ")[3], rs.getString(3));
                t.setPicturePath();
                t.setCurrentLocation("Pause / Nicht im Haus");
                list.add(t);
            }
            p.close();
            rs.close();
            for (Teacher te : list) {
                p = connection.prepareStatement("SELECT teacherLocation.roomNumber FROM `teacher` \n" +
                        "JOIN classLessons on classLessons.linkIdTeacherArray like CONCAT(teacher.linkIdTeacher, ';')\n" +
                        "JOIN room as teacherLocation on teacherLocation.linkIdRoom = classLessons.linkIdRoom\n" +
                        "where teacher.teacherName = ?\n" +
                        "AND classLessons.startTime <= ?\n" +
                        "AND classLessons.endTime >= ?\n" +
                        "AND classLessons.lessonDate = ?");
                p.setString(1, te.getName());
                p.setString(2, myTimeString);
                p.setString(3, myTimeString);
                p.setString(4, myDateString);
                ResultSet rs1 = p.executeQuery();
                if (rs1.next()) {
                    te.setCurrentLocation("Aktuell im Raum " + rs1.getString(1));
                }
                rs1.close();
                p.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public String getClassName(int classIdDB) {
        String result = "null";
        try {
            p = connection.prepareStatement("select className from `class`\n" +
                    "     where linkIdClass = ?");
            p.setInt(1, classIdDB);
            ResultSet res = p.executeQuery();
            if (res.next()) {
                result = res.getString(1);
            }
            p.close();


        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public void updateClass(int aInt, int mainTeacherId) {
        try {
            p = connection.prepareStatement("UPDATE class SET linkIdTeacher=? WHERE linkIdClass=?");
            p.setInt(1, mainTeacherId);
            p.setInt(2, aInt);
            p.execute();
            p.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deleteActualSchedule() {
        try {
            stat = connection.createStatement();
            stat.executeUpdate("delete from classLessons");
            stat.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void delete(String tabletId) {
        try {
            stat = connection.createStatement();
            stat.executeUpdate("delete from tablet where tabletId = '" + tabletId + "'");
            stat.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void dropRooms() {
        try {
            stat = connection.createStatement();
            stat.executeUpdate("delete from room where linkIdRoom != 0");
            stat.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<ClassLocationDTO> getCurrentLocations() {
        List<ClassLocationDTO> list = new ArrayList<>();
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String myDateString = dateFormat.format(new java.util.Date());
            DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            String myTimeString = timeFormat.format(new java.util.Date());
            p = connection.prepareStatement("SELECT c.className, r.roomNumber FROM classLessons as cl \n" +
                    "LEFT JOIN class c ON cl.linkIdClass = c.linkIdClass \n" +
                    "LEFT JOIN room r ON cl.linkIdRoom = r.linkIdRoom \n" +
                    "WHERE cl.startTime <= ? \n" +
                    "AND cl.endTime >= ? \n" +
                    "AND cl.lessonDate = ?");
            p.setString(1, myTimeString);
            p.setString(2, myTimeString);
            p.setString(3, myDateString);
            ResultSet rs1 = p.executeQuery();
            while (rs1.next()) {
                list.add(new ClassLocationDTO(rs1.getString(1), rs1.getString(2)));
            }
            rs1.close();
            p.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
}
    /*public File getPdfByTabletId(String tabletId) {
        EventTablet eventTablet = new EventTablet();
        String getEventTabletQuery = "select * from eventTablet where tabletId like '" + tabletId + "'";
        String pdfName = "";
        String getPdfNameQuery = "select pdfName from eventTablet where tabletId like  '" + tabletId + "'";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(getPdfNameQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) { pdfName = resultSet.getString(1); }
            File file = new File(pdfName);
            PreparedStatement ps = connection.prepareStatement(getEventTabletQuery);
            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                eventTablet.setTabletId(rs.getString("tabletId"));
                eventTablet.setTitle(rs.getString("title"));
                eventTablet.setDescription(rs.getString("description"));
                eventTablet.setTemplate(rs.getString("template"));
                eventTablet.setEventName(rs.getString("eventName"));
                file = writeByteArrayToFile(rs.getBytes("pdf"), pdfName);
                eventTablet.setPdf(file);
            }
            return file;
        }catch (SQLException ex){ System.out.println("Get event tablet by tabletId error: " + ex.getMessage()); }
        return null;
    }*/
    /*public File writeByteArrayToFile(byte[] bArray, String pdfName){
        String FILE_PATH = pdfName;
        File file = new File(FILE_PATH);
        try{
            OutputStream os = new FileOutputStream(file);
            os.write(bArray);
            os.close();
        }catch (Exception exception){ System.out.println("Write Byte Array To File - Error " + exception.getMessage()); }
        return file;
    }*/
    /*public File getPdfFileFromDatabase(String pdfName){
        String getPdfFileQuery = "select pdf from eventTablet where pdfName = '" + pdfName + "'";
        File file = new File(pdfName);
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(getPdfFileQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){ file = writeByteArrayToFile(resultSet.getBytes("pdf"), pdfName); }
        }catch (SQLException exception){ System.out.println("Get pdf file from database error: " + exception.getMessage()); }
        return file;
    }*/
    /*public void addPdfFileToDatabase(File file, String pdfName){
        String addPdfFileQuery = "update eventTablet set pdf = ? where pdfName = '" + pdfName + "'";
        byte[] bArray = readFileToByteArray(file);
        Blob blob;
        try{
            blob = new SerialBlob(bArray);
            PreparedStatement preparedStatement = connection.prepareStatement(addPdfFileQuery);
            preparedStatement.setBlob(1, blob);
            preparedStatement.executeUpdate();
        }catch (SQLException exception){ System.out.println("add Pdf File to database error:" + exception.getMessage()); }
    }*/
