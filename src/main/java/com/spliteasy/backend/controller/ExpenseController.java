package com.spliteasy.backend.controller;

import com.spliteasy.backend.dto.ExpenseRequest;
import com.spliteasy.backend.dto.ExpenseResponse;
import com.spliteasy.backend.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    private String getLoggedInEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @PostMapping("/{id}/expenses")
    public ResponseEntity<ExpenseResponse> addExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request) {
        String loggedInEmail = getLoggedInEmail();
        ExpenseResponse response = expenseService.addExpense(id, request, loggedInEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/expenses")
    public ResponseEntity<List<ExpenseResponse>> getExpensesByGroup(@PathVariable Long id) {
        String loggedInEmail = getLoggedInEmail();
        List<ExpenseResponse> expenses = expenseService.getExpensesByGroup(id, loggedInEmail);
        return ResponseEntity.ok(expenses);
    }
}
