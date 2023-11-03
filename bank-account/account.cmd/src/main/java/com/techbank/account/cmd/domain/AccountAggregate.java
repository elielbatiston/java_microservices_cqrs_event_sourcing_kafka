package com.techbank.account.cmd.domain;

import com.techbank.account.cmd.api.commands.OpenAccountCommand;
import com.techbank.account.common.dto.AccountType;
import com.techbank.account.common.events.AccountClosedEvent;
import com.techbank.account.common.events.AccountOpenedEvent;
import com.techbank.account.common.events.FundsDepositedEvent;
import com.techbank.account.common.events.FundsWithdrawEvent;
import com.techbank.cqrs.core.domain.AggregateRoot;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
public class AccountAggregate extends AggregateRoot {
	private Boolean active;
	@Getter
	private double balance;

	public AccountAggregate(final OpenAccountCommand command) {
		raiseEvent(AccountOpenedEvent.builder()
			.id(command.getId())
			.accountHolder(command.getAccountHolder())
			.createdDate(new Date())
			.accountType(command.getAccountType())
			.openingBalance(command.getOpeningBalance())
			.build()
		);
	}

	public void apply(final AccountOpenedEvent event) {
		this.id = event.getId();
		this.active = true;
		this.balance = event.getOpeningBalance();
	}

	public void depositFunds(final double amount) {
		if (!this.active) {
			throw new IllegalStateException("Funds cannot be depoisted into a closed account!");
		}
		if (amount <= 0) {
			throw new IllegalStateException("The deposit amount be greater than 0!");
		}
		raiseEvent(FundsDepositedEvent.builder()
			.id(this.id)
			.amount(amount)
			.build()
		);
	}

	public void apply(final FundsDepositedEvent event) {
		this.id = event.getId();
		this.active = true;
		this.balance = event.getAmount();
	}

	public void withdrawFunds(final double amount) {
		if (!this.active) {
			throw new IllegalStateException("Funds cannot be withdrawn from a closed account!");
		}
		raiseEvent(FundsWithdrawEvent.builder()
			.id(this.id)
			.amount(amount)
			.build()
		);
	}

	public void apply(final FundsWithdrawEvent event) {
		this.id = event.getId();
		this.balance = event.getAmount();
	}

	public void closeAccount() {
		if (!this.active) {
			throw new IllegalStateException("The bank account has already been closed!");
		}
		raiseEvent(AccountClosedEvent.builder()
			.id(this.id)
			.build()
		);
	}

	public void apply(final AccountClosedEvent event) {
		this.id = event.getId();
		this.active = false;
	}
}
