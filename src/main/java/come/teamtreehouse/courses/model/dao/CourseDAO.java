package come.teamtreehouse.courses.model.dao;

import come.teamtreehouse.courses.model.Course;
import come.teamtreehouse.courses.model.exc.DaoException;

import java.util.List;

public interface CourseDAO {

    void add(Course course) throws DaoException;

    List<Course> findAll();
}
