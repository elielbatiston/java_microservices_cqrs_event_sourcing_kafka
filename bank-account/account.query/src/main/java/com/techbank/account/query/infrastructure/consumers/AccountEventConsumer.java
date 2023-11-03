package com.techbank.account.query.infrastructure.consumers;

import com.techbank.account.common.events.AccountClosedEvent;
import com.techbank.account.common.events.AccountOpenedEvent;
import com.techbank.account.common.events.FundsDepositedEvent;
import com.techbank.account.common.events.FundsWithdrawnEvent;
import com.techbank.account.query.infrastructure.handlers.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class AccountEventConsumer implements EventConsumer {

	@Autowired
	private EventHandler eventHandler;

	@KafkaListener(topics = "AccountOpenedEvent", groupId = "${spring.kafka.consumer.group-id}")
	@Override
	public void consume(@Payload final AccountOpenedEvent event, final Acknowledgment ack) {
		eventHandler.on(event);
		ack.acknowledge();
	}

	@KafkaListener(topics = "FundsDepositedEvent", groupId = "${spring.kafka.consumer.group-id}")
	@Override
	public void consume(@Payload final FundsDepositedEvent event, final Acknowledgment ack) {
		eventHandler.on(event);
		ack.acknowledge();
	}

	@KafkaListener(topics = "FundsWithdrawnEvent", groupId = "${spring.kafka.consumer.group-id}")
	@Override
	public void consume(@Payload final FundsWithdrawnEvent event, final Acknowledgment ack) {
		eventHandler.on(event);
		ack.acknowledge();
	}

	@KafkaListener(topics = "AccountClosedEvent", groupId = "${spring.kafka.consumer.group-id}")
	@Override
	public void consume(@Payload final AccountClosedEvent event, final Acknowledgment ack) {
		eventHandler.on(event);
		ack.acknowledge();
	}
}
