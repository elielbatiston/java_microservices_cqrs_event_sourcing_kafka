package com.techbank.cqrs.core.domain;

import com.techbank.cqrs.core.events.BaseEvent;
import lombok.Getter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AggregateRoot {
	@Getter
	protected String id;
	@Getter
	private int version = -1;

	private final List<BaseEvent> changes = new ArrayList<>();
	private final Logger logger = Logger.getLogger(AggregateRoot.class.getName());

	public void setVersion(final int version) {
		this.version = version;
	}

	public List<BaseEvent> getUncommittedChanges() {
		return this.changes;
	}

	public void markChangesAsCommitted() {
		this.changes.clear();
	}

	protected void applyChanges(final BaseEvent event, final Boolean isNewEvent) {
		try {
			var method = getClass().getDeclaredMethod("apply", event.getClass());
			method.setAccessible(true);
			method.invoke(this, event);
		} catch (final NoSuchMethodException e) {
			logger.log(
				Level.WARNING,
				MessageFormat.format("The apply method was not found in the aggregate for {0}", event.getClass().getName())
			);
		} catch (final Exception e) {
			logger.log(Level.SEVERE, "Error applying event to aggregate", e);
		} finally {
			if (isNewEvent) {
				changes.add(event);
			}
		}
	}

	public void raiseEvent(final BaseEvent event) {
		applyChanges(event, true);
	}

	public void replayEvents(final Iterable<BaseEvent> events) {
		events.forEach(event -> applyChanges(event, false));
	}
}
