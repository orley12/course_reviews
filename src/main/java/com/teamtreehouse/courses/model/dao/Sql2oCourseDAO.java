package com.teamtreehouse.courses.model.dao;

import com.teamtreehouse.courses.model.Course;
import com.teamtreehouse.courses.model.exc.DaoException;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

public class Sql2oCourseDAO implements CourseDAO {

    private final Sql2o sql2o;

    public Sql2oCourseDAO(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void add(Course course) throws DaoException {
        String sql = "INSERT INTO courses(name, url) VALUES(:name, :url)";
        try(Connection connection = sql2o.open()){
            int id = (int) connection.createQuery(sql)
                    .bind(course)
                    .executeUpdate()
                    .getKey();
            course.setId(id);
        }catch (Sql2oException ex){
            throw new DaoException(ex, "Problem adding course");
        }
    }

    @Override
    public List<Course> findAll() {
         try(Connection connection = sql2o.open()){
             return connection.createQuery("SELECT * FROM courses")
                     .executeAndFetch(Course.class);
         }
    }

    @Override
    public Course findById(int id) {
        try(Connection connection = sql2o.open()){
            return connection.createQuery("SELECT * from courses WHERE id = :id")
                    .addParameter("id", id)
                    .executeAndFetchFirst(Course.class);
        }
    }
}
