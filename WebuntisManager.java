package WebUntis;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import db.DatabaseRepository;
import entity.RoomName;
import entity.Teacher;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
/**
 * @author Sabrina, Belmin
 */
public class WebuntisManager {
    private List<RoomInfo> roomInfo = new ArrayList<RoomInfo>();
    private String user = "test";
    private String password = "test1";
    private String session = "";
    private String schoolName = "";
    private OkHttpClient client = null;
    private Request request = null;
    private Response response = null;

    public void init() {
        if (login()) {
            initializeDatabase();
        }
    }

    public boolean login() {
        boolean ret = false;
        try {
            client = new com.squareup.okhttp.OkHttpClient();

            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            client.setCookieHandler(cookieManager);

            request = new Request.Builder()
                    .url("https://mese.webuntis.com/WebUntis/?school=htbla%20linz%20leonding")
                    .get()
                    .build();
            try {
                response = client.newCall(request).execute();
            } catch (IOException ex) {
                Logger.getLogger(WebuntisManager.class.getName()).log(Level.SEVERE, null, ex);
            }

            session = cookieManager.getCookieStore().getCookies().get(0).toString();
            schoolName = cookieManager.getCookieStore().getCookies().get(1).toString();

            RequestBody body = new FormEncodingBuilder()
                    .add("school", "htbla linz leonding")
                    .add("j_username", user)
                    .add("j_password", password)
                    .add("token", "")
                    .build();

            request = new Request.Builder()
                    .url("https://mese.webuntis.com/WebUntis/j_spring_security_check")
                    .addHeader("accept", "application/json")
                    .addHeader("connection", "keep-alive")
                    .addHeader("cookie", schoolName + ";" + session)
                    .addHeader("host", "mese.webuntis.com")
                    .addHeader("origin", "https://mese.webuntis.com")
                    .addHeader("referer", "https://mese.webuntis.com/WebUntis/index.do")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("content-type", "application/x-www-form-urlencoded")
                    .addHeader("cache-control", "no-cache")
                    .post(body)
                    .build();
            try {
                response = client.newCall(request).execute();
                ret = response.body().string().contains("SUCCESS");
            } catch (IOException ex) {
                Logger.getLogger(WebuntisManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } finally {
            try {
                response.body().close();
            } catch (IOException ex) {
                Logger.getLogger(WebuntisManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }

    private JSONArray getJsonArray(String type) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String myDateString = dateFormat.format(new Date());
        JSONArray arr = null;
        try {
            request = new Request.Builder()
                    .url("https://mese.webuntis.com/WebUntis/api/public/timetable/weekly/pageconfig?type=" + type + "&date=" + myDateString)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36")
                    .addHeader("referer", "https://mese.webuntis.com/WebUntis/index.do")
                    .addHeader("accept-encoding", "gzip, deflate, br")
                    .addHeader("accept-language", "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("cookie", schoolName + ";" + session)
                    .build();
            try {
                response = client.newCall(request).execute();
            } catch (IOException ex) {
                Logger.getLogger(WebuntisManager.class.getName()).log(Level.SEVERE, null, ex);
            }

            JSONObject obj;
            try {
                obj = new JSONObject(response.body().string());
                arr = obj.getJSONObject("data").getJSONArray("elements");
            } catch (IOException ex) {
                Logger.getLogger(WebuntisManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } finally {
            try {
                response.body().close();
            } catch (IOException ex) {
                Logger.getLogger(WebuntisManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return arr;
    }

    public void getSchedule(String id, String type, String roomNumber) {
        if (login()) {
            try {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String myDateString = dateFormat.format(new java.util.Date());

                request = new Request.Builder()
                        .url("https://mese.webuntis.com/WebUntis/api/public/timetable/weekly/data?elementType=" + type + "&elementId=" + id + "&date=" + myDateString + "&formatId=1")
                        .get()
                        .addHeader("accept", "application/json")
                        .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36")
                        .addHeader("referer", "https://mese.webuntis.com/WebUntis/index.do")
                        .addHeader("accept-encoding", "gzip, deflate, br")
                        .addHeader("accept-language", "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
                        .addHeader("cookie", schoolName)
                        .addHeader("cookie", session)
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "6c20db3c-b2ad-f5e1-71bb-fb0fade39be7")
                        .build();

                Response response = client.newCall(request).execute();
                JSONArray arr = new JSONObject(response.body().string()).getJSONObject("data").getJSONObject("result").getJSONObject("data").getJSONObject("elementPeriods").getJSONArray(id);
                roomInfo.clear();

                for (int i = 0; i < arr.length(); i++) {
                    RoomInfo r = new RoomInfo(arr.getJSONObject(i).getInt("date"), arr.getJSONObject(i).getInt("startTime"), arr.getJSONObject(i).getInt("endTime"), arr.getJSONObject(i).optString("studentGroup", "free"), roomNumber);
                    r.setLinkIdRoom(Integer.parseInt(id));
                    int periodId = arr.getJSONObject(i).getInt("id");
                    r.setLessonInfo(arr.getJSONObject(i).getString("lessonText"));
                    r.setPeriodId(periodId);
                    JSONArray elements = arr.getJSONObject(i).getJSONArray("elements");
                    for (int j = 0; j < elements.length(); j++) {
                        if (elements.getJSONObject(j).getInt("type") == 1) {
                            r.setLinkIdClass(elements.getJSONObject(j).getInt("id"));
                        }
                    }
                    if (type.equals("4")) {
                        String cellState = arr.getJSONObject(i).getString("cellState");
                        if ("EXAM".equals(cellState)) {
                            r.setIsExam(true);
                        } else if ("SUBSTITUTION".equals(cellState)) {
                            r.setIsSubstitution(true);
                        } else if ("CONFIRMED".equals(cellState)) {
                            r.setIsEvent(true);
                        }

                    }
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    myDateString = r.getDate().format(formatter);

                    request = new Request.Builder()
                            .url("https://mese.webuntis.com/WebUntis/api/public/period/info?date=" + myDateString + "&starttime=" + r.getStartTime() + "&endtime=" + r.getEndTime() + "&elemid=" + id + "&elemtype=4&ttFmtId=1&selectedPeriodId=" + r.getPeriodId())
                            .get()
                            .addHeader("accept", "application/json")
                            .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36")
                            .addHeader("referer", "https://mese.webuntis.com/WebUntis/index.do")
                            .addHeader("accept-encoding", "gzip, deflate, br")
                            .addHeader("accept-language", "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
                            .addHeader("cookie", schoolName)
                            .addHeader("cookie", session)
                            .addHeader("cache-control", "no-cache")
                            .addHeader("postman-token", "6c20db3c-b2ad-f5e1-71bb-fb0fade39be7")
                            .build();

                    String linkIdTeacher = "";
                    response = client.newCall(request).execute();
                    JSONObject objectTeacher = new JSONObject(response.body().string());
                    JSONArray arrTeacher = objectTeacher.getJSONObject("data").getJSONArray("blocks");

                    JSONArray activityType = arrTeacher.getJSONArray(0);
                    String eventType = activityType.getJSONObject(0).getString("activityTypeName");
                    if (r.getLessonInfo().isEmpty()) {
                        r.setLessonInfo(eventType);
                    } else {
                        r.setLessonInfo(r.getLessonInfo());
                    }
                    if ("Externe Veranst.".equals(eventType) || "Interne Veranst.".equals(eventType)) {
                        r.setIsEvent(true);
                    } else {
                        r.setIsEvent(false);
                    }

                    if (arrTeacher.length() > 0) {
                        JSONArray string = arrTeacher.getJSONArray(0);
                        JSONArray teacher = string.getJSONObject(0).getJSONArray("periods").getJSONObject(0).getJSONArray("teachers");

                        for (int j = 0; j < teacher.length(); j++) {
                            linkIdTeacher = linkIdTeacher + teacher.getJSONObject(j).getInt("id") + ";";
                        }
                        String subject = string.getJSONObject(0).getString("subjectName");
                        r.setSubject(subject);
                    }
                    r.setTeacher(linkIdTeacher);
                    roomInfo.add(r);
                }
            } catch (IOException | ParseException ex) {
                Logger.getLogger(WebuntisManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getFullNameTeacher(String teacherShortName) {
        if (teacherShortName.contains("\\d+")) {
            return teacherShortName;
        }
        JSONArray arr = getJsonArray("2");
        for (int i = 0; i < arr.length(); i++) {
            String s = arr.getJSONObject(i).getString("forename") + " " + arr.getJSONObject(i).getString("longName") + ";" + arr.getJSONObject(i).getString("displayname");
            if (s.contains(teacherShortName)) {
                return s.split(";")[0];
            }
        }
        return "";
    }

    public boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public void actualiseSchedules() {
        try {
            List<RoomName> listRooms = DatabaseRepository.getInstance().getRooms();
            DatabaseRepository.getInstance().deleteActualSchedule();
            for (int i = 0; i < listRooms.size(); i++) {
                getSchedule(String.valueOf(listRooms.get(i).getRoom()), "4", listRooms.get(i).getName());
                DatabaseRepository.getInstance().addLessonsClass(roomInfo);
            }
            DatabaseRepository.getInstance().addEventsFromWebuntis();
        } catch (SQLException ex) {
            Logger.getLogger(WebuntisManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initializeDatabase() {
        try {
            JSONArray arr = getJsonArray("4");
            JSONArray arrayClass = getJsonArray("1");
            final JSONArray arrayTeacher = getJsonArray("2");
            List<Teacher> teacherList = HTLWebsiteManager.getTeacherArray();
            Map<String, Integer> rooms = new HashMap<String, Integer>();
            int roomId = 0;
            for (int i = 1; i < arr.length(); i++) {
                RoomIdToLink r = new RoomIdToLink(arr.getJSONObject(i).getInt("id"), arr.getJSONObject(i).getString("displayname"), arr.getJSONObject(i).getString("longName"));
                DatabaseRepository.getInstance().addRoom(r);
            }

            for (int i = 0; i < arrayClass.length(); i++) {
                DatabaseRepository.getInstance().addClass(arrayClass.getJSONObject(i).getInt("id"), arrayClass.getJSONObject(i).getString("name"));
            }
            List<RoomName> listRooms =  DatabaseRepository.getInstance().getRooms();
            for (RoomName listRoom : listRooms) {
                getSchedule(String.valueOf(listRoom.getRoom()), "4", listRoom.getName());
                DatabaseRepository.getInstance().addLessonsClass(roomInfo);
            }
            for (int i = 1; i < arr.length(); i++) {
                RoomIdToLink r = new RoomIdToLink(arr.getJSONObject(i).getInt("id"), arr.getJSONObject(i).getString("displayname"), arr.getJSONObject(i).getString("longName"));
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String myDateString = dateFormat.format(new Date());

                request = new Request.Builder()
                        .url("https://mese.webuntis.com/WebUntis/api/public/timetable/weekly/data?elementType=4&elementId=" + r.getLinkId() + "&date=" + myDateString + "&formatId=1&filter.roomGroupId=-1")
                        .get()
                        .addHeader("accept", "application/json")
                        .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36")
                        .addHeader("referer", "https://mese.webuntis.com/WebUntis/index.do")
                        .addHeader("accept-encoding", "gzip, deflate, br")
                        .addHeader("accept-language", "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
                        .addHeader("cookie", schoolName)
                        .addHeader("cookie", session)
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "6c20db3c-b2ad-f5e1-71bb-fb0fade39be7")
                        .build();

                Response response = client.newCall(request).execute();
                JSONArray array = new JSONObject(response.body().string()).getJSONObject("data").getJSONObject("result").getJSONObject("data").getJSONObject("elementPeriods").getJSONArray(String.valueOf(r.getLinkId()));
                roomInfo.clear();

                for (int ind = 0; ind < array.length(); ind++) {
                    //RoomInfo r = new RoomInfo(arr.getJSONObject(i).getInt("date"), arr.getJSONObject(i).getInt("startTime"), arr.getJSONObject(i).getInt("endTime"),arr.getJSONObject(i).optString("studentGroup", "free"), roomNumber);
                    int periodId = array.getJSONObject(ind).getInt("id");

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    myDateString = LocalDate.now().format(formatter);

                    request = new Request.Builder()
                            .url("https://mese.webuntis.com/WebUntis/api/public/period/info?date=" + myDateString + "&starttime=" + array.getJSONObject(ind).getInt("startTime") + "&endtime=" + array.getJSONObject(ind).getInt("endTime") + "&elemid=" + r.getLinkId() + "&elemtype=4&ttFmtId=1&selectedPeriodId=" + periodId)
                            .get()
                            .addHeader("accept", "application/json")
                            .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36")
                            .addHeader("referer", "https://mese.webuntis.com/WebUntis/index.do")
                            .addHeader("accept-encoding", "gzip, deflate, br")
                            .addHeader("accept-language", "de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7")
                            .addHeader("cookie", schoolName)
                            .addHeader("cookie", session)
                            .addHeader("cache-control", "no-cache")
                            .addHeader("postman-token", "6c20db3c-b2ad-f5e1-71bb-fb0fade39be7")
                            .build();

                    response = client.newCall(request).execute();
                    System.out.println("response: " + response);
                    JSONObject objectTeacher = new JSONObject(response.body().string());
                    JSONArray arrTeacher = objectTeacher.getJSONObject("data").getJSONArray("blocks");
                    if (arrTeacher.length() > 0) {
                        JSONArray string = arrTeacher.getJSONArray(0);
                        String mainTeacher = string.getJSONObject(0).getString("klasseNameLong");
                        for (int teacherInd = 0; teacherInd < arrayTeacher.length(); teacherInd++) {
                            if (arrayTeacher.getJSONObject(teacherInd).getString("longName").equals(mainTeacher)) {
                                r.setLinkIdTeacher(arrayTeacher.getJSONObject(teacherInd).getInt("id"));
                                teacherInd = arrayTeacher.length();
                            }
                        }
                        ind = array.length();
                    }
                }

                //check if room is edv room, if false, set right class
                if (r.getRoomName().contains("EDV")) {
                    r.setRoomType("edv");
                } else if (r.getRoomName().contains("Werk")) {
                    r.setRoomType("werk");
                }
                //else if (r.getRoomDisplayId().equals(r.getRoomName())){
                int classIdDB = DatabaseRepository.getInstance().getClassId(r.getLinkId());
                if (classIdDB != -1) {
                    String className = DatabaseRepository.getInstance().getClassName(classIdDB);
                    if (!className.equals("null")) {
                        r.setLinkIdClass(classIdDB);
                        r.setRoomName(className);
                        r.setRoomType("class");
                    }
                }
                DatabaseRepository.getInstance().updateRoom(r);
            }

            for (Teacher teacher : teacherList) {
                roomId = 0;
                for (int j = 0; j < arrayTeacher.length(); j++) {
                    if ((teacher.getName().toLowerCase().split(" ")[0] + " " + teacher.getName().toLowerCase().split(" ")[1]).equals(arrayTeacher.getJSONObject(j).getString("longName").toLowerCase() + " " + arrayTeacher.getJSONObject(j).getString("forename").toLowerCase())) {
                        teacher.setLinkId(arrayTeacher.getJSONObject(j).getInt("id"));
                        j = arrayTeacher.length();
                        if (!rooms.containsKey(teacher.getRoomNumber())) {
                            roomId = DatabaseRepository.getInstance().addTeacherRoom(teacher.getRoomNumber()) + 1;
                            rooms.put(teacher.getRoomNumber(), roomId);
                        }
                        if (roomId == 0) {
                            roomId = rooms.get(teacher.getRoomNumber());
                        }
                        teacher.setRoomId(roomId);
                        DatabaseRepository.getInstance().addTeacher(teacher);
                    }
                }
            }
            for (int i = 0; i < arrayClass.length(); i++) {
                int mainTeacherId = 0;
                for (int j = 0; j < teacherList.size(); j++) {
                    if (teacherList.get(j).getName().split(" ")[0].toLowerCase().equals(arrayClass.getJSONObject(i).getString("longName").toLowerCase())) {
                        mainTeacherId = teacherList.get(j).getLinkId();
                        j = teacherList.size();
                    }
                }
                DatabaseRepository.getInstance().updateClass(arrayClass.getJSONObject(i).getInt("id"), mainTeacherId);
            }
        } catch (SQLException | IOException ex) {
            Logger.getLogger(WebuntisManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
