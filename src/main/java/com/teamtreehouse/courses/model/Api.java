package com.teamtreehouse.courses.model;

import com.google.gson.Gson;
import com.teamtreehouse.courses.model.dao.CourseDAO;
import com.teamtreehouse.courses.model.dao.Sql2oCourseDAO;
import com.teamtreehouse.courses.model.exc.ApiError;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.Map;

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
            int id = Integer.parseInt(req.params("id"));
            Course course = courseDAO.findById(id);
            if (course == null){
                throw new ApiError(404, "Could not find course with id " + id);
            }
                return course;
                },gson::toJson);

        /*this represents our catch block for the servers errors this will catch the Server Error that is thrown
         *we cast this server error into the ApiError Object and we call the methods of the ApiError object
         *getStatus() and getMessage() this two properties would have been defined while throwing the error
         *this two properties are then put in a map with there respective keys(this so that we can parse this data and
         *return it as a json the user of the Api can see and not just a staus code)
         *we set the responds type, and set the status code by getting the status for the ApiError Object method getStatus
         *finally we parse the map Object value and convert them into json to be seen by user via gson.toJson() mehod */
        exception(ApiError.class, (exc, req, res) -> {
            ApiError err = (ApiError) exc;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("status", err.getStatus());
            jsonMap.put("errorMessage", err.getMessage());
            res.type("application/json");
            res.status(err.getStatus());
            res.body(gson.toJson(jsonMap));
        });

        after((req, res) -> {
            res.type("application/json");
        });
    }
}
