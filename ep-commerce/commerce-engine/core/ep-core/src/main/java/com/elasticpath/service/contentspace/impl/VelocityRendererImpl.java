/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.contentspace.impl;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.elasticpath.service.contentspace.RenderContext;
import com.elasticpath.service.contentspace.Renderer;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * Velocity implementation of {@link Renderer}.
 */
public class VelocityRendererImpl implements Renderer {

	private Properties props = new Properties();
	private VelocityEngine engine;
	private SettingValueProvider<String> contentWrappersLocationProvider;

	/**
	 * Renders a velocity template for a specific content space using a rendering context.
	 *
	 * @param contentSpaceName the content space name
	 * @param renderContext the context that is used when rendering a template
	 * @return the rendered velocity template as a string representation
	 * @throws Exception when template fails to render
	 */
	@Override
	public String doRender(final String contentSpaceName, final RenderContext renderContext) throws Exception {
		final String templateName = renderContext.getContentWrapper().getTemplateName();
		final Map<String, Object> resolvedParameters = renderContext.getParameters();

		final VelocityEngine velocityEngine = getVelocityEngine();

		return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
				getTemplateFullPath(templateName), resolvedParameters);
	}

	/**
	 * Gets the relative path to the template.
	 * Based on the assets path and concatenates 'content-wrapper'.
	 *
	 * @param templateName the template name
	 * @return a relative to the 'assets' folder path
	 */
	String getTemplateFullPath(final String templateName) {
		return getTemplatesLocation() + templateName;
	}

	/**
	 * Gets the templates location.
	 *
	 * @return the templates location relative to assets root folder
	 */
	protected String getTemplatesLocation() {
		return withTrailingSlash(getContentWrappersLocationProvider().get());
	}

	/**
	 * Creates a new velocity engine.
	 *
	 * @return a new velocity engine
	 * @throws Exception if error occurs
	 */
	VelocityEngine getVelocityEngine() throws Exception {
		if (engine == null) {
			engine = new VelocityEngine();
			for (final Map.Entry<Object, Object> entry : props.entrySet()) {
				String keyStr = (String) entry.getKey();
				engine.addProperty(keyStr, entry.getValue());
			}
			engine.init();
		}
		return engine;
	}

	/**
	 *
	 * @param props the properties to set
	 */
	public void setVelocityProperties(final Properties props) {
		this.props = props;
	}

	private String withTrailingSlash(final String input) {
		if (StringUtils.isEmpty(input) || input.endsWith("/") || input.endsWith("\\")) {
			return input;
		}
		return input + File.separator;
	}

	public void setContentWrappersLocationProvider(final SettingValueProvider<String> contentWrappersLocationProvider) {
		this.contentWrappersLocationProvider = contentWrappersLocationProvider;
	}

	protected SettingValueProvider<String> getContentWrappersLocationProvider() {
		return contentWrappersLocationProvider;
	}

}
