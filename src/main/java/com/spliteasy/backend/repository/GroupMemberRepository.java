package com.spliteasy.backend.repository;

import com.spliteasy.backend.entity.Group;
import com.spliteasy.backend.entity.GroupMember;
import com.spliteasy.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findAllByUser(User user);
    List<GroupMember> findAllByGroup(Group group);
    Optional<GroupMember> findByGroupAndUser(Group group, User user);
    boolean existsByGroupAndUser(Group group, User user);
}
