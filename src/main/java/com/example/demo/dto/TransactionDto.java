package com.example.demo.dto;

import java.time.LocalDateTime;

public record TransactionDto(Long id, Long accountId, Long toAccountId,  double amount, String transactionType, LocalDateTime timestamp) {

}
