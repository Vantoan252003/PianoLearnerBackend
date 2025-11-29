package com.piano.learn.PianoLearn.repository.auth;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.piano.learn.PianoLearn.entity.auth.Role;
import com.piano.learn.PianoLearn.entity.auth.User;

public interface UserRepository extends JpaRepository<User, Integer>{
    Optional<User> findByEmail(String email);
    long countByRole(Role role);
    List<User> findByLevelName(String levelName);
}
