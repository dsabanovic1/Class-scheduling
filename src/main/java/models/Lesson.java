package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import database.Database;

import javax.mail.MessagingException;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Lesson {
    @JsonProperty
    String lesson_id;
    @JsonProperty
    String teacher_id;
    @JsonProperty
    String parent_id;
    @JsonProperty
    String lesson_name;
    @JsonProperty
    Long starttime;
    @JsonProperty
    Long endtime;
    @JsonProperty
    Long created;
    @JsonProperty
    Long modified;

    public String getLesson_id() {
        return lesson_id;
    }

    public void setLesson_id(String lesson_id) {
        this.lesson_id = lesson_id;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public Long getStarttime() {
        return starttime;
    }

    public void setStarttime(Long starttime) {
        this.starttime = starttime;
    }

    public Long getEndtime() {
        return endtime;
    }

    public void setEndtime(Long endtime) {
        this.endtime = endtime;
    }

    public String getLesson_name() {
        return lesson_name;
    }

    public void setLesson_name(String lesson_name) {
        this.lesson_name = lesson_name;
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

    public static List<Lesson> getLessons() throws  ClassNotFoundException, SQLException {

        PreparedStatement ps = Database.prepareStatement("SELECT * FROM school.lessons");
        ResultSet rs = ps.executeQuery();

        List<Lesson> lessons= new ArrayList<>();

        while(rs.next()){
            Lesson lesson = new Lesson();
            lesson.setLesson_id(rs.getString("lesson_id"));
            lesson.setLesson_name(rs.getString("lesson_name"));
            lesson.setTeacher_id(rs.getString("teacher_id"));
            lesson.setParent_id(rs.getString("parent_id"));
            lesson.setStarttime(rs.getLong("starttime"));
            lesson.setEndtime(rs.getLong("endtime"));
            lesson.setCreated(System.currentTimeMillis());
            lesson.setModified(System.currentTimeMillis());

            lessons.add(lesson);

        }
        return lessons;
    }
    public static Lesson addLesson(Lesson lesson)throws ClassNotFoundException, SQLException, MessagingException,IOException, ValidationException
    {
        if(lesson.endtime-lesson.starttime != 3600000f) throw new ValidationException("Duration of one lesson have to be one hour");
        PreparedStatement ps = Database.prepareStatement("INSERT INTO school.lesson (lesson_id,lesson_name,teacher_id,parent_id,starttime,endtime,created,modified) VALUES(?,?,?,?,?,?,?,?)");
        //String id = database.generatorId(5);
        if(Teacher.getTeacher(lesson.getTeacher_id())==null)throw new ValidationException("that teacher doesnt exist.");
        if(Parent.getParent(lesson.getParent_id())==null) throw new ValidationException("That parent doesnt exist.");
        if(lesson.endtime<=lesson.starttime) throw new ValidationException("Improperly set lesson start and end time.");
        Parent.hasParentCredits(lesson.getParent_id());
        Teacher.updateAvailabilityTeacher(lesson.getTeacher_id(),lesson);
        ps.setString(1,lesson.getLesson_id());
        ps.setString(2,lesson.getLesson_name());
        ps.setString(3,lesson.getTeacher_id());
        ps.setString(4,lesson.getParent_id());
        ps.setLong(5,lesson.getStarttime());
        ps.setLong(6,lesson.getEndtime());
        ps.setLong(7,System.currentTimeMillis());
        ps.setLong(8,System.currentTimeMillis());
        ps.executeUpdate();
        ContactEmail contactEmail = new ContactEmail();
        contactEmail.setName("Approve");
        contactEmail.setMessage("You successfully reserved lesson from : "+ convertTime(lesson.getStarttime())+" to: "+ convertTime(lesson.getEndtime()));
        contactEmail.setToEmail(User.getUser(Parent.getParent(lesson.getParent_id()).getUser_id()).getEmail());
        contactEmail.setMailBody("mail body");
        //EmailService.sendContactEmail(contactEmail);
        return lesson;
    }

    public static Lesson getLesson(String id) throws ClassNotFoundException, SQLException{

        PreparedStatement ps = Database.prepareStatement("SELECT * FROM school.lesson WHERE lesson_id=?");
        ps.setString(1,id);
        ResultSet rs = ps.executeQuery();
        Lesson lesson = new Lesson();

        while(rs.next()){
            lesson.setLesson_id(rs.getString("lesson_id"));
            lesson.setLesson_name(rs.getString("lesson_name"));
            lesson.setTeacher_id(rs.getString("teacher_id"));
            lesson.setParent_id(rs.getString("parent_id"));
            lesson.setStarttime(rs.getLong("starttime"));
            lesson.setEndtime(rs.getLong("endtime"));
            lesson.setCreated(rs.getLong("created"));
            lesson.setModified(rs.getLong("modified"));
        }
        return lesson;
    }

    public static List<Lesson> getLessonsByTeacher(String id) throws ClassNotFoundException, SQLException{

        PreparedStatement ps = Database.prepareStatement("SELECT * FROM school.lesson WHERE teacher_id=?");
        ps.setString(1,id);
        ResultSet rs = ps.executeQuery();
        List<Lesson> lessons = new ArrayList<>();

        while(rs.next()){
            Lesson lesson = new Lesson();
            lesson.setLesson_id(rs.getString("lesson_id"));
            lesson.setLesson_name(rs.getString("lesson_name"));
            lesson.setTeacher_id(rs.getString("teacher_id"));
            lesson.setParent_id(rs.getString("parent_id"));
            lesson.setStarttime(rs.getLong("starttime"));
            lesson.setEndtime(rs.getLong("endtime"));
            lesson.setCreated(rs.getLong("created"));
            lesson.setModified(rs.getLong("modified"));
            lessons.add(lesson);
        }
        return lessons;
    }

    public static void deleteLesson(String lesson_id) throws ClassNotFoundException,SQLException{

        PreparedStatement ps = Database.prepareStatement("DELETE FROM school.lesson WHERE lesson_id= ?");
        ps.setString(1,lesson_id);
        ps.executeUpdate();
    }
    public static String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return format.format(date);
    }

    public static List<Lesson> getLessonsByParent(String parent_id) throws  ClassNotFoundException, SQLException {

        PreparedStatement ps = Database.prepareStatement("SELECT * FROM school.lessons WHERE parent_id =?");
        ps.setString(1,parent_id);
        ResultSet rs = ps.executeQuery();

        List<Lesson> lessons= new ArrayList<>();

        while(rs.next()){
            Lesson lesson = new Lesson();
            lesson.setLesson_id(rs.getString("lesson_id"));
            lesson.setLesson_name(rs.getString("lesson_name"));
            lesson.setTeacher_id(rs.getString("teacher_id"));
            lesson.setParent_id(rs.getString("parent_id"));
            lesson.setStarttime(rs.getLong("starttime"));
            lesson.setEndtime(rs.getLong("endtime"));
            lesson.setCreated(System.currentTimeMillis());
            lesson.setModified(System.currentTimeMillis());

            lessons.add(lesson);

        }
        return lessons;
    }
}
