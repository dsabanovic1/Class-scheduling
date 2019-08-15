package models;

import database.Database;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Student{

    @JsonProperty
    String student_id;
    @JsonProperty
    String user_id;
    @JsonProperty
    Long created;
    @JsonProperty
    Long modified;

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public static List<Student> getStudents() throws  ClassNotFoundException, SQLException {

        PreparedStatement ps = Database.prepareStatement("SELECT * FROM school.student");
        ResultSet rs = ps.executeQuery();

        List<Student> students= new ArrayList<>();

        while(rs.next()){
            Student student = new Student();
            student.setStudent_id(rs.getString("student_id"));
            student.setUser_id("user_id");
            student.setCreated(rs.getLong("created"));
            student.setModified(rs.getLong("modified"));
            students.add(student);

        }
        return students;
    }

    public static Student addStudent(Student student)throws ClassNotFoundException, SQLException, JsonProcessingException
    {
        PreparedStatement ps = Database.prepareStatement("INSERT INTO school.student (student_id,user_id,created,modified) VALUES(?,?,?,?)");
        String id = Database.generatorId(5);
        ps.setString(1, id);
        ps.setString(2,student.getUser_id());
        ps.setLong(3,System.currentTimeMillis());
        ps.setLong(4,System.currentTimeMillis());
        ps.executeUpdate();
        return student;
    }
    public static Student getStudent(String student_id) throws ClassNotFoundException, SQLException{

        PreparedStatement ps = Database.prepareStatement("SELECT * FROM school.student WHERE student_id=?");
        ps.setString(1,student_id);
        ResultSet rs = ps.executeQuery();

        Student student = new Student();

        while(rs.next()){
            student.setStudent_id(rs.getString("student_id"));
            student.setUser_id(rs.getString("user_id"));
            student.setCreated(rs.getLong("created"));
            student.setModified(rs.getLong("modified"));

        }
        return student;
    }

    public static void updateStudent(String student_id, Student student) throws ClassNotFoundException, SQLException, IOException {

        PreparedStatement ps = Database.prepareStatement("UPDATE school.student SET username = ? WHERE student_id = ? ");
        ps.setString(2,student_id);
        ps.executeUpdate();
    }
    public static void deleteStudent(String student_id) throws ClassNotFoundException,SQLException{

        PreparedStatement ps = Database.prepareStatement("DELETE FROM school.student WHERE student_id = ?");
        ps.setString(1,student_id);
        ps.executeUpdate();
    }
}
