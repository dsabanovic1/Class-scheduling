package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import database.Database;

import javax.xml.bind.ValidationException;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Teacher{
    @JsonProperty
    String teacher_id;
    @JsonProperty
    String user_id;
    @JsonProperty
    ArrayList<Map<String,Object>> available;
    @JsonProperty
    Long created;
    @JsonProperty
    Long modified;

    public String getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        this.teacher_id = teacher_id;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public ArrayList<Map<String, Object>> getAvailable() {
        return available;
    }

    public void setAvailable(ArrayList<Map<String, Object>> available) {
        this.available = available;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public static List<Teacher> getTeachers() throws  ClassNotFoundException, SQLException {

        PreparedStatement ps = Database.prepareStatement("SELECT * FROM school.teacher");
        ResultSet rs = ps.executeQuery();

        List<Teacher> teachers = new ArrayList<>();

        while(rs.next()){
            Teacher teacher = new Teacher();
            teacher.setTeacher_id(rs.getString("teacher_id"));
            teacher.setCreated(rs.getLong("created"));
            teacher.setModified(rs.getLong("modified"));
            teacher.setUser_id(rs.getString("user_id"));

            Array arrayField = rs.getArray("available");
            Object[] arrayFieldValue = (Object[]) arrayField.getArray();
            ArrayList<Map<String,Object>> lista = new ArrayList<>();
            for (Object o : arrayFieldValue) {

                Map<String, Object> objectValue = (Map<String, Object>) o;
                Object objectField = objectValue.get("fromtime");
                lista.add(objectValue);
            }
           teacher.setAvailable(lista);

            teachers.add(teacher);

        }
        return teachers;
    }

    public static Teacher addTeacher(Teacher teacher)throws ClassNotFoundException, SQLException, JsonProcessingException,ValidationException
    {
        if(teacherExists(teacher.getUser_id())){
            throw new ValidationException("Teacher already exists");
        }
        if(!availabilityCheck(teacher)){
            throw new ValidationException("Unproperly set teacher available time.");
        }

        if(User.getUser(teacher.getUser_id())==null) throw new ValidationException("User doesnt exist");


        PreparedStatement ps = Database.prepareStatement("INSERT INTO school.teacher (teacher_id,created,modified,user_id,available) VALUES(?,?,?,?,?)");
        //String id = database.generatorId(5);
        ps.setString(1, teacher.getTeacher_id());
        ps.setLong(2,System.currentTimeMillis());
        ps.setLong(3,System.currentTimeMillis());
        ps.setString(4,teacher.getUser_id());
        ps.setArray(5,Database.getConnection().createArrayOf("object", teacher.getAvailable().toArray()));
        ps.executeUpdate();
        return teacher;
    }
    public static Teacher getTeacher(String teacher_id) throws ClassNotFoundException, SQLException{

        PreparedStatement ps = Database.prepareStatement("SELECT * FROM school.teacher WHERE teacher_id=?");
        ps.setString(1,teacher_id);
        ResultSet rs = ps.executeQuery();

        Teacher teacher = new Teacher();

        while(rs.next()) {
            teacher.setTeacher_id(rs.getString("teacher_id"));
            teacher.setCreated(rs.getLong("created"));
            teacher.setModified(rs.getLong("modified"));
            teacher.setUser_id(rs.getString("user_id"));

            Array arrayField = rs.getArray("available");
            Object[] arrayFieldValue = (Object[]) arrayField.getArray();
            ArrayList<Map<String,Object>> lista = new ArrayList<>();
            for (Object o : arrayFieldValue) {

                Map<String, Object> objectValue = (Map<String, Object>) o;
                lista.add(objectValue);
            }
            teacher.setAvailable(lista);
        }    return teacher;
    }
   /* public static Teacher getTeacherByUserId(String user_id) throws ClassNotFoundException, SQLException{

        PreparedStatement ps = Database.prepareStatement("SELECT * FROM school.teacher WHERE user_id=?");
        ps.setString(1,user_id);
        ResultSet rs = ps.executeQuery();

        Teacher teacher = new Teacher();

        while(rs.next()) {
            teacher.setTeacher_id(rs.getString("teacher_id"));
            teacher.setCreated(rs.getLong("created"));
            teacher.setModified(rs.getLong("modified"));
            teacher.setUser_id(rs.getString("user_id"));

            Array arrayField = rs.getArray("available");
            Object[] arrayFieldValue = (Object[]) arrayField.getArray();
            ArrayList<Map<String,Object>> lista = new ArrayList<>();
            for (Object o : arrayFieldValue) {

                Map<String, Object> objectValue = (Map<String, Object>) o;
                Object objectField = objectValue.get("fromtime");
                lista.add(objectValue);
            }
            teacher.setAvailable(lista);
        }    return teacher;
    }
*/
    public static void updateTeacher(String teacher_id, Teacher teacher) throws ClassNotFoundException, SQLException {

        PreparedStatement ps = Database.prepareStatement("UPDATE school.teacher SET available = ? WHERE teacher_id = ? ");
        ps.setArray(1,Database.getConnection().createArrayOf("object", teacher.getAvailable().toArray()));
        ps.setString(2,teacher_id);
        ps.executeUpdate();
    }

    public static void updateAvailabilityTeacher(String teacher_id, Lesson lesson) throws ClassNotFoundException,
            SQLException, ValidationException{

        Teacher teacher = getTeacher(teacher_id);
        ArrayList<Map<String,Object>> availables = teacher.getAvailable();

        Optional<Map<String, Object>> availableSlot = availables.stream()
                .filter(map -> lesson.getStarttime() >= convertToLong(map.get("fromtime"))
                        && lesson.getEndtime() <= convertToLong(map.get("totime"))).findFirst();

        if (!availableSlot.isPresent()) {
            throw new ValidationException("Teacher is not available for required time.");
        }

        Long startTime = convertToLong(availableSlot.get().get("fromtime"));
        Long endTime = convertToLong(availableSlot.get().get("totime"));

        availables.removeIf(map -> convertToLong(map.get("fromtime")).equals(startTime)
                && convertToLong(map.get("totime")).equals(endTime));

        HashMap<String,Object> map = new HashMap<>();
        map.put("fromtime", startTime);
        map.put("totime", lesson.starttime);
        availables.add(map);

        HashMap<String,Object> map1 = new HashMap<>();
        map1.put("fromtime",lesson.endtime);
        map1.put("totime",endTime);
        availables.add(map1);

        teacher.setAvailable(availables);

        updateTeacher(teacher.getTeacher_id(), teacher);

    }

    public static void deleteTeacher(String teacher_id) throws ClassNotFoundException,SQLException{

        PreparedStatement ps = Database.prepareStatement("DELETE FROM school.teacher WHERE teacher_id= ?");
        ps.setString(1,teacher_id);
        ps.executeUpdate();
    }

    public static boolean teacherExists(String user_id) throws SQLException, ClassNotFoundException {
        PreparedStatement preparedStatement = Database.prepareStatement("SELECT * FROM school.teacher WHERE user_id = ? LIMIT 1");
        preparedStatement.setString(1, user_id);
        ResultSet rs = preparedStatement.executeQuery();

        return rs.next();
    }
    public static boolean availabilityCheck(Teacher teacher){
        for (Map<String,Object> map: teacher.getAvailable()) {

            if(convertToLong(map.get("fromtime"))>convertToLong(map.get("totime"))) return false;
        }
        return true;
    }
    public static Long convertToLong(Object o){
        String stringToConvert = String.valueOf(o);
        return Long.parseLong(stringToConvert);

    }

}
