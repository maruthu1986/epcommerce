/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.impl;

import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.util.VelocityEngineInstanceFactory;
import com.elasticpath.email.producer.spi.composer.TemplateRenderer;
import com.elasticpath.service.catalogview.impl.ThreadLocalStorageImpl;

/**
 * Implementation of {@link TemplateRenderer} that uses Velocity to render templates.
 */
public class TemplateRendererImpl implements TemplateRenderer {

	// We need to use the thread local implementation to set storecode on the active thread. Will be picked up by velocity and resource managers
	private ThreadLocalStorageImpl tlStoreConfig;

	private VelocityEngineInstanceFactory velocityEngineInstanceFactory;

	@Override
	public String renderTemplate(final String template, final String storeCode, final Map<String, Object> templateResources) {
		final VelocityEngine velocityEngine = getVelocityEngineInstanceFactory().getVelocityEngine(storeCode);

		getStoreConfig().setStoreCode(storeCode);

		try {
			return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, templateResources);
		} catch (final VelocityException e) {
			throw new EpServiceException("Velocity error occurred while rendering email", e);
		}
	}

	protected ThreadLocalStorageImpl getStoreConfig() {
		return tlStoreConfig;
	}

	/**
	 * Set the store config object. Resource loading components used by the email service are using implementations that depend on a thread aware
	 * store config object, and therefore we force the implementation to be the ThreadLocal one.
	 *
	 * @param tlStoreConfig the threadlocalstoreconfig object
	 */
	public void setStoreConfig(final ThreadLocalStorageImpl tlStoreConfig) {
		this.tlStoreConfig = tlStoreConfig;
	}

	protected VelocityEngineInstanceFactory getVelocityEngineInstanceFactory() {
		return velocityEngineInstanceFactory;
	}

	public void setVelocityEngineInstanceFactory(final VelocityEngineInstanceFactory velocityEngineInstanceFactory) {
		this.velocityEngineInstanceFactory = velocityEngineInstanceFactory;
	}

}
