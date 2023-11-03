package com.techbank.account.query.infrastructure.handlers;

import com.techbank.account.common.events.AccountClosedEvent;
import com.techbank.account.common.events.AccountOpenedEvent;
import com.techbank.account.common.events.FundsDepositedEvent;
import com.techbank.account.common.events.FundsWithdrawEvent;
import com.techbank.account.query.domain.AccountRepository;
import com.techbank.account.query.domain.BankAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountEventHandler implements EventHandler {

	@Autowired
	private AccountRepository accountRepository;

	@Override
	public void on(final AccountOpenedEvent event) {
		var bankAccount = BankAccount.builder()
			.id(event.getId())
			.accountHoler(event.getAccountHolder())
			.creationDate(event.getCreatedDate())
			.accountType(event.getAccountType())
			.balance(event.getOpeningBalance())
			.build();
		accountRepository.save(bankAccount);
	}

	@Override
	public void on(final FundsDepositedEvent event) {
		var bankAccount = accountRepository.findById(event.getId());
		if (bankAccount.isEmpty()) {
			return ;
		}
		var currentBalance = bankAccount.get().getBalance();
		var latestBalance = currentBalance + event.getAmount();
		bankAccount.get().setBalance(latestBalance);
		accountRepository.save(bankAccount.get());
	}

	@Override
	public void on(final FundsWithdrawEvent event) {
		var bankAccount = accountRepository.findById(event.getId());
		if (bankAccount.isEmpty()) {
			return ;
		}
		var currentBalance = bankAccount.get().getBalance();
		var latestBalance = currentBalance - event.getAmount();
		bankAccount.get().setBalance(latestBalance);
		accountRepository.save(bankAccount.get());
	}

	@Override
	public void on(final AccountClosedEvent event) {
		accountRepository.deleteById(event.getId());
	}
}
