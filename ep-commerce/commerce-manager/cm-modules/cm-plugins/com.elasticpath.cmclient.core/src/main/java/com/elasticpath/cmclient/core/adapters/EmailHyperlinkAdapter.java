/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.adapters;

import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

import com.elasticpath.cmclient.core.util.HyperlinkUtil;

/**
 * More concrete version of {@link HyperlinkAdapter} used for opening email URLs
 * (e.g. mailto:user@domain)
 */
public class EmailHyperlinkAdapter extends HyperlinkAdapter {

	/**
	 * Default constructor.
	 */
	public EmailHyperlinkAdapter() {
		super();
	}

	@Override
	public void linkActivated(final HyperlinkEvent hyperlinkEvent) {
		HyperlinkUtil.openEmailHyperLink(hyperlinkEvent.getLabel());
	}
}
