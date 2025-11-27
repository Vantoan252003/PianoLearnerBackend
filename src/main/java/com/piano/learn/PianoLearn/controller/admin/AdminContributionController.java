package com.piano.learn.PianoLearn.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.entity.Contribution;
import com.piano.learn.PianoLearn.service.admin.ContributionService;

@RestController
@RequestMapping("/api/admin/contributions")
public class AdminContributionController {

    @Autowired
    private ContributionService contributionService;

    @GetMapping
    public ResponseEntity<List<Contribution>> getAllContributions() {
        return ResponseEntity.ok(contributionService.getAllContributions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contribution> getContributionById(@PathVariable Integer id) {
        Contribution contribution = contributionService.getContributionById(id);
        if (contribution != null) {
            return ResponseEntity.ok(contribution);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approveContribution(@PathVariable Integer id) {
        Contribution contribution = contributionService.getContributionById(id);
        if (contribution != null) {
            contribution.setStatus("approved");
            contributionService.saveContribution(contribution);
            return ResponseEntity.ok("Approved");
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<String> rejectContribution(@PathVariable Integer id) {
        Contribution contribution = contributionService.getContributionById(id);
        if (contribution != null) {
            contribution.setStatus("rejected");
            contributionService.saveContribution(contribution);
            return ResponseEntity.ok("Rejected");
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteContribution(@PathVariable Integer id) {
        if (contributionService.getContributionById(id) != null) {
            contributionService.deleteContribution(id);
            return ResponseEntity.ok("Deleted");
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/feedback")
    public ResponseEntity<String> addFeedback(@PathVariable Integer id, @RequestBody String feedback) {
        Contribution contribution = contributionService.getContributionById(id);
        if (contribution != null) {
            contribution.setAdminFeedback(feedback);
            contribution.setStatus("approved");
            contributionService.saveContribution(contribution);
            return ResponseEntity.ok("Feedback added");
        }
        return ResponseEntity.notFound().build();
    }
}