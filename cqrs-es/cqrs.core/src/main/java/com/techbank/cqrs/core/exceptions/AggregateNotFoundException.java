package com.techbank.cqrs.core.exceptions;

public class AggregateNotFoundException extends RuntimeException {

	public AggregateNotFoundException(final String message) {
		super(message);
	}
}
