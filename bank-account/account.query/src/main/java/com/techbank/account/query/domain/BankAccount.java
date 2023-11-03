package com.techbank.account.query.domain;

import com.techbank.account.common.dto.AccountType;
import com.techbank.cqrs.core.domain.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class BankAccount extends BaseEntity {

	@Id
	private String id;
	private String accountHolder;
	private Date creationDate;
	private AccountType accountType;
	private double balance;
}
