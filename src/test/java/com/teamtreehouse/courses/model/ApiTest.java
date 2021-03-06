package com.teamtreehouse.courses.model;

import com.google.gson.Gson;
import com.teamtreehouse.courses.model.dao.CourseDAO;
import com.teamtreehouse.courses.model.dao.ReviewDAO;
import com.teamtreehouse.courses.model.dao.Sql2oCourseDAO;
import com.teamtreehouse.courses.model.dao.Sql2oReviewDAO;
import com.teamtreehouse.courses.model.exc.DaoException;
import com.teamtreehouse.courses.model.testing.ApiClient;
import com.teamtreehouse.courses.model.testing.ApiResponse;
import org.junit.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.Spark;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ApiTest {
    public static final String PORT = "4568";
    public static final String TEST_DATASOURCE = "jdbc:h2:mem:testing";
    private Connection conn;
    private ApiClient client;
    private Gson gson ;
    private CourseDAO courseDao;
    private ReviewDAO reviewDAO;

    @BeforeClass
    public static void startServer() {
        String[] args = {PORT, TEST_DATASOURCE};
        Api.main(args);
    }

    @AfterClass
    public static void stopServer() {
        Spark.stop();
    }


        @Before
    public void setUp() throws Exception {
        Sql2o sql2o = new Sql2o(TEST_DATASOURCE + ";INIT=RUNSCRIPT from 'classpath:db/init.sql'", "","");
        conn = sql2o.open();
        client = new ApiClient("http://localhost:" + PORT);
        gson = new Gson();
        courseDao = new Sql2oCourseDAO(sql2o);
        reviewDAO = new Sql2oReviewDAO(sql2o);
        }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingCourseReturnsCreatedStatus() {
        Map<String, String> values = new HashMap<>();
        values.put("name", "Test");
        values.put("url", "http://test.com");

        ApiResponse res = client.request("POST", "/courses", gson.toJson(values));

        assertEquals(201, res.getStatus());
    }

    @Test
    public void coursesCanBeAccessedById() throws DaoException {
        Course course = newTestCourse();
        courseDao.add(course);

        ApiResponse response =
                client.request("GET", "/courses/" + course.getId());
        Course retrieved = gson.fromJson(response.getBody(), Course.class);

        assertEquals(course, retrieved);
    }

    @Test
    public void addingReviewReturnsreatedStatus() throws DaoException {
        Course course = newTestCourse();
        courseDao.add(course);

        Map<String, Object> values = new HashMap<>();
        values.put("rating", 5);
        values.put("comment", "Kennenth as an awesome beard");

        ApiResponse res = client.request("POST", "/courses/1", gson.toJson(values));

        assertEquals(201, res.getStatus());
    }

    @Test
    public void addingReviewToUnknownCourseThrowsError() throws DaoException {
        Map<String, Object> values = new HashMap<>();
        values.put("rating", 5);
        values.put("comment", "Kennenth as an awesome beard");

        ApiResponse res = client.request("GET", "/courses/47", gson.toJson(values));

        assertEquals(500, res.getStatus());
    }

    @Test
    public void findsMultipleReviewForSingleCourseById() throws DaoException {
        Course course = newTestCourse();
        courseDao.add(course);

        reviewDAO.add(new Review(course.getId(), 5, "Was an awesome Course"));
        reviewDAO.add(new Review(course.getId(), 4, "Was an amazing Course"));
        reviewDAO.add(new Review(course.getId(), 1, "Course Test"));

        ApiResponse res = client.request("GET", "/courses/1/reviews");

        Review[] reviews = gson.fromJson(res.getBody(), Review[].class);

        assertEquals(3, reviews.length);
    }

    @Test
    public void missingCoursesReturnNotFoundStatus() {
        ApiResponse response = client.request("GET", "/courses/42");
        assertEquals(404, response.getStatus());


    }

    private Course newTestCourse() {
        return new Course("Test", "http://test.com");
    }
}