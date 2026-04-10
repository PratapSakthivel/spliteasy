package com.spliteasy.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementRequest {

    @NotNull(message = "Group ID is required")
    private Long groupId;

    @NotNull(message = "Paid by user ID is required")
    private Long paidById;

    @NotNull(message = "Paid to user ID is required")
    private Long paidToId;

    @NotNull(message = "Amount is required")
    private Double amount;
}
