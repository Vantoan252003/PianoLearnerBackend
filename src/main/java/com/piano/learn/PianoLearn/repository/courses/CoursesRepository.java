package com.piano.learn.PianoLearn.repository.courses;

import org.springframework.data.jpa.repository.JpaRepository;

import com.piano.learn.PianoLearn.entity.courses.Courses;

public interface CoursesRepository extends JpaRepository <Courses, Integer>{


}
