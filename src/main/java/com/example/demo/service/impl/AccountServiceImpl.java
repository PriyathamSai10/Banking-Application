package com.example.demo.service.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;

import com.example.demo.dto.AccountDto;
import com.example.demo.dto.TransactionDto;
import com.example.demo.dto.TransferFundDto;
import com.example.demo.entity.Account;
import com.example.demo.entity.Transaction;
import com.example.demo.exception.AccountException;
import com.example.demo.mapper.AccountMapper;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

	private AccountRepository accountRepository;
	private TransactionRepository transactionRepository;

	public AccountServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository){
		this.accountRepository = accountRepository;
		this.transactionRepository = transactionRepository;
	}

	@Override
	public AccountDto createAccount(AccountDto accountDto) {
		Account account = AccountMapper.mapToAccount(accountDto);
		Account savedAccount = accountRepository.save(account);

		return AccountMapper.mapToAccountDto(savedAccount);
	}

	@Override
	public AccountDto getAccountById(Long id) {

		Account accounts = 	accountRepository
							.findById(id)
							.orElseThrow(() -> new AccountException("Account does not exist"));

		return AccountMapper.mapToAccountDto(accounts);

	}

	@Override
	public AccountDto deposit(long Id, double amount) {
		
		Account account = accountRepository
							.findById(Id)
							.orElseThrow(() -> new AccountException("Account does not exist"));
		
		double total = account.getBalance() + amount;
		account.setBalance(total);
		Account savedAccount = accountRepository.save(account);

		Transaction transaction = new Transaction();
		transaction.setAccountId(Id);
		transaction.setAmount(amount);
		transaction.setTransactionType("DEPOSIT");
		transaction.setTimestamp(LocalDateTime.now());

		transactionRepository.save(transaction);

		return  AccountMapper.mapToAccountDto(savedAccount);
	}

	@Override
	public AccountDto withdraw(long Id, double amount) {
		Account account = accountRepository
							.findById(Id)
							.orElseThrow(() -> new AccountException("Account does not exist"));

		double userBalance = account.getBalance();

		if(userBalance < amount) throw new RuntimeException("Insufficient amount");

		userBalance = userBalance - amount;
		account.setBalance(userBalance);
		Account savedAccount = accountRepository.save(account);

		Transaction transaction= new Transaction();
		transaction.setAccountId(Id);
		transaction.setAmount(amount);
		transaction.setTransactionType("WITHDRAW");
		transaction.setTimestamp(LocalDateTime.now());

		transactionRepository.save(transaction);

		return AccountMapper.mapToAccountDto(savedAccount);
	}

	@Override
	public List<AccountDto> getAllAccounts() {
		List<Account> accounts =  accountRepository.findAll();
		List<AccountDto> accountDtos;

		accountDtos = accounts.stream().map((account) -> AccountMapper.mapToAccountDto(account)).collect(Collectors.toList());
		
		return accountDtos;
	}

	@Override
	public void delete(long id) {
		accountRepository
			.findById(id)
			.orElseThrow(() -> new AccountException("Account does not exist"));

		accountRepository.deleteById(id);

	}

	@Override
	public void transferFund(TransferFundDto transferFundDto) {
		
		Account senderAccount = accountRepository
									.findById(transferFundDto.fromAccountId())
									.orElseThrow(() -> new AccountException("Sender account does not exist"));

		Account receiverAccount = accountRepository
									.findById(transferFundDto.toAccountId())
									.orElseThrow(() -> new AccountException("Receiver account does not exist"));

		double senderBalance = senderAccount.getBalance();
		double receiverBalance = receiverAccount.getBalance();

		if(senderBalance < transferFundDto.amount()) throw new RuntimeException("Insufficient Amount");

		senderAccount.setBalance(senderBalance - transferFundDto.amount());
		receiverAccount.setBalance(receiverBalance + transferFundDto.amount());

		accountRepository.saveAll(Arrays.asList(senderAccount, receiverAccount));

		Transaction transaction = new Transaction();
		transaction.setAccountId(transferFundDto.fromAccountId());
		transaction.setToAccountId(transferFundDto.toAccountId());
		transaction.setAmount(transferFundDto.amount());
		transaction.setTransactionType("TRANSFER");
		transaction.setTimestamp(LocalDateTime.now());

		transactionRepository.save(transaction);


	}

	@Override
	public List<TransactionDto> getAccountTransactions(Long accountId) {
		List<Transaction> transactionHistory = transactionRepository
													.fetchTransactionHistory(accountId);


		List<TransactionDto> transactionDto = transactionHistory.stream()
												.map((transaction) -> convertEntityToDto(transaction))
												.collect(Collectors.toList());

		
		return transactionDto;
	}


	private TransactionDto convertEntityToDto(Transaction transaction) {
		return new TransactionDto( 
						transaction.getId(),
						transaction.getAccountId(),
						transaction.getToAccountId(),
						transaction.getAmount(),
						transaction.getTransactionType(),
						transaction.getTimestamp()
					);

	}

}

