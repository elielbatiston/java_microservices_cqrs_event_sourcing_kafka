package com.techbank.account.cmd.api.controllers;

import com.techbank.account.cmd.api.commands.CloseAccountCommand;
import com.techbank.account.cmd.api.commands.WithdrawFundsCommand;
import com.techbank.account.common.dto.BaseResponse;
import com.techbank.cqrs.core.exceptions.AggregateNotFoundException;
import com.techbank.cqrs.core.infrastructure.CommandDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping(path = "/api/v1/closeBankAccount")
public class CloseAccountController {

	private final Logger logger = Logger.getLogger(OpenAccountController.class.getName());

	@Autowired
	private CommandDispatcher commandDispatcher;

	@DeleteMapping(path = "/{id}")
	public ResponseEntity<BaseResponse> closeAccount(
		@PathVariable(value = "id") final String id
	) {
		try {
			commandDispatcher.send(new CloseAccountCommand(id));
			return new ResponseEntity<>(new BaseResponse("Bank account closure request successfully completed!"), HttpStatus.OK);
		} catch (final IllegalStateException | AggregateNotFoundException e) {
			logger.log(Level.WARNING, MessageFormat.format("Client made a bade request - {0}.", e.toString()));
			return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.BAD_REQUEST);
		} catch (final Exception e) {
			final var safeErrorMessage = MessageFormat.format("Error while processing request to close funds from bank account for id - {0}.", id);
			logger.log(Level.SEVERE, safeErrorMessage, e);
			return new ResponseEntity<>(new BaseResponse(safeErrorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
