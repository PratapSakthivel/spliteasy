package com.spliteasy.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spliteasy.backend.dto.BalanceResponse;
import com.spliteasy.backend.dto.GroupRequest;
import com.spliteasy.backend.dto.GroupResponse;
import com.spliteasy.backend.dto.InviteLinkResponse;
import com.spliteasy.backend.dto.SettlementSuggestion;
import com.spliteasy.backend.service.DebtSimplificationService;
import com.spliteasy.backend.service.GroupService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final DebtSimplificationService debtSimplificationService;

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

    @GetMapping("/groups/{id}/balances")
    public ResponseEntity<List<BalanceResponse>> getGroupBalances(@PathVariable Long id) {
        String loggedInEmail = getLoggedInEmail();
        List<BalanceResponse> balances = groupService.getGroupBalances(id, loggedInEmail);
        return ResponseEntity.ok(balances);
    }

    @GetMapping("/groups/{id}/settle")
    public ResponseEntity<List<SettlementSuggestion>> getSettlementSuggestions(@PathVariable Long id) {
        String loggedInEmail = getLoggedInEmail();
        List<BalanceResponse> balances = groupService.getGroupBalances(id, loggedInEmail);
        List<SettlementSuggestion> settlements = debtSimplificationService.simplify(balances);
        return ResponseEntity.ok(settlements);
    }
}
