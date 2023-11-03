package com.techbank.account.cmd.api.commands;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.cqrs.core.handlers.EventSourcingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountCommandHandler implements CommandHandler {

	@Autowired
	private EventSourcingHandler<AccountAggregate> eventSourcingHandler;

	@Override
	public void handle(final OpenAccountCommand command) {
		var aggregate = new AccountAggregate(command);
		eventSourcingHandler.save(aggregate);
	}

	@Override
	public void handle(final DepositFundsCommand command) {
		var aggregate = eventSourcingHandler.getById(command.getId());
		aggregate.depositFunds(command.getAmount());
		eventSourcingHandler.save(aggregate);
	}

	@Override
	public void handle(final WithdrawFundsCommand command) {
		var aggregate = eventSourcingHandler.getById(command.getId());
		if (command.getAmount() > aggregate.getBalance()) {
			throw new IllegalStateException("Withdrawal declined, insufficient funds!");
		}
		aggregate.withdrawFunds(command.getAmount());
		eventSourcingHandler.save(aggregate);
	}

	@Override
	public void handle(final CloseAccountCommand command) {
		var aggregate = eventSourcingHandler.getById(command.getId());
		aggregate.closeAccount();
		eventSourcingHandler.save(aggregate);
	}
}
