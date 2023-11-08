package com.techbank.account.cmd.infrastructure;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.cqrs.core.domain.AggregateRoot;
import com.techbank.cqrs.core.events.BaseEvent;
import com.techbank.cqrs.core.handlers.EventSourcingHandler;
import com.techbank.cqrs.core.infrastructure.EventStore;
import com.techbank.cqrs.core.producers.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class AccountEventSourcingHandler implements EventSourcingHandler<AccountAggregate> {

	@Autowired
	private EventStore eventStore;

	@Autowired
	private EventProducer eventProducer;

	@Override
	public void save(final AggregateRoot aggregate) {
		eventStore.saveEvents(aggregate.getId(), aggregate.getUncommittedChanges(), aggregate.getVersion());
		aggregate.markChangesAsCommitted();
	}

	@Override
	public AccountAggregate getById(final String id) {
		final var aggregate = new AccountAggregate();
		final var events = eventStore.getEvents(id);
		if (events != null && !events.isEmpty()) {
			aggregate.replayEvents(events);
			var lastestVersion = events.stream().map(BaseEvent::getVersion).max(Comparator.naturalOrder());
			aggregate.setVersion(lastestVersion.get());
		}
		return aggregate;
	}

	@Override
	public void republishEvents() {
		final var aggregateIds = eventStore.getAggregateIds();
		for (final var aggregateId: aggregateIds) {
			final var aggragate = getById(aggregateId);
			if (aggragate == null || !aggragate.getActive()) {
				continue;
			}
			final var events = eventStore.getEvents(aggregateId);
			for (var event: events) {
				eventProducer.produce(event.getClass().getSimpleName(), event);
			}
		}
	}
}
