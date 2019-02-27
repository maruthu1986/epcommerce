/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.persistence.impl;

import org.apache.openjpa.enhance.PersistenceCapable;
import org.apache.openjpa.event.AbstractLifecycleListener;
import org.apache.openjpa.event.LifecycleEvent;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.DatabaseCreationDate;
import com.elasticpath.domain.DatabaseLastModifiedDate;
import com.elasticpath.service.misc.TimeService;

/**
 * EntityListener which sets the {@code lastModifiedDate} and {@code creationDate} on entities.
 */
public class DatabaseTimestampsEntityListener extends AbstractLifecycleListener {
	
	private TimeService timeService;
	private BeanFactory beanFactory;
	
	@Override
	public void eventOccurred(final LifecycleEvent event) {
		switch(event.getType()) {
		case LifecycleEvent.BEFORE_ATTACH:
		case LifecycleEvent.BEFORE_STORE:
			PersistenceCapable pcObject = (PersistenceCapable) event.getSource();
			if (pcObject.pcIsDirty()) {
				setDatabaseTimestamps(event);
			}
			break;
		case LifecycleEvent.BEFORE_PERSIST:
			setLastModifiedDate(event);
			break;
		default:
			// No - op
		}
	}

	private void setDatabaseTimestamps(final LifecycleEvent event) {
		setCreationDate(event);
		setLastModifiedDate(event);
	}

	private void setCreationDate(final LifecycleEvent event) {
		Object source = event.getSource();
		if (source instanceof DatabaseCreationDate) {
			DatabaseCreationDate creationDateObject = (DatabaseCreationDate) source;
			if (creationDateObject.getCreationDate() == null) {
				creationDateObject.setCreationDate(getTimeService().getCurrentTime());
			}
		}
	}

	private void setLastModifiedDate(final LifecycleEvent event) {
		Object source = event.getSource();
		if (source instanceof DatabaseLastModifiedDate) {
			DatabaseLastModifiedDate lmdObject = (DatabaseLastModifiedDate) source;
			lmdObject.setLastModifiedDate(getTimeService().getCurrentTime());
		}
	}
	
	/**
	 * Get the time service.
	 * 
	 * @return the time service.
	 */
	protected TimeService getTimeService() {
		if (timeService == null) {
			timeService = beanFactory.getBean(ContextIdNames.TIME_SERVICE);
		}
		return timeService;
	}
	
	/**
	 * 
	 * @param beanFactory The bean factory.
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory; 
	}
}
