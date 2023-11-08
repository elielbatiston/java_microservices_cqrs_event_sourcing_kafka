package com.techbank.cqrs.core.infrastructure;

import com.techbank.cqrs.core.queries.BaseQuery;
import com.techbank.cqrs.core.queries.QueryHandlerMethod;

import java.util.List;

public interface QueryDispatcher {
	<T extends BaseQuery> void registerHandler(Class<T> type, QueryHandlerMethod<T> handler);
	<U extends BaseQuery> List<U> send(BaseQuery query);
}
