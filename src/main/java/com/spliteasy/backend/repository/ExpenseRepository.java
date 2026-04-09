package com.spliteasy.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spliteasy.backend.entity.Expense;
import com.spliteasy.backend.entity.Group;
import com.spliteasy.backend.entity.User;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByGroup(Group group);
    List<Expense> findAllByGroupAndPaidBy(Group group, User user);
}
