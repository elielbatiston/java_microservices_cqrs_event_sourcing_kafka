package com.techbank.account.cmd.infrastructure;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.account.cmd.domain.EventStoreRepository;
import com.techbank.cqrs.core.events.BaseEvent;
import com.techbank.cqrs.core.events.EventModel;
import com.techbank.cqrs.core.exceptions.AggregateNotFoundException;
import com.techbank.cqrs.core.exceptions.ConcurrencyException;
import com.techbank.cqrs.core.infrastructure.EventStore;
import com.techbank.cqrs.core.producers.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountEventStore implements EventStore {

	@Autowired
	private EventProducer eventProducer;

	@Autowired
	private EventStoreRepository eventStoreRepository;

	@Override
	public void saveEvents(final String aggregateId, final Iterable<BaseEvent> events, final int expectedVersion) {
		final var eventStream = eventStoreRepository.findByAggregateIdentifier(aggregateId);
		if (expectedVersion != -1 && eventStream.get(eventStream.size() - 1).getVersion() != expectedVersion) {
			throw new ConcurrencyException();
		}
		var version = expectedVersion;
		for (final var event: events) {
			version++;
			event.setVersion(version);
			final var eventModel = EventModel.builder()
				.timeStamp(new Date())
				.aggregateIdentifier(aggregateId)
				.aggregateType(AccountAggregate.class.getTypeName())
				.version(version)
				.eventType(event.getClass().getTypeName())
				.eventData(event)
				.build();
			final var persistedEvent = eventStoreRepository.save(eventModel);
			if (!persistedEvent.getId().isEmpty()) {
				eventProducer.produce(event.getClass().getSimpleName(), event);
			}
		}
	}

	@Override
	public List<BaseEvent> getEvents(final String aggregateId) {
		final var eventStream = eventStoreRepository.findByAggregateIdentifier(aggregateId);
		if (eventStream == null || eventStream.isEmpty()) {
			throw new AggregateNotFoundException("Incorrect account ID provided!");
		}
		return eventStream.stream().map(EventModel::getEventData).collect(Collectors.toList());
	}

	@Override
	public List<String> getAggregateIds() {
		final var eventStream = eventStoreRepository.findAll();
		if (eventStream == null || eventStream.isEmpty()) {
			throw new IllegalStateException("Could not retrieve event stream from the event store!");
		}
		return eventStream.stream()
			.map(EventModel::getAggregateIdentifier)
			.distinct()
			.collect(Collectors.toList());
	}
}
