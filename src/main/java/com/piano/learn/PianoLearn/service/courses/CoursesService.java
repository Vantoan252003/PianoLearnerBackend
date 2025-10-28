package com.piano.learn.PianoLearn.service.courses;

import java.util.List;

import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.entity.courses.Courses;
import com.piano.learn.PianoLearn.repository.courses.CoursesRepository;

@Service
public class CoursesService {
    private final CoursesRepository coursesRepository;
    public CoursesService(CoursesRepository coursesRepository){
        this.coursesRepository = coursesRepository;
    }
    public List<Courses> getAllCourses (){
        return coursesRepository.findAll();
    }
}
