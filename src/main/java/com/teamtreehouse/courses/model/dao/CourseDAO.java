package com.teamtreehouse.courses.model.dao;

import com.teamtreehouse.courses.model.Course;
import com.teamtreehouse.courses.model.exc.DaoException;

import java.util.List;

public interface CourseDAO {

    void add(Course course) throws DaoException;

    List<Course> findAll();

    Course findById(int courseId);
}
