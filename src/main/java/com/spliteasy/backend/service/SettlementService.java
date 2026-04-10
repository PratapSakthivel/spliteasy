package com.spliteasy.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spliteasy.backend.dto.SettlementRequest;
import com.spliteasy.backend.dto.SettlementResponse;
import com.spliteasy.backend.entity.Group;
import com.spliteasy.backend.entity.Settlement;
import com.spliteasy.backend.entity.User;
import com.spliteasy.backend.repository.GroupRepository;
import com.spliteasy.backend.repository.SettlementRepository;
import com.spliteasy.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Transactional
    public SettlementResponse markSettled(SettlementRequest request, String loggedInEmail) {
        // Find group
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Find paidBy user
        User paidBy = userRepository.findById(request.getPaidById())
                .orElseThrow(() -> new RuntimeException("Paid by user not found"));

        // Find paidTo user
        User paidTo = userRepository.findById(request.getPaidToId())
                .orElseThrow(() -> new RuntimeException("Paid to user not found"));

        // Create and save Settlement
        Settlement settlement = new Settlement();
        settlement.setGroup(group);
        settlement.setPaidBy(paidBy);
        settlement.setPaidTo(paidTo);
        settlement.setAmount(request.getAmount());
        settlement = settlementRepository.save(settlement);

        // Return SettlementResponse
        return new SettlementResponse(
                settlement.getId(),
                settlement.getPaidBy().getEmail(),
                settlement.getPaidTo().getEmail(),
                settlement.getAmount(),
                settlement.getSettledAt()
        );
    }

    public List<SettlementResponse> getSettlements(Long groupId, String email) {
        // Find group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Find all settlements for this group
        List<Settlement> settlements = settlementRepository.findAllByGroup(group);

        // Convert to SettlementResponse
        return settlements.stream()
                .map(s -> new SettlementResponse(
                        s.getId(),
                        s.getPaidBy().getEmail(),
                        s.getPaidTo().getEmail(),
                        s.getAmount(),
                        s.getSettledAt()
                ))
                .collect(Collectors.toList());
    }
}
