/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.persistence.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.openjpa.event.LifecycleEvent;
import org.apache.openjpa.event.LoadListener;
import org.springframework.beans.factory.BeanCreationException;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.support.PersistablePostLoadStrategy;

/**
 * Occasionally, domain objects need to be post processed after they are loaded by OpenJPA.
 * If this can be done without invoking services or persistence, then this is a straightforward processing
 * task involving a @PostLoad annotation or an @EntityListener.
 *
 * However, if services, and especially spring services are required to do the post processing,
 * then this is more complicated, since @PostLoad listeners and @EntityListeners have no access
 * to the Spring context.
 *
 * The PersistablePostLoadListener provides a standardized way to do post load initialization
 * on OpenJPA entities with spring injected beans.
 */
public class PersistablePostLoadListener implements LoadListener {
	private static final Logger LOG = Logger.getLogger(PersistablePostLoadListener.class);

	private List<PersistablePostLoadStrategy<? extends Persistable>> postLoadStrategies;
	private List<String> postLoadStrategyIds;
	private BeanFactory beanFactory;

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void afterLoad(final LifecycleEvent event) {
		for (PersistablePostLoadStrategy strategy : getPostLoadStrategies()) {
			if (strategy.canProcess(event.getSource())) {
				strategy.process((Persistable) event.getSource());
			}
		}
	}

	@Override
	public void afterRefresh(final LifecycleEvent event) {
		//  No implementation
	}

	/**
	 * Lazy load the PersistablePostLoadStrategy beans using the bean factory to avoid cycles in the
	 * Spring graph.
	 *
	 * @return the post load strategies
	 */
	protected List<PersistablePostLoadStrategy<? extends Persistable>> getPostLoadStrategies() {
		if (postLoadStrategies == null) {
			try {
				List<PersistablePostLoadStrategy<? extends Persistable>> strategies =
					new ArrayList<>(postLoadStrategyIds.size());
				for (String beanName : postLoadStrategyIds) {
					final PersistablePostLoadStrategy<? extends Persistable> strategy =
							getBeanFactory().getBean(beanName);
					strategies.add(strategy);
				}

				this.postLoadStrategies = strategies;
			} catch (BeanCreationException ex) {
				LOG.warn("PersistablePostLoadListener lazy initialization failed because the context is still being initialized and bean ["
						+ ex.getBeanName() + "] could not be created as a result."
						+ "This is generally OK as long as it stops happening after the spring context is up.  Will retry on next invocation");
				return Collections.emptyList();
			}
		}

		return postLoadStrategies;
	}

	public void setPostLoadStrategyBeanIds(final List<String> beanIds) {
		this.postLoadStrategyIds = beanIds;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
