package com.spliteasy.backend.repository;

import com.spliteasy.backend.entity.Group;
import com.spliteasy.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findAllByCreatedBy(User user);
}
