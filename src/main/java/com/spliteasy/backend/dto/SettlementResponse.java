package com.spliteasy.backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementResponse {

    private Long id;
    private String from;
    private String to;
    private Double amount;
    private LocalDateTime settledAt;
}
