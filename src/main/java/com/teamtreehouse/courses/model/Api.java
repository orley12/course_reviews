package com.teamtreehouse.courses.model;

import com.google.gson.Gson;
import com.teamtreehouse.courses.model.dao.CourseDAO;
import com.teamtreehouse.courses.model.dao.Sql2oCourseDAO;
import org.sql2o.Sql2o;

import static spark.Spark.*;

public class Api {

    public static void main(String[] args) {
        String dataSoursce = "jdbc:h2:~/reviews.db";
        if (args.length > 0){
            if (args.length != 2){
                System.out.print("java Api <port> <datasource>");
                System.exit(0);
            }
            port(Integer.parseInt(args[0]));
            dataSoursce = args[1];
        }
        Sql2o sql2o =
                new Sql2o(String.format("%s;INIT=RUNSCRIPT from 'classpath:db/init.sql'", dataSoursce)
                        ,"","");
        CourseDAO courseDAO = new Sql2oCourseDAO(sql2o);
        Gson gson = new Gson();

        post("/courses", "application/json", (req, res) -> {
            Course course = gson.fromJson(req.body(), Course.class);
            courseDAO.add(course);
            res.status(201);
            return course;
        }, gson::toJson);

        get("/courses", "application/json",
                (req, res) -> courseDAO.findAll(),gson::toJson);

        get("/courses/:id", "application/json",
                (req, res) -> {
            int courseId = Integer.parseInt(req.params("id"));
            Course course = courseDAO.findById(courseId);
                return course;
                },gson::toJson);

        after((req, res) -> {
            res.type("application/json");
        });
    }
}
