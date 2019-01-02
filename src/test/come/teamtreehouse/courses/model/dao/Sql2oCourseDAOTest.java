package come.teamtreehouse.courses.model.dao;

import come.teamtreehouse.courses.model.Course;
import come.teamtreehouse.courses.model.exc.DaoException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.Assert.*;

public class Sql2oCourseDAOTest {

    private Sql2oCourseDAO dao;
    private Connection conn;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        dao =  new Sql2oCourseDAO(sql2o);
//       keep connection open through each test so that it is not wiped
        conn = sql2o.open();
    }

    @Test
    public void addingCourseSetsId() throws DaoException {
        Course course = new Course("Test", "http://test.com");
        int originalCourseId = course.getId();
        dao.add(course);
        assertNotEquals(originalCourseId, course.getId());
    }

    @Test
    public void addesCoursesAreReturnedFromFindAll() throws DaoException {
        Course course = new Course("Test", "http://test.com");
        dao.add(course);
        assertEquals(course, dao.findAll().get(0));
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }
}