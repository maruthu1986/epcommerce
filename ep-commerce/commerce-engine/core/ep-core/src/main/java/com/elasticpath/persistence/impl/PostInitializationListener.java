/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.persistence.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.openjpa.event.AttachListener;
import org.apache.openjpa.event.LifecycleEvent;
import org.apache.openjpa.event.LoadListener;
import org.apache.openjpa.event.PersistListener;
import org.springframework.beans.factory.BeanCreationException;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.support.PostInitializationStrategy;

/**
 * Occasionally, domain objects need to be post processed after they are loaded by OpenJPA.
 * If this can be done without invoking services or persistence, then this is a straightforward processing
 * task involving a @PostLoad and related annotations or an @EntityListener.
 *
 * However, if services, and especially spring services are required to do the post processing,
 * then this is more complicated, since @PostLoad listeners and @EntityListeners have no access
 * to the Spring context.
 *
 * The PostInitializationListener provides a standardized way to do post load initialization
 * on OpenJPA entities with spring injected beans.
 *
 * PersistablePostLoadStrategies attached to the PostInitializationListener are invoked during both the PostLoad
 * and PostAttach events, since both of these (may) result in new objects being created by OpenJPA.
 * They are not invoked during the PostRefresh event.
 *
 */
public class PostInitializationListener implements LoadListener, AttachListener, PersistListener {
	private static final Logger LOG = Logger.getLogger(PostInitializationListener.class);

	private List<PostInitializationStrategy<? extends Persistable>> postInitStrategies;
	private List<String> postInitStrategyIds;
	private BeanFactory beanFactory;

	@Override
	public void afterLoad(final LifecycleEvent event) {
		firePostInitializationEvent(event);
	}

	@Override
	public void afterRefresh(final LifecycleEvent event) {
		// Do Nothing
	}

	@Override
	public void beforeAttach(final LifecycleEvent event) {
		// Do Nothing
	}

	@Override
	public void afterAttach(final LifecycleEvent event) {
		firePostInitializationEvent(event);
	}

	@Override
	public void beforePersist(final LifecycleEvent event) {
		firePostInitializationEvent(event);
	}

	@Override
	public void afterPersist(final LifecycleEvent event) {
		// Do Nothing
	}

	/**
	 * Fires the process event on attached PostInitializationStrategy objects.
	 *
	 * @param event the underlying OpenJPA LifecycleEvent which triggered this event
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	protected void firePostInitializationEvent(final LifecycleEvent event) {
		for (PostInitializationStrategy strategy : getPostInitializationStrategies()) {
			if (strategy.canProcess(event.getSource(), getEventType(event))) {
				strategy.process((Persistable) event.getSource(), getEventType(event));
			}
		}
	}

	private PostInitializationStrategy.EventType getEventType(final LifecycleEvent event) {
		switch (event.getType()) {
			case LifecycleEvent.AFTER_LOAD:
				return PostInitializationStrategy.EventType.PostLoad;

			case LifecycleEvent.AFTER_ATTACH:
				return PostInitializationStrategy.EventType.PostUpdate;

			case LifecycleEvent.BEFORE_PERSIST:
				return PostInitializationStrategy.EventType.PreInsert;

			default:
				throw new EpServiceException("Unknown OpenJPA lifecycle event type " + event.getType());
		}
	}

	/**
	 * Lazy load the PostInitializationStrategy beans using the bean factory to avoid cycles in the
	 * Spring graph.
	 *
	 * @return the post load strategies
	 */
	protected List<PostInitializationStrategy<? extends Persistable>> getPostInitializationStrategies() {
		if (postInitStrategies == null) {
			try {
				List<PostInitializationStrategy<? extends Persistable>> strategies =
					new ArrayList<>(postInitStrategyIds.size());
				for (String beanName : postInitStrategyIds) {
					final PostInitializationStrategy<? extends Persistable> strategy =
							getBeanFactory().getBean(beanName);
					strategies.add(strategy);
				}

				this.postInitStrategies = strategies;
			} catch (BeanCreationException ex) {
				LOG.warn("PostInitializationListener initialization failed because bean ["
						+ ex.getBeanName() + "] could not be created.  Don't panic - "
						+ "this usually occurs because the spring context hasn't finished initializing.  "
						+ "This is OK as long as it stops happening after the spring context is up.");
				return Collections.emptyList();
			}
		}

		return postInitStrategies;
	}

	public void setPostInitializationStrategyBeanIds(final List<String> beanIds) {
		this.postInitStrategyIds = beanIds;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
