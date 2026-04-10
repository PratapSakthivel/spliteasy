package com.spliteasy.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spliteasy.backend.entity.Group;
import com.spliteasy.backend.entity.Settlement;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    List<Settlement> findAllByGroup(Group group);
}
