package com.spliteasy.backend.service;

import com.spliteasy.backend.dto.ExpenseRequest;
import com.spliteasy.backend.dto.ExpenseResponse;
import com.spliteasy.backend.entity.Expense;
import com.spliteasy.backend.entity.ExpenseSplit;
import com.spliteasy.backend.entity.Group;
import com.spliteasy.backend.entity.User;
import com.spliteasy.backend.repository.ExpenseRepository;
import com.spliteasy.backend.repository.ExpenseSplitRepository;
import com.spliteasy.backend.repository.GroupRepository;
import com.spliteasy.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Transactional
    public ExpenseResponse addExpense(Long groupId, ExpenseRequest request, String loggedInEmail) {
        // Find group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Find paidBy user
        User paidByUser = userRepository.findById(request.getPaidById())
                .orElseThrow(() -> new RuntimeException("Paid by user not found"));

        // Create and save Expense
        Expense expense = new Expense();
        expense.setGroup(group);
        expense.setPaidBy(paidByUser);
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setCategory(request.getCategory());
        expense = expenseRepository.save(expense);

        // Calculate share amount
        double shareAmount = request.getAmount() / request.getSplitAmong().size();

        // Create ExpenseSplit for each user in splitAmong
        for (Long userId : request.getSplitAmong()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            ExpenseSplit expenseSplit = new ExpenseSplit();
            expenseSplit.setExpense(expense);
            expenseSplit.setUser(user);
            expenseSplit.setAmount(shareAmount);
            expenseSplitRepository.save(expenseSplit);
        }

        // Return ExpenseResponse
        return new ExpenseResponse(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getPaidBy().getEmail(),
                expense.getCreatedAt()
        );
    }

    public List<ExpenseResponse> getExpensesByGroup(Long groupId, String email) {
        // Find group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Find all expenses for this group
        List<Expense> expenses = expenseRepository.findAllByGroup(group);

        // Convert to ExpenseResponse
        return expenses.stream()
                .map(expense -> new ExpenseResponse(
                        expense.getId(),
                        expense.getDescription(),
                        expense.getAmount(),
                        expense.getCategory(),
                        expense.getPaidBy().getEmail(),
                        expense.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
