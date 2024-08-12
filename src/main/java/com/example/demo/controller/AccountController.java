package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AccountDto;
import com.example.demo.dto.TransactionDto;
import com.example.demo.dto.TransferFundDto;
import com.example.demo.entity.Transaction;
import com.example.demo.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

	private AccountService accountService;

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@PostMapping("/create-account")
	public ResponseEntity<AccountDto> addAccount(@RequestBody AccountDto accountDto) {
		return new ResponseEntity<>(accountService.createAccount(accountDto), HttpStatus.CREATED);		
	}

	@GetMapping("/get-accounts/{id}")
	public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id) {
		AccountDto accountDto = accountService.getAccountById(id);

		return ResponseEntity.ok(accountDto);
	} 

	@PutMapping("/deposit/{id}")
	public ResponseEntity<AccountDto> deposit(@PathVariable Long id, @RequestBody Map<String, Double> request) {
		Double amount = request.get("amount");
		AccountDto accountDto =  accountService.deposit(id, amount);

		return ResponseEntity.ok(accountDto);
	}	

	@PutMapping("/withdraw/{id}")
	public ResponseEntity<AccountDto> withdraw(@PathVariable Long id, @RequestBody Map<String, Double> request) {
		double amount = request.get("amount");

		AccountDto accountDto = accountService.withdraw(id, amount);

		return ResponseEntity.ok(accountDto);

	}

	@GetMapping("/get-all-accounts")
	public ResponseEntity<List<AccountDto>> getAllAccounts() {
		List<AccountDto> accountsDto = accountService.getAllAccounts();

		return ResponseEntity.ok(accountsDto);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> delete(@PathVariable Long id) {
		accountService.delete(id);

		return ResponseEntity.ok("Account deletion is successfull");
	}

	@PostMapping("/transfer")
	public ResponseEntity<String> transfer(@RequestBody TransferFundDto transferFundDto) {
		accountService.transferFund(transferFundDto);

		return ResponseEntity.ok("Transfer successful");
	}


	@GetMapping("/{id}/transactions")
	public ResponseEntity<List<TransactionDto>> fetchAccountTransactions(@PathVariable Long id){
		List<TransactionDto> transactions = accountService.getAccountTransactions(id);

		return ResponseEntity.ok(transactions);
	}

}
