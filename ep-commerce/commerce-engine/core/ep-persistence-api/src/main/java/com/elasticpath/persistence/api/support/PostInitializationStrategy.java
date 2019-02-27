/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.persistence.api.support;

import com.elasticpath.persistence.api.Persistable;

/**
 * PostInitializationStrategies are used by the {@link com.elasticpath.persistence.impl.PostInitializationListener}
 * to process specific OpenJPA entities after loading and attach (merge).
 *
 * Because PostInitializationStrategies can be spring beans and can invoke persistent services,
 * this gets around limitations in @PostLoad annotated domain methods and @EntityListener annotated
 * listeners.
 *
 * PostInitializationStrategies attached to OpenJPA listeners are invoked during PostLoad,
 * PostAttach and PrePersist events, since all of these (may) result in new objects being created by OpenJPA.
 * They are not invoked during the PostRefresh event.
 *
 * @param <P> the persistable type that this strategy can process
 */
public interface PostInitializationStrategy<P extends Persistable> {
	/**
	 * The type of event that triggered this initialization.
	 */
	enum EventType {
		/** Event sent before this object was inserted/persisted. */
		PreInsert,

		/** Event sent after this object was updated/merged. */
		PostUpdate,

		/** Event sent after this object was loaded. */
		PostLoad
	}

	/**
	 * Returns true if this strategy is able to process the given object.
	 *
	 * @param obj the object
	 * @param eventType the event type which triggered the event
	 * @return true if this strategy is able to process the given object
	 */
	boolean canProcess(Object obj, EventType eventType);

	/**
	 * Process the given object.
	 * @param persistable the object to process.
	 * @param eventType the event type which triggered the event
	 */
	void process(P persistable, EventType eventType);
}
