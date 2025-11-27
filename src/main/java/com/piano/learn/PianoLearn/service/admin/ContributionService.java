package com.piano.learn.PianoLearn.service.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.piano.learn.PianoLearn.entity.Contribution;
import com.piano.learn.PianoLearn.repository.ContributionRepository;

@Service
public class ContributionService {

    @Autowired
    private ContributionRepository contributionRepository;

    public List<Contribution> getAllContributions() {
        return contributionRepository.findAllByOrderByCreatedAtDesc();
    }

    public Contribution getContributionById(Integer id) {
        return contributionRepository.findById(id).orElse(null);
    }

    public Contribution saveContribution(Contribution contribution) {
        return contributionRepository.save(contribution);
    }

    public void deleteContribution(Integer id) {
        contributionRepository.deleteById(id);
    }

    public List<Contribution> getContributionsByStatus(String status) {
        return contributionRepository.findByStatus(status);
    }

    public List<Contribution> getContributionsByUser(Integer userId) {
        return contributionRepository.findByUser_UserId(userId);
    }
}