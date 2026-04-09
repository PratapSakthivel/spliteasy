package com.spliteasy.backend.controller;

import com.spliteasy.backend.dto.GroupRequest;
import com.spliteasy.backend.dto.GroupResponse;
import com.spliteasy.backend.dto.InviteLinkResponse;
import com.spliteasy.backend.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    private String getLoggedInEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping("/groups")
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody GroupRequest request) {
        String loggedInEmail = getLoggedInEmail();
        GroupResponse response = groupService.createGroup(request.getName(), loggedInEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/groups/{id}/invite")
    public ResponseEntity<String> inviteByEmail(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String loggedInEmail = getLoggedInEmail();
        String inviteeEmail = body.get("email");
        String message = groupService.inviteByEmail(id, loggedInEmail, inviteeEmail);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/groups/{id}/invite-link")
    public ResponseEntity<InviteLinkResponse> generateInviteLink(@PathVariable Long id) {
        String loggedInEmail = getLoggedInEmail();
        InviteLinkResponse response = groupService.generateInviteLink(id, loggedInEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/invite/join/{token}")
    public ResponseEntity<String> joinByToken(@PathVariable String token) {
        String loggedInEmail = getLoggedInEmail();
        String message = groupService.joinByToken(token, loggedInEmail);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/groups")
    public ResponseEntity<List<GroupResponse>> getMyGroups() {
        String loggedInEmail = getLoggedInEmail();
        List<GroupResponse> groups = groupService.getMyGroups(loggedInEmail);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/groups/{id}")
    public ResponseEntity<GroupResponse> getGroupById(@PathVariable Long id) {
        String loggedInEmail = getLoggedInEmail(); // Added for authentication
        GroupResponse response = groupService.getGroupById(id);
        return ResponseEntity.ok(response);
    }
}
