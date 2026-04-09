package com.spliteasy.backend.repository;

import com.spliteasy.backend.entity.Expense;
import com.spliteasy.backend.entity.ExpenseSplit;
import com.spliteasy.backend.entity.Group;
import com.spliteasy.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {
    List<ExpenseSplit> findAllByExpense(Expense expense);
    List<ExpenseSplit> findAllByUserAndExpense_Group(User user, Group group);
}
