package com.teamtreehouse.courses.model.dao;

import com.teamtreehouse.courses.model.Review;
import com.teamtreehouse.courses.model.exc.DaoException;

import java.util.List;

public interface ReviewDAO {

    void add(Review review) throws DaoException;

    List<Review> findAll();

    List<Review> findCourseById(int courseId);
}
