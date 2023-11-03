package com.techbank.account.common.events;

import com.techbank.cqrs.core.events.BaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
public class AccountClosedEvent extends BaseEvent {

}
