package com.teamtreehouse.courses.model.dao;

import com.teamtreehouse.courses.model.Course;
import com.teamtreehouse.courses.model.Review;
import com.teamtreehouse.courses.model.exc.DaoException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

import static org.junit.Assert.*;

public class Sql2oReviewDAOTest {

    private Sql2oReviewDAO reviewDAO;
    private Sql2oCourseDAO courseDAO;
    private Connection conn;
    private Course course;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        conn = sql2o.open();
        courseDAO = new Sql2oCourseDAO(sql2o);
        reviewDAO =  new Sql2oReviewDAO(sql2o);
//       keep connection open through each test so that it is not wiped
        course = new Course("Test",  "http://testing.com");
        courseDAO.add(course);
    }

    @Test
    public void addingReviewSetsNewId() throws DaoException {
        int courseId = course.getId();
        Review review = new Review(courseId, 5, "Was an awesomeCourse");
        int originalId = review.getId();
        reviewDAO.add(review);
        assertNotEquals(originalId, review.getId());

    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void findAll() {
    }

    @Test
    public void multipleReviewsAreFoundWenTheyExistForACourse() throws DaoException {
        reviewDAO.add(new Review(course.getId(), 5, "Was an awesome Course"));
        reviewDAO.add(new Review(course.getId(), 4, "Was an amazing Course"));
        reviewDAO.add(new Review(course.getId(), 1, "Course Test"));

        List<Review> reviews = reviewDAO.findCourseById(course.getId());

        assertTrue(reviews.size() > 2);
    }

    @Test(expected = DaoException.class)
    public void addingReviewToANonExistingCourseFails() throws DaoException {
        Review review = new Review(42, 5, "Course Test");
        reviewDAO.add(review);
    }
}