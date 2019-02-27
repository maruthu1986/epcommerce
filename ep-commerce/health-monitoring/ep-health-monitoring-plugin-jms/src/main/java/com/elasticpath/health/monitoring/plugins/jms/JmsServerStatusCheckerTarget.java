/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.plugins.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import com.elasticpath.health.monitoring.Status;
import com.elasticpath.health.monitoring.StatusType;
import com.elasticpath.health.monitoring.impl.AbstractStatusCheckerTarget;

/**
 * Tries to connect to JMS Server to check if the instance is running.
 */
public class JmsServerStatusCheckerTarget extends AbstractStatusCheckerTarget {

	private ConnectionFactory jmsConnectionFactory;

	@Override
	public Status check() {

		Connection connection = null;
		Status status = createStatus(StatusType.OK, "Successfully connected to JMS Server", null);

		try {
			connection = jmsConnectionFactory.createConnection();
		} catch (JMSException e) {
			status = createStatus(StatusType.CRITICAL, "Failed to connect to JMS Server", e.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					status = createStatus(StatusType.CRITICAL, "Failed to close connection to JMS Server", e.getMessage());
				}
			}
		}

		return status;
	}

	public void setJmsConnectionFactory(final ConnectionFactory jmsConnectionFactory) {
		this.jmsConnectionFactory = jmsConnectionFactory;
	}

}
