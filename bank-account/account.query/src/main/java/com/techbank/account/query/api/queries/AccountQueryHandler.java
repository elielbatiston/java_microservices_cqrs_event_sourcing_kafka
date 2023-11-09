package com.techbank.account.query.api.queries;

import com.techbank.account.query.api.dto.EqualityType;
import com.techbank.account.query.domain.AccountRepository;
import com.techbank.account.query.domain.BankAccount;
import com.techbank.cqrs.core.domain.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountQueryHandler implements QueryHandler {

	@Autowired
	private AccountRepository accountRepository;

	@Override
	public List<BaseEntity> handle(final FindAllAccountsQuery query) {
		final Iterable<BankAccount> bankAccounts = accountRepository.findAll();
		final List<BaseEntity> bankAccountList = new ArrayList<>();
		bankAccounts.forEach(bankAccountList::add);
		return bankAccountList;
	}

	@Override
	public List<BaseEntity> handle(final FindAccountByIdQuery query) {
		final var bankAccount = accountRepository.findById(query.getId());
		if (bankAccount.isEmpty()) {
			return null;
		}
		final List<BaseEntity> bankAccountList = new ArrayList<>();
		bankAccountList.add(bankAccount.get());
		return bankAccountList;
	}

	@Override
	public List<BaseEntity> handle(final FindAccountByHolderQuery query) {
		final var bankAccount = accountRepository.findByAccountHolder(query.getAccountHolder());
		if (bankAccount.isEmpty()) {
			return null;
		}
		final List<BaseEntity> bankAccountList = new ArrayList<>();
		bankAccountList.add(bankAccount.get());
		return bankAccountList;
	}

	@Override
	public List<BaseEntity> handle(final FindAccountWithBalanceQuery query) {
		return query.getEqualityType() == EqualityType.GREATER_THAN
			? accountRepository.findByBalanceGreaterThan(query.getBalance())
			: accountRepository.findByBalanceLessThan(query.getBalance());
	}
}
