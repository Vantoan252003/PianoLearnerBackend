package com.piano.learn.PianoLearn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.piano.learn.PianoLearn.entity.Contribution;

@Repository
public interface ContributionRepository extends JpaRepository<Contribution, Integer> {

    List<Contribution> findByUser_UserId(Integer userId);

    List<Contribution> findByStatus(String status);

    List<Contribution> findAllByOrderByCreatedAtDesc();
}