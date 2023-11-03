package com.techbank.account.cmd.infrastructure;

import com.techbank.account.cmd.domain.AccountAggregate;
import com.techbank.cqrs.core.domain.AggregateRoot;
import com.techbank.cqrs.core.events.BaseEvent;
import com.techbank.cqrs.core.handlers.EventSourcingHandler;
import com.techbank.cqrs.core.infrastructure.EventStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class AccountEventSourcingHandler implements EventSourcingHandler<AccountAggregate> {
	@Autowired
	private EventStore eventStore;

	@Override
	public void save(final AggregateRoot aggregate) {
		eventStore.saveEvents(aggregate.getId(), aggregate.getUncommittedChanges(), aggregate.getVersion());
		aggregate.markChangeAsCommitted();
	}

	@Override
	public AccountAggregate getById(final String id) {
		var aggregate = new AccountAggregate();
		var events = eventStore.getEvents(id);
		if (events != null && !events.isEmpty()) {
			aggregate.replayEvents(events);
			var lastestVersion = events.stream().map(BaseEvent::getVersion).max(Comparator.naturalOrder());
			aggregate.setVersion(lastestVersion.get());
		}
		return aggregate;
	}
}