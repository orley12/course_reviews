package com.teamtreehouse.courses.model.dao;

import com.teamtreehouse.courses.model.Course;
import com.teamtreehouse.courses.model.Review;
import com.teamtreehouse.courses.model.exc.DaoException;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

public class Sql2oReviewDAO implements ReviewDAO {

    private Sql2o sql2o;

    public Sql2oReviewDAO(Sql2o sql2o){
        this.sql2o = sql2o;
    }
    @Override
    public void add(Review review) throws DaoException {
        String sql = "INSERT INTO reviews(course_Id, rating, comment) VALUES(:courseId, :rating, :comment)";
        try(Connection connection = sql2o.open()){
            int id = (int) connection.createQuery(sql)
                    .bind(review)
                    .executeUpdate()
                    .getKey();
            review.setId(id);
        }catch (Sql2oException ex){
            throw new DaoException(ex, "Problem adding review");
        }

    }

    @Override
    public List<Review> findAll() {
        try(Connection connection = sql2o.open()){
            return connection.createQuery("SELECT * FROM reviews")
                    .executeAndFetch(Review.class);
        }
    }

    @Override
    public List<Review> findCourseById(int courseId) {
        try(Connection connection = sql2o.open()){
            return connection.createQuery("SELECT * from reviews WHERE course_id = :courseId")
                    .addColumnMapping("COURSE_ID", "courseId")
                    .addParameter("courseId", courseId)
                    .executeAndFetch(Review.class);
        }
    }
}
