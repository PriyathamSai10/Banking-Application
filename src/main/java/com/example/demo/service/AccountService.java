package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.AccountDto;
import com.example.demo.dto.TransactionDto;
import com.example.demo.dto.TransferFundDto;

public interface AccountService {
	AccountDto createAccount(AccountDto accountDto);

	AccountDto getAccountById(Long id);

	AccountDto deposit(long Id, double amount);

	AccountDto withdraw(long Id, double amount);

	List<AccountDto> getAllAccounts();

	void delete(long id);

	void transferFund(TransferFundDto transferFundDto);

	List<TransactionDto> getAccountTransactions(Long accountId);

}
