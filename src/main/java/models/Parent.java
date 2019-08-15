package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import database.Database;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Parent {

    @JsonProperty
    String parent_id;
    @JsonProperty
    String student_id;
    @JsonProperty
    String user_id;
    @JsonProperty
    Float credits;
    @JsonProperty
    Long created;
    @JsonProperty
    Long modified;

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public Float getCredits() {
        return credits;
    }

    public void setCredits(Float credits) {
        this.credits = credits;
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

    public static List<Parent> getParents() throws  ClassNotFoundException, SQLException {

        PreparedStatement ps = Database.prepareStatement("SELECT * FROM school.parent");
        ResultSet rs = ps.executeQuery();

        List<Parent> parents= new ArrayList<>();

        while(rs.next()){
            Parent parent = new Parent();
            parent.setParent_id(rs.getString("parent_id"));
            parent.setStudent_id(rs.getString("student_id"));
            parent.setUser_id(rs.getString("user_id"));
            parent.setCredits(rs.getFloat("credits"));
            parent.setCreated(rs.getLong("created"));
            parent.setModified(rs.getLong("modified"));
            parents.add(parent);

        }
        return parents;
    }

    public static Parent addParent(Parent parent)throws ClassNotFoundException, SQLException, JsonProcessingException
    {
        PreparedStatement ps = Database.prepareStatement("INSERT INTO school.parent (parent_id,student_id,user_id,credits,created,modified) VALUES(?,?,?,?,?,?)");
        String id = Database.generatorId(5);
        ps.setString(1, id);
        ps.setString(2,parent.getStudent_id());
        ps.setString(3,parent.getUser_id());
        ps.setFloat(4,parent.getCredits());
        ps.setLong(5,System.currentTimeMillis());
        ps.setLong(6,System.currentTimeMillis());
        ps.executeUpdate();
        return parent;
    }
    public static Parent getParent(String parent_id) throws ClassNotFoundException, SQLException{

        PreparedStatement ps = Database.prepareStatement("SELECT * FROM school.parent WHERE parent_id=?");
        ps.setString(1,parent_id);
        ResultSet rs = ps.executeQuery();

        Parent parent = new Parent();

        while(rs.next()){
            parent.setParent_id(rs.getString("parent_id"));
            parent.setStudent_id(rs.getString("student_id"));
            parent.setUser_id(rs.getString("user_id"));
            parent.setCredits(rs.getFloat("credits"));
            parent.setCreated(rs.getLong("created"));
            parent.setModified(rs.getLong("modified"));

        }
        return parent;
    }

    public static void updateParent(String parent_id, Parent parent) throws ClassNotFoundException, SQLException, IOException {

        PreparedStatement ps = Database.prepareStatement("UPDATE school.parent SET credits = ? WHERE parent_id = ? ");
        ps.setFloat(1,parent.getCredits());
        ps.setString(2,parent_id);
        ps.executeUpdate();
    }
    public static void deleteParent(String parent_id) throws ClassNotFoundException,SQLException{

        PreparedStatement ps = Database.prepareStatement("DELETE FROM school.parent WHERE parent_id= ?");
        ps.setString(1,parent_id);
        ps.executeUpdate();
    }
    public static boolean hasParentCredits(String parent_id) throws ClassNotFoundException, SQLException,IOException{
        PreparedStatement ps = Database.prepareStatement("SELECT credits FROM school.parent WHERE parent_id = ? LIMIT 1");
        ps.setString(1,parent_id);
        ResultSet rs = ps.executeQuery();
        rs.next();
        final float lesson_cost = 30f;
        Parent parent = Parent.getParent(parent_id);
        parent.setCredits(getParent(parent_id).getCredits()-lesson_cost);
        updateParent(parent_id,parent);
        return (rs.getInt("credits")>30);

    }
}
