package com.spliteasy.backend.service;

import com.spliteasy.backend.dto.BalanceResponse;
import com.spliteasy.backend.dto.GroupResponse;
import com.spliteasy.backend.dto.InviteLinkResponse;
import com.spliteasy.backend.entity.Group;
import com.spliteasy.backend.entity.GroupMember;
import com.spliteasy.backend.entity.InviteToken;
import com.spliteasy.backend.entity.User;
import com.spliteasy.backend.repository.ExpenseRepository;
import com.spliteasy.backend.repository.ExpenseSplitRepository;
import com.spliteasy.backend.repository.GroupMemberRepository;
import com.spliteasy.backend.repository.GroupRepository;
import com.spliteasy.backend.repository.InviteTokenRepository;
import com.spliteasy.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final InviteTokenRepository inviteTokenRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository expenseSplitRepository;

    @Transactional
    public GroupResponse createGroup(String name, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Group group = new Group();
        group.setName(name);
        group.setCreatedBy(user);
        group = groupRepository.save(group);

        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(group);
        groupMember.setUser(user);
        groupMemberRepository.save(groupMember);

        return new GroupResponse(group.getId(), group.getName(), 1);
    }

    @Transactional
    public String inviteByEmail(Long groupId, String inviterEmail, String inviteeEmail) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User invitee = userRepository.findByEmail(inviteeEmail)
                .orElseThrow(() -> new RuntimeException("Invitee user not found"));

        if (groupMemberRepository.existsByGroupAndUser(group, invitee)) {
            throw new RuntimeException("Already a member");
        }

        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(group);
        groupMember.setUser(invitee);
        groupMemberRepository.save(groupMember);

        return "User invited successfully";
    }

    @Transactional
    public InviteLinkResponse generateInviteLink(Long groupId, String email) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        InviteToken inviteToken = new InviteToken();
        inviteToken.setGroup(group);
        inviteToken.setToken(UUID.randomUUID().toString());
        inviteToken.setExpiresAt(LocalDateTime.now().plusHours(48));
        inviteToken.setUsed(false);
        inviteTokenRepository.save(inviteToken);

        String inviteLink = "http://localhost:8080/api/invite/join/" + inviteToken.getToken();
        return new InviteLinkResponse(inviteLink);
    }

    @Transactional
    public String joinByToken(String token, String userEmail) {
        InviteToken inviteToken = inviteTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid invite link"));

        if (inviteToken.isUsed()) {
            throw new RuntimeException("Invite link already used");
        }

        if (inviteToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invite link expired");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (groupMemberRepository.existsByGroupAndUser(inviteToken.getGroup(), user)) {
            throw new RuntimeException("Already a member");
        }

        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(inviteToken.getGroup());
        groupMember.setUser(user);
        groupMemberRepository.save(groupMember);

        inviteToken.setUsed(true);
        inviteTokenRepository.save(inviteToken);

        return "Successfully joined the group";
    }

    public List<GroupResponse> getMyGroups(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<GroupMember> groupMembers = groupMemberRepository.findAllByUser(user);

        return groupMembers.stream()
                .map(gm -> {
                    Group group = gm.getGroup();
                    int memberCount = groupMemberRepository.findAllByGroup(group).size();
                    return new GroupResponse(group.getId(), group.getName(), memberCount);
                })
                .collect(Collectors.toList());
    }

    public GroupResponse getGroupById(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        int memberCount = groupMemberRepository.findAllByGroup(group).size();

        return new GroupResponse(group.getId(), group.getName(), memberCount);
    }

    public List<BalanceResponse> getGroupBalances(Long groupId, String email) {
        // Find group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Find all members in this group
        List<GroupMember> groupMembers = groupMemberRepository.findAllByGroup(group);

        // Calculate balance for each member
        return groupMembers.stream()
                .map(gm -> {
                    User user = gm.getUser();

                    // Calculate total paid by this user
                    double totalPaid = expenseRepository.findAllByGroupAndPaidBy(group, user)
                            .stream()
                            .mapToDouble(expense -> expense.getAmount())
                            .sum();

                    // Calculate total owed by this user
                    double totalOwed = expenseSplitRepository.findAllByUserAndExpense_Group(user, group)
                            .stream()
                            .mapToDouble(split -> split.getAmount())
                            .sum();

                    // Calculate net balance
                    double netBalance = totalPaid - totalOwed;

                    // Round to 2 decimal places
                    netBalance = Math.round(netBalance * 100.0) / 100.0;

                    return new BalanceResponse(user.getId(), user.getEmail(), netBalance);
                })
                .collect(Collectors.toList());
    }
}
