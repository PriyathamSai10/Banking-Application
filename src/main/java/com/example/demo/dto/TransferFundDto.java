package com.example.demo.dto;

public record TransferFundDto(Long fromAccountId, Long toAccountId, double amount) {

}
