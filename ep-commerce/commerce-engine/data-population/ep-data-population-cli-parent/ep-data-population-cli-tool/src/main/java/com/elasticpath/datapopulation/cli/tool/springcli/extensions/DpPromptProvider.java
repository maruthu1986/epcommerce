/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.cli.tool.springcli.extensions;

import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.PromptProvider;
import org.springframework.stereotype.Component;

/**
 * Standard Data Population CLI {@link PromptProvider} implementation.
 */
@Component
@Order(DpBannerProvider.STANDARD_DATA_POPULATION_PROVIDER_PRECEDENCE)
public class DpPromptProvider implements PromptProvider {
	/**
	 * The prompt returned by {@link #getPrompt()}.
	 */
	protected static final String PROMPT = "dp-cli>";
	/**
	 * The name returned by {@link #name()}.
	 */
	private static final String PROMPT_PROVIDER_NAME = "Data Population CLI Prompt Provider";

	@Override
	public String name() {
		return PROMPT_PROVIDER_NAME;
	}

	@Override
	public String getPrompt() {
		return PROMPT;
	}
}
