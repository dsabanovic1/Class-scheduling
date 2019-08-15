package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import enums.UserRights;
import models.*;
import security.Secured;

import javax.mail.MessagingException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.List;

@Path("/")
public class Controller {
    @POST
    @Path("/login")
    @Produces("application/json")
    @Consumes("application/x-www-form-urlencoded")
    public Response login(@FormParam("username") String username, @FormParam("password") String password) {
        try {
            TokenSession tokenSession = User.logIn(username, password);
            return Response.status(200).entity(tokenSession).build();

        } catch (ClassNotFoundException | NoSuchAlgorithmException | InvalidKeySpecException | SQLException e) {
            return Response.status(500).build();

        } catch (ValidationException e) {
            return Response.status(401).build();
        }

    }
    @GET
    @Path("/students")
    @Secured(UserRights.getStudents)
    @Produces({"application/json"})
    public Response getStudents() {
        try {
            List<Student> students = Student.getStudents();
            return Response.status(200).entity(students).build();
        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/parents")
    @Secured(UserRights.getParents)
    @Produces({"application/json"})
    public Response getParents() {
        try {
            List<Parent> parents = Parent.getParents();
            return Response.status(200).entity(parents).build();
        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).build();
        }
    }
    @POST
    @Path("/student")
    @Secured({UserRights.modifyStudents})
    @Consumes("application/json")
    @Produces("application/json")
    public Response addStudent(Student student) {
        try {
            return Response.status(200).entity(Student.addStudent(student)).build();

        } catch (ClassNotFoundException | JsonProcessingException | SQLException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/parent")
    @Secured({UserRights.modifyParents})
    @Consumes("application/json")
    @Produces("application/json")
    public Response addParent(Parent parent) {
        try {
            return Response.status(200).entity(Parent.addParent(parent)).build();

        } catch (ClassNotFoundException | JsonProcessingException | SQLException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/student/{id}")
    @Secured(UserRights.getStudents)
    @Produces("application/json")
    public Response getStudent(@PathParam("id") String student_id) {
        try {
            return Response.status(200).entity(Student.getStudent(student_id)).build();

        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).build();

        }
    }

    @GET
    @Path("/parent/{id}")
    @Secured(UserRights.getParents)
    @Produces("application/json")
    public Response getParent(@PathParam("id") String parent_id) {
        try {
            return Response.status(200).entity(Parent.getParent(parent_id)).build();

        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).build();

        }
    }

    @PUT
    @Path("/student/{id}")
    @Secured(UserRights.modifyStudents)
    @Produces("application/json")
    @Consumes("application/json")
    public Response updateStudent(@PathParam("id") String student_id, Student student, SecurityContext sc) {
        try {
            String userRequesting = sc.getUserPrincipal().getName();
            Student.updateStudent(student_id,student);
            return Response.status(200).build();

        } catch (ClassNotFoundException | IOException | SQLException e) {
            return Response.status(500).build();
        }

    }

    @PUT
    @Path("/parent/{id}")
    @Secured(UserRights.modifyParents)
    @Produces("application/json")
    @Consumes("application/json")
    public Response updateParent(@PathParam("id") String parent_id, Parent parent) {
        try {
            Parent.updateParent(parent_id,parent);
            return Response.status(200).build();

        } catch (ClassNotFoundException | IOException | SQLException e) {
            return Response.status(500).build();
        }

    }

    @DELETE
    @Path("/student/{id}")
    @Secured(UserRights.modifyStudents)
    @Produces("application/json")
    @Consumes("application/json")
    public Response deleteStudent(@PathParam("id") String student_id) {
        try {
            Student.deleteStudent(student_id);
            return Response.status(200).build();
        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).build();
        }

    }

    @DELETE
    @Path("/parent/{id}")
    @Secured(UserRights.modifyParents)
    @Produces("application/json")
    @Consumes("application/json")
    public Response deleteParent(@PathParam("id") String parent_id) {
        try {
            Parent.deleteParent(parent_id);
            return Response.status(200).build();
        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).build();
        }

    }
    @GET
    @Path("/teachers")
    @Secured(UserRights.getTeachers)
    @Produces({"application/json"})
    public Response getTeachers() {
        try {
            List<Teacher> teachers = Teacher.getTeachers();
            return Response.status(200).entity(teachers).build();
        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/teacher")
    @Secured({UserRights.modifyTeachers})
    @Consumes("application/json")
    @Produces("application/json")
    public Response addTeacher(Teacher teacher) {
        try {
            return Response.status(200).entity(Teacher.addTeacher(teacher)).build();

        } catch (ClassNotFoundException | JsonProcessingException | SQLException|ValidationException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/teacher/{id}")
    @Secured(UserRights.getTeachers)
    @Produces("application/json")
    public Response getTeacher(@PathParam("id") String teacher_id) {
        try {
            return Response.status(200).entity(Teacher.getTeacher(teacher_id)).build();

        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).build();

        }
    }
    @DELETE
    @Path("/teacher/{id}")
    @Secured(UserRights.modifyTeachers)
    @Produces("application/json")
    @Consumes("application/json")
    public Response deleteTeacher(@PathParam("id") String teacher_id) {
        try {
            Teacher.deleteTeacher(teacher_id);
            return Response.status(200).build();
        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).build();
        }
    }

    @PUT
    @Path("/teacher/{id}")
    @Secured({UserRights.updateTeacher})
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateTeacher(@PathParam("id") String id, Teacher teacher) {

        try {
            Teacher.updateTeacher(id,teacher);
            return Response.status(200).build();
        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/lessons")
    @Secured(UserRights.getLessons)
    @Produces({"application/json"})
    public Response getLessons() {
        try {
            List<Lesson> lessons = Lesson.getLessons();
            return Response.status(200).entity(lessons).build();
        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).build();
        }
    }
    @GET
    @Path("/lesson/parent/{id}")
    @Secured(UserRights.getLessons)
    @Produces({"application/json"})
    public Response getLessonsByParent(@PathParam("id") String parent_id) {
        try {
            List<Lesson> lessons = Lesson.getLessonsByParent(parent_id);
            return Response.status(200).entity(lessons).build();
        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/lesson")
    @Secured({UserRights.addLesson})
    @Consumes("application/json")
    @Produces("application/json")
    public Response addLesson(Lesson lesson) {
        try {
            return Response.status(200).entity(Lesson.addLesson(lesson)).build();

        } catch (ClassNotFoundException | SQLException|IOException | ValidationException | MessagingException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/lesson/{id}")
    @Secured(UserRights.getLessons)
    @Produces("application/json")
    public Response getLesson(@PathParam("id") String lesson_id) {
        try {
            return Response.status(200).entity(Lesson.getLesson(lesson_id)).build();

        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).build();

        }
    }
    @GET
    @Path("/user")
    @Secured({UserRights.getUsers})
    @Produces("application/json")
    public Response getUsers() {

        try {
            List<User> users = User.getUsers();
            return Response.status(200).entity(users).build();
        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/user/{id}")
    @Secured({UserRights.getUsers})
    @Produces("application/json")
    public Response getUser(@PathParam("id") String userId) {

        try {
            return Response.status(200).entity(User.getUser(userId)).build();
        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).build();
        }
    }



    @POST
    @Path("/user")
    @Secured({UserRights.modifyUsers})
    @Consumes("application/json")
    @Produces("application/json")
    public Response addUser(User user) {
        try {
            return Response.status(200).entity(User.addUser(user)).build();

        } catch (ClassNotFoundException | SQLException | ValidationException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/user/{id}")
    @Secured({UserRights.modifyUsers})
    @Consumes("application/json")
    @Produces("application/json")
    public Response updateUser(@PathParam("id") String id, User user) {

        try {
            User.updateUser(id,user);
            return Response.status(200).build();
        } catch (ClassNotFoundException | SQLException |IOException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
    @DELETE
    @Path("/user/{id}")
    @Secured(UserRights.modifyTeachers)
    @Produces("application/json")
    @Consumes("application/json")
    public Response deleteUser(@PathParam("id") String user_id) {
        try {
            User.deleteUser(user_id);
            return Response.status(200).build();
        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).build();
        }
    }

    @DELETE
    @Path("/lesson/{id}")
    @Secured(UserRights.modifyLessons)
    @Produces("application/json")
    @Consumes("application/json")
    public Response deleteLesson(@PathParam("id") String lesson_id) {
        try {
            Lesson.deleteLesson(lesson_id);
            return Response.status(200).build();
        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).build();
        }

    }
}
