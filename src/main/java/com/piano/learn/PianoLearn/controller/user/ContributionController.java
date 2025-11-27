package com.piano.learn.PianoLearn.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.piano.learn.PianoLearn.dto.contribution.ContributionRequest;
import com.piano.learn.PianoLearn.entity.Contribution;
import com.piano.learn.PianoLearn.entity.auth.User;
import com.piano.learn.PianoLearn.service.UserService;
import com.piano.learn.PianoLearn.service.admin.ContributionService;

@RestController
@RequestMapping("/api/contributions")
public class ContributionController {

    @Autowired
    private ContributionService contributionService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<Contribution>> listContributions() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findUserByEmail(username);
        List<Contribution> contributions = contributionService.getContributionsByUser(user.getUserId());
        return ResponseEntity.ok(contributions);
    }

    @PostMapping
    public ResponseEntity<String> submitContribution(@RequestBody ContributionRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.findUserByEmail(username);
        Contribution contribution = Contribution.builder()
            .user(user)
            .title(request.getTitle())
            .content(request.getContent())
            .build();
        contributionService.saveContribution(contribution);
        return ResponseEntity.ok("Đóng góp của bạn đã được gửi thành công!");
    }
}