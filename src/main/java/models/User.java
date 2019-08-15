package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import database.Database;
import enums.UserRights;
import helpers.Validation;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User {
    @JsonProperty
    String user_id;
    @JsonProperty
    String username;
    @JsonProperty
    String password;
    @JsonProperty
    String role_name;
    @JsonProperty
    String email;
    @JsonProperty
    String firstname;
    @JsonProperty
    String lastname;
    @JsonProperty
    Boolean active;
    @JsonProperty
    Long created;
    @JsonProperty
    Long modified;

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String userId) {
        this.user_id = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role_name;
    }

    public void setRole(String role) {
        this.role_name = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }


    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public static TokenSession logIn(String username, String password)
            throws ClassNotFoundException, InvalidKeySpecException, SQLException, NoSuchAlgorithmException, ValidationException {

        PreparedStatement ps = Database.prepareStatement("SELECT * FROM user_ WHERE username = ? LIMIT 1");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        TokenSession tokenSession = null;

        if (!rs.isBeforeFirst()) {
            System.out.println("if rs is before");
            throw new ValidationException("User does not exist!");
        }

        if (rs.next()) {
            boolean matched = password.equals(rs.getString("password"));
            if (!matched) {
                throw new ValidationException("Invalid credentials");
            }

            tokenSession = TokenSession.generateSessionToken(rs.getString("user_id"));

        }

        return tokenSession;

    }

    public static boolean hasRequiredRights(String userId, UserRights[] allowedRights,String parameter)
            throws ClassNotFoundException, SQLException {

        User user = getUser(userId);
        if (user == null) return false;
        String role = user.getRole();
        PreparedStatement ps = Database.prepareStatement("SELECT * FROM school.roles WHERE role_name= ? LIMIT 1");
        ps.setString(1, role);
        ResultSet rs = ps.executeQuery();
        List<String> userRights = new ArrayList<>();
        while (rs.next()) {
            userRights = Arrays.asList((String[]) rs.getArray("role_rights").getArray());
        }
        List<String> userRightsList = new ArrayList<>();
       userRightsList.addAll(userRights);

        if(!parameter.equals("0")) {
            switch(role){
                case "teacher": PreparedStatement preparedStatement = Database.prepareStatement("SELECT t.user_id,les.teacher_id FROM school.teacher t, school.lesson les WHERE les.teacher_id = t.teacher_id AND les.lesson_id=?");
                    preparedStatement.setString(1, parameter);
                    ResultSet rs1 = preparedStatement.executeQuery();
                    if (rs1.next()) {
                        String user_id = rs1.getString(1);
                        if (user_id.equals(userId)) {
                            userRightsList.add("modifyLessons");

                        }
                    }
                case "parent": PreparedStatement preparedStatement1 = Database.prepareStatement("SELECT p.user_id,les.parent_id FROM school.parent p, school.lesson les WHERE p.parent_id=les.parent_id AND les.lesson_id=?");
                                preparedStatement1.setString(1,parameter);
                                ResultSet rs2=preparedStatement1.executeQuery();
                                if(rs2.next()) {
                                    String user_id = rs2.getString(1);
                                    if (user_id.equals(userId)) userRightsList.add("modifyLessons");
                                }
            }

        }
        for (UserRights allowedRight : allowedRights) {

            if (userRightsList != null && userRightsList.contains(allowedRight.name())) {
                return true;
            }

        }
        return false;
    }

    public static User getUser(String userId) throws ClassNotFoundException, SQLException {

        PreparedStatement ps = Database.prepareStatement("SELECT * FROM school.user_ WHERE user_id = ?");
        ps.setString(1, userId);
        ResultSet rs = ps.executeQuery();

        User user = new User();

        if (rs.next()) {
            user.setUserId(rs.getString("user_id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setFirstname(rs.getString("firstname"));
            user.setLastname(rs.getString("lastname"));
            user.setEmail(rs.getString("email"));
            user.setRole(rs.getString("role_name"));
            user.setActive(rs.getBoolean("active"));
            user.setCreated(rs.getLong("created"));
            user.setModified(rs.getLong("modified"));
        }

        return user;
    }

    public static User addUser(User user) throws ValidationException, ClassNotFoundException, SQLException {
        if(!Validation.validate(user.getEmail())) throw new ValidationException("Uncorrect email.");
        PreparedStatement ps = Database.prepareStatement("INSERT INTO school.user_(user_id , username, password,firstname,lastname,email, role_name,active,created,modified)" +
                " VALUES (?,?,?,?,?,?,?,?,?,?)");
        String id = Database.generatorId(5);
        user.setUserId(id);
        ps.setString(1, user.getUserId());
        ps.setString(2, user.getUsername());
        ps.setString(3, user.getPassword());
        ps.setString(4,user.getFirstname());
        ps.setString(5,user.getLastname());
        ps.setString(6,user.getEmail());
        ps.setString(7, user.getRole());
        ps.setBoolean(8,user.getActive());
        ps.setLong(9,System.currentTimeMillis());
        ps.setLong(10,System.currentTimeMillis());

        ps.executeUpdate();

        return user;

    }
    public static List<User> getUsers() throws ClassNotFoundException, SQLException {
        PreparedStatement ps = Database.prepareStatement("SELECT * FROM school.user_");
        ResultSet rs = ps.executeQuery();
        List<User> users = new ArrayList<User>();
        while (rs.next()) {
            User user = new User();
            user.setUserId(rs.getString("user_id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setFirstname(rs.getString("firstname"));
            user.setLastname(rs.getString("lastname"));
            user.setEmail(rs.getString("email"));
            user.setRole(rs.getString("role_name"));
            user.setActive(rs.getBoolean("active"));
            user.setCreated(rs.getLong("created"));
            user.setModified(rs.getLong("modified"));
            users.add(user);
        }
        return users;
    }

    public static void updateUser(String user_id, User user) throws ClassNotFoundException, SQLException, IOException {
        StringBuilder sql = new StringBuilder("UPDATE school.user_ SET");
        if(null!=user.getUsername()){
            sql.append(" username = ?,");
        }
        if(null!=user.getPassword()){
            sql.append(" password = ?,");
        }
        if(null!=user.getFirstname()){
            sql.append(" firstname = ?,");
        }
        if(null!=user.getLastname()){
            sql.append(" lastname = ?,");
        }
        if(null!=user.getEmail()){
            sql.append(" email = ?,");
        }
        if(null!=user.getRole()){
            sql.append(" role = ?,");
        }
        sql.append(" modified = ?");
        sql.append(" WHERE user_id = ?");

        PreparedStatement ps = Database.prepareStatement(sql.toString());
        int index = 1;

        if(null!=user.getUsername()){
            ps.setString(index,user.getUsername());
            index++;
        }
        if(null!=user.getPassword()){
            ps.setString(index,user.getPassword());
            index++;
        }
        if(null!=user.getFirstname()){
            ps.setString(index,user.getFirstname());
            index++;
        }
        if(null!=user.getLastname()){
            ps.setString(index,user.getLastname());
            index++;
        }
        if(null!=user.getEmail()){
            ps.setString(index,user.getEmail());
            index++;
        }
        if(null!=user.getRole()){
            ps.setString(index,user.getRole());
            index++;
        }
        ps.setLong(index,System.currentTimeMillis());
        index++;
        ps.setString(index,user_id);
        ps.executeUpdate();

    }
    public static void deleteUser(String user_id) throws ClassNotFoundException,SQLException{

        PreparedStatement ps = Database.prepareStatement("DELETE FROM school.user_ WHERE user_id= ?");
        ps.setString(1,user_id);
        ps.executeUpdate();
    }

}
