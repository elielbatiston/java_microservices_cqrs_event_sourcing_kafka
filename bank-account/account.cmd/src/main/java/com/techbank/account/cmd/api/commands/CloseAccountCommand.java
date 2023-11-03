package com.techbank.account.cmd.api.commands;

import com.techbank.cqrs.core.commands.BaseCommand;

public class CloseAccountCommand extends BaseCommand {
	public CloseAccountCommand(final String id) {
		super(id);
	}
}
