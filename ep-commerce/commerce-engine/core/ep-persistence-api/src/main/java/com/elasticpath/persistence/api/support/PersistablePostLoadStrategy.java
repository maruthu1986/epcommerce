/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.persistence.api.support;

import com.elasticpath.persistence.api.Persistable;

/**
 * PersistablePostLoadStrategies are used by the PersistablePostLoadListener to process
 * specific OpenJPA entities after loading.
 *
 * Because PersistablePostLoadStrategies can be spring beans and can invoke persistent services,
 * this gets around limitations in @PostLoad annotated domain methods and @EntityListener annotated
 * listeners.
 *
 * @param <P> the persistable type that this strategy can process
 */
public interface PersistablePostLoadStrategy<P extends Persistable> {
	/**
	 * Returns true if this strategy is able to process the given object.
	 *
	 * @param obj the object
	 * @return true if this strategy is able to process the given object
	 */
	boolean canProcess(Object obj);

	/**
	 * Process the given object.
	 * @param persistable the object to process.
	 */
	void process(P persistable);
}
