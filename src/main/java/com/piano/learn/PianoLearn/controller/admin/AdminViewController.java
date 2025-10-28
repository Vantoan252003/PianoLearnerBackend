package com.piano.learn.PianoLearn.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminViewController {
    
    @GetMapping("/login")
    public String showAdminLogin() {
        return "admin/login";
    }
    
    @GetMapping("/dashboard")
    public String showAdminDashboard() {
        return "admin/dashboard";
    }
    
    @GetMapping("/users")
    public String showUsers() {
        return "admin/users";
    }
    
    @GetMapping("/courses")
    public String showCourses() {
        return "admin/courses";
    }
    
    @GetMapping("/lessons")
    public String showLessons() {
        return "admin/lessons";
    }
    
    @GetMapping("/exercises")
    public String showExercises() {
        return "admin/exercises";
    }
    
    @GetMapping("/songs")
    public String showSongs() {
        return "admin/songs";
    }
    
    @GetMapping("/achievements")
    public String showAchievements() {
        return "admin/achievements";
    }
    
    @GetMapping("/user-progress")
    public String showUserProgress() {
        return "admin/user_progress";
    }
    
    @GetMapping("/exercise-results")
    public String showExerciseResults() {
        return "admin/exercise_results";
    }
    
    @GetMapping("/user-achievements")
    public String showUserAchievements() {
        return "admin/user_achievements";
    }
    
    @GetMapping("/practice-sessions")
    public String showPracticeSessions() {
        return "admin/practice_sessions";
    }
    
    @GetMapping("/daily-goals")
    public String showDailyGoals() {
        return "admin/daily_goals";
    }
    
    @GetMapping("/favorites")
    public String showFavorites() {
        return "admin/favorites";
    }

    @GetMapping("/piano-questions")
    public String showPianoQuestions() {
        return "admin/piano_question";
    }
}
