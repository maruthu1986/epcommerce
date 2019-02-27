/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.contentspace.impl;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import com.elasticpath.domain.contentspace.ContentWrapper;
import com.elasticpath.domain.contentspace.Parameter;
import com.elasticpath.domain.contentspace.impl.ContentWrapperImpl;
import com.elasticpath.domain.contentspace.impl.TemplateParameterImpl;
import com.elasticpath.service.contentspace.RenderContext;
import com.elasticpath.settings.test.support.SimpleSettingValueProvider;

/**
 * Velocity renderer test.
 */
public class VelocityRendererImplTest {

	private static final String TEMPLATE_PATH = "target/test-classes/conf/content-wrappers";
	private static final String TEMPLATE_NAME = "simpleContentSpacetemplate.cwt";

	/**
	 * Try to get render result from dummy implementations.
	 *
	 * @throws Exception exception thrown on render failure
	 */
	@Test
	public void testRendererImpl() throws Exception {
		final RenderContext renderContext = new RenderContext();

		final List<Parameter> paramDefs = new ArrayList<>();
		paramDefs.add(new TemplateParameterImpl("param1"));
		paramDefs.add(new TemplateParameterImpl("param2"));

		final ContentWrapper contentWrapper = new ContentWrapperImpl();
		contentWrapper.setTemplateParameters(paramDefs);
		contentWrapper.setTemplateName(TEMPLATE_NAME);
		renderContext.setContentWrapper(contentWrapper);

		final Map<String, Object> resolvedParameters = new HashMap<>();
		resolvedParameters.put("param1", "hello");
		resolvedParameters.put("param2", "bye");
		renderContext.setParameters(resolvedParameters);

		final VelocityRendererImpl renderer = new VelocityRendererImpl();
		renderer.setContentWrappersLocationProvider(new SimpleSettingValueProvider<>(TEMPLATE_PATH));
		renderer.setVelocityProperties(new Properties());

		final String rez = renderer.doRender("cs1", renderContext);

		assertTrue(rez.contains("hello"));
		assertTrue(rez.contains("bye"));
	}

}
