package com.spliteasy.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spliteasy.backend.dto.SettlementRequest;
import com.spliteasy.backend.dto.SettlementResponse;
import com.spliteasy.backend.service.SettlementService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    private String getLoggedInEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping("/settlements")
    public ResponseEntity<SettlementResponse> markSettled(@Valid @RequestBody SettlementRequest request) {
        String loggedInEmail = getLoggedInEmail();
        SettlementResponse response = settlementService.markSettled(request, loggedInEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/groups/{id}/settlements")
    public ResponseEntity<List<SettlementResponse>> getSettlements(@PathVariable Long id) {
        String loggedInEmail = getLoggedInEmail();
        List<SettlementResponse> settlements = settlementService.getSettlements(id, loggedInEmail);
        return ResponseEntity.ok(settlements);
    }
}
